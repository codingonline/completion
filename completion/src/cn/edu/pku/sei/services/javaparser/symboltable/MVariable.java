package cn.edu.pku.sei.services.javaparser.symboltable;


import java.util.*;

import org.eclipse.jdt.core.dom.ASTNode;

public class MVariable {
	public List<String> modifiers = new ArrayList<String>();
	public String type ;
	public String name;
	public String javadoc;
	public int startPosition;
	public MVariable()
	{
	}
	public MVariable(List<String> modifiers, String type, String name, int startPosition)
	{
		this.modifiers = modifiers;
		this.type = type;
		this.name = name;
		this.startPosition = startPosition;
	}
	public int getStartPosition()
	{
		return startPosition;
	}
	public void setStartPosition(int startPosition)
	{
		this.startPosition = startPosition;
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
	
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getJavadoc()
	{
		return javadoc;
	}
	public void setJavadoc(String javadoc)
	{
		this.javadoc = javadoc;
	}
	
	public MVariable(String v_type, String v_name, int v_startPosition)
	{
		modifiers = new ArrayList<String>();
		type = v_type;
		name = v_name;
		javadoc = "";
		startPosition = v_startPosition;
	}
	
	public String toString()
	{
		return type + ":" + name;
	}
	
	public MVariable(List<ASTNode> v_modifiers, String v_type, String v_name, String v_javadoc, int startPosition)
	{
		modifiers = new ArrayList<String>();
		for(ASTNode node: v_modifiers)
		{
			modifiers.add(node.toString());
		}
		type = v_type;
		name = v_name;
		javadoc = v_javadoc;
		this.startPosition = startPosition;
	}
}
