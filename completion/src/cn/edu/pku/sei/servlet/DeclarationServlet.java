package cn.edu.pku.sei.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.sei.services.javaparser.JavaParser;
import cn.edu.pku.sei.services.javaparser.MethodParser;
import cn.edu.pku.sei.utils.EnvironmentProperty;
import cn.edu.pku.sei.utils.javaJson.JSONArray;
import cn.edu.pku.sei.utils.javaJson.JSONException;
import cn.edu.pku.sei.utils.javaJson.JSONObject;

public class DeclarationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String individualFolder = EnvironmentProperty
			.readConf("individualFolder");
	private String repository = EnvironmentProperty.readConf("repository");
	private String commonFolder = EnvironmentProperty.readConf("commonFolder");
	private String jarLibPath = EnvironmentProperty.readConf("jarLibPath");
	private String srcPath = EnvironmentProperty.readConf("srcPath");
	private String declarationFile = null;
	private int declarationStartPosition = -1;
	private int declarationEndPosition = -1;
	private boolean local;
	private String type;//1.method 2.variable
	private String name;
	private static String METHOD = "METHOD";
	private static String VARIABLE = "VARIABLE";
	private static String CLASS = "CLASS";
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		
		response.setHeader("Content-Type",
				"application/x-javascript;charset=UTF-8");
		response.setHeader("Access-Control-Allow-origin","*");
		response.setDateHeader("Expires", 0);
		String userName = request.getParameter("username");
		String appName = request.getParameter("appname");
		String code = request.getParameter("code");
		String path = request.getParameter("path");
		String relativePath = path.substring(srcPath.length());
		//String jsonpCallback = request.getParameter("jsonpCallback");
		System.out.println(relativePath);
		//System.out.println(jsonpCallback);
		int position = Integer.valueOf(request.getParameter("position"));
		System.out.println("pos: "+position);

	    System.out.println("find declaration start");
	    declarationFile = null;
		declarationStartPosition = -1;
		declarationEndPosition = -1;

		String projectPath = userName + "/" + appName + "/";
		if(code == null || code.equals(""))
			code = getFileContent(repository + projectPath + path);
		String positionInfo= getdeclarationStartPosition(projectPath, code, position,
				relativePath);
		PrintWriter out;
		try {
			out = response.getWriter();
			//out.print(jsonpCallback + "(" + positionInfo + ")");
			out.print(positionInfo);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(positionInfo);

		
	}

	public void analysisDeclarationType(String currentCode, int position){
		int length = currentCode.length();
		if(position >= 0 && position < length){
			int p = position;
			while(Character.isLetterOrDigit(currentCode.charAt(p))){
				p++;
			}
			int right_position = p;
			while(currentCode.charAt(p) == ' '|| currentCode.charAt(p)=='\n'){
				p++;
			}
			if(p < length && currentCode.charAt(p) == '('){
				type = METHOD;
			}
			else
				type = VARIABLE;
			
			p = position-1;
			while(Character.isLetterOrDigit(currentCode.charAt(p))){
				p--;
			}
			if(p >= 0 && currentCode.charAt(p) == '.'){
				local = false;
			}
			else
				local = true;
			int left_position = p+1;
			if(currentCode.substring(p-4, p).equals("this"))
				local = true;
			//System.out.println(left_position+" "+right_position);
			//System.out.println(currentCode.charAt(left_position));
			//System.out.println(currentCode.charAt(right_position));
			System.out.println(currentCode.charAt(position));
			name = currentCode.substring(left_position, right_position);
			
			
		}
	}

	public String getdeclarationStartPosition(String projectPath, String currentCode,
			int position, String fileRelativePath){
		analysisDeclarationType(currentCode, position);
		MethodParser mp = new MethodParser();
		if(!(name.equals("") || mp.tokeniskeyword(name) || mp.tokenistype(name))){
			
			JavaParser javaparser = new JavaParser();
			javaparser.buildCode(currentCode.toCharArray(), position,
					fileRelativePath, repository + projectPath + srcPath,
					commonFolder, individualFolder + projectPath + jarLibPath,"");
			String classType = javaparser.findImportClass(name);
			if(classType != null){
				type = CLASS;
				local = false;
			}
			System.out.println(name);
			System.out.println(local);
			System.out.println(type);
			if(!local ){
				if(type.equals(CLASS)){
					String classPath = classType.replaceAll("\\.", "/");
					declarationFile = srcPath + classPath + ".java";
					declarationStartPosition = 0;
				}
				else{
					JavaParser jp = new JavaParser();
					classType = jp.getType(currentCode.toCharArray(), position,
							fileRelativePath, repository + projectPath + srcPath,
							commonFolder, individualFolder + projectPath + jarLibPath,"");
					//String classPath = classType.replaceAll("\\.", "/");
					//declarationFile = srcPath + classPath + ".java";
					System.out.println(classType);
					
					
					if(type.equals(METHOD)){
						int right_position = position;
						while(currentCode.charAt(right_position) != ')'){
							right_position++;
						}
						right_position++;
						List<String> parameterTypes = jp.getParameterTypes(right_position);
						for(int i=0;i<parameterTypes.size();i++){
							System.out.println(parameterTypes.get(i));
						}
						findMethod(projectPath, classType, parameterTypes);
						System.out.println(declarationFile);
						System.out.println(declarationStartPosition);	
						
					}
					else if(type.equals(VARIABLE)){
						findVariable(projectPath, classType);
							
					}
				}
					
			}
			else{

				JavaParser jp = new JavaParser();
				jp.buildCode(currentCode.toCharArray(), position,
						fileRelativePath, repository + projectPath + srcPath,
						commonFolder, individualFolder + projectPath + jarLibPath,"");
				
				if(type.equals(METHOD)){
					int p = jp.checkMethodDeclaration(position);
					if(p!=-1){
						declarationFile = "LOCAL";
						local = true;
						declarationStartPosition = p;
						
					}
					else{
						int right_position = position;
						while(currentCode.charAt(right_position) != ')'){
							right_position++;
						}
						right_position++;
						System.out.println(right_position);
						List<String> parameterTypes = jp.getParameterTypes(right_position);
						if(parameterTypes!=null){
							int methodPosition = jp.getLocalMethodDeclarePositon(name, parameterTypes);
							System.out.println("methodpos:"+methodPosition);
							if(methodPosition>=0){
								declarationFile ="LOCAL";
								local = true;
								declarationStartPosition = methodPosition;
								}
							else{
								local = false;
								findMethod(projectPath,jp.getSuperClassName(), parameterTypes);
								
							}
						}
					}
					
				
				}
				else if(type.equals(VARIABLE)){
					int varPosition = jp.getLocalVarDeclarePositon(position, name);
					if(varPosition>=0){
						declarationFile ="LOCAL";
						local = true;
						declarationStartPosition = varPosition;
					}
					else{
						local = false;
						findVariable(projectPath,jp.getSuperClassName());
					}
						
				}
			}		
		}
		String code = "";
		List<Integer> startRowAndColumn = null;
		List<Integer> endRowAndColumn = null;
		JSONObject jo = new JSONObject();
		
		try {
			if(declarationFile !=null && !declarationFile.equals("") && declarationStartPosition>=0){
				jo.put("success", true);
				if(local){
					code = currentCode;
				}
				else
					code = getFileContent(repository + projectPath + declarationFile);
				declarationEndPosition = findEndPosition(code, declarationStartPosition);
				startRowAndColumn = getRowAndColumn(code, declarationStartPosition);
				endRowAndColumn = getRowAndColumn(code, declarationEndPosition);
			}
			else{
				jo.put("success", false);
			}
			jo.put("declarationFile", declarationFile);
			jo.put("declarationStartPosition", declarationStartPosition);
			jo.put("row1", startRowAndColumn.get(0));
			jo.put("column1", startRowAndColumn.get(1));
			jo.put("row2", endRowAndColumn.get(0));
			jo.put("column2", endRowAndColumn.get(1));
			jo.put("token", name);
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = jo.toString();
		return json;
	}
	
	public int findEndPosition(String code, int startPosition){
		int endPosition = startPosition;
		if(type.equals(METHOD)){
			while(code.charAt(endPosition)!= '{')
				endPosition++;
		}
		else if(type.equals(VARIABLE)){
			while(code.charAt(endPosition)!= ';' && code.charAt(endPosition)!= '=' 
					&& code.charAt(endPosition)!= ')' && code.charAt(endPosition)!= ','
					&& code.charAt(endPosition)!= ':')
				endPosition++;
			endPosition--;
			while(code.charAt(endPosition)== ' ')
				endPosition--;
			endPosition++;
		}
		return endPosition;
	}
	
	public List<Integer> getRowAndColumn(String code, int position){
		List<Integer> _ret = new ArrayList<Integer>();
		
		String[] lines = code.split("\n");
		
		int row = 0;
		int column = 0;
		while(position > lines[row].length()+1){
			position -= lines[row].length()+1;
			row++;
			
		}
		column = position;
		_ret.add(row);
		_ret.add(column);
		return _ret;
	}
	
	public void findMethod(String projectPath, String classFullName , List<String> parameterTypes){
		String astJson = getClassJson(projectPath, classFullName);
		//System.out.println(astJson);
		if(astJson==null)
			return ;
		JSONObject jsonObject;
		
		int declarePosition = -1;
		JSONArray methods;
		String father = null;
		try {
			jsonObject = new JSONObject(astJson);
			JSONObject classObject;
			classObject = jsonObject.getJSONArray("classes").getJSONObject(0);
			if(!classFullName.equals("java.lang.Object"))
				father = classObject.getString("superclass");
			methods = classObject.getJSONArray("methods");
			int i,j;
			for(i = 0; i<methods.length();i++){
				JSONObject method = methods.getJSONObject(i);
				if(name.equals(method.getString("name"))){
					JSONArray params = method.getJSONArray("parameters");
					if(params.length() == parameterTypes.size()){
						for(j = 0; j < params.length(); j++){
							String callType = parameterTypes.get(j);
							String declareType = params.getJSONObject(j).getString("type");
							if(!callType.equals("*")&& !callType.equals(declareType))
								break;
						}
						if(j == params.length()){
							declarePosition = method.getInt("startPosition");	
							declarationStartPosition = declarePosition;
							String classPath = classFullName.replaceAll("\\.", "/");
							declarationFile = srcPath + classPath + ".java";
							return;
						}
					}
					
				}
			}
			if (father != null && !father.equals("")){
				findMethod(projectPath, father,parameterTypes);
			}
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}
	
	public void findVariable(String projectPath, String classFullName){
		String astJson = getClassJson(projectPath, classFullName);
		//System.out.println(astJson);
		if(astJson==null)
			return ;
		JSONObject jsonObject;
		
		int declarePosition = -1;
		JSONArray variables;
		String father = null;
		try {
			jsonObject = new JSONObject(astJson);
			JSONObject classObject;
			classObject = jsonObject.getJSONArray("classes").getJSONObject(0);
			if(!classFullName.equals("java.lang.Object"))
				father = classObject.getString("superclass");
			variables = classObject.getJSONArray("variables");
			int i,j;
			for(i = 0; i<variables.length();i++){
				JSONObject variable = variables.getJSONObject(i);
				if(name.equals(variable.getString("name"))){
					declarePosition = variable.getInt("startPosition");	
					declarationStartPosition = declarePosition;
					String classPath = classFullName.replaceAll("\\.", "/");
					declarationFile = srcPath + classPath + ".java";
					return ;
				}
			}
			if (father != null && !father.equals("")){
				findVariable(projectPath, father);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
	}
	
	public String getClassJson(String projectPath, String classFullName){
		String classPath = classFullName.replaceAll("\\.", "/");
		File file = new File(repository + projectPath + srcPath + classPath
				+ ".java");
		System.out.println(repository + projectPath + srcPath + classPath
				+ ".java");
		File jarJson = new File(individualFolder + projectPath + jarLibPath
				+ classPath + ".json");
		String indexFileName, astJson=null;
		File indexFile = null;
		if (file.exists()) {
			indexFileName = (individualFolder + projectPath + srcPath
					+ classPath + ".json");
			indexFile = new File(indexFileName);
		} else if (jarJson.exists()) {
			indexFile = jarJson;
		} else {
			String[] subdirs = new File(commonFolder).list();
			for (String subdir : subdirs) {
				File commonJson = new File(commonFolder + subdir + "/"
						+ classPath + ".json");
				if (commonJson.exists())
					indexFile = commonJson;
			}
			if (indexFile == null)
				return null;
		}
		if (file.exists()
				&& (!indexFile.exists() || file.lastModified() > indexFile
						.lastModified())) {
			// generate index
			JavaParser jp = new JavaParser();
			astJson = jp.toJson(classPath + ".java", repository + projectPath
					+ srcPath, commonFolder, individualFolder + projectPath
					+ jarLibPath,"");
			try {
				File indexFolder = indexFile.getParentFile();
				if (!indexFolder.exists()) {
					indexFolder.mkdirs();
				}
				FileOutputStream output = new FileOutputStream(indexFile);
				output.write(astJson.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else {
			FileInputStream input = null;
			try {
				input = new FileInputStream(indexFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Scanner sc = new Scanner(input);
			StringBuffer sb = new StringBuffer("");
			while (sc.hasNext()) {
				sb.append(sc.nextLine() + "\n");
			}
			astJson = sb.toString();
		}
		//System.out.println("json:"+astJson);
		return astJson;
	}
	
	public String getFileContent(String path){
		File file = new File(path);
		StringBuffer java_code = null;
		try {
			Scanner input = new Scanner(file);
			java_code = new StringBuffer();
			while (input.hasNextLine()) {
				String line = input.nextLine();
				
				java_code.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return java_code.toString();
	}
	
	public static void main(String[] args) {
		String projectPath = "yxb/java/";
		File file = new File("/mnt/nfs/repo/javaweb/yxb/java/src/main/java/Test.java");
		StringBuffer java_code = null;
		int position = 0;
		try {
			Scanner input = new Scanner(file);
			java_code = new StringBuffer();
			while (input.hasNextLine()) {
				String line = input.nextLine();
				position += line.length()+1;
				System.out.println(position);
				java_code.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		position=350;
		DeclarationServlet dServlet = new DeclarationServlet();
		String info = dServlet.getdeclarationStartPosition(projectPath, java_code.toString(), position, "Test.java");
		System.out.println(dServlet.declarationStartPosition);
		System.out.println(info);
	}

}
