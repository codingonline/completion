package cn.edu.pku.sei.services.phpparser.phpSymbolTable;

import java.util.ArrayList;
import java.util.List;

public class MClass 
{
	public String name;
	public List<String> modifiers;
	public String superclass;
	public List<String> interfaces;
	public int start;
	public int length;
	public List<MMethod> methods;
	public List<MVariable> fields;
	public String comment;
	public MClass(String _name, List<String> _modifier, String _superclass, List<String> _interfaces, 
			int _start, int _length)
	{
		name = _name;
		modifiers = _modifier;
		superclass = _superclass;
		interfaces = _interfaces;
		start = _start;
		length = _length;
		methods = new ArrayList<MMethod>();
		fields = new ArrayList<MVariable>();
		comment = null;
	}
	
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	
	public void setModifiers(List<String> modifier)
	{
		this.modifiers = modifier;
	}
	public List<String> getModifiers()
	{
		return modifiers;
	}
	
	public void setSuperclass(String superclass)
	{
		this.superclass = superclass;
	}
	public String getSuperclass()
	{
		return superclass;
	}
	
	public void setInterfaces(List<String> interfaces)
	{
		this.interfaces = interfaces;
	}
	public List<String> getInterfaces()
	{
		return interfaces;
	}
	
	public void setStart(int start)
	{
		this.start = start;
	}
	public int getStart()
	{
		return start;
	}
	public void setLength(int length)
	{
		this.length = length;
	}
	public int getLength()
	{
		return length;
	}
	
	public void setMethods(List<MMethod> methods)
	{
		this.methods = methods;
	}
	public List<MMethod> getMethods()
	{
		return methods;
	}
	
	public void setFields(List<MVariable> fields)
	{
		this.fields = fields;
	}
	public List<MVariable> getFields()
	{
		return fields;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	public String getComment()
	{
		return comment;
	}
}
