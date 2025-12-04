# 스마트시티 데이터허브–스마트시티 통합플랫폼 인터워킹 모듈 매뉴얼

- 패키지 루트: `kr.re.keti.sc.interworking`
- 목적: **스마트도시 안전망서비스(Integration Platform)** 과 **스마트시티 데이터허브** 간 이벤트/알림을 상호 변환·전송하는 Spring Boot 기반 인터워킹 서비스

## 1. 전체 개념 및 동작 흐름

### 1.1 주요 역할

이 모듈은 두 가지 방향의 연동을 담당합니다.

1. **스마트도시 안전망서비스 → 스마트시티 데이터허브**
   - 스마트도시 안전망서비스에서 발생한 이벤트를 HTTP로 수신
   - DB에 원문 이벤트 및 처리 상태 저장
   - 매핑 규칙에 따라 NGSI-LD 스타일의 엔티티로 변환
   - Data Hub Ingest 인터페이스로 전송

2. **스마트시티 데이터허브 → 스마트도시 안전망서비스**
   - Data Hub Subscription Notification 수신
   - 매핑 규칙에 따라 스마트도시 안전망서비스 이벤트 포맷으로 변환
   - 스마트도시 안전망서비스 인증(세션키 발급) 수행
   - 변환된 이벤트를 스마트도시 안전망서비스 API로 전송

### 1.2 전체 흐름 다이어그램(개념)

```text
[스마트도시 안전망서비스]                 [인터워킹 모듈]                      [스마트시티 데이터허브]

   1) 이벤트 HTTP POST       ->  /integrationPlatformEvent
                                  ↓ DB 저장(History)
                                  ↓ (스케줄러) 미처리 이벤트 조회
                                  ↓ Mapping Rule 조회
                                  ↓ Datahub 엔티티로 변환
                                  ↓ Datahub Ingest API 호출
                                                                                ← 2) 엔티티 저장

[스마트시티 데이터허브]                  [인터워킹 모듈]                      [스마트도시 안전망서비스]

   3) Notification POST      ->  /datahubNotification
                                  ↓ Mapping Rule 조회 (엔티티 type 기준)
                                  ↓ 스마트도시 안전망서비스 이벤트로 변환
                                  ↓ 인증 API 호출(세션키 획득)
                                  ↓ 스마트도시 안전망서비스 이벤트 API 호출            ->  4) 이벤트 수신/처리
```

---

## 2. 패키지 구조

```text
kr.re.keti.sc.interworking
 ├─ Application.java                  // Spring Boot 시작점
 ├─ common
 │   ├─ HttpConstants.java            // 공통 HTTP 헤더 상수
 │   ├─ ResponseCode.java             // 응답코드 Enum
 │   ├─ ControllerExceptionHandler…   // 전역 예외 처리
 │   └─ exception/*                   // 커스텀 예외들
 ├─ datahub
 │   ├─ controller/DatahubController  // /datahubNotification 엔드포인트
 │   ├─ mapper/DatahubMapper(.xml)    // DataHub 측 매핑룰 조회
 │   ├─ model/*                       // Notification, CommonEntityVO 등 도메인 모델
 │   └─ service/DatahubService        // Notification history 등 (확장 포인트)
 ├─ integrationplatform
 │   ├─ controller/IntegrationPlatformEventController
 │   │                                  // /integrationPlatformEvent 엔드포인트
 │   ├─ mapper/IntegrationPlatformEventMapper(.xml)
 │   │                                  // 통합플랫폼 이벤트/매핑룰/History DB 접근
 │   ├─ model/*                         // IntegrationPlatformEventMessage, MappingRule 등
 │   └─ service/*
 │       ├─ IntegrationPlatformEventService
 │       │                              // 수신 이벤트 배치 처리 → DataHub Ingest 요청 이벤트 발생
 │       ├─ IntegrationPlatformAuthenticationService
 │       │                              // 인증 메시지 전송 및 세션키 획득
 │       └─ AuthenticationInitializer   // 기동 시 1회 인증 트리거
 └─ transmission
     ├─ DatahubIngestRequestEventHandler
     │                                  // 통합플랫폼→DataHub 방향 Ingest 호출
     └─ IntegrationPlatformSendingEventHandler
                                        // DataHub→통합플랫폼 방향 이벤트 송신
```

---

## 3. REST API 명세

### 3.1 스마트도시 안전망서비스 이벤트 수신 API

- **URL**: `/integrationPlatformEvent`
- **HTTP Method**: `POST`
- **Content-Type**: `application/x-www-form-urlencoded`
- **컨트롤러**: `IntegrationPlatformEventController.receiveEvent(...)`

#### 3.1.1 요청 파라미터 (Form URL Encoded)

`IntegrationPlatformEventDTO` 기준:

| 필드명     | 설명                                      |
|-----------|-------------------------------------------|
| `svcTy`   | 서비스 타입                               |
| `hdCnt`   | 헤더 개수                                 |
| `dtCnt`   | 데이터 개수                               |
| `apSeq`   | Application Sequence (추적용)             |
| `cmnMtd`  | 통신 방식                                 |
| `sendCd`  | 송신 기관 코드                            |
| `rcvCd`   | 수신 기관 코드                            |
| `encAt`   | 암호화 여부(Y/N)                          |
| `tranTy`  | 트랜잭션 타입                             |
| `rrKey`   | 요청/응답 키                              |
| `reqDate` | 요청 일시                                 |
| `bodyLen` | Body 길이                                 |
| `sysCd`   | 시스템 코드                               |
| `tranSysHis` | 트랜잭션 시스템 이력(JSON 문자열)      |
| `Body`    | **이벤트 본문(JSON 문자열, EventBody)**  |

컨트롤러 내부에서:

1. `IntegrationPlatformEventDTO` → `IntegrationPlatformEventMessage` 로 변환 (`convertDTOtoVO`)
2. `IntegrationPlatformEventHistory` 엔티티 생성
3. `IntegrationPlatformEventService.createIntegrationPlatformEventHistory(...)` 호출 → DB 저장
4. 성공 시 `IntegrationPlatformResponse` 반환

#### 3.1.2 요청 헤더

`HttpConstants.HeaderFieldName` 기준:

| 헤더명             | 설명                  |
|--------------------|-----------------------|
| `Accept`           | 응답 Accept 타입      |
| `Accept-Charset`   | 문자셋                |
| `Authorization`    | (선택) 인증정보       |

통합플랫폼 자체의 헤더(`X-Scsns-*`)는 여기서는 사용하지 않고, **실제 이벤트 Body 안의 값**으로 관리합니다.

#### 3.1.3 응답

- Body 타입: `IntegrationPlatformResponse`

예시:

```json
{
  "startTag": "$DA",
  "result": "0",
  "type": "2000",
  "message": "",
  "finishTag": "$DF"
}
```

- `result`  
  - `"0"`: 성공  
  - `"1"`: 실패  
- `type`: `ResponseCode` Enum 값(상세코드)
- `message`: 에러 메시지 등

에러 발생 시 `ControllerExceptionHandler` 가 `IntegrationPlatformResponse.obtainFailureResponse(...)` 형태로 응답합니다.

---

### 3.2 스마트시티 데이터허브 Notification 수신 API

- **URL**: `/datahubNotification`
- **HTTP Method**: `POST`
- **Content-Type**: `application/json`
- **컨트롤러**: `DatahubController.receiveNotification(...)`

#### 3.2.1 요청 Body (Notification)

`kr.re.keti.sc.interworking.datahub.model.Notification`

```json
{
  "id": "notification-id",
  "type": "Notification",
  "subscriptionId": "sub-001",
  "notifiedAt": "2024-01-01T10:00:00.000+09:00",
  "data": [
    {
      "id": "urn:ngsi-ld:IntegrityEvent:1",
      "type": "IntegrityEvent",
      "datasetId": "dataset-001",
      "evtId": "E001",
      "ocrEvtTime": "2024-01-01T09:59:00.000+09:00",
      "...": "..."
    }
  ]
}
```

- `data` 배열의 각 요소는 `CommonEntityVO` (실제 구현은 `HashMap<String, Object>` 기반)로 처리합니다.

#### 3.2.2 동작 플로우

1. 수신한 `Notification` 전체를 로그로 출력
   ```java
   String bodyJson = objectMapper.writeValueAsString(notification);
   log.info("### Received notification body: {}", bodyJson);
   ```
2. `notification.getData()` 의 각 `CommonEntityVO`에 대해
   - 엔티티 `type` 추출
   - `DatahubMapper.selectMappingRule(QueryValue(datahubEntityType))` 로 매핑 룰 조회
   - 룰이 없으면 로그만 남기고 continue
   - 룰이 있으면 `IntegrationPlatformRequestEvent` 생성 후 `ApplicationEventPublisher` 로 발행
3. 항상 `IntegrationPlatformResponse.obtainSuccessResponse()` 반환

> **주의**: Notification 처리 중 오류가 나더라도, 통합플랫폼과의 연동 실패가
> Data Hub 쪽에서 재전송 폭주를 일으키지 않도록 REST 응답은 기본적으로 성공 형태를 유지하고,  
> 상세 오류는 로그 및 내부 상태로 관리하는 구조입니다.

#### 3.2.3 응답

- Body 타입: `IntegrationPlatformResponse` (3.1.3과 동일 규격)

---

## 4. 내부 이벤트 처리 플로우

### 4.1 스마트도시 안전망서비스 → 스마트시티 데이터허브

#### 4.1.1 수신 및 저장

1. `/integrationPlatformEvent` 에서 이벤트 수신  
2. `IntegrationPlatformEventMessage` 로 파싱
3. `IntegrationPlatformEventHistory` 생성 및 `processStatus = NOT_PROCESSED`
4. DB 테이블: `integration_platform.integration_platform_received_event_history`

#### 4.1.2 배치 처리 (스케줄러)

- 클래스: `IntegrationPlatformEventService`
- 메서드: `processReceivedIntegrationPlatformEvent()`
  - `@Scheduled(fixedDelayString = "${integrationPlatform-to-datahub.scheduleMillisecond}")`
  - 필요 시 스케줄러 활성화

동작:

1. `selectNotProcessedIntegrationPlatformEventHistory()` 로 `NOT_PROCESSED` 상태의 레코드 조회
2. 각 `IntegrationPlatformEventHistory` 에 대해:
   - Body 파싱 오류 → `PARSING_ERROR`
   - 복호화 실패 → `DECRYPTION_ERROR`
   - 이미 처리된 이벤트 존재 여부 체크 (`selectAlreadyProcessedIntegrationPlatformEventHistory`)
   - 매핑 룰 조회 (`IntegrationPlatformEventMapper.selectMappingRule`)
     - 없으면 `MAPPING_RULE_NOT_FOUND`
   - 매핑 룰 선택 (`obtainProperMappingRule`)
   - `DatahubIngestRequestEvent`를 발행

#### 4.1.3 스마트시티 데이터허브 Ingest 인터페이스 호출

- 클래스: `DatahubIngestRequestEventHandler`
- 이벤트 리스너: `onApplicationEvent(DatahubIngestRequestEvent event)`

주요 내용:

1. `MappingRule` + `IntegrationPlatformEventHistory` → `IngestInterfacePayload` 생성
   - `datasetId` 세팅 (`mappingRule.getDatahubDatasetId()`)
   - `entities` 리스트에 NGSI-LD 스타일 엔티티 추가
   - 엔티티 필드:
     - `id`: 통합플랫폼 이벤트 ID + 발생번호 등을 조합 (`obtainEntityId`)
     - `type`: `mappingRule.getDatahubEntityType()`
     - `@context`: 고정 URL (`http://uri.citydatahub.kr/ngsi-ld/v1/ngsi-ld-core-context-v1.3.jsonld`)
     - 이벤트 관련 속성들:
       - `evtId`, `evtNm`, `evtGrad`, `ocrNbr`, `ocrSts` …
       - `ocrEvtTime`, `ocrEvtStsTime`, `ocrEvtEndTime` 등
       - `ocrEvtItem`: MappingRule.Attribute 기반으로 생성되는 항목들
   - `Property`, `GeoProperty`, `DateProperty` 등을 사용해 NGSI-LD 속성 구조로 변환

2. REST 호출
   - URL: `${datahub.ingestInterfaceUri}`
   - Method: `POST`
   - Content-Type: `application/json`
   - Request Body: `IngestInterfacePayload`
   - Response Body: `Response` (성공/에러 리스트)

3. 결과에 따른 상태 업데이트
   - 성공 시: `ProcessStatus.PROCESSED`
   - 실패 시: `DATAHUB_INTERWORKING_ERROR` / `DATAHUB_NOT_REACHABLE` 등

---

### 4.2 스마트시티 데이터허브 → 스마트도시 안전망서비스

#### 4.2.1 Notification 수신

- `DatahubController.receiveNotification(...)`  
- 위 3.2에 설명한 대로, 매핑 룰 조회 후 `IntegrationPlatformRequestEvent` 발행

#### 4.2.2 스마트도시 안전망서비스 인증

- 클래스: `IntegrationPlatformAuthenticationService`
- 설정:
  - `${datahub-to-integrationPlatform.apiKey}`
  - `${integrationPlatform.authenticationUri}`

동작:

1. `IntegrationPlatformAuthenticationMessage.obtainDefaultIntegrationPlatformAuthentication()` 로 기본 헤더 생성
2. Body에 `apiKey` 세팅
3. `HttpHeaders` 구성:
   - `Content-Type: application/json`
   - `X-Scsns-svcTy`, `X-Scsns-sendCd`, `X-Scsns-rcvCd`, `X-Scsns-sysCd`, `X-Scsns-encAt`, `X-Scsns-Secret`, `X-Scsns-type`
4. 인증 요청 전송: `POST {authenticationUri}`
5. 응답이 성공(`result == "0"`)이면 `message` 필드에 포함된 세션키를 `CommonVariables.sessionKey` 에 저장

- 기동 시 자동 인증:
  - `AuthenticationInitializer` 가 `@PostConstruct` 에서 `AuthenticationRequestEvent` 발행

#### 4.2.3 이벤트 송신

- 클래스: `IntegrationPlatformSendingEventHandler`
- 리스너: `onMyEvent(IntegrationPlatformRequestEvent event)`

동작:

1. `integrationPlatformAuthenticationService.authenticate()` 호출 → 세션키 확보
2. `IntegrationPlatformEventMessage.obtainDefaultIntegrationPlatformEvent(encryptionYn)` 로 헤더 기본값 생성
3. `MappingRule` + `Notification` 데이터(`CommonEntityVO`)를 이용해 `EventBody` 생성
   - 기본 필드:
     - `evtId`, `evtNm`, `evtGrad`
     - `ocrNbr`(발생번호), `ocrEvtTime`, `ocrEvtStsTime` 등
     - `ocrEvtSts`, `ocrEvtStsUsr`, `ocrEvtDtl` 등 상태/상세
   - `attributes` 리스트를 순회하며:
     - DataHub 속성 타입/데이터타입(`AttributeType`, `AttributeDataType`)에 따라 값 추출
     - `observedAt` 유무(`datahubAttributeHasObservedAt`)에 따라 시각 계산
     - GeoJSON(Point) → `OccurrenceLocation(crdntX, crdntY, crdntZ)` 변환
4. 암호화 처리(옵션)
   - 설정: `${datahub-to-integrationPlatform.encryptionYn}`, `${datahub-to-integrationPlatform.encryptionKey}`
   - `AriaCipher` 사용
   - `encryptionYn == "Y"` 이면 Body를 암호화 문자열로 치환

5. HTTP 요청
   - URL: `${integrationPlatform.eventUri}`
   - Method: `POST`
   - Headers:
     - `Content-Type: application/json`
     - `Accept: application/json`
     - `X-Scsns-svcTy`, `X-Scsns-sendCd`, `X-Scsns-rcvCd`, `X-Scsns-sysCd`, `X-Scsns-encAt`, `X-Scsns-Secret(sessionKey)`, `X-Scsns-type`
   - Body: `IntegrationPlatformEventMessage` (암호화 여부에 따라 Body 문자열이 달라짐)

6. 응답에 따라 로깅 및 오류 처리

---

## 5. 설정 파일(`application.yml`) 요약

```yaml
server:
  port: 8081

spring:
  application:
    name: interworking

  datasource:
    driverClassName: org.postgresql.Driver
    url: jdbc:postgresql://<DB_HOST>:5432/interworking
    username: <USERNAME>
    password: <PASSWORD>

logging:
  ...  # logback.xml 과 연동되는 로깅 설정

datahub:
  ingestInterfaceUri: http://<DATAHUB_HOST>:<PORT>/ingest  # 실제 파일에서는 ... 로 가려져 있음

integrationPlatform:
  authenticationUri: http://<IP_HOST>:<PORT>/auth.json
  eventUri:          http://<IP_HOST>:<PORT>/linkHttp/recvEvent.json

datahub-to-integrationPlatform:
  encryptionYn: N
  encryptionKey: <BASE64_ARIA_KEY>
  apiKey: <AUTH_API_KEY>
  sendingCityCode: B4113000000
  receivingCityCode: B4113000000
  SystemCode: DHB

integrationPlatform-to-datahub:
  encryptionKey: <BASE64_ARIA_KEY>
  apiKey: <API_KEY_2>
  scheduleMillisecond: 5000   # 스케줄러로 이벤트 풀링 시 사용 예정
```

> 운영 설정에서는 실제 IP, 계정, 키를 반드시 별도 보안 저장소(ex. Vault, Config Server 등)에 관리하고,  
> `application.yml`에는 placeholder 또는 profile 별 분리 사용을 권장합니다.

---

## 6. DB 스키마 및 매핑 룰 설정

DB 초기 스크립트: `main/resources/init.sql`  

### 6.1 매핑 룰 테이블

#### 6.1.1 `integration_platform.event_mapping_rule`

| 컬럼명                               | 설명                                   |
|--------------------------------------|----------------------------------------|
| `integration_platform_event_id`      | 통합플랫폼 이벤트 ID                   |
| `datahub_entity_type`               | 대응되는 DataHub 엔티티 타입          |
| `integration_platform_event_id_detail` | 상세 코드(송신 시스템 등, `*` 가능) |
| `datahub_dataset_id`                | DataHub Dataset ID                     |
| (+) `integration_platform_event_name`                  |
| (+) `integration_platform_event_grade`                 |
| (+) `integration_platform_occurrence_event_content`    |
| (+) `integration_platform_occurrence_event_detail_code`|
| (+) `integration_platform_occurrence_event_detail_code_detail` |

(마지막 5개 컬럼은 MyBatis 매핑에서 사용되며, `init.sql`에서는 이후 ALTER TABLE로 추가된 것으로 가정)

#### 6.1.2 `integration_platform.attribute_mapping_rule`

| 컬럼명                               | 설명                                                  |
|--------------------------------------|-------------------------------------------------------|
| `integration_platform_event_id`      | 이벤트 ID (FK)                                       |
| `integration_platform_attribute_name`| 통합플랫폼 이벤트 Body 필드명 (`ocrEvtTime` 등)      |
| `datahub_attribute_name`            | DataHub 속성명 (`waterLevel`, `ocrEvtTime` 등)       |
| `datahub_attribute_type`            | `PROPERTY`, `GEOPROPERTY`, ... (`AttributeType`)     |
| `datahub_attribute_datatype`        | `STRING`, `DATE`, `GEOJSON`, ... (`AttributeDataType`) |
| `datahub_attribute_has_observed_at` | `observedAt` 사용 여부                               |
| `integration_platform_attribute_datatype` | 통합플랫폼 측 데이터 타입                            |
| `integration_platform_event_id_detail` | 상세 코드                                           |

#### 6.1.3 수신 이벤트 테이블

- `integration_platform.received_event_base`
  - 최초 수신 이벤트의 기본 정보 저장
- `integration_platform.received_event_history`
  - 발생 순번(`sequence`) 단위로 상세 처리 상태 저장
  - 주요 컬럼:
    - `sequence`, `event_id`, `occurrence_number`, `process_status`, `serialized_event_data`, `received_body_data`, `creation_time`, `modification_time`

`ProcessStatus` Enum:

- `NOT_PROCESSED`, `PROCESSED`, `PARSING_ERROR`, `DECRYPTION_ERROR`,
  `DATAHUB_INTERWORKING_ERROR`, `DATAHUB_NOT_REACHABLE`, `MAPPING_RULE_NOT_FOUND`

---

## 7. 확장/커스터마이징 포인트

1. **새로운 이벤트 타입 추가**
   - `event_mapping_rule` 및 `attribute_mapping_rule` 테이블에 레코드 추가
   - 필요 시 DataHub 엔티티 타입/데이터셋 ID 추가
2. **데이터 변환 로직 확장**
   - `IntegrationPlatformSendingEventHandler.obtainMappingValue(...)`
     - DataHub 속성 → 스마트도시 안전망서비스 필드 변환 규칙 확장
   - `DatahubIngestRequestEventHandler.obtainIngestInterfacePayload(...)`
     - 스마트도시 안전망서비스 이벤트 → DataHub 엔티티 변환 규칙 확장
3. **Notification History 저장**
   - `DatahubService.createNotificationHistory(...)` 내부 주석 처리된 부분 활성화 후
   - `NotificationHistory` 테이블 설계 및 Mapper 추가
4. **스케줄러 활성화**
   - `IntegrationPlatformEventService.processReceivedIntegrationPlatformEvent()` 에 달린 `@Scheduled` 주석 해제
   - `integrationPlatform-to-datahub.scheduleMillisecond` 값 조정

---

## 8. 빌드 & 실행 (예시)

소스 구조 상 `src/main/java`, `src/main/resources` 환경에 맞게 배치되었다고 가정합니다.

### 8.1 실행

```bash
# Maven 기준
mvn spring-boot:run

# 또는 jar 빌드 후
mvn clean package
java -jar target/interworking-*.jar
```

