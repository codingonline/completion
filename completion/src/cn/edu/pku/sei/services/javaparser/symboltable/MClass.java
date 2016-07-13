package cn.edu.pku.sei.services.javaparser.symboltable;

import java.util.*;

import org.eclipse.jdt.core.dom.*;


public class MClass 
{
	public List<String> modifiers = new ArrayList<String>();
	public String name;
	public String superclass;
	public List<String> interfaces = new ArrayList<String>();
	public String javadoc;
	public List<MMethod> methods = new ArrayList<MMethod>();
	public List<MVariable> variables = new ArrayList<MVariable>();
	public MClass()
	{
		
	}
	public MClass(List<String> modifiers, String name, String superclass, List<String> interfaces)
	{
		this.modifiers = modifiers;
		this.name = name;
		this.superclass = superclass;
		this.interfaces = interfaces;
	}
	public List<String> getModifiers()
	{
		return 	modifiers;
	}
	public void setModifiers(List<String> modifiers)
	{
		this.modifiers = modifiers;
	}
	
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getSuperclass()
	{
		return superclass;
	}
	public void setSuperclass(String superclass)
	{
		this.superclass = superclass;
	}
	
	public String getJavadoc()
	{
		return javadoc;
	}
	public void setJavadoc(String javadoc)
	{
		this.javadoc = javadoc;
	}
	
	public List<String> getInterfaces()
	{
		return 	interfaces;
	}
	public void setInterfaces(List<String> interfaces)
	{
		this.interfaces = interfaces;
	}
	
	public List<MMethod> getMethods()
	{
		return 	methods;
	}
	public void setMethods(List<MMethod> methods)
	{
		this.methods = methods;
	}
	
	public List<MVariable> getVariables()
	{
		return 	variables;
	}
	public void setVariables(List<MVariable> variables)
	{
		this.variables = variables;
	}
	
	
	
	public MClass(List<ASTNode> v_modifiers, String v_name, String v_superclass, List<ASTNode> v_interfaces, String v_javadoc)
	{
		modifiers = new ArrayList<String>();
		for(ASTNode node: v_modifiers)
		{
			modifiers.add(node.toString());
		}
		name = v_name;
		superclass = v_superclass;
		interfaces = new ArrayList<String>();
		for(ASTNode node: v_interfaces)
		{
			modifiers.add(node.toString());
		}
		javadoc = v_javadoc;
	}
}
