package cn.edu.pku.sei.utils;

import java.io.File;

public class FilePath {
	private static String repository = EnvironmentProperty.readConf("repository");
	private static String completionFolder = EnvironmentProperty.readConf("completionFolder");
	private static String commonFolder = EnvironmentProperty.readConf("commonFolder");
	private static String individualFolder = EnvironmentProperty
			.readConf("individualFolder");
	private static String jarLibPath = EnvironmentProperty.readConf("jarLibPath");
	private static String srcPath = EnvironmentProperty.readConf("srcPath");	
	
	public static String getProjectRelativePath (String userName, String appName){		
		return File.separator + userName + File.separator + appName + File.separator;
	}
	
	public static String getProjectPath (String relativePath, String type){		
		return repository + type + File.separator + relativePath;
	}

	public static String getProjectSrcPath (String relativePath){		
		return repository + "javaweb" + File.separator + relativePath + srcPath;
	}
	
	public static String getUserJarPath (String relativePath){		
		return repository + "javaweb" + File.separator + relativePath + jarLibPath;
	}
	
	public static String getCommonIndexPath (String type){		
		return completionFolder + type + File.separator + commonFolder;
	}
	
	public static String getPHPUserIndexPath (String relativePath){		
		return completionFolder + "php" + File.separator + individualFolder + relativePath;
	}
	
	public static String getJavaUserSrcIndexPath (String relativePath){		
		return completionFolder + "javaweb" + File.separator + individualFolder + relativePath + srcPath;
	}
	
	public static String getUserJarIndexPath (String relativePath){		
		return completionFolder + "javaweb" + File.separator + individualFolder + relativePath + jarLibPath;
	}
	
	public static String getMavenJarIndexPath (String relativePath){		
		return completionFolder + "javaweb" + File.separator + individualFolder + relativePath + "maven" + File.separator;
	}
	
	public static String getMvnTmpPath (String relativePath){		
		return completionFolder + "javaweb" + File.separator + individualFolder + relativePath + "mvnTmp" + File.separator;
	}
}
