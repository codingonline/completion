package cn.edu.pku.sei.model;
// default package

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * User entity. @author MyEclipse Persistence Tools
 */
public class Completion implements java.io.Serializable {

	// Fields

	private Integer id;
	private String classname;
	private String method;
	private Long count;

	// Constructors
	
	/** default constructor */
	public Completion(String classname, String method) {
		this.classname = classname;
		this.method = method;
		this.count = (long) 1;
	}

	public Completion(Integer id, String classname, String method, Long count) {
		this.id = id;
		this.classname = classname;
		this.method = method;
		this.count = count;
	}
	
	public Completion(HashMap<String, Object> map) {
		this((Integer)map.get("id"), (String)map.get("class"), (String)map.get("method"), 
				(Long)map.get("count"));
	}
	
	public static List<Completion> getCompletions(ArrayList<HashMap<String, Object>> cl){
		List<Completion> completions = new ArrayList<Completion>();
		for(HashMap<String, Object> cu : cl){
			completions.add(new Completion(cu));
		}
		return completions;
	}
	
	public static HashMap<String, Long> getMethodsCount(ArrayList<HashMap<String, Object>> cl){
		HashMap<String, Long> result = new HashMap<String, Long>();
		for(HashMap<String, Object> cu : cl){
			Completion c = new Completion(cu);
			result.put(c.getMethod(), c.getCount());
		}
		return result;
	}

	// Property accessors
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getClassname () {
		return this.classname;
	}
	
	public String getMethod () {
		return this.method;
	}

	public Long getCount() {
		return this.count;
	}

	public void addOneCount() {
		this.count ++;
	}

}