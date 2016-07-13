package cn.edu.pku.sei.services.javaparser.jarparser;

import java.io.File;
import java.io.FileOutputStream;

class GenJson implements Runnable // generate json file for a class
{
	String classname = null;
	String savepath = null;
	public GenJson(String classname, String savepath)
	{
		this.savepath = savepath;
		this.classname = classname;
	}
	public void run() {
    	FileOutputStream output;
    	File jsonfile = null;
		try {
			int lastdo = classname.lastIndexOf(".");
			String dirname = savepath;
			if(lastdo != -1)
				dirname = dirname + classname.substring(0,lastdo);
			dirname = dirname.replaceAll("\\.", "/");
			String filename = savepath + classname.replaceAll("\\.", "/") + ".json";
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