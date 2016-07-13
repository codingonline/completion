package cn.edu.pku.sei.services.javaparser.jarasmparser;

import java.io.*;

import cn.edu.pku.sei.services.javaparser.jarasmparser.GenSymbolTable;
import cn.edu.pku.sei.utils.EnvironmentProperty;

class GenJson implements Runnable // generate json file for a class
{
	String classname = null;
	String savepath = null;
	private static String tempFolder = EnvironmentProperty.readConf("completionFolder") + EnvironmentProperty.readConf("jarTemp");

	public GenJson(String classname, String savepath)
	{
		this.savepath = savepath;
		this.classname = classname;
	}
	public void run() 
	{
    	FileOutputStream output;
    	File jsonfile = null;
		try {
			int lastdo = classname.lastIndexOf(".");
			String dirname = savepath;
			if(lastdo != -1)
				dirname = dirname + classname.substring(0,lastdo);
			dirname = dirname.replaceAll("\\.", "/");
			String filename = savepath + classname.replaceAll("\\.", "/") + ".json";
			classname = tempFolder + classname.replaceAll("\\.", "/") + ".class";
			File dir = new File(dirname);
			if(!dir.exists())
				dir.mkdirs();
			jsonfile = new File(filename);
			if(!jsonfile.exists())
			{
				jsonfile.createNewFile();
			}			
			String json = GenSymbolTable.gen(classname);
			output = new FileOutputStream(jsonfile);
			output.write(json.getBytes());
		} catch (Throwable e) {
			//e.printStackTrace();
			if(jsonfile.exists())
			{
				jsonfile.delete();
			}
			throw new NullPointerException();
		}
	}
}