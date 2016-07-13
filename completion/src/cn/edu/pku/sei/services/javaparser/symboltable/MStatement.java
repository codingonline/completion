package cn.edu.pku.sei.services.javaparser.symboltable;

import java.util.ArrayList;
import java.util.List;

public class MStatement 
{
	public int type;
	public int startposition = 0;
	public int endposition = 0;
	public static int FOR_STATEMENT = 0;
	public static int WHILE_STATMENT = 1;
	public static int IF_STATMENT = 2;
	public static int TRY_STATMENT = 3;
	public static int DO_STATMENT = 4;
	public static int BLOCK_STATMENT = 5;
	public static int SWITCH_STATEMENT = 6;
	public static int ELSE_STATEMENT = 7;
	public boolean forwithoutblock = false;
	public List<MVariable> variables = new ArrayList<MVariable>();
	public List<MStatement> statements = new ArrayList<MStatement>();
	public MStatement current = null;
	public MStatement belong = null;
	public MStatement(int v_type)
	{
		type = v_type;
	}
	
	public String findType(String name)
	{
		for(MVariable var: variables)
		{
			if(var.name.endsWith(name))
				return var.type;
		}
		if(belong != null)
			return belong.findType(name);
		return null;
	}
}
