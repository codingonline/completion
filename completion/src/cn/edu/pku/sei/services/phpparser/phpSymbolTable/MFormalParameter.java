package cn.edu.pku.sei.services.phpparser.phpSymbolTable;

public class MFormalParameter 
{
	public String name;
	public String type;
	public String defaultValue;
	public String defaultValue_type;
	public int start;
	public int length;
	public boolean isMandatory;
	public MFormalParameter(String _name, String _type, String _defaultValue, 
			String _defaultVaule_type, int _start, int _length, boolean _isMandatory)
	{
		name = _name;
		type = _type;
		defaultValue = _defaultValue;
		defaultValue_type = _defaultVaule_type;
		start = _start;
		length = _length;
		isMandatory = _isMandatory;				
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getType()
	{
		return type;
	}
	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	public String getDefaultValue()
	{
		return defaultValue;
	}
	public void setDefaultValue_type(String defaultValue_type)
	{
		this.defaultValue_type = defaultValue_type;
	}
	public String getDefaultValue_type()
	{
		return defaultValue_type;
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
	public void setIsMandatory(boolean isMandatory)
	{
		this.isMandatory = isMandatory;
	}
	public boolean getIsMandatory()
	{
		return isMandatory;
	}
}
