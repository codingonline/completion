package cn.edu.pku.sei.services.phpparser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.core.ast.nodes.*;

import cn.edu.pku.sei.services.phpparser.PHPParser;
import cn.edu.pku.sei.services.phpparser.ParseFolder;


public class Test 
{

	public static void main(String args[])
	{
		PHPParser parser = new PHPParser();
		System.out.print(parser.getType(parser.inputfile("hello"),parser.sourceCode.length()-1));
	}
}
