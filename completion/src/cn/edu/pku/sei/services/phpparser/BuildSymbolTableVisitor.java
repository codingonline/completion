package cn.edu.pku.sei.services.phpparser;
import java.util.*;

import org.eclipse.php.internal.core.ast.nodes.*;
import org.eclipse.php.internal.core.ast.visitor.HierarchicalVisitor;

import cn.edu.pku.sei.services.phpparser.phpSymbolTable.*;


public class BuildSymbolTableVisitor extends HierarchicalVisitor
{
	public MProgram program = new MProgram();
	private MMethod lastFunction;
	
	public boolean visit(FieldsDeclaration fieldsDeclaration) 
	{
		MClass mclass = program.classes.get(program.classes.size() - 1);
		
		String s_modifier = fieldsDeclaration.getModifierString();
		String[] s_modifiers = s_modifier.split(" ");
		List<String> modifiers = new ArrayList<String>();
		for(String modifier: s_modifiers)
		{
			modifiers.add(modifier);
		}
		List<SingleFieldDeclaration> singlefields = fieldsDeclaration.fields();
		for(SingleFieldDeclaration single: singlefields)
		{
			Variable var = single.getName();
			Expression value = single.getValue();
			
			String name = ((Identifier)var.getName()).getName();
			int start = var.getStart();
			int length = var.getLength();
			boolean isDollared = var.isDollared();
			
			String type = null;
			if(value != null && value.getType() == ASTNode.CLASS_INSTANCE_CREATION)
			{
				ClassInstanceCreation create = (ClassInstanceCreation)value;
				Expression className = create.getClassName().getName();
				if(className.getType() == ASTNode.NAMESPACE_NAME)
				{
					type = ((NamespaceName)className).getName();
				}
			}
			
			MVariable variable = new MVariable(name, type, modifiers, start, length, isDollared);
			
			mclass.fields.add(variable);
		}
		return visit((BodyDeclaration) fieldsDeclaration);
	}
	
	public boolean visit(FunctionDeclaration functionDeclaration) 
	{
		boolean inClass = (functionDeclaration.getParent().getType() == ASTNode.METHOD_DECLARATION);
		String name = functionDeclaration.getFunctionName().getName();
		List<String> modifiers = new ArrayList<String>();
		if(inClass)
		{
			String s_modifier = null;
			MethodDeclaration method = (MethodDeclaration)functionDeclaration.getParent();
			s_modifier = method.getModifierString();
			String[] s_modifiers = s_modifier.split(" ");
			for(String modifier: s_modifiers)
			{
				modifiers.add(modifier);
			}
		}
		int start = functionDeclaration.getStart();
		int length = functionDeclaration.getLength();
		boolean isReference = functionDeclaration.isReference();
		MMethod function = new MMethod(name, modifiers,start, length, isReference);
		for(FormalParameter formal:functionDeclaration.getFormalParameters())
		{
			name = formal.getParameterNameIdentifier().getName();
			start = formal.getStart();
			length = formal.getLength();
			boolean isMandatory = formal.isMandatory();
			
			Expression paratype = formal.getParameterType();
			String type = null;
			if(paratype != null)
			{
				if(paratype.getType() == ASTNode.IDENTIFIER)
					type = ((Identifier)paratype).getName();
				if(paratype.getType() == ASTNode.NAMESPACE_NAME)
					type = ((NamespaceName)paratype).getName();
			}
			
			String defaultvalue = null;
			String defaultvalue_type = null;
			Expression dvalue = formal.getDefaultValue();
			if(dvalue != null)
			{
				defaultvalue = ((Scalar)dvalue).getStringValue();
				defaultvalue_type = Scalar.getType(((Scalar)dvalue).getScalarType());
			}
			MFormalParameter formalpara = new MFormalParameter(name, type, defaultvalue, 
					defaultvalue_type, start, length, isMandatory);
			function.formalParameters.add(formalpara);
		}
		if(!inClass)
			program.methods.add(function);
		else
		{
			if(program.classes.size() > 0)
			{
				MClass mclass = program.classes.get(program.classes.size() - 1);
				mclass.methods.add(function);
			}
		}
		lastFunction = function;
		return visit((Statement) functionDeclaration);
	}
	public boolean visit(ClassDeclaration classDeclaration) 
	{
		String name = classDeclaration.getName().getName();
		String s_modifier = classDeclaration.getModifier(classDeclaration.getModifier());
		
		String[] s_modifiers = s_modifier.split(" ");
		List<String> modifiers = new ArrayList<String>();
		for(String modifier: s_modifiers)
		{
			modifiers.add(modifier);
		}
		
		List<Identifier> Iinterfaces = classDeclaration.interfaces();
		List<String> interfaces = new ArrayList<String>();
		for(Identifier iden:Iinterfaces)
		{
			interfaces.add(iden.getName());
		}
		
		String superclass = null;
		Expression EsuperClass = classDeclaration.getSuperClass();
		if(EsuperClass != null)
		{
			superclass = ((NamespaceName)EsuperClass).getName();
		}
		
		int start = classDeclaration.getStart();
		int length = classDeclaration.getLength();
		
		MClass mclass = new MClass(name, modifiers, superclass, interfaces, start, length);
		program.classes.add(mclass);
		
		boolean _ret = visit((TypeDeclaration) classDeclaration);
		return _ret;
	}
	
	public boolean visit(ExpressionStatement expressionStatement) 
	{
		ASTNode father = expressionStatement.getParent();
		if(father.getType() != ASTNode.PROGRAM)
		{
			ASTNode grandFather = father.getParent();
			if(grandFather.getType() != ASTNode.FUNCTION_DECLARATION)
				return visit((Statement) expressionStatement);
		}

		Expression exp = expressionStatement.getExpression();
		if(exp.getType() != ASTNode.ASSIGNMENT)
			return visit((Statement) expressionStatement);
		
		Assignment assign = (Assignment)exp;
		
		VariableBase left = assign.getLeftHandSide();
		Expression right = assign.getRightHandSide();
		
		if(!(left.getType() == ASTNode.VARIABLE))
			return visit((Statement) expressionStatement);
		
		Variable variable = (Variable)left;
		Expression Ename = variable.getName();
		if(Ename.getType() != ASTNode.IDENTIFIER)
			return visit((Statement) expressionStatement);
		String name = ((Identifier)Ename).getName();
		MVariable var = null;
		List<MVariable> variables = null;
		if(father.getType() == ASTNode.PROGRAM)
		{
			variables = program.variables;
		}
		else
		{
			variables = lastFunction.variables;
		}
		String type = null;
		List<String> modifiers = new ArrayList<String>();
		int start = variable.getStart();
		int length = variable.getLength();
		boolean isDollared = variable.isDollared();
		if(var == null)
		{
			var = new MVariable(name, type, modifiers, start, length, isDollared);
			variables.add(var);
		}
		
		
		if(right.getType() == ASTNode.CLASS_INSTANCE_CREATION)
		{
			ClassInstanceCreation create = (ClassInstanceCreation)right;
			Expression className = create.getClassName().getName();
			if(className.getType() == ASTNode.NAMESPACE_NAME)
			{
				type = ((NamespaceName)className).getName();
			}
			var.setType(type);
		}
		if(right.getType() == ASTNode.VARIABLE)
		{
			Variable another = (Variable)right;
			Expression EanoName = another.getName();
			if(EanoName.getType() == ASTNode.IDENTIFIER)
			{
				String anoName = ((Identifier)EanoName).getName();
				for(MVariable t_var: program.variables)
				{
					if(t_var.getName().equals(anoName))
					{
						var.setType(t_var.getType());
					}
				}
			}
		}
		
		return visit((Statement) expressionStatement);
	}
	
	public boolean visit(Include include) 
	{
		Expression exp = include.getExpression();
		if(exp.getType() == ASTNode.PARENTHESIS_EXPRESSION)
		{
			ParenthesisExpression pexp = (ParenthesisExpression)exp;
			Expression sexp = pexp.getExpression();
			if(sexp.getType() == ASTNode.SCALAR)
			{
				String includeFile = ((Scalar)sexp).getStringValue();
				if(includeFile.startsWith("'") && includeFile.endsWith("'"))
					includeFile = includeFile.substring(1, includeFile.length() - 1);
				if(includeFile.startsWith("\"") && includeFile.endsWith("\""))
					includeFile = includeFile.substring(1, includeFile.length() - 1);
				program.includes.add(includeFile);
			}
		}
		if(exp.getType() == ASTNode.SCALAR)
		{
			String includeFile = ((Scalar)exp).getStringValue();
			if(includeFile.startsWith("'") && includeFile.endsWith("'"))
				includeFile = includeFile.substring(1, includeFile.length() - 1);
			if(includeFile.startsWith("\"") && includeFile.endsWith("\""))
				includeFile = includeFile.substring(1, includeFile.length() - 1);
			program.includes.add(includeFile);
		}
		//System.out.println(exp);
		return visit((Expression) include);
	}
}
