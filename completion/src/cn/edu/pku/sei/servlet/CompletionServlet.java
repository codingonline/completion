package cn.edu.pku.sei.servlet;

import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import java_cup.internal_error;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.sei.utils.javaJson.JSONArray;
import cn.edu.pku.sei.utils.javaJson.JSONException;
import cn.edu.pku.sei.utils.javaJson.JSONObject;

import cn.edu.pku.sei.jdbc.CompletionJDBC;
import cn.edu.pku.sei.model.Completion;
import cn.edu.pku.sei.services.javaparser.JavaParser;
import cn.edu.pku.sei.services.phpparser.PHPParser;
import cn.edu.pku.sei.utils.EnvironmentProperty;
import cn.edu.pku.sei.utils.FilePath;

public class CompletionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String srcPath = EnvironmentProperty.readConf("srcPath");
	private JSONArray methods;
	private JSONArray variables;
	private int parentCnt;
	private List<String> fileList;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Content-Type",
				"application/x-javascript;charset=UTF-8");
		response.setDateHeader("Expires", 0);
		String jsonpCallback = request.getParameter("jsonpCallback");
		if (jsonpCallback == null) {
			String classname = request.getParameter("classname");
			String method = request.getParameter("method");
			if (method == null)
				return;
			recordUsage(classname, method);
		} else {
			String userName = request.getParameter("username");
			String appName = request.getParameter("appname");
			String appType = request.getParameter("apptype");
			String code = request.getParameter("code");
			String path = request.getParameter("path");
			if (userName == null || appName == null)
				return;
			// System.out.println(code);
			int position = Integer.valueOf(request.getParameter("position"));
			// System.out.println(position);

			methods = new JSONArray();
			variables = new JSONArray();
			parentCnt = 0;

			String projectRelativePath = FilePath.getProjectRelativePath(
					userName, appName);
			String completionList = "";

			if (appType.equals("javaweb")) {
				String relativePath = path.substring(srcPath.length() - 1);
				completionList = getJavaCompletionList(projectRelativePath,
						code, position, relativePath);
			} else if (appType.equals("php")) {
				completionList = getPHPCompletionList(projectRelativePath,
						code, position, path);
			}

			try {
				PrintWriter out = response.getWriter();
				out.print(jsonpCallback + "(" + completionList + ")");
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void recordUsage(String classname, String method) {
		try {
			Completion c = CompletionJDBC.findCompletionByClassAndMethod(
					classname, method);
			if (c == null) {
				c = new Completion(classname, method);
				CompletionJDBC.insert(c);
			} else {
				c.addOneCount();
				CompletionJDBC.update(c);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getPHPCompletionList(String projectRelativePath,
			String code, int position, String fileRelativePath) {
		PHPParser pp = new PHPParser();
		String type = pp.getType(code.toCharArray(), position);
		JSONObject jo = new JSONObject();
		System.out.println(type);
		findPHPClass(projectRelativePath, fileRelativePath, type);
		try {
			jo.put("methods", methods);
			jo.put("variables", variables);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = jo.toString();
		return json;
	}

	private void findPHPClass(String projectRelativePath,
			String fileRelativePath, String className) {
		String path = FilePath.getProjectPath(projectRelativePath, "php")
				+ File.separator + fileRelativePath;
		File file = new File(path);
		String indexPath = FilePath.getPHPUserIndexPath(projectRelativePath)
				+ File.separator + fileRelativePath.replace(".php", ".json");
		File indexFile = new File(indexPath);
		String astJson;
		if (file.exists()
				&& (!indexFile.exists() || file.lastModified() > indexFile
						.lastModified())) {
			PHPParser pp = new PHPParser();
			astJson = pp.toJson(path);
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
		} else {
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
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(astJson);
			JSONArray classesArray = jsonObject.getJSONArray("classes");
			int i, len = classesArray.length();
			JSONObject curClass = null;
			for (i = 0; i < len; i++) {
				curClass = classesArray.getJSONObject(i);
				if (curClass.get("name").equals(className))
					break;
			}
			if (i == len) {
				JSONArray includesArray = jsonObject.getJSONArray("includes");
				int j;
				for (j = 0; j < includesArray.length(); j++) {
					int k = fileRelativePath.lastIndexOf(File.separator);
					String parentPath;
					if (k < 0)
						parentPath = "";
					else
						parentPath = fileRelativePath.substring(0, k);
					findPHPClass(projectRelativePath, parentPath
							+ File.separator + includesArray.getString(j),
							className);
				}
			} else {
				getPHPCompletionInFile(projectRelativePath, fileRelativePath,
						curClass, className);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getPHPCompletionInFile(String projectRelativePath,
			String fileRelativePath, JSONObject classObject, String className) {
		if (classObject == null)
			return;
		String father = null;
		try {
			JSONArray curMethods = classObject.getJSONArray("methods");
			JSONArray curVariables = classObject.getJSONArray("fields");
			int oldcnt = methods.length();
			int newcnt = curMethods.length();
			int i, j;
			for (i = 0; i < newcnt; i++) {
				JSONObject cur = curMethods.getJSONObject(i);
				for (j = 0; j < oldcnt; j++) {
					JSONObject pre = methods.getJSONObject(j);
					if (cur.getString("name").equals(pre.getString("name")))
						break;
				}
				if (j == oldcnt) {
					cur.put("class", className);
					cur.put("parent_cnt", parentCnt);
					methods.put(cur);
				}
			}
			oldcnt = variables.length();
			newcnt = curVariables.length();
			for (i = 0; i < newcnt; i++) {
				JSONObject cur = curVariables.getJSONObject(i);
				for (j = 0; j < oldcnt; j++) {
					JSONObject pre = variables.getJSONObject(j);
					if (cur.getString("name").equals(pre.getString("name"))) {
						break;
					}
				}
				if (j == oldcnt) {
					cur.put("class", className);
					cur.put("parent_cnt", parentCnt);
					variables.put(cur);
				}
			}
			father = classObject.getString("superclass");
		} catch (JSONException e) {
		}
		if (father != null && !father.equals("")) {
			parentCnt++;
			findPHPClass(projectRelativePath, fileRelativePath, father);
		}
	}

	private String getJavaCompletionList(String projectRelativePath,
			String currentCode, int position, String fileRelativePath) {
		JavaParser jp = new JavaParser();
		String type = jp.getType(currentCode.toCharArray(), position,
				fileRelativePath,
				FilePath.getProjectSrcPath(projectRelativePath),
				FilePath.getCommonIndexPath("javaweb"),
				FilePath.getUserJarIndexPath(projectRelativePath),
				FilePath.getMavenJarIndexPath(projectRelativePath));
		JSONObject jo = new JSONObject();
		System.out.println(type);
		getJavaCompletionInFile(projectRelativePath, type);
		try {
			jo.put("methods", methods);
			jo.put("variables", variables);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json = jo.toString();
		return json;
	}

	private void getJavaCompletionInFile(String projectRelativePath,
			String classFullName) {
		String classPath = classFullName.replaceAll("\\.", "/");
		File file = new File(FilePath.getProjectSrcPath(projectRelativePath)
				+ classPath + ".java");
		File jarJson = new File(
				FilePath.getUserJarIndexPath(projectRelativePath) + classPath
						+ ".json");
		File mvnJsonFile = new File(FilePath.getMavenJarIndexPath(projectRelativePath) + classPath + ".json");
		String indexFileName, astJson;
		File indexFile = null;
		boolean isCommon = false;
		HashMap<String, Long> map = null;
		if (file.exists()) {
			indexFileName = (FilePath
					.getJavaUserSrcIndexPath(projectRelativePath) + classPath + ".json");
			indexFile = new File(indexFileName);
		} else if (jarJson.exists()) {
			indexFile = jarJson;
		} else if (mvnJsonFile.exists()) {
			indexFile = mvnJsonFile;
		} else {
			isCommon = true;
			try {
				map = CompletionJDBC.findMethodsCountByClass(classFullName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String[] subdirs = new File(FilePath.getCommonIndexPath("javaweb"))
					.list();
			for (String subdir : subdirs) {
				File commonJson = new File(
						FilePath.getCommonIndexPath("javaweb") + subdir + "/"
								+ classPath + ".json");
				if (commonJson.exists())
					indexFile = commonJson;
			}
			if (indexFile == null)
				return;
		}
		if (file.exists()
				&& (!indexFile.exists() || file.lastModified() > indexFile
						.lastModified())) {
			// generate index
			JavaParser jp = new JavaParser();
			astJson = jp.toJson(classPath + ".java",
					FilePath.getProjectSrcPath(projectRelativePath),
					FilePath.getCommonIndexPath("javaweb"),
					FilePath.getUserJarIndexPath(projectRelativePath),
					FilePath.getMavenJarIndexPath(projectRelativePath));
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
		} else {
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
		// System.out.println(astJson);
		// get completion from index
		String father = null;
		try {
			JSONObject jsonObject = new JSONObject(astJson);
			JSONObject classObject;
			classObject = jsonObject.getJSONArray("classes").getJSONObject(0);
			JSONArray curMethods = classObject.getJSONArray("methods");
			JSONArray curVariables = classObject.getJSONArray("variables");
			int oldcnt = methods.length();
			int newcnt = curMethods.length();
			int i, j;
			for (i = 0; i < newcnt; i++) {
				JSONObject cur = curMethods.getJSONObject(i);
				for (j = 0; j < oldcnt; j++) {
					JSONObject pre = methods.getJSONObject(j);
					if (cur.getString("name").equals(pre.getString("name"))) {
						JSONArray curParams = cur.getJSONArray("parameters");
						JSONArray preParams = pre.getJSONArray("parameters");
						if (curParams.length() == preParams.length()) {
							int k, s = curParams.length();
							for (k = 0; k < s; k++) {
								String curType = curParams.getJSONObject(k)
										.getString("type");
								String preType = preParams.getJSONObject(k)
										.getString("type");
								if (!curType.equals(preType))
									break;
							}
							if (k == s)
								break;
						}
					}
				}
				if (j == oldcnt) {
					cur.put("class", classFullName);
					cur.put("parent_cnt", parentCnt);
					cur.put("common", isCommon);
					if (map != null) {
						String method = cur.getString("name") + "(";
						JSONArray params = cur.getJSONArray("parameters");
						int k, s = params.length();
						for (k = 0; k < s; k++) {
							JSONObject param = params.getJSONObject(k);
							String type = param.getString("type");
							type = type.substring(type.lastIndexOf(".") + 1);
							method += type + " " + param.getString("name");
							if (k != s - 1)
								method += ", ";
						}
						method += ")";
						Long count = map.get(method);
						if (count != null)
							cur.put("count", count);
						else
							cur.put("count", 0);
					}
					methods.put(cur);
				}
			}
			oldcnt = variables.length();
			newcnt = curVariables.length();
			for (i = 0; i < newcnt; i++) {
				JSONObject cur = curVariables.getJSONObject(i);
				for (j = 0; j < oldcnt; j++) {
					JSONObject pre = variables.getJSONObject(j);
					if (cur.getString("type").equals(pre.getString("type"))
							&& cur.getString("name").equals(
									pre.getString("name"))) {
						break;
					}
				}
				if (j == oldcnt) {
					cur.put("class", classFullName);
					cur.put("parent_cnt", parentCnt);
					variables.put(cur);
				}
			}
			father = classObject.getString("superclass");
		} catch (JSONException e) {
		}
		if (father != null && !father.equals("")) {
			parentCnt++;
			getJavaCompletionInFile(projectRelativePath, father);
		}
	}

	public static void main(String[] args) {
		String projectPath = "/pop-php/";
		File file = new File("/data/pop-php/src/Test.java");
		StringBuffer java_code = null;
		try {
			Scanner input = new Scanner(file);
			java_code = new StringBuffer();
			while (input.hasNextLine()) {
				java_code.append(input.nextLine() + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int position = 185;
		CompletionServlet cServlet = new CompletionServlet();
		String completionList = cServlet.getJavaCompletionList(projectPath,
				java_code.toString(), position, "Test.java");
		System.out.println(completionList);
	}
}
