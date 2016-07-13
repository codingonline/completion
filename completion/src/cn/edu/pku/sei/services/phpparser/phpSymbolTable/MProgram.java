package cn.edu.pku.sei.services.phpparser.phpSymbolTable;

import java.util.ArrayList;
import java.util.List;

public class MProgram 
{
	public List<String> includes = new ArrayList<String>();
	public List<MClass> classes = new ArrayList<MClass>();
	public List<MMethod> methods = new ArrayList<MMethod>();
	public List<MVariable> variables = new ArrayList<MVariable>();
	
	public void setIncludes(List<String> includes)
	{
		this.includes = includes;
	}
	public List<String> getIncludes()
	{
		return includes;
	}
	
	public void setClasses(List<MClass> classes)
	{
		this.classes = classes;
	}
	public List<MClass> getClasses()
	{
		return classes;
	}
	
	public void setMethods(List<MMethod> methods)
	{
		this.methods = methods;
	}
	public List<MMethod> getMethods()
	{
		return methods;
	}
	
	public void setVariables(List<MVariable> variables)
	{
		this.variables = variables;
	}
	public List<MVariable> getVariables()
	{
		return variables;
	}
}
