package cn.edu.pku.sei.services.javaparser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.pku.sei.services.javaparser.symboltable.*;

public class Linker {
	public static void analysis(String filePath, String projectpath,
			String commonpath, String jarPath, String MavenPath, MClasses classes) {
		classes.imports.add("java.lang.*");
		String pack = null;
		if (filePath.indexOf("/") != -1) {
			pack = filePath.substring(0, filePath.lastIndexOf("/"));
			pack = pack.replaceAll("/", ".") + ".*";
			classes.imports.add(pack);
		}
		Set<String> newim = new HashSet<String>();
		for (String im : classes.imports) {
			if (im.contains(".*")) {
				String dirname = im.substring(0, im.indexOf(".*"));

				String ddirname = projectpath + dirname.replaceAll("\\.", "/");
				try {
					File dir = new File(ddirname);
					for (String classname : dir.list()) {
						if (classname.endsWith(".json")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".json")));
						}
						if (classname.endsWith(".java")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".java")));
						}
						if (classname.endsWith(".class")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".class")));
						}
					}
				} catch (Exception e) {
				}
				try {
					File commonFolder = new File(commonpath);
					String[] dirs = commonFolder.list();
					for (String subdir : dirs) {
						ddirname = commonpath + subdir + "/" + dirname.replaceAll("\\.", "/");
						File dir = new File(ddirname);
						if (! dir.exists())
							continue;
						for (String classname : dir.list()) {
							if (classname.endsWith(".json")) {
								newim.add(dirname
										+ "."
										+ classname.substring(0,
												classname.indexOf(".json")));
							}
							if (classname.endsWith(".java")) {
								newim.add(dirname
										+ "."
										+ classname.substring(0,
												classname.indexOf(".java")));
							}
							if (classname.endsWith(".class")) {
								newim.add(dirname
										+ "."
										+ classname.substring(0,
												classname.indexOf(".class")));
							}
						}
					}
				} catch (Exception e) {
				}
				ddirname = MavenPath + dirname.replaceAll("\\.", "/");
				try {
					File dir = new File(ddirname);
					for (String classname : dir.list()) {
						if (classname.endsWith(".json")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".json")));
						}
						if (classname.endsWith(".java")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".java")));
						}
						if (classname.endsWith(".class")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".class")));
						}
					}
				} catch (Exception e) {
				}
				ddirname = jarPath + dirname.replaceAll("\\.", "/");
				try {
					File dir = new File(ddirname);
					for (String classname : dir.list()) {
						if (classname.endsWith(".json")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".json")));
						}
						if (classname.endsWith(".java")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".java")));
						}
						if (classname.endsWith(".class")) {
							newim.add(dirname
									+ "."
									+ classname.substring(0,
											classname.indexOf(".class")));
						}
					}
				} catch (Exception e) {
				}
			} else
				newim.add(im);
		}
		List<String> real_newim = new ArrayList<String>();
		for (String im : newim)
			real_newim.add(im);
		classes.imports = real_newim;
		for (String im : classes.imports) {
			for (MClass mclass : classes.classes) {
				if (mclass.superclass.equals(""))
					mclass.superclass = "java.lang.Object";
				else {
					String superclass = mclass.superclass;
					if (im.endsWith("." + superclass)) {
						mclass.superclass = im;
						break;
					}
				}
				List<String> newinter = new ArrayList<String>();
				for (String inter : mclass.interfaces) {
					if (im.endsWith("." + inter)) {
						newinter.add(inter);
						break;
					}
				}
				mclass.interfaces = newinter;
				for (MMethod method : mclass.methods) {
					if (method.return_value != null) {
						if (im.endsWith("." + method.return_value)) {
							method.return_value = im;
							break;
						}
					}
					for (MParameter para : method.parameters) {
						if (im.endsWith("." + para.type)) {
							para.type = im;
							break;
						}
					}
				}
				for (MVariable var : mclass.variables) {
					if (im.endsWith("." + var.type)) {
						var.type = im;
						break;
					}
				}
			}
		}
	}

}
