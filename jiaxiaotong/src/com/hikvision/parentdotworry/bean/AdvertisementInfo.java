package com.hikvision.parentdotworry.bean;

public class AdvertisementInfo {
	private String picUrl;
	private String connectintWeb;
	
	
	public AdvertisementInfo( String picUrl,
			String connectintWeb) {
		super();
		this.picUrl = picUrl;
		this.connectintWeb = connectintWeb;
	}

	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getConnectintWeb() {
		return connectintWeb;
	}
	public void setConnectintWeb(String connectintWeb) {
		this.connectintWeb = connectintWeb;
	}
	
	
}
