package cn.edu.pku.sei.services.javaparser.jarparser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import cn.edu.pku.sei.services.javaparser.symboltable.*;

import com.alibaba.fastjson.JSON;

class GenSymbolTable 
{
	public static String parse(String name)
	{
		int count = 0;
		while(name.startsWith("["))
		{
			count++;
			name = name.substring(name.indexOf("[") + 1);
		}
		if(count == 0)
		{
			return name;
		}
		char start = name.charAt(0);
		switch(start)
		{
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
		return name;
	}
	public static String gen(String classname)
	{
		//System.out.println(classname);
		try {
			Class<?> clazz = Class.forName(classname);
			MClasses classes = new MClasses();
			
			String name = parse(clazz.getName()); //class name
			
			String superclass = null;
			if(clazz.getSuperclass() != null)
			{
				superclass = parse(clazz.getSuperclass().getName());
			} // superclass's name
			
			List<String> modifiers = new ArrayList<String>();
			int mod = clazz.getModifiers();
			String s_modifier = Modifier.toString(mod);
			String[] t_modifiers = s_modifier.split(" ");
			for(String modifier: t_modifiers)
				modifiers.add(modifier);
			//modifiers
			
			List<String> interfaces = new ArrayList<String>();
			for(Class<?> interzz:clazz.getInterfaces())
			{
				interfaces.add(parse(interzz.getName()));
			}
			//interfaces
			
			MClass mclass = new MClass(modifiers, name, superclass, interfaces);
			classes.classes.add(mclass);
			
			for(Method method: clazz.getMethods()) // methods
			{
				mclass.methods.add(genMethod(method));
			}
			
			for(Constructor con: clazz.getConstructors()) // constructor
			{
				mclass.methods.add(genConstructor(con));
			}
			
			for(Field field: clazz.getFields()) // variables
			{
				mclass.variables.add(genVariable(field));
			}
			
			//System.out.println(JSON.toJSON(classes));
			return JSON.toJSON(classes).toString();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static MMethod genMethod(Method method)
	{
		String name = method.getName(); // name
		
		List<String> modifiers = new ArrayList<String>();
		int mod = method.getModifiers();
		String s_modifier = Modifier.toString(mod);
		String[] t_modifiers = s_modifier.split(" ");
		for(String modifier: t_modifiers)
			modifiers.add(modifier);
		//modifiers
		
		String return_value = null;
		if(method.getReturnType() != null)
			return_value = parse(method.getReturnType().getName());
		//return value
		
		List<MParameter> parameters = new ArrayList<MParameter>();
		int i = 0;
		for(Class<?> para: method.getParameterTypes())
		{
			String type = parse(para.getName());
			String tname = "arg"+i;
			i++;
			parameters.add(new MParameter(type,tname,0));
		}
		//parameters
		
		return new MMethod(modifiers, return_value, name, parameters);
	}
	public static MMethod genConstructor(Constructor method) // same as method
	{
		String name = parse(method.getName());
		List<String> modifiers = new ArrayList<String>();
		int mod = method.getModifiers();
		String s_modifier = Modifier.toString(mod);
		String[] t_modifiers = s_modifier.split(" ");
		for(String modifier: t_modifiers)
			modifiers.add(modifier);
		String return_value = null;
		List<MParameter> parameters = new ArrayList<MParameter>();
		int i = 0;
		for(Class<?> para: method.getParameterTypes())
		{
			String type = parse(para.getName());
			String tname = "arg"+i;
			i++;
			parameters.add(new MParameter(type,tname,0));
		}
		return new MMethod(modifiers, return_value, name, parameters);
	}
	public static MVariable genVariable(Field field)
	{
		String name = parse(field.getName()); // name
		
		List<String> modifiers = new ArrayList<String>();
		int mod = field.getModifiers();
		String s_modifier = Modifier.toString(mod);
		String[] t_modifiers = s_modifier.split(" ");
		for(String modifier: t_modifiers)
			modifiers.add(modifier);
		//modifiers
		
		String type = parse(field.getType().getName()); // type
		
		return new MVariable(modifiers, type, name,0);
	}
}
