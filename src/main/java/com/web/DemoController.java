package com.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.domain.JavaToNeo4j;

@Controller
@RequestMapping("/node")
public class DemoController {
	@Autowired
	private JavaToNeo4j jt;
	
	@SuppressWarnings("rawtypes")
	@GetMapping("/reload")
	@ResponseBody
	public Map reload(String name1,String name2) {
		//jt.getConnect("bolt://localhost:7687", "neo4j", "123456");
		return jt.toMap(name1,name2);
	}
}
