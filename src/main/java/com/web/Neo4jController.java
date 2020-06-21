package com.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.domain.JavaToNeo4j;

@Controller
public class Neo4jController {
	@Autowired
	private JavaToNeo4j jt;
	
	@RequestMapping("/index")
	public String getcon() {
		//jt.getConnect("bolt://localhost:7687", "neo4j", "123456");
		//jt.toJosn(name1,name2);
		return "index";
	}
	@RequestMapping("/demo")
	public String show(Model model,HttpServletRequest request) {
		String name1 = request.getParameter("name1");
		String name2 = request.getParameter("name2");
		model.addAttribute("name1", name1);
		model.addAttribute("name2", name2);
		//System.out.println(name1+"  "+name2);
		//jt.getConnect("bolt://localhost:7687", "neo4j", "123456");
		jt.toJosn(name1,name2);
		return "demo";
	}
	@RequestMapping("/Alldemo")
	public String showAll() {
		String name1=null;
		String name2=null;
		//jt.getConnect("bolt://localhost:7687", "neo4j", "123456");
		jt.toJosn(name1,name2);
		return "demo";
	}
}
