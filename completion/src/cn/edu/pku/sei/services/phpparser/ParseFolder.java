package cn.edu.pku.sei.services.phpparser;

import java.io.*;
import java.util.*;

import com.alibaba.fastjson.JSON;

import cn.edu.pku.sei.services.phpparser.phpSymbolTable.MMethod;

public class ParseFolder 
{
	public static List<MMethod> methods;
	public static void parse(String sourcePath, String savePath)
	{
		File sourceFolder = new File(sourcePath);
		File saveFolder = new File(savePath);
		if(!saveFolder.exists())
		{
			saveFolder.mkdirs();
		}
		String[] sourceFiles = sourceFolder.list();
		for(String sourceFile:sourceFiles)
		{
			File source = new File(sourceFolder, sourceFile);
			if(source.isDirectory())
			{
				parse(sourcePath + sourceFile + "/", savePath + sourceFile + "/");
				continue;
			}
			if(!sourceFile.endsWith(".php"))
				continue;
			PHPParser parser = new PHPParser();
			String json = parser.toJson(sourcePath + sourceFile);
			String saveName = sourceFile.substring(0, sourceFile.indexOf(".php")) + ".json";
			File saveFile = new File(saveFolder, saveName);
			try {
				FileOutputStream fos = new FileOutputStream(saveFile);
				fos.write(json.getBytes());
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	public static void parseIntoOne(String sourcePath, String savePath)
	{
		methods = new ArrayList<MMethod>();
		parse(sourcePath);
		Collections.sort(methods);
		System.out.println(methods);
		String json = JSON.toJSONString(methods);
		try {
			FileOutputStream fos = new FileOutputStream(savePath);
			fos.write(json.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static void parse(String sourcePath)
	{
		File sourceFolder = new File(sourcePath);
		String[] sourceFiles = sourceFolder.list();
		for(String sourceFile:sourceFiles)
		{
			File source = new File(sourceFolder, sourceFile);
			if(source.isDirectory())
			{
				parse(sourcePath + sourceFile + "/");
				continue;
			}
			if(!sourceFile.endsWith(".php"))
				continue;
			PHPParser parser = new PHPParser();
			parser.init(sourcePath + sourceFile);
			for(MMethod method: parser.program.methods)
			{
				methods.add(method);
			}
		}
	}
}
