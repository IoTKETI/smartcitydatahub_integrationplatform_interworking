package kr.re.keti.sc.interworking.integrationplatform.utils;

import java.security.InvalidKeyException;

public class AriaCipher {
	/** 마스터 키 */
	String masterKey = null;

	/**
	 * 암호 설정.
	 * 
	 * @param masterKey 암호문자열
	 */
	public void setPassword(String masterKey) {
		masterKey = (masterKey.length() > 32 ? masterKey.substring(0, 32) : masterKey);

		this.masterKey = masterKey;
	}

	
	public AriaCipher(String masterKey) {
		this.setPassword(masterKey);
	}
	
	/**
	 * 바이트 배열 리턴 암호화
	 * 
	 * @param data 암호화할 바이트배열
	 * @return 암호화된 바이트배열
	 */
	public byte[] encrypt(byte[] data) {
		try {
			CryptoPadding padding = new AnsiX923Padding();

			byte[] mk = padding.addPadding(masterKey.getBytes(), 32);

			AriaEngine instance = new AriaEngine(256);

			return instance.encrypt(data, mk);
		} catch (InvalidKeyException ike) {
			throw new RuntimeException(ike);
		}
	}

	/**
	 * 복호화 배열 리턴 암호화.
	 * 
	 * @param encryptedData 복호화할 데이타 바이트배열
	 * @return 복호화된 바이트배열
	 */
	public byte[] decrypt(byte[] encryptedData) {
		try {
			CryptoPadding padding = new AnsiX923Padding();

			byte[] mk = padding.addPadding(masterKey.getBytes(), 32);

			AriaEngine instance = new AriaEngine(256);

			return instance.decrypt(encryptedData, mk);
		} catch (InvalidKeyException ike) {
			throw new RuntimeException(ike);
		}
	}
}