package com.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.model.GithubData;
import com.service.ScrapperService;

@Component
@Controller
@RequestMapping("/searchskill")
public class GithubSearchController {
	
	@Autowired
    private ScrapperService service;
	
	@ModelAttribute("skills")
    public GithubSearchController getGithubData() {
        return new GithubSearchController();
    }
	
	
	@GetMapping
    public String showSkillForm(Model model) {    
    	model.addAttribute("skill", "");
        return "search";
    }

	@RequestMapping(value = "/searchskill", method=RequestMethod.POST)
    public String scrapGithub(@RequestParam("skill") String skill) {
		return "redirect:/searchskill/searchskill?skill=" + skill;    	 
    }
    

	@RequestMapping(value = "/searchskill", method = RequestMethod.GET)
	public String gitData(@RequestParam("skill") String skill, Model model) {
		List<GithubData> gitList = new ArrayList<>();
		try {
			gitList = service.fetchDataFromGithub(skill);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		model.addAttribute("resultList", gitList);
		return "searchskill";
	}

}
