package cn.edu.pku.sei.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RWFile {

	private String path;
	private String ownername;
	private String apptype;
	private String username;
	
	public RWFile(HashMap<String, Object> map){
		this((String)map.get("path"),(String)map.get("ownername"),
				(String)map.get("apptype"), (String)map.get("username"));
	}
	
	public RWFile(String path, String ownername, String apptype, String username) {
		super();
		this.path = path;
		this.ownername = ownername;
		this.apptype = apptype;
		this.username = username;
	}
	
	public static List<RWFile> getRWFileAuths(ArrayList<HashMap<String, Object>>ul){
		List<RWFile> auth = new ArrayList<RWFile>();
		for(HashMap<String, Object> uu : ul){
			auth.add(new RWFile(uu));
		}
		return auth;
	}

	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getOwnername() {
		return ownername;
	}

	public void setOwnername(String ownername) {
		this.ownername = ownername;
	}

	public String getApptype() {
		return apptype;
	}

	public void setApptype(String apptype) {
		this.apptype = apptype;
	}
	
	
}
