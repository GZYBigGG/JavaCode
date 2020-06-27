package com.domain;

import static org.neo4j.driver.v1.Values.parameters;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.stereotype.Service;
@Service
public class JavaToNeo4j {
	Driver driver;
	public JavaToNeo4j() {
		driver = GraphDatabase.driver("bolt://123.56.170.16:7687", AuthTokens.basic("neo4j", "123456"));
	}
    public void getConnect(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
  
    public void toJosn(String name1,String name2) {
    	Session session = driver.session();
    	StatementResult result;
    	if(name1==null && name2==null) {
            result = session.run(
                    "MATCH p=(n:People)--() RETURN p");
    	}else {
          result = session.run(
                "MATCH p=(n:People)--(m:People) where n.name = {x} and m.name = {y} RETURN p",
                parameters("x", name1,"y",name2));
    	}
    	
        StringBuffer nodes = new StringBuffer();
        StringBuffer links = new StringBuffer();
        nodes.append("\"nodes\":[");
        links.append("\"links\":[");
        Set<Long> s = new HashSet<Long>();
		while(result.hasNext()) {
        	Record record = result.next();

            //System.out.println(record);
            List<Value> list = record.values();
            //System.out.println(list);
            for(Value v : list)
            {
                Path p = v.asPath();
                //System.out.println(p);
                for(Node n:p.nodes())
                {	
                	
                    //nodes.append("{");
                    
                if(!s.contains(n.id())) {
                	nodes.append("{");
                	for(String k:n.keys())
                    {	
                    	//System.out.println(k);
                        //nodes.append("\""+k+"\":"+n.get(k)+",");
                        
                        	//System.out.println(n.size());
        
                        	nodes.append("\""+k+"\":"+n.get(k)+",");
                        
                    }
                	nodes.append("\"id\":"+n.id());
                    s.add(n.id());

                    nodes.append("},");
                }
                }
                nodes=new StringBuffer(nodes.toString().substring(0,nodes.toString().length()-1));

                for(Relationship r:p.relationships())
                {
                    links.append("{");
                    int num = 0;
                    links.append("\"source\":"+r.startNodeId()+","+"\"target\":"+r.endNodeId());
                    links.append(",\"type\":\""+r.type()+"\"");
                    links.append("},");
                }
                links=new StringBuffer(links.toString().substring(0,links.toString().length()-1));

            }
            nodes.append(",");
            links.append(",");
        }
        nodes=new StringBuffer(nodes.toString().substring(0,nodes.toString().length()-1));
        links=new StringBuffer(links.toString().substring(0,links.toString().length()-1));

        nodes.append("]");
        links.append("]");
       String resultJson = "{" + nodes + ","+ links + "}";
       //String resultJson = "{"+links+"}";
//        System.out.println(nodes.toString());
//        System.out.println(links.toString());

        try {
        	FileOutputStream fos = new FileOutputStream("src/main/resources/static/Neo4jSon.json");     
            fos.write(resultJson.getBytes());
            fos.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    @SuppressWarnings("unchecked")   
	public Map toMap(String name1,String name2) {
    	Map<String, Object> resultMap = new HashMap<>();
    	Session session = driver.session();
    	StatementResult result;
    	if(Objects.equals(name1, "") && Objects.equals(name2, "")) {
    		result = session.run(
    				"MATCH p=(n:People)--() RETURN p");
    	}else {
    		result = session.run(
    				"MATCH p=shortestpath((n:People)-[*..6]-(m:People)) where n.name = {x} and m.name = {y} RETURN p",
    				parameters("x", name1,"y",name2));
    	}
    	Set<Long> s = new HashSet<Long>();
    	List<Object> nodeList = new ArrayList<>();
    	List<Object> linkList = new ArrayList<>();
    	while(result.hasNext()) {
    		Record record = result.next();
    		
    		//System.out.println(record);
    		List<Value> list = record.values();
    		//System.out.println(list);
    		for(Value v : list)
    		{
    			Path p = v.asPath();
    			//System.out.println(p);
    			for(Node n:p.nodes())
    			{	
    				
    				//nodes.append("{");
    				
    				if(!s.contains(n.id())) {
    					s.add(n.id());
    					Map subMap = new HashMap<>(n.asMap());
    					subMap.put("id", n.id());
    					nodeList.add(subMap);
    				}
    			}
    			
    			for(Relationship r:p.relationships())
    			{
    				Map<String,Object> subMap = new HashMap<>();
    				subMap.put("source", r.startNodeId());
    				subMap.put("target", r.endNodeId());
    				subMap.put("type", r.type());
    				linkList.add(subMap);
    			}
    			
    		}
    	}
    	resultMap.put("nodes", nodeList);
    	resultMap.put("links", linkList);
    	return resultMap;
    }

}
