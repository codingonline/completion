package cn.edu.pku.sei.services.javaparser;

import org.eclipse.jdt.core.dom.*;

public class MethodVisitor extends ASTVisitor{
	public boolean visie(Expression node)
	{
		//System.out.println(node);
		return true;
	}
	public boolean visit(MethodDeclaration node) //���ʺ�������
	{
		MethodDeclaration a = node;
		a.getBody().getStartPosition();
		//System.out.println(a.getBody().getStartPosition());
		return true;
	}

}
