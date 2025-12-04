CREATE DATABASE interworking WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'C';

\connect interworking

CREATE SCHEMA integration_platform;

CREATE TABLE integration_platform.attribute_mapping_rule (
    integration_platform_event_id character varying(64) NOT NULL,
    integration_platform_attribute_name character varying(128) NOT NULL,
    datahub_attribute_name character varying(128),
    datahub_attribute_type character varying(32),
    datahub_attribute_datatype character varying(32),
    datahub_attribute_has_observed_at boolean,
    integration_platform_attribute_datatype character varying(32),
    integration_platform_event_id_detail character varying(32) NOT NULL
);

CREATE TABLE integration_platform.event_mapping_rule (
    integration_platform_event_id character varying(64) NOT NULL,
    datahub_entity_type character varying(128) NOT NULL,
    integration_platform_event_id_detail character varying(32) NOT NULL,
    datahub_dataset_id character varying(128)
);

CREATE TABLE integration_platform.received_event_base (
    event_id character varying(64) NOT NULL,
    occurrence_number character varying(128) NOT NULL,
    occurrence_status character varying(16),
    occurrence_event_status_detail character varying(256),
    occurrence_event_time timestamp without time zone,
    occurrence_event_end_time timestamp without time zone,
    creation_time timestamp without time zone,
    modification_time timestamp without time zone,
    send_code character varying(16) NOT NULL,
    receive_code character varying(16),
    serialized_event_data character varying(8192)
);

CREATE TABLE integration_platform.received_event_history (
    sequence integer NOT NULL,
    event_id character varying(64),
    occurrence_number character varying(128),
    occurrence_status character varying(16),
    occurrence_event_status_detail character varying(256),
    occurrence_event_time timestamp without time zone,
    occurrence_event_end_time timestamp without time zone,
    event_original_data character varying,
    creation_time timestamp without time zone,
    process_status character varying(32),
    send_code character varying(16),
    receive_code character varying(16),
    modification_time timestamp without time zone,
    serialized_event_data character varying(8192)
);
CREATE SEQUENCE integration_platform.received_event_history_sequence_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER SEQUENCE integration_platform.received_event_history_sequence_seq OWNED BY integration_platform.received_event_history.sequence;
ALTER TABLE ONLY integration_platform.received_event_history ALTER COLUMN sequence SET DEFAULT nextval('integration_platform.received_event_history_sequence_seq'::regclass);
INSERT INTO integration_platform.attribute_mapping_rule (integration_platform_event_id, integration_platform_attribute_name, datahub_attribute_name, datahub_attribute_type, datahub_attribute_datatype, datahub_attribute_has_observed_at, integration_platform_attribute_datatype, integration_platform_event_id_detail) VALUES ('MOJUC110', 'IMAGE_SEND_TY_CD', 'imageSendTypeCd', 'PROPERTY', 'STRING', false, 'STRING', '*');
INSERT INTO integration_platform.attribute_mapping_rule (integration_platform_event_id, integration_platform_attribute_name, datahub_attribute_name, datahub_attribute_type, datahub_attribute_datatype, datahub_attribute_has_observed_at, integration_platform_attribute_datatype, integration_platform_event_id_detail) VALUES ('MOJUC110', 'IMAGE_URL', 'imageUrl', 'PROPERTY', 'STRING', false, 'STRING', '*');
INSERT INTO integration_platform.event_mapping_rule (integration_platform_event_id, datahub_entity_type, integration_platform_event_id_detail, datahub_dataset_id) VALUES ('MOJUC110', 'kr.citydatahub.moj:1.0', '*', 'datasetId');

INSERT INTO integration_platform.received_event_base (event_id, occurrence_number, occurrence_status, occurrence_event_status_detail, occurrence_event_time, occurrence_event_end_time, creation_time, modification_time, send_code, receive_code, serialized_event_data) VALUES ('MOJUC110', '20210701111112', '10', '발생', '2021-07-01 11:11:11', '2021-07-01 11:11:11', '2021-07-08 18:00:42.813965', NULL, 'Siheung', 'Siheung', '{"svcTy":"EVENT","hdCnt":"14","dtCnt":"16","apSeq":{"apCurrent":"1","apTotal":"1"},"cmnMtd":"1","sendCd":"Siheung","rcvCd":"Siheung","encAt":"N","tranTy":"HTTP","rrKey":"rrKey","reqDate":"20210701120044","bodyLen":"123","sysCd":"IPC","tranSysHis":{"tranSysHisN":"9101112","tranSysHis2":"5678","tranSysHis1":"1234"},"Body":{"startTag":"$DA","endTag":"$DB","finishTag":"$DF","evtId":"MOJUC110","evtNm":"전자발찌 위치추적","evtGrad":"10","ocrNbr":"20210701111112","ocrSts":"10","ocrLc":{"crdntX":"126.16687","crdntY":"35.9563","crdntZ":"0"},"ocrPlc":"공공 삼거리 인근","ocrEvtCn":"접근금지구역 침입","ocrEvtTime":"20210701111111","ocrEvtSts":{"ocrEvtStsCd":"10","ocrEvtStsDtl":"발생"},"ocrEvtUsr":{"ocrEvtUsrId":"user1","ocrEvtUsrNm":"사용자1"},"ocrEvtEndTime":"20210701111111","ocrEvtItemCnt":"2","ocrEvtItem":[{"key":"IMAGE_SEND_TY_CD","val":"FTP"},{"key":"IMAGE_URL","val":"asdasd"}],"ocrEvtDtl":{"ocrEvtDtlCd":"접근금지침입","ocrEvtDtlCdDtl":"세부분류"}}}');

INSERT INTO integration_platform.received_event_history (sequence, event_id, occurrence_number, occurrence_status, occurrence_event_status_detail, occurrence_event_time, occurrence_event_end_time, event_original_data, creation_time, process_status, send_code, receive_code, modification_time, serialized_event_data) VALUES (4, 'MOJUC110', '20210701111112', '10', '발생', '2021-07-01 11:11:11', '2021-07-01 11:11:11', '{ "startTag": "$DA",
		"evtId": "MOJUC110",
		"evtNm": "전자발찌 위치추적",
		"evtGrad": "10",
		"ocrNbr": "20210701111112",
		"ocrSts": "10",
		"ocrLc": {
			"crdntX": "126.16687",
			"crdntY": "35.9563",
			"crdntZ": "0"
		},
		"ocrPlc": "공공 삼거리 인근",
		"ocrEvtCn": "접근금지구역 침입",
        "ocrEvtSts": {
            "ocrEvtStsCd" : "10", "ocrEvtStsDtl" : "발생"
        },
		"ocrEvtTime": "20210701111111",
		"ocrEvtUsr": {
			"ocrEvtUsrId": "user1",
			"ocrEvtUsrNm": "사용자1"
		},
		"ocrEvtEndTime": "20210701111111",
		"ocrEvtItemCnt": "2",
		"ocrEvtItem": [{
			"key": "IMAGE_SEND_TY_CD",
			"val": "FTP"
		}, {
			"key": "IMAGE_URL",
			"val": "asdasd"
		}],
		"ocrEvtDtl": {
			"ocrEvtDtlCd": "접근금지침입",
			"ocrEvtDtlCdDtl": "세부분류"
		},
		"endTag": "$DB",
		"finishTag": "$DF"
	}', '2021-07-08 18:00:37.676386', 'PROCESSED', 'Siheung', 'Siheung', '2021-07-08 18:00:42.8227', '{"svcTy":"EVENT","hdCnt":"14","dtCnt":"16","apSeq":{"apCurrent":"1","apTotal":"1"},"cmnMtd":"1","sendCd":"Siheung","rcvCd":"Siheung","encAt":"N","tranTy":"HTTP","rrKey":"rrKey","reqDate":"20210701120044","bodyLen":"123","sysCd":"IPC","tranSysHis":{"tranSysHisN":"9101112","tranSysHis2":"5678","tranSysHis1":"1234"}}');

SELECT pg_catalog.setval('integration_platform.received_event_history_sequence_seq', 4, true);