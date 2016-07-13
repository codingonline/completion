package cn.edu.pku.sei.services.javaparser.symboltable;


import java.util.*;

import org.eclipse.jdt.core.dom.ASTNode;

public class MMethod 
{
	public List<String> modifiers = new ArrayList<String>();
	public String return_value;
	public String name;
	public List<MParameter> parameters = new ArrayList<MParameter>();
	public String javadoc;
	public MStatement statement = null;
	public int startPosition;
	public int bodystartPosition;
	public MMethod()
	{
		
	}
	public int getStartPosition()
	{
		return startPosition;
	}
	public void setStartPosition(int startPosition)
	{
		this.startPosition = startPosition;
	}
	public int getBodystartPosition()
	{
		return bodystartPosition;
	}
	public void setBodystartPosition(int bodystartPosition)
	{
		this.bodystartPosition = bodystartPosition;
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
	
	public String getReturn_value()
	{
		return return_value;
	}
	public void setReturn_value(String return_value)
	{
		this.return_value = return_value;
	}
	
	public String getJavadoc()
	{
		return javadoc;
	}
	public void setJavadoc(String javadoc)
	{
		this.javadoc = javadoc;
	}
	
	public List<MParameter> getParameters()
	{
		return 	parameters;
	}
	public void setParameters(List<MParameter> parameters)
	{
		this.parameters = parameters;
	}
	public MMethod(List<String> modifiers, String return_value, String name, List<MParameter> parameters)
	{
		this.modifiers = modifiers;
		this.return_value = return_value;
		this.name = name;
		this.parameters = parameters;
	}
	public MMethod(List<ASTNode> v_modifiers, String v_return, String v_name, List<ASTNode> v_parameters, String v_javadoc, int v_startPosition, int v_bodystartPosition)
	{
		modifiers = new ArrayList<String>();
		for(ASTNode node: v_modifiers)
		{
			modifiers.add(node.toString());
		}
		return_value = v_return;
		name = v_name;
		parameters = new ArrayList<MParameter>();
		for(ASTNode node: v_parameters)
		{
			String para = node.toString();
			String type = para.substring(0,para.indexOf(" "));
			String name = para.substring(para.indexOf(" ")+1);
			int startPosition = node.getStartPosition();
			parameters.add(new MParameter(type,name,startPosition));
		}
		javadoc = v_javadoc;
		startPosition = v_startPosition;
		bodystartPosition = v_bodystartPosition;
	}
}
