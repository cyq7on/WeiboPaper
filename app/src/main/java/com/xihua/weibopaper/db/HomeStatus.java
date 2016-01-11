package com.xihua.weibopaper.db;

import java.util.List;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.db
 * @Description:主页面微博
 * @date 2016/1/1114:43
 */
public class HomeStatus extends BaseDbBean{
    private String accessToken;
    private String content;
    private List<Category> categoryList;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
