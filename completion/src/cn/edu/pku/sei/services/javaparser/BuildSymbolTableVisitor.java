package cn.edu.pku.sei.services.javaparser;


import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import cn.edu.pku.sei.services.javaparser.symboltable.MClasses;

import com.alibaba.fastjson.JSON;

public class BuildSymbolTableVisitor extends ASTVisitor
{
	public MClasses classes = new MClasses();
	public boolean visit(ImportDeclaration node)
	{
		String t_imports = node.toString();
		t_imports = t_imports.substring(t_imports.indexOf(" ")+1,
				t_imports.indexOf(";"));
		classes.imports.add(t_imports);
		return true;
	}
	public boolean visit(TypeDeclaration node) // 访问类定义
	{
		List<ASTNode> modifiers = node.modifiers();
		String name = node.getName().toString();
		String superclass = null;
		try{
			superclass = node.getSuperclassType().toString();
		}catch(NullPointerException e){}
		List<ASTNode> interfaces = node.superInterfaceTypes();
		String javadoc = null;
		try{
			javadoc = node.getJavadoc().toString();
		}catch(NullPointerException e){}
		classes.insertClass(modifiers, name, superclass, interfaces, javadoc);
		return true;
	}
	public boolean visit(MethodDeclaration node) //访问函数定义
	{
		//System.out.println(node.getStartPosition());
		//System.out.println(node.getBody().getStartPosition());
		List<ASTNode> modifiers = node.modifiers();
		String return_value = null; 
		try{
			return_value = node.getReturnType2().toString();
		}catch(NullPointerException e){}
		String name = node.getName().toString();
		List<ASTNode> parameters = node.parameters();
		String javadoc = null;
		try{
			javadoc = node.getJavadoc().toString();
		}catch(NullPointerException e){}
		classes.insertMethod(modifiers, return_value, name, parameters, javadoc, node.getStartPosition(), node.getBody().getStartPosition());
		return true;
	}
	
	public boolean visit(VariableDeclarationFragment node)//访问变量定义
	{
		TypeDeclaration pp = null;
		try
		{
			pp = (TypeDeclaration) node.getParent().getParent();
		}catch(ClassCastException e){return false;} // 如果不是类的变量就返回
		FieldDeclaration p = (FieldDeclaration) node.getParent();
		List<ASTNode> modifiers = p.modifiers();
		String type = p.getType().toString();
		String name = node.getName().toString();
		String javadoc = null;
		int startPosition = node.getStartPosition();
		try{
			javadoc = p.getJavadoc().toString();
		}catch(NullPointerException e){}
		classes.insertVariable(modifiers, type, name, javadoc,startPosition);
		return true;
	}
	public String toJSON2()
	{
		String jsonString = JSON.toJSONString(classes, true);
		return jsonString;
	}
}
