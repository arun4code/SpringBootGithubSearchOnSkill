package com.service;

import java.util.List;

import com.model.GithubData;

public interface ScrapperService {

	public List<GithubData> fetchDataFromGithub(String skill)  throws Exception;

}
