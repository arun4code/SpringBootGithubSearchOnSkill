package com.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.model.GithubData;
import com.model.GithubLinks;


@Service
public class ScrapperServiceImpl implements ScrapperService {
	public static final String BASE_URL = "https://api.github.com/";

	public static final String _TOKEN = "ADD YOUR AUTH CODE";
	
	@Override
	public List<GithubData> fetchDataFromGithub(String skill) throws Exception {
		List<GithubData> gitList = null;
		try {
			
			long t1 = System.currentTimeMillis();
			gitList = connectGithub(skill);
			long t2 = System.currentTimeMillis();			
			
			System.out.println("Time: " + (t2 - t1));
		  
		} catch(Exception e){
		  e.printStackTrace();
		}	
	
		return gitList;
	}


	private List<GithubData> connectGithub(String skill) throws Exception {
		
		String url = "https://api.github.com/search/users?q=" + 
		URLEncoder.encode(skill, "UTF-8") + "&access_token=" + _TOKEN;
		
		HttpURLConnection conn = 
				(HttpURLConnection) new URL(url) //"https://api.github.com/search/users?q=" + "python")
				.openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		//Read line by line
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine() ) != null) {
			sb.append("\n" + line);
		}
		br.close();
		
		conn.disconnect();
		GithubLinks git = parseGit(sb.toString());
		
		
		List<Integer> followersCount = fetchFollowerCounts(git.getFollowerList());
		
		
		/**name*/
		List<String> nameList = fetchName(git.getUserList());
		
		
		/**avatar_url*/
		List<String> avtList = Arrays.stream(sb.toString()
				.split("\"avatar_url\":"))
		.skip(1).map(l -> l.split(",")[0]).collect(Collectors.toList());
		
		
		
		List<GithubData> ghDataList = new ArrayList<GithubData>();
		
		int size = git.getLoginList().size();
		GithubData ghData = null;
		for(int i = 0; i < size; i++) {
			ghData = new GithubData();
			ghData.setLogin(git.getLoginList().get(i));
			ghData.setName(nameList.get(i));
			ghData.setAvatar_url(avtList.get(i));
			ghData.setFollowers(followersCount.get(i).toString());
			ghDataList.add(ghData);
		}
		
		return ghDataList;
				
	}
	
	public GithubLinks parseGit(String jsonStr) throws ParseException {
		GithubLinks links = new GithubLinks();
		
		List<String> loginList = new ArrayList<>();
		List<String> userList = new ArrayList<>();
		List<String> avatarList = new ArrayList<>();
		List<String> followerList = new ArrayList<>();
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonStr);
		JSONObject obj2 = (JSONObject)obj;
		JSONArray arr = (JSONArray)obj2.get("items");
		
		@SuppressWarnings("unchecked")
		Iterator<Object> iterator = arr.iterator();
		
		int limit = 0;
		
		while (iterator.hasNext()) {
			if (limit > 5) {
				break;
			}
			
            JSONObject objt = (JSONObject) iterator.next();
            
            loginList.add((String)objt.get("login"));
            userList.add((String)objt.get("url"));
            
            avatarList.add((String)objt.get("avatar_url"));
            followerList.add((String)objt.get("followers_url"));
            
            limit++;
        }
		
		links.setLoginList(loginList);
		links.setUserList(userList);
		links.setAvatarList(avatarList);
		links.setFollowerList(followerList);
		
		return links;
		
	}
	
    private List<Integer> fetchFollowerCounts(List<String> followerList) throws MalformedURLException, IOException {
    	List<Integer> countList = new ArrayList<>();
    	
    	for(String url : followerList) {
	    	HttpURLConnection conn = 
					(HttpURLConnection) new URL(url 
							+ "?access_token=" + _TOKEN)
					.openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			Map<String, List<String>> headerMap = conn.getHeaderFields();
			
			int totalPage = 1;
			String link = null;
			if(headerMap != null && headerMap.containsKey("Link")) {
				List<String> linkList = headerMap.get("Link");
				
				String[] arr = linkList.toString().split(",");
				
				if(arr != null && arr.length > 1) {
					String str = arr[1];
					link = str.substring(str.indexOf("<") + 1, str.indexOf(">"));
					String pageCount = link.substring(link.indexOf("page=") + 5);
					totalPage = Integer.parseInt(pageCount);
				}				
			}
			
			if(totalPage > 1 && link != null) {
				HttpURLConnection conn2 = 
						(HttpURLConnection) new URL(link)
						.openConnection();
				conn2.addRequestProperty("User-Agent", "Mozilla/5.0");
				br = new BufferedReader(new InputStreamReader(conn2.getInputStream()));				
			}
			
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine() ) != null) {
				sb.append("\n" + line);
			}
			Integer count = -1;
			try {
				count = parseFollwersURL(sb.toString());
			} catch (ParseException e) {
				//if error, ignore it.
				System.out.println(e.getMessage());
			}
			int totalFollowers = count + (totalPage - 1) * 30;
			countList.add(totalFollowers);
			br.close();
    	}
		return countList;
	}


	private Integer parseFollwersURL(String jsonStr) throws ParseException {
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonStr);
		JSONArray array = (JSONArray)obj;
		
		return array.size();
		
	}


	private List<String> fetchName(List<String> urlList) throws MalformedURLException, IOException {
    	List<String> userList = new ArrayList<String>();
    	
    	for(String url : urlList) {
	    	HttpURLConnection conn = 
					(HttpURLConnection) new URL(url + "?access_token=" + _TOKEN)
					.openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/5.0");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine() ) != null) {
				sb.append("\n" + line);
			}
			conn.disconnect();
			
			String user = "";
			try {
				user = parseUserName(sb.toString());
			} catch (ParseException e) {
				//if error, ignore it.
				System.out.println(e.getMessage());
			}			
			userList.add(user);
    	}
		return userList;
	}
    
    
	private String parseUserName(String jsonStr) throws ParseException {
		
		String user = "";
		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonStr);
		JSONObject jObj = (JSONObject)obj;
		user = (String)jObj.get("name");
		
		return user;
	}

}
