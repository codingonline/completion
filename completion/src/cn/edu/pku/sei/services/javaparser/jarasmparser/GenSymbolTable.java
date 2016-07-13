package cn.edu.pku.sei.services.javaparser.jarasmparser;

import java.io.*;
import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import com.alibaba.fastjson.JSON;

import cn.edu.pku.sei.services.javaparser.symboltable.*;

public class GenSymbolTable 
{
	static int TYPE_CLASS = 0;
	static int TYPE_METHOD = 1;
	static int TYPE_FIELD = 2;
	public static String parse(String name)
	{
		int count = 0;
		while(name.startsWith("["))
		{
			count++;
			name = name.substring(name.indexOf("[") + 1);
		}
		char start = name.charAt(0);
		switch(start)
		{
		case 'V':
			name = "void";
			break;
		case 'Z':
			name = "boolean";
			break;
		case 'B':
			name = "byte";
			break;
		case 'C':
			name = "char";
			break;
		case 'L':
			name = name.substring(1);
			if(name.endsWith(";"))
				name = name.substring(0, name.indexOf(";"));
			break;
		case 'D':
			name = "double";
			break;
		case 'F':
			name = "float";
			break;
		case 'I':
			name = "int";
			break;
		case 'J':
			name = "long";
			break;
		case 'S':
			name = "short";
			break;
			default:
				break;
		}
		while(count > 0)
		{
			name = name + "[]";
			count--;
		}
		name = name.replaceAll("/", ".");
		return name;
	}
	
	public static List<String> getModifiers(int access, int type)
	{
		List<String> modifiers = new ArrayList<String>();
		if(access % 2 == 1)
		{
			modifiers.add("public");
			access -= 1;
		}
		if(access % 4 == 2)
		{
			modifiers.add("private");
			access -= 2;
		}
		if(access % 8 == 4)
		{
			modifiers.add("protected");
			access -= 4;
		}
		if(access % 16 == 8)
		{
			modifiers.add("static");
			access -= 8;
		}
		if(access % 32 == 16)
		{
			modifiers.add("final");
			access -= 16;
		}
		if(access % 64 == 32)
		{
			if(type == TYPE_METHOD)
				modifiers.add("synchronized");
			access -= 32;
		}
		if(access % 128 == 64)
		{
			if(type == TYPE_FIELD)
				modifiers.add("volatie");
			access -= 64;
		}
		if(access % 256 == 128)
		{
			if(type == TYPE_FIELD)
				modifiers.add("transient");
			access -= 128;
		}
		if(access % 512 == 256)
		{
			modifiers.add("native");
			access -= 256;
		}
		if(access % 1024 == 512)
		{
			modifiers.add("interface");
			access -= 512;
		}
		if(access % 2048 == 1024)
		{
			modifiers.add("abstract");
			access -= 1024;
		}
		if(access % 4096 == 2048)
		{
			modifiers.add("strict");
			access -= 2048;
		}
		if(access % 8192 == 4096)
		{
			//modifiers.add("synthtic");
			access -= 4096;
		}
		if(access % 16384 == 8192)
		{
			modifiers.add("annotation");
			access -= 8192;
		}
		if(access % 32768 == 16384)
		{
			modifiers.add("enum");
			access -= 16384;
		}
		if(access % 65536 == 32768)
		{
			modifiers.add("mandated");
			access -= 32768;
		}
		if(access == 131072)
		{
			modifiers.add("deprecated");
		}
		return modifiers;
	}
	
	public static List<String> getInterfaces(List<String> cns)
	{
		List<String> _ret = new ArrayList<String>();
		for(String cn : cns)
		{
			String name = cn.replaceAll("/", ".");
			_ret.add(name);
		}
		return _ret;
	}
	
	public static String gen(String classname)
	{
		try {
			FileInputStream fi = new FileInputStream(classname);
			byte[] b=new byte[fi.available()];
			fi.read(b);
			fi.close();
			ClassReader cr = new ClassReader(b);
			ClassNode cn = new ClassNode(); 
			cr.accept(cn, 0); 
			
			MClasses classes = new MClasses();
			String superclass = cn.superName;
			if(superclass != null)
			{
				superclass = superclass.replaceAll("/", "\\.");
			}
			
			String name = cn.name.replaceAll("/", "\\.");
			List<String> modifiers = getModifiers(cn.access, TYPE_CLASS);
			
			List<String> interfaces = getInterfaces(cn.interfaces);
			
			
			MClass mclass = new MClass(modifiers, name, superclass, interfaces);
			
			classes.classes.add(mclass);
			
			List<MethodNode> methodList = cn.methods;
			
			for(MethodNode md : methodList)
			{
				if(md.name.equals("<clinit>"))
					continue;
				MMethod method = genMethod(md);
				if(method.name.equals("<init>"))
				{
					String class_name = mclass.name;
					int lastdo = class_name.lastIndexOf(".");
					if(lastdo != -1)
					{
						class_name = class_name.substring(lastdo + 1);
					}
					method.name = class_name;
				}
				mclass.methods.add(method);
			}
			
			List<FieldNode> fieldList = cn.fields; 
            for (FieldNode fieldNode : fieldList) 
            { 
            	mclass.variables.add(genField(fieldNode));
            }
            return JSON.toJSON(classes).toString();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
		return null;
	}
	public static MMethod genMethod(MethodNode md)
	{
		String name = md.name;
		//System.out.println(name);
		
		List<String> modifiers = getModifiers(md.access, TYPE_METHOD);
		
		String desc = md.desc;
		
		String paras = desc.substring(desc.indexOf("(")+1, desc.indexOf(")"));
		String return_value = desc.substring(desc.indexOf(")")+1);
		return_value = parse(return_value);

		List<MParameter> parameters = new ArrayList<MParameter>();
		int i = 0;
		while(paras.length() > 0)
		{
			int lastindex = 0;
			while(paras.charAt(lastindex) == '[')
			{
				lastindex++;
			}
			if(paras.charAt(lastindex) == 'L')
			{
				lastindex = paras.indexOf(";") + 1;
			}
			else
				lastindex += 1;
			String para = paras.substring(0, lastindex);
			para = parse(para);
			paras = paras.substring(lastindex);
			
			String para_name = "arg" + i;
			i++;
			parameters.add(new MParameter(para,para_name,0));
		}
		return new MMethod(modifiers, return_value, name, parameters);
	}
	
	public static MVariable genField(FieldNode fn)
	{
		List<String> modifiers = getModifiers(fn.access, TYPE_FIELD);
		String name = fn.name;
		String desc = fn.desc;
		String type = parse(desc);
		//System.out.println(type);
		return new MVariable(modifiers, type, name,0);
	}
	public static void main(String args[])
	{
		System.out.println(GenSymbolTable.gen("String.class"));
	}
}
