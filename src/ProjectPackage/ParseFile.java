package ProjectPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;


public class ParseFile {
	
	String result="", packageMembers="", addAfter="";
	Map<String, List<String>> implementsMap =  new HashMap<String,List<String>>();
	Map<String, List<String>> extendsMap =  new HashMap<String,List<String>>();
	Map<String, List<String>> methodsMap =  new HashMap<String,List<String>>();
	Map<String, List<String>> fieldsMap =  new HashMap<String,List<String>>();
	
	List<String> implementsList = new ArrayList<String>();
	List<String> extendsList = new ArrayList<String>();
	List<String> methodsList = new ArrayList<String>();
	List<String> fieldsList = new ArrayList<String>();	
	
	public void findElements(File file) throws IOException, ParseException{
//	public static void main(String args[])throws IOException, ParseException{	
	
	
		
	//	FileInputStream in = new FileInputStream("C:\\UML_Workspace\\uml-parser-test-2\\B2.java");

	//	System.out.println("File name : "+file);
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu ;
		
		try{
			cu = JavaParser.parse(in);
			System.out.println(cu.toString());
		} finally
		{
			in.close();
		}

	try{ for (TypeDeclaration type : cu.getTypes()) {
         if (type instanceof ClassOrInterfaceDeclaration) {
             ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) type;
             System.out.println("Class Found :  "+cid.getName());
             result += cid.getName() + "\n";
             result += "  color:lightblue\n";
             
             System.out.println(cid.getImplements());
             if (cid.getImplements() != null) {
                 for (ClassOrInterfaceType ici : cid.getImplements()) {
                     result += "  implements:" + ici.getName() + "\n";
                     addAfter += ici.getName() + "\n";
                     System.out.println("Test Check : "+ici.getName());
                     implementsList.add(ici.getName());
                     
                     if (cu.getImports() != null) {
                         for (ImportDeclaration id : cu.getImports()) {
                             if (id.toString().trim().endsWith("." + ici.getName() + ";")) {
                                 addAfter += "package " + cu.getPackage() + "\n";
                                 addAfter += "  has:" + ici.getName() + "\n";
                                 break;
                             }
                         }
                     }
                 }
             }
        	 
             implementsMap.put(cid.getName(), implementsList);
             
             System.out.println(cid.getExtends());
             if (cid.getExtends() != null) {
                 for (ClassOrInterfaceType eci : cid.getExtends()) {
                     result += "  extends:" + eci.getName() + "\n";
                     addAfter += eci.getName() + "\n";
                     extendsList.add(eci.getName());
                     if (cu.getImports() != null) {
                         for (ImportDeclaration id : cu.getImports()) {
                             if (id.toString().trim().endsWith("." + eci.getName() + ";")) {
                                 addAfter += "package " + cu.getPackage() + "\n";
                                 addAfter += "  has:" + eci.getName() + "\n";
                                 break;
                             }
                         }
                     }
                 }
             }
             extendsMap.put(cid.getName(), extendsList);
             if (cid.getAnnotations() != null) {
                 for (AnnotationExpr ann : cid.getAnnotations()) {
                     result += "  stereotype:" + ann.getName() + "\n";
                 }
             }
             System.out.println(cid.getMembers());
             if (cid.getMembers() != null) {
                 for (BodyDeclaration bd : cid.getMembers()) {
                     if (bd instanceof MethodDeclaration) {
                         MethodDeclaration m = (MethodDeclaration) bd;
                         if (!ModifierSet.isPrivate(m.getModifiers())) {
                             result += "  method:" + m.getName() + "\n";
                             methodsList.add(m.getName());
                         }
                     }
                     if (bd instanceof FieldDeclaration) {
                         FieldDeclaration m = (FieldDeclaration) bd;
                         if (!ModifierSet.isPrivate(m.getModifiers())) {
                             for (VariableDeclarator v : m.getVariables()) {
                                 result += "  field:" + v.getId().getName() + "\n";
                             fieldsList.add(v.getId().getName());
                             }
                         }
                     }
                  }
                 methodsMap.put(cid.getName(), methodsList);
                 fieldsMap.put(cid.getName(), fieldsList);
             }

             packageMembers += packageMembers.isEmpty() ? "" : ",";
             packageMembers += cid.getName();
         }
     }

     result += addAfter;
 } catch (Exception e) {
     e.printStackTrace();
 }
			
		System.out.println(implementsMap.toString());
		System.out.println(extendsMap.toString());
		System.out.println(methodsMap.toString());
		System.out.println(fieldsMap.toString());
	
//	new ParseFile().writeToOutput(extendMap,implementMap,methodMap,fieldMap);
	//System.out.println("\n\n"+result);
	

	}
	
	/*
public void writeToOutput(Multimap extendMap,Multimap implementMap, Multimap methodMap,Multimap fieldMap) throws IOException{
		File outputFile = new File(System.getProperty("user.dir")+"\\src\\ProjectPackage","output.txt");

		FileWriter fstream = new FileWriter(outputFile,true);
		BufferedWriter out = new BufferedWriter(fstream);
		
		out.write("@startuml\n");
		out.write("Bob<|--Alice\n");
		out.write("@enduml\n");
		out.close();
}	*/
}

	
	

