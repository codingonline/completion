package cn.edu.pku.sei.services.phpparser.phpSymbolTable;

import java.util.*;

public class MMethod implements Comparable<MMethod>
{
	public String name;
	public List<String> modifiers;
	public int start;
	public int length;
	public boolean isReference;
	public List<MFormalParameter> formalParameters;
	public List<MVariable> variables = new ArrayList<MVariable>();
	public String comment;
	public MMethod(String _name, List<String> _modifier,int _start, int _length, boolean _isReference)
	{
		name = _name;
		modifiers = _modifier;
		start = _start;
		length = _length;
		isReference = _isReference;
		formalParameters = new ArrayList<MFormalParameter>();
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
	public void setIsReference(boolean isReference)
	{
		this.isReference = isReference;
	}
	public boolean getIsReference()
	{
		return isReference;
	}
	public void setFormalParameters(List<MFormalParameter> formalParameters)
	{
		this.formalParameters = formalParameters;
	}
	public List<MFormalParameter> getFormalParameters()
	{
		return formalParameters;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	public String getComment()
	{
		return comment;
	}
	@Override
	public int compareTo(MMethod o) 
	{
		return name.compareTo(o.name);
	}
	
	public String toString()
	{
		return name;
	}
}
