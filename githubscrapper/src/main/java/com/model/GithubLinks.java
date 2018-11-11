package com.model;

import java.util.List;

public class GithubLinks {

	private List<String> loginList;
	private List<String> userList;
	private List<String> avatarList;
	private List<String> followerList;
	
	public List<String> getLoginList() {
		return loginList;
	}
	public void setLoginList(List<String> loginList) {
		this.loginList = loginList;
	}
	public List<String> getUserList() {
		return userList;
	}
	public void setUserList(List<String> userList) {
		this.userList = userList;
	}
	public List<String> getAvatarList() {
		return avatarList;
	}
	public void setAvatarList(List<String> avatarList) {
		this.avatarList = avatarList;
	}
	public List<String> getFollowerList() {
		return followerList;
	}
	public void setFollowerList(List<String> followerList) {
		this.followerList = followerList;
	}
}
