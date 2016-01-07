package com.xihua.weibopaper.bean;

/**
 * Created by wangdan on 13-12-14.
 */
public class PicUrls extends BaseBean{

	private static final long serialVersionUID = 2354439978931122615L;
	private String thumbnail_pic;

    //用于litePal
    private StatusContent statusContent;

    public StatusContent getStatusContent() {
        return statusContent;
    }

    public void setStatusContent(StatusContent statusContent) {
        this.statusContent = statusContent;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }
}
