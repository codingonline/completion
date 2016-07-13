package cn.edu.pku.sei.services.phpparser;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import java_cup.runtime.Symbol;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.*;
import org.eclipse.php.internal.core.ast.rewrite.TokenScanner;

import cn.edu.pku.sei.services.phpparser.phpSymbolTable.*;

import com.alibaba.fastjson.JSON;


public class PHPParser 
{
	public MProgram program;
	public ASTParser ast;
	public Program root;
	public String sourceCode;
	public char[] inputfile(String filename) 
	{
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(fs));
		StringBuffer sb = new StringBuffer();
		String line;  
        try {
			while ((line = reader.readLine()) != null) 
			{  
				sb.append(line + "\n"); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    //System.out.println(javafile);
	    try {
			fs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    sourceCode = sb.toString();
		return sourceCode.toCharArray();
	}
	public void init(String path)
	{
		ast = ASTParser.newParser(PHPVersion.PHP7_0);
		BuildSymbolTableVisitor build = new BuildSymbolTableVisitor();
		try {
			ast.setSource(inputfile(path));
			root = ast.createAST(null);
			System.out.println(root);
			root.accept(build);
			program = build.program;
			for(Comment comment: root.comments())
			{
				addComment(comment);
			}
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void init(char[] content)
	{
		ast = ASTParser.newParser(PHPVersion.PHP7_0);
		BuildSymbolTableVisitor build = new BuildSymbolTableVisitor();
	    sourceCode = String.valueOf(content);
		try {
			ast.setSource(content);
			root = ast.createAST(null);
			root.accept(build);
			program = build.program;
			for(Comment comment: root.comments())
			{
				addComment(comment);
			}
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String toJson(String path)
	{
		ast = ASTParser.newParser(PHPVersion.PHP7_0);
		BuildSymbolTableVisitor build = new BuildSymbolTableVisitor();
		try {
			ast.setSource(inputfile(path));
			root = ast.createAST(null);
			root.accept(build);
			program = build.program;
			for(Comment comment: root.comments())
			{
				addComment(comment);
			}
			} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JSON.toJSONString(program, true);
	}
	public void addComment(Comment comment)
	{
		if(comment.getCommentType() != Comment.TYPE_PHPDOC)
			return;
		int start = comment.getStart();
		int end = comment.getEnd();
		for(MClass mclass:program.classes)
		{
			if(mclass.start >= end)
			{
				int bt_start = end;
				int bt_end = mclass.start;
				String between = sourceCode.substring(bt_start,bt_end);
				if(between.matches("[ \n]*"))
				{
					String content = sourceCode.substring(start, end);
					mclass.comment = content;
					break;
				}
			}
			for(MMethod function:mclass.methods)
			{
				if(function.start >= end)
				{
					int bt_start = end;
					int bt_end = function.start;
					String between = sourceCode.substring(bt_start,bt_end);
					if(between.matches("[ \n]*"))
					{
						String content = sourceCode.substring(start, end);
						function.comment = content;
						break;
					}
				}
			}
		}
		for(MMethod function:program.methods)
		{
			if(function.start >= end)
			{
				int bt_start = end;
				int bt_end = function.start;
				String between = sourceCode.substring(bt_start,bt_end);
				if(between.matches("[ \n]*"))
				{
					String content = sourceCode.substring(start, end);
					function.comment = content;
					break;
				}
			}
		}
	}
	
	public String getType(char[] src, int position)
	{
		init(src);
		String source = sourceCode.substring(0, position);
		Reader reader = new StringReader(source);
		org.eclipse.php.internal.core.ast.scanner.php7.PhpAstLexer lexer;
		lexer = new org.eclipse.php.internal.core.ast.scanner.php7.PhpAstLexer(reader);
		try {
			TokenScanner scanner = new TokenScanner(lexer, source.toCharArray());
			scanner.setOffset(position-30);
			List<Symbol> tokens = new ArrayList<Symbol>();
			while(true)
			{
				Symbol token = null;
				try {
					token = scanner.readNext();
					tokens.add(token);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					break;
				}
			}
			int index = tokens.size()-1;
			Symbol token = tokens.get(index);
			if(token.sym != 56)
				return null;
			
			scanner = new TokenScanner(lexer, source.toCharArray());
			scanner.setOffset(position-4);
			scanner.readToToken(token);
			
			int start = scanner.getCurrentStartOffset();
			int length = scanner.getCurrentLength();
			
			index--;
			token = tokens.get(index);
			if(token.value == null)
				return null;
			String name = (String) token.value;
			name = name.substring(name.indexOf("$") + 1);
			
			for(MClass mclass:program.classes)
			{
				if(mclass.start < start && (mclass.start + mclass.length) > (start + length))
				{
					for(MMethod method: mclass.methods)
					{
						if(method.start < start && (method.start + method.length) > (start + length))
						{
							String type = null;
							for(MVariable var: method.variables)
							{
								if(var.name.equals(name) && (var.start + var.length) < start)
									type = var.type;
							}
							return type;
						}
					}
				}
			}
			
			for(MMethod method: program.methods)
			{
				if(method.start < start && (method.start + method.length) > (start + length))
				{
					String type = null;
					for(MVariable var: method.variables)
					{
						if(var.name.equals(name) && (var.start + var.length) < start)
							type = var.type;
					}
					return type;
				}
			}
			
			String type = null;
			for(MVariable var: program.variables)
			{
				if(var.name.equals(name) && (var.start + var.length) < start)
					type = var.type;
			}
			return type;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
