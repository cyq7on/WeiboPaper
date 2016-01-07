package com.xihua.weibopaper.bean;

public class PhotoBean extends BaseBean{

	private static final long serialVersionUID = -4887284841243544423L;

	private PicUrls photo;
	
	private StatusContent status;

	public PicUrls getPhoto() {
		return photo;
	}

	public void setPhoto(PicUrls photo) {
		this.photo = photo;
	}

	public StatusContent getStatus() {
		return status;
	}

	public void setStatus(StatusContent status) {
		this.status = status;
	}
	
}
