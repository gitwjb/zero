package com.wjb.zero.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@RequestMapping("/getUser")
	public String getUser(){		
		System.out.println("getUser.........");
		return "login";
	}
}
