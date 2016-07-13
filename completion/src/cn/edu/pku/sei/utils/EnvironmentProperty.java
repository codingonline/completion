package cn.edu.pku.sei.utils;

import java.io.IOException;
import java.util.Properties;

public class EnvironmentProperty {
	private static Properties properties = new Properties();
	static{
		try {
			properties.load(EnvironmentProperty.class.getClassLoader().getResourceAsStream("environment.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readConf(String key){		
		return (String)properties.get(key);
	}
}
