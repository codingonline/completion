package cn.edu.pku.sei.services.javaparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

import cn.edu.pku.sei.services.javaparser.symboltable.MStatement;
import cn.edu.pku.sei.services.javaparser.symboltable.MVariable;

class Token
{
	int index;
	int startposition;
	int endposition;
	String type;
	int act;
}


public class MethodParser 
{
	MStatement ms = null;
	List<String> tokens = new ArrayList<String>();
	List<Token> tokensmap = new ArrayList<Token>();
	public boolean tokenistype(String token)
	{
		if(token.startsWith("Identifier(") ||token.equals("int") || token.equals("boolean") 
				|| token.equals("double") || token.equals("float")
				|| token.equals("char") || token.equals("byte") 
				|| token.equals("long") || token.equals("short"))
			return true;
		return false;
	}
	
	public boolean tokeniskeyword(String token)
	{
		if(token.equals("abstract") || token.equals("break") || token.equals("case")
				|| token.equals("catch") || token.equals("class") || token.equals("continue")
				|| token.equals("defalut") || token.equals("do") || token.equals("else")
				|| token.equals("extends") || token.equals("final") || token.equals("finally")
				|| token.equals("for") || token.equals("if") || token.equals("implements") 
				|| token.equals("implements") || token.equals("import") || token.equals("instanceof")
				|| token.equals("interface") || token.equals("native") || token.equals("package")
				|| token.equals("private") || token.equals("protected") || token.equals("public")
				|| token.equals("return") || token.equals("static") || token.equals("super")
				|| token.equals("switch") || token.equals("synchronized") || token.equals("throw")
				|| token.equals("throws") || token.equals("transient") || token.equals("try")
				|| token.equals("void") || token.equals("while") || token.equals("int") 
				|| token.equals("boolean") || token.equals("double") || token.equals("float")
				|| token.equals("char") || token.equals("byte")
				|| token.equals("long") || token.equals("short"))
			return true;
		return false;
	}
	
	public void findvariable(MStatement ms, List<String> tokens, int index)
	{
		String type = null;
		String type2 = null;
		boolean isdec = false;
		int typeindex = index - 1;
		int arraysize = 0;
		if(tokens.get(typeindex).equals(">"))
		{
			typeindex--;
			if(tokenistype(tokens.get(typeindex)))
			{
				type2 = tokens.get(typeindex);
				type2 = type2.substring(type2.indexOf("(")+1, type2.indexOf(")"));
				type2 = "<" + type2 + ">";
				typeindex--;
			}
			if(tokens.get(typeindex).equals("<"))
				typeindex--;
		}
		if(tokens.get(typeindex).equals("]"))
		{
			arraysize++;
			typeindex--;
			while(tokens.get(typeindex).equals("[") || tokens.get(typeindex).equals("]"))
			{
				if(tokens.get(typeindex).equals("]"))
					arraysize++;
				typeindex--;
			}
		}
		if(tokenistype(tokens.get(typeindex)))
		{
			if(tokens.get(typeindex).startsWith("Identifier("))
				type = tokens.get(typeindex).substring(tokens.get(typeindex).indexOf("(")+1,
					tokens.get(typeindex).indexOf(")"));
			else
				type = tokens.get(typeindex);
			if(type2 != null)
			{
				type = type + type2;
			}
			for(int i = 0 ; i < arraysize; i++)
			{
				type = type + "[]";
			}
			if(type.endsWith("Exception"))
				return;
			String name = tokens.get(index).substring(tokens.get(index).indexOf("(")+1,
					tokens.get(index).indexOf(")"));
			int startPosition = tokensmap.get(index).startposition;
			ms.current.variables.add(new MVariable(type,name,startPosition));
			ms.current.variables.add(new MVariable(type,name,startPosition));
			isdec = true;
		}
		if(isdec)
		{
			int tindex = index;
			while(tindex < tokens.size())
			{
				if(tokens.get(tindex).equals(";") || tokens.get(tindex).equals("{")
						|| tokens.get(tindex).equals("}"))
				{
					index = tindex + 1;
					break;
				}
				if(tokeniskeyword(tokens.get(tindex)))
				{
					index = tindex;
					break;
				}
				if(tokens.get(tindex).startsWith("Identifier(") && 
						tokens.get(tindex-1).equals(","))
				{
					String name = tokens.get(tindex).substring(tokens.get(tindex).indexOf("(")+1,
							tokens.get(tindex).indexOf(")"));
					int startPosition = tokensmap.get(tindex).startposition;
					ms.current.variables.add(new MVariable(type,name,startPosition));
				}
				tindex++;
			}
		}
	}
	
	public MStatement parseMethod(char[] src, int methodbodyoffset)
	{
		Scanner tokenscanner = new Scanner();
		tokenscanner.setSource(src);
		try {
			int index = 0;
			while(true)
			{
				int token = tokenscanner.getNextToken();
				String type = tokenscanner.toStringAction(token);
				tokens.add(type);
				Token ttoken = new Token();
				ttoken.act = token;
				ttoken.type = type;
				ttoken.index = index;
				ttoken.startposition = methodbodyoffset + tokenscanner.getCurrentTokenStartPosition();
				ttoken.endposition = methodbodyoffset + tokenscanner.getCurrentTokenEndPosition();
				tokensmap.add(ttoken);
				index++;
				if(token == 60)
					break;
			}
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(tokens);
		int index = 1;
		ms = new MStatement(MStatement.BLOCK_STATMENT);
		ms.current = ms;
		while(index < tokens.size())
		{
			boolean isnotfor = true; 
			if(tokens.get(index).startsWith("Identifier("))
			{
				findvariable(ms,tokens,index);
			}
			if(tokens.get(index).equals("for"))
			{
				MStatement tempms = new MStatement(MStatement.FOR_STATEMENT);
				tempms.startposition = tokensmap.get(index).startposition;
				ms.current.statements.add(tempms);
				tempms.belong = ms.current;
				ms.current = tempms;
				int tindex = index + 1;
				int count = 0;
				while(tindex < tokens.size())
				{
					if(tokens.get(tindex).startsWith("Identifier("))
					{
						findvariable(ms,tokens,tindex);
					}
					if(tokens.get(tindex).equals("("))
						count++;
					if(tokens.get(tindex).equals(")"))
					{
						count--;
						if(count < 1)
						{
							if(tokens.get(tindex+1).equals("{"))
							{
								index = tindex + 1;
								isnotfor = false;
								break;
							}
							else
							{
								index = tindex;
								ms.current = ms.current.belong;
								break;
							}
						}
					}
					tindex++;
				}
			}
			if(tokens.get(index).equals("{") && isnotfor)
			{
				MStatement tempms = new MStatement(MStatement.BLOCK_STATMENT);
				tempms.startposition = tokensmap.get(index).startposition;
				ms.current.statements.add(tempms);
				tempms.belong = ms.current;
				ms.current = tempms;
			}
			if(tokens.get(index).equals("}"))
			{
				ms.current.endposition =  tokensmap.get(index).endposition;
				if(ms.current.belong == null)
					break;
				ms.current = ms.current.belong;
			}
			index++;
		}
		//System.out.println(ms.variables);
		return ms;
	}
	
	
	public List<String> findPosition()
	{
		List<String> _ret = null;
		Token lastToken = tokensmap.get(tokensmap.size()-2);
		int index = lastToken.index;
		int turn = 0;
		if(turn == 0 && tokens.get(index).equals(")"))
		{
			int stack = 1;
			while(index >= 0)
			{
				index--;
				if(tokens.get(index).equals(")"))
					stack++;
				if(tokens.get(index).equals("("))
					stack--;
				if(stack == 0)
				{
					index--;
					break;
				}
			}
		}
		if(tokens.get(index).equals("."))
		{
			turn = 0;
			_ret = new ArrayList<String>();
			_ret.add(tokens.get(index));
		}
		else if(tokens.get(index).startsWith("Identifier("))
		{
			turn = 1;
			String temp = tokens.get(index).substring(tokens.get(index).indexOf("(")+1,
					tokens.get(index).indexOf(")"));
			_ret = new ArrayList<String>();
			_ret.add(temp);
		}
		else
			return null;
		while(index >= 0)
		{
			index--;
			if(turn == 0 && tokens.get(index).equals(")"))
			{
				int stack = 1;
				while(index >= 0)
				{
					index--;
					if(tokens.get(index).equals(")"))
						stack++;
					if(tokens.get(index).equals("("))
						stack--;
					if(stack == 0)
					{
						index--;
						break;
					}
				}
			}
			if(turn == 0 && tokens.get(index).startsWith("Identifier"))
			{
				turn = 1;
				String temp = tokens.get(index).substring(tokens.get(index).indexOf("(")+1,
						tokens.get(index).indexOf(")"));
				_ret.add(temp);
			}
			else if(turn == 1 && tokens.get(index).equals("."))
			{
				turn = 0;
				_ret.add(".");
			}
			else
				break;
		}
		List<String> real_ret = new ArrayList<String>();
		for(index = _ret.size()-1; index >=0; index--)
		{
			if(index == _ret.size()-1)
			{
				String temp = ms.current.findType(_ret.get(index));
				if(temp != null)
				{
					temp = "##" + temp;
					real_ret.add(temp);
					continue;
				}
			}
			real_ret.add(_ret.get(index));
		}
		return real_ret;
	}
	
	public List<String> getParameters(){
		List<String> _ret = new ArrayList<String>();
		Token lastToken = tokensmap.get(tokensmap.size()-2);
		int index = lastToken.index;
		while(index >= 0 && !tokens.get(index).equals(")")){
			index--;
		}
		index--;
		while(index >= 0 && !tokens.get(index).equals("(")){
			if(index >= 1 && (tokens.get(index-1).equals("(") || tokens.get(index-1).equals(","))){
				if(tokens.get(index).startsWith("Identifier") && 
						(tokens.get(index+1).equals(")") || tokens.get(index+1).equals(","))){
					String temp = tokens.get(index).substring(tokens.get(index).indexOf("(")+1,
							tokens.get(index).indexOf(")"));
					_ret.add(temp);
				}
				else{
					_ret.add("*");
				}
			}
			index--;
		}
		index--;
		if(!tokens.get(index).startsWith("Identifier")){
			System.out.println(tokens.get(index));
			return null;
		}
		
		
		List<String> real_ret = new ArrayList<String>();
		for(index = _ret.size()-1; index >=0; index--)
		{
			if(!_ret.get(index).equals("*"))
			{
				String temp = ms.current.findType(_ret.get(index));
				if(temp != null)
				{
					temp = "##" + temp;
					real_ret.add(temp);
					continue;
				}
			}
			real_ret.add(_ret.get(index));
		}
		return real_ret;
	}
	
	public String getCurrentIdentifier(){
		Token lastToken = tokensmap.get(tokensmap.size()-2);
		int index = lastToken.index;
		while(!tokens.get(index).startsWith("Identifier")){
			index--;
		}
		return tokens.get(index).substring(tokens.get(index).indexOf("(")+1,
				tokens.get(index).indexOf(")"));
	}
	
	public int findVariablePosition(String varName){
		int position = -1;
		for(MVariable var:ms.current.variables){
			if(varName.equals(var.name))
				position = var.getStartPosition();
		}
		return position;
	}
	

}
