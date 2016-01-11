package com.xihua.weibopaper.db;

import com.xihua.weibopaper.bean.BaseBean;
import com.xihua.weibopaper.bean.StatusContent;

import java.util.List;

/**
 * @author cyq7on
 * @version V1.0
 * @Package com.xihua.weibopaper.bean
 * @Description:
 * @date 2016/1/815:55
 */
public class Category extends BaseDbBean {
    private String name;
    private List<HomeStatus> homeStatusList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HomeStatus> getHomeStatusList() {
        return homeStatusList;
    }

    public void setHomeStatusList(List<HomeStatus> homeStatusList) {
        this.homeStatusList = homeStatusList;
    }
}
