package cn.edu.pku.sei.services.javaparser.jarasmparser;
import java.io.*;  
import java.nio.*;   
import java.nio.channels.*;
import java.util.*;
import java.util.jar.*;

import cn.edu.pku.sei.utils.EnvironmentProperty;

public class JarFileUtil {  
  
	public static Set<String> classes = new HashSet<String>(); // all classes in jars
	public static Set<File> files = new HashSet<File>();
	public static Set<File> dirs = new HashSet<File>();
	private static String tempFolder = EnvironmentProperty.readConf("completionFolder") + EnvironmentProperty.readConf("jarTemp");
	
    public static void uncompress(File jarFile) throws IOException // unzip jar
    {  
        JarFile jfInst = new JarFile(jarFile);  
        Enumeration<JarEntry> enumEntry = jfInst.entries();  
        while (enumEntry.hasMoreElements()) {  
            JarEntry jarEntry = enumEntry.nextElement();  
            //锟斤拷锟斤拷锟窖癸拷募锟绞碉拷锟�
            String jarname = jarEntry.getName();
            
            if(jarname.endsWith(".class")) // if is a class then add to classes
            {
            	String temp = jarname.replaceAll("/", ".");
            	temp = temp.substring(0, temp.indexOf(".class"));
            	classes.add(temp);
            }

            File tarFile = new File(tempFolder + jarEntry.getName()); 
            files.add(tarFile);
            //锟斤拷锟斤拷锟侥硷拷 
            if (!jarEntry.isDirectory()) {  
            	File tarFileDir = tarFile.getParentFile();
	                 dirs.add(tarFileDir);
	                 if(!tarFileDir.exists())
	                 {
	                	 tarFileDir.mkdirs();
	                 }
            }  
            if(tarFile.exists())
            	continue;
            makeFile(jarEntry, tarFile);  
            if (jarEntry.isDirectory()) {  
                continue;  
            }  
            //锟斤拷锟斤拷锟斤拷锟斤拷锟� 
            FileChannel fileChannel = new FileOutputStream(tarFile).getChannel();  
            //取锟斤拷锟斤拷锟斤拷  
            InputStream ins = jfInst.getInputStream(jarEntry);  
            transferStream(ins, fileChannel);  
        }  
    }  
  
    private static void transferStream(InputStream ins, FileChannel targetChannel) 
    {  
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 10);  
        ReadableByteChannel rbcInst = Channels.newChannel(ins);  
        try {  
            while (-1 != (rbcInst.read(byteBuffer))) {  
                byteBuffer.flip();  
                targetChannel.write(byteBuffer);  
                byteBuffer.clear();  
            }  
        } catch (IOException ioe) {  
            ioe.printStackTrace();  
        } finally {  
            if (null != rbcInst) {  
                try {  
                    rbcInst.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (null != targetChannel) {  
                try {  
                    targetChannel.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
  
    private static void makeFile(JarEntry jarEntry, File fileInst) 
    {  
        if (!fileInst.exists()) {  
            if (jarEntry.isDirectory()) {  
                fileInst.mkdirs();  
            } else {  
                try {  
                    fileInst.createNewFile();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
    
    private static void deleteDir(File dir)
    {
    	if(!dir.exists())
    		return;
    	if(dir.isDirectory())
    	{
    		for(String name:dir.list())
    		{
    			deleteDir(new File(dir, name)); 
    		}
    	}
    	dir.delete();
    }
    
    public static void parse(String jarsdir_name, String savepath) {
    	File jarsdir = new File(jarsdir_name);
    	if(!jarsdir.isDirectory())
    		return;
    	for(String jarname: jarsdir.list()) // for all jar in args
    	{
    		if(!jarname.endsWith(".jar"))
    			continue;
	        File jarFile = new File(jarsdir,jarname);  
	        try {  
	            JarFileUtil.uncompress(jarFile);  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }
    	}
    	File savefile = new File(savepath);
    	deleteDir(savefile);
    	savefile.mkdirs();
    	int i = 0;
    	int errors = 0;
        for(String classname:classes)
        {
        	i++;
        	GenJson gen = new GenJson(classname,savepath);
        	try
        	{
        		gen.run();
        	}
        	catch(NullPointerException e)
        	{
        		errors++;
        	}
//        	System.out.println((double)i/(double)classes.size());
        }
        for(String classname:classes)
        {
        	GenJson gen = new GenJson(classname,savepath);
        	try
        	{
        		gen.run();
        	}
        	catch(NullPointerException e)
        	{
        		errors++;
        	}
        	//System.out.println((double)i/(double)classes.size());
        }
        File temp = new File(tempFolder);
        for(String name:temp.list())
        {
        	deleteDir(new File(temp,name));
        }
        System.out.println("done with errors : " + errors/2 + "\n");
    }  
}  
