package com.domain;

import static org.neo4j.driver.v1.Values.parameters;

import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
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
    public void getConnect(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }
    public void printPeople(String initial)
    {
            Session session = driver.session();

            // Auto-commit transactions are a quick and easy way to wrap a read.
            StatementResult result = session.run(
                    "MATCH (a:People) WHERE a.name STARTS WITH {x} RETURN a.name as PeopleName  ",
                    parameters("x", initial));
            // Each Cypher execution returns a stream of records.
            while (result.hasNext())
            {
                //Record 是一行记录，内容是什么取决于你return的东西
                Record record = result.next();
                System.out.println(record);
                // Values can be extracted from a record by index or name.
                System.out.println(record.get("PeopleName").asString());
            }

    }
    public void getAllNodes()
    {
        Session session = driver.session();

        // Auto-commit transactions are a quick and easy way to wrap a read.
        StatementResult result = session.run(
                "MATCH (b:People) where n.name = '蔡成功' RETURN b");
        // Each Cypher execution returns a stream of records.
        while (result.hasNext())
        {
            //Record 是一行记录，内容是什么取决于你return的东西
            Record record = result.next();
            System.out.println(record);

            List<Value> list = record.values();
            for(Value v : list)
            {
                Node n = v.asNode();
                System.out.println(n.labels().iterator().next()+"--"+n.id());

                for(String k:n.keys())
                {
                    System.out.println(k+"---"+n.get(k) );
                }
                System.out.println("==========================");

            }

            // Values can be extracted from a record by index or name.
//            System.out.println(record.get("b").asString());
        }

    }
    public void toJosn() {
    	Session session = driver.session();
        StatementResult result = session.run(
                "MATCH p=(n:People)--() RETURN p");
        StringBuffer nodes = new StringBuffer();
        StringBuffer links = new StringBuffer();
        nodes.append("\"nodes\":[");
        links.append("\"links\":[");
        Set<Long> s = new HashSet<Long>();
        while(result.hasNext()) {
        	Record record = result.next();

            
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

}
