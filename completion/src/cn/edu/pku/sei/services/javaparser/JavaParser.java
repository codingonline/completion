package cn.edu.pku.sei.services.javaparser;
import java.io.*;
import java.util.*;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import cn.edu.pku.sei.services.javaparser.symboltable.MClass;
import cn.edu.pku.sei.services.javaparser.symboltable.MMethod;
import cn.edu.pku.sei.services.javaparser.symboltable.MParameter;
import cn.edu.pku.sei.services.javaparser.symboltable.MVariable;



public class JavaParser 
{
	private ASTParser parser;
	private CompilationUnit cu;
	public BuildSymbolTableVisitor build;
	private String javafile;
	public JavaParser()
	{
		parser = ASTParser.newParser(AST.JLS4); //閿熸枻鎷烽敓鏂ゆ嫹Java閿熸枻鎷烽敓鐨嗚鑼冮敓鑺ユ湰
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
				
		Map<String, String> compilerOptions = JavaCore.getOptions();
		compilerOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7); //閿熸枻鎷烽敓鏂ゆ嫹Java閿熸枻鎷烽敓鐨嗙増鏈�		compilerOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		compilerOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		parser.setCompilerOptions(compilerOptions); //閿熸枻鎷烽敓鐭唻鎷烽敓鏂ゆ嫹閫夐敓鏂ゆ嫹
	}
	
	public String toJson(String absolutePath, String projectPath, String commonPath, String jarPath, String MavenPath)
	{
		char[] src = inputfile(projectPath + absolutePath);
		parser.setSource(src);
		cu = (CompilationUnit) parser.createAST(null); //閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熺祤ProgessMonitor,閿熸枻鎷烽敓鏂ゆ嫹GUI閿熶茎鏂ゆ嫹閿熸枻鎷烽敓缁烇拷閿熸枻鎷烽敓瑙掕鎷烽敓鏂ゆ嫹瑕侀敓鏂ゆ嫹閿熸枻鎷烽敓绲ll. 閿熸枻鎷烽敓鏂ゆ嫹鍊奸敓鏂ゆ嫹AST閿熶茎闈╂嫹閿熸枻鎷�		BuildSymbolTableVisitor build = new BuildSymbolTableVisitor(); // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻娉曢敓鏂ゆ嫹
		build = new BuildSymbolTableVisitor();
		cu.accept(build);
		Linker.analysis(absolutePath, projectPath, commonPath, jarPath, MavenPath,build.classes);
		return build.toJSON2();
	}

	private char[] inputfile(String filename) // 閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹閿熸枻鎷穋har[]
	{
		FileInputStream input = null;
		try {
			input = new FileInputStream(new File(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Scanner sc = new Scanner(input);
		 StringBuffer sb = new StringBuffer("");
		 while(sc.hasNext())
		 {
			 sb.append(sc.nextLine()+"\n");
		 }
		 javafile = sb.toString();
		 return sb.toString().toCharArray();
	}
	public String getType(char[] src, int position,String absolutePath, String projectPath, String commonPath, String jarPath, String MavenPath)
	{
		parser.setSource(src);
		this.javafile = new String(src);
		cu = (CompilationUnit) parser.createAST(null); //閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熺祤ProgessMonitor,閿熸枻鎷烽敓鏂ゆ嫹GUI閿熶茎鏂ゆ嫹閿熸枻鎷烽敓缁烇拷閿熸枻鎷烽敓瑙掕鎷烽敓鏂ゆ嫹瑕侀敓鏂ゆ嫹閿熸枻鎷烽敓绲ll. 閿熸枻鎷烽敓鏂ゆ嫹鍊奸敓鏂ゆ嫹AST閿熶茎闈╂嫹閿熸枻鎷�		BuildSymbolTableVisitor build = new BuildSymbolTableVisitor(); // 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻娉曢敓鏂ゆ嫹
		build = new BuildSymbolTableVisitor();
		cu.accept(build);
		Linker.analysis(absolutePath, projectPath, commonPath, jarPath, MavenPath, build.classes);
		return getMethod(position);
	}
	public String getMethod(int position)
	{
		int max = 0;
		int min = javafile.length();
		MMethod method = null;
		for(MClass t_class: build.classes.classes)
		{
			for(MMethod t_method: t_class.methods)
			{
				if(t_method.bodystartPosition <= position)
				{
					if(max < t_method.bodystartPosition)
					{
						method = t_method;
						max = t_method.bodystartPosition;
					}
				}
				if(t_method.startPosition >= position)
				{
					if(min > t_method.startPosition)
						min = t_method.startPosition;
				}
			}
		}
		String methodbody = javafile.substring(max, position);
		MethodParser mp = new MethodParser();
		mp.parseMethod(methodbody.toCharArray(), max);
		List<String> list = mp.findPosition();
//		System.out.println(list);
		String type = list.get(0);
		if(type.startsWith("##"))
		{
			type = type.substring(type.indexOf("##") + 2);
			for(String im: this.build.classes.imports)
			{
				if(im.endsWith("." + type))
				{
					type = im;
					break;
				}
			}
		}
		else
		{
			for(MParameter var:method.parameters)
			{
				if(var.name.equals(type))
					type = var.type;
			}
			for(MVariable var:this.build.classes.getClasses().get(0).variables)
			{
				if(var.name.equals(type))
					type = var.type;
			}
		}
		if(type.startsWith("."))
			type = type.substring(1);
		if(list.size() < 3)
			return type;
		return type;
	}
	
	public List<String> getParameterTypes(int position)
	{
		List<String> _ret = new ArrayList<String>();
		MethodParser mp = new MethodParser();

		MMethod method = getCurrentMethod(position,mp);
		if(method==null)
			return null;
		List<String> list= mp.getParameters();
		if(list == null || list.size() == 0)
			return list;
		//System.out.println(list);
		for(String type: list){
			if(type.startsWith("##"))
			{
				type = type.substring(type.indexOf("##") + 2);
				for(String im: this.build.classes.imports)
				{
					if(im.endsWith("." + type))
					{
						type = im;
						break;
					}
				}
			}
			else
			{
				for(MParameter var:method.parameters)
				{
					if(var.name.equals(type))
						type = var.type;
				}
				for(MVariable var:this.build.classes.getClasses().get(0).variables)
				{
					if(var.name.equals(type))
						type = var.type;
				}
			}
			_ret.add(type);
		}
		
		return _ret;
	}
	
	public void buildCode(char[] src, int position,String absolutePath, String projectPath, String commonPath, String jarPath, String MavenPath){
		parser.setSource(src);
		this.javafile = new String(src);
		cu = (CompilationUnit) parser.createAST(null); 
		build = new BuildSymbolTableVisitor();
		cu.accept(build);
		Linker.analysis(absolutePath, projectPath, commonPath, jarPath, MavenPath, build.classes);
	}
	
	private MMethod getCurrentMethod(int position, MethodParser mp){
		int max = 0;
		int min = javafile.length();
		MMethod method = null;
		for(MClass t_class: build.classes.classes)
		{
			for(MMethod t_method: t_class.methods)
			{
				if(t_method.bodystartPosition <= position)
				{
					if(max < t_method.bodystartPosition)
					{
						method = t_method;
						max = t_method.bodystartPosition;
					}
				}
				if(t_method.startPosition >= position)
				{
					if(min > t_method.startPosition)
						min = t_method.startPosition;
				}
			}
		}
		String methodbody = javafile.substring(max, position);
		mp.parseMethod(methodbody.toCharArray(), max);
		return method;
	}
	
	public int getLocalVarDeclarePositon(int position, String varName)
	{
		int declarePosition = -1;
		MethodParser mp = new MethodParser();

		MMethod method = getCurrentMethod(position,mp);
		if(method==null)
			return declarePosition;
		declarePosition = mp.findVariablePosition(varName);
		//System.out.println(list);
	    if(declarePosition>=0)
	    	return declarePosition;
		else
		{
			for(MParameter var:method.getParameters())
			{
				if(var.name.equals(varName))
					return var.getStartPosition();
				
			}
			for(MVariable var:this.build.classes.getClasses().get(0).getVariables())
			{
				if(var.name.equals(varName))
					return var.getStartPosition();
			}
		}
	    return declarePosition;
	}
	
	public int getLocalMethodDeclarePositon(String methodName, List<String> parameterTypes)
	{
		int declarePosition = -1;
		for(MMethod method:this.build.classes.getClasses().get(0).getMethods())
		{
			if(method.name.equals(methodName)){
				int j =0 ;
				List<MParameter> params = method.getParameters();
				//System.out.println(parameterTypes);
				if(params.size() == parameterTypes.size()){
					for(j = 0; j < params.size(); j++){
						String callType = parameterTypes.get(j);
						String declareType = params.get(j).getType();
						if(!callType.equals("*")&& !callType.equals(declareType))
							break;
					}
					if(j == params.size()){
						declarePosition = method.getStartPosition();	
						
					}
				}
			}
		
		}
	    return declarePosition;
	}
	
	public int checkMethodDeclaration(int position){
		int _ret =-1;
		for(MClass t_class: build.classes.classes)
		{
			for(MMethod t_method: t_class.methods)
			{
				if(t_method.bodystartPosition >= position && t_method.startPosition <= position)
					return t_method.startPosition;
				
			}
		}
		return _ret;
	}
	
	public String getSuperClassName(){
		return this.build.classes.getClasses().get(0).getSuperclass();
	}
	
	public String findImportClass(String name){
		for(String im: this.build.classes.imports)
		{
			if(im.endsWith("." + name))
			{
				return im;
			}
		}
		return null;
	}
}