package com.pkj.skeleton.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SkeletonController {

	@RequestMapping(value = "ping", method = RequestMethod.GET)
	@ResponseBody
	public String ping(HttpServletRequest request) {
		return "pong";
	}

}
