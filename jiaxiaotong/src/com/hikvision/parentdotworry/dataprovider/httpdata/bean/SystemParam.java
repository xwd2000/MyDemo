package com.hikvision.parentdotworry.dataprovider.httpdata.bean;

public class SystemParam {
	private String sign;

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("{sign:")
			.append(sign)
			.append("}")
			.toString();
	}
}
