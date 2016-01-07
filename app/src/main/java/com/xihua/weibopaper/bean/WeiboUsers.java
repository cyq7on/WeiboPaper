package com.xihua.weibopaper.bean;

import java.util.List;

public class WeiboUsers extends BaseBean {

	private static final long serialVersionUID = 6598510583769514324L;
	private List<WeiBoUser> users;

	public List<WeiBoUser> getUsers() {
		return users;
	}

	public void setUsers(List<WeiBoUser> users) {
		this.users = users;
	}

}
