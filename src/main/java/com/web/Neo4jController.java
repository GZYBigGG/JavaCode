package com.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.domain.JavaToNeo4j;

@Controller
public class Neo4jController {
	@Autowired
	private JavaToNeo4j jt;
	
	@RequestMapping("/index")
	public String getcon() {
		jt.getConnect("bolt://localhost:7687", "neo4j", "123456");
		jt.toJosn();
		return "index";
	}
	@RequestMapping("/demo")
	public String show() {

		return "demo";
	}

}
