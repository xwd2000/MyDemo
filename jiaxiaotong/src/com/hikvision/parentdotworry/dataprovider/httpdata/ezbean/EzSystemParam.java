package com.hikvision.parentdotworry.dataprovider.httpdata.ezbean;

public class EzSystemParam {
	/*
	 * system ver 协议版本号 sign 签名值 key 开放平台appkey值 time
	 * UTC时间戳，自1970年1月1日起计算的时间，单位为秒
	 */
	private String ver;
	private String sign;
	private String key;
	private Long time;

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

}
