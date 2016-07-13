package cn.edu.pku.sei.services.phpparser.phpSymbolTable;

import java.util.List;

public class MVariable 
{
	public String name;
	public String type;
	public List<String> modifiers;
	public int start;
	public int length;
	public boolean isDollared;
	public MVariable(String _name, String _type, List<String> _modifier, int _start, int _length, boolean _isDollared)
	{
		name = _name;
		type = _type;
		modifiers = _modifier;
		start = _start;
		length = _length;
		isDollared = _isDollared;
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
	public void setType(String type)
	{
		this.type = type;
	}
	public String getType()
	{
		return type;
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
	public void setIsDollared(boolean isDollared)
	{
		this.isDollared = isDollared;
	}
	public boolean getIsDollared()
	{
		return isDollared;
	}
}
