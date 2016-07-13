package cn.edu.pku.sei.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.sei.services.javaparser.JavaParser;
import cn.edu.pku.sei.utils.EnvironmentProperty;
import cn.edu.pku.sei.utils.FilePath;

public class OutlineServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String srcPath = EnvironmentProperty.readConf("srcPath");

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Content-Type",
				"application/x-javascript;charset=UTF-8");
		response.setDateHeader("Expires", 0);
		String userName = request.getParameter("username");
		String appName = request.getParameter("appname");
		String projectType = request.getParameter("apptype");
		String path = request.getParameter("path");
		String jsonpCallback = request.getParameter("jsonpCallback");
		String astJson = "";
		if (path.endsWith(".java")) {
			String classPath = path.substring(srcPath.length() - 1);
			String projectRelativePath = FilePath.getProjectRelativePath(
					userName, appName);
			JavaParser jp = new JavaParser();
			astJson = jp.toJson(classPath,
					FilePath.getProjectSrcPath(projectRelativePath),
					FilePath.getCommonIndexPath(projectType),
					FilePath.getUserJarIndexPath(projectRelativePath),
					FilePath.getMavenJarIndexPath(projectRelativePath));
		}

		try {
			PrintWriter out = response.getWriter();
			out.print(jsonpCallback + "(" + astJson + ")");
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	}
}
