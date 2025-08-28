package prolixa;
import prolixa.lexer.*;
import prolixa.node.*;
import prolixa.parser.*;
import java.io.*;

public class Main
{
	public static void main(String[] args)
	{
		try
		{
			String arquivo = "testcode/test.prlx";

			Parser p = 
					new Parser(
					new Lexer(
				    new PushbackReader(  
				    new FileReader(arquivo), 1024))
			); 
				    	   
		    Start tree = p.parse();

		    tree.apply(new ASTDisplay());
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}