package com.model;

public class GithubData {
	private String login;
	private String name;
	private String avatar_url;
	private String followers;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar_url() {
		return avatar_url;
	}
	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}
	public String getFollowers() {
		return followers;
	}
	public void setFollowers(String followers) {
		this.followers = followers;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	@Override
	public String toString() {
		return "GithubData [login=" + login + ", name=" + name + ", avatar_url=" + avatar_url + ", followers="
				+ followers + "]";
	}
	
}
