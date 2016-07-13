package cn.edu.pku.sei.services.javaparser.symboltable;

import java.io.File;
import java.util.*;

import org.eclipse.jdt.core.dom.ASTNode;

public class MClasses 
{
	public String projectpath = null;
	public List<String> imports = new ArrayList<String>();
	public List<MClass> classes = new ArrayList<MClass>();
	public MClasses()
	{
		
	}
	public List<String> getImports()
	{
		return imports;
	}
	public void setImports(List<String> imports)
	{
		this.imports = imports;
	}
	public List<MClass> getClasses()
	{
		return classes;
	}
	public void setClasses(List<MClass> classes)
	{
		this.classes = classes;
	}
	public void insertClass(List<ASTNode> v_modifiers, String v_name, String v_superclass, List<ASTNode> v_interfaces, String v_javadoc)
	{
		if(v_superclass == null)
			v_superclass = "";
		if(v_javadoc == null)
			v_javadoc = "";
		classes.add(new MClass(v_modifiers, v_name, v_superclass, v_interfaces, v_javadoc));
	}
	public void insertMethod(List<ASTNode> v_modifiers, String v_return, String v_name, List<ASTNode> v_parameters, String v_javadoc, int startPosition, int bodystartPosition)
	{
		if(v_return == null)
			v_return = "";
		if(v_javadoc == null)
			v_javadoc = "";
		classes.get(classes.size()-1).methods.add(new MMethod(v_modifiers, v_return, v_name, v_parameters, v_javadoc, startPosition, bodystartPosition));
	}
	public void insertVariable(List<ASTNode> v_modifiers, String v_type, String v_name, String v_javadoc, int startPosition)
	{
		if(v_javadoc == null)
			v_javadoc = "";
		classes.get(classes.size()-1).variables.add(new MVariable(v_modifiers, v_type, v_name, v_javadoc, startPosition));
	}
	
	
}
