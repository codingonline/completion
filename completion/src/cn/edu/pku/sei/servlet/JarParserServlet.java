package cn.edu.pku.sei.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.pku.sei.services.javaparser.jarasmparser.JarFileUtil;
import cn.edu.pku.sei.utils.FilePath;

public class JarParserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Content-Type", "text/plain;charset=UTF-8");
		response.setDateHeader("Expires", 0);
		String userName = request.getParameter("username");
		String appName = request.getParameter("appname");
		String operation = request.getParameter("operation");
		System.out.println(operation);
		if (operation.equals("mvn")) {
			generateMavenLibAst(userName, appName);
		} else if (operation.equals("userjar")) {
			String projectRelativePath = FilePath.getProjectRelativePath(
					userName, appName);
			String userJarPath = FilePath.getUserJarPath(projectRelativePath);
			String jarIndexPath = FilePath
					.getUserJarIndexPath(projectRelativePath);
			generateJarLibAst(userJarPath, jarIndexPath);
		}
	}

	public void generateMavenLibAst(String userName, String appName) {
		String projectRelativePath = FilePath.getProjectRelativePath(userName,
				appName);
		String jarPath = FilePath.getMvnTmpPath(projectRelativePath);
		String savePath = FilePath.getMavenJarIndexPath(projectRelativePath);
		String projectPath = FilePath.getProjectPath(projectRelativePath, "javaweb");

		// mvn dependency:copy-dependencies -DoutputDirectory=[outPath]
		// -DoverWriteIfNewer=true
		String[] mvnCmd = { "mvn", "dependency:copy-dependencies",
				"-DoutputDirectory=" + jarPath, "-DoverWriteIfNewer=true" };
		try {
			ProcessBuilder pb = new ProcessBuilder(mvnCmd);
			pb.directory(new File(projectPath));
			pb.redirectErrorStream(true);
			Process process = pb.start();
			process.waitFor();
			int exitVal = process.exitValue();
			if (exitVal == 0) {
				System.out.println("mvn package over");
				JarFileUtil.parse(jarPath, savePath);
				File dir = new File(jarPath);
				File[] children = dir.listFiles();
				if (children != null) {
					for (int i = 0; i < children.length; i++) {
						children[i].delete();
					}
				}
				dir.delete();
			} else {
				System.out.println("mvn package failure");
				System.out.println("subprocess.exitValue is " + exitVal);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void generateJarLibAst(String jarPath, String indexPath) {
		JarFileUtil.parse(jarPath, indexPath);
	}

	public static void main(String[] args) {
		JarParserServlet jpServlet = new JarParserServlet();
		jpServlet.generateMavenLibAst("ls", "DebugTest");
	}
}
