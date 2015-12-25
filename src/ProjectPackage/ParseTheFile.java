package ProjectPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.SourceFileReader;


public class ParseTheFile {
	
	Map<String, List<String>> implementsMap =  new HashMap<String,List<String>>();
	Map<String, List<String>> extendsMap =  new HashMap<String,List<String>>();
	Map<String, List<String>> methodsMap =  new HashMap<String,List<String>>();
	Map<String, List<String>> fieldsMap =  new HashMap<String,List<String>>();
	
	List<String> classList = new ArrayList<>();
	List<String> interfaceList = new ArrayList<>();
	List<String> checkGetSet = new ArrayList<>();
	List<String> variablesList = new ArrayList<String>();	
	Set<String> potentialFields = new HashSet<>();
	Set<String> dependancySet = new HashSet<>();
	Map<String,List<String>> potentialDependancy = new HashMap<String,List<String>>();
	
	List<String> implementsList ;
	List<String> extendsList ;
	List<String> methodsList ;
	List<String> fieldsList ;
	List<String> depClass;
	
	 boolean getFlag=false,setFlag=false;   	 

	public void findElements(File file) throws IOException, ParseException{
		
		FileInputStream in = new FileInputStream(file);
		CompilationUnit cu ;
		
		try{ 	cu = JavaParser.parse(in);	} 
		finally{	in.close();  	}		
		
		try{			
			for (TypeDeclaration type : cu.getTypes()) {
				
				implementsList = new ArrayList<String>();
			    extendsList = new ArrayList<String>();
				methodsList = new ArrayList<String>();
				fieldsList = new ArrayList<String>();	
				depClass = new ArrayList<String>();
				
				if (type instanceof ClassOrInterfaceDeclaration) {
		             ClassOrInterfaceDeclaration cid = (ClassOrInterfaceDeclaration) type;
		             if(cid.isInterface()){
		            	 interfaceList.add(cid.getName());
		             }else{
		            	 classList.add(cid.getName());
		             }
		             if(cid.getImplements()!=null){
		            	 for (ClassOrInterfaceType cit : cid.getImplements() ){
		            		 implementsList.add(cit.getName());
		            	 }
		             }
		             
		             if(cid.getExtends()!=null){
		            	 for(ClassOrInterfaceType cit : cid.getExtends()){
		            		 extendsList.add(cit.getName());
		            	 }
		             }
		             
		             if (cid.getMembers() != null) {
		                 for (BodyDeclaration bd : cid.getMembers()) {
		                	 if(bd instanceof ConstructorDeclaration){
		                		 String modifier="",constructorParams="";
		                		 boolean flag=false;
		                		 ConstructorDeclaration m = (ConstructorDeclaration) bd;
		         
		                    switch(m.getModifiers()){
		                    case 0 : modifier="~"; break;
		                    case 1 : modifier="+"; break;
		                    case 2 : modifier ="-"; break;
		                    case 4 : modifier ="#"; break;
		                    }
		                    List<Parameter> parameters = m.getParameters();
		                    Iterator itr = parameters.iterator();
		                    while(itr.hasNext()){
		                    	StringTokenizer stoken = new StringTokenizer(itr.next().toString());
		                    	String temp1=stoken.nextToken(),temp2=stoken.nextToken();
		                    	
		                    	if(flag){ constructorParams+="," +temp2 +" : "+temp1; continue;}
		                    	else{ constructorParams = temp2 +" : "+temp1;}
		                    	flag = true;
		                    	depClass.add(temp1+" "+temp2);
		                    }
		                	 methodsList.add(modifier+" "+m.getName()+"("+constructorParams+")" );	
		                	 }

		               if (bd instanceof MethodDeclaration) {
		                         MethodDeclaration m = (MethodDeclaration) bd;
		                         List<Parameter> methods = m.getParameters();
		                       for(int q=0;q<methods.size();q++){
		                    	   String[] sdk =methods.get(q).toString().split(" ");
		                   	   depClass.add(sdk[0]+" "+sdk[1]);
		                       }
		                         if ((ModifierSet.isPublic(m.getModifiers()))) {
		                        	 String methodParams="";
		                        	 Boolean flag = false;
		                        	 List<Parameter> parameters = m.getParameters();
		 		                    Iterator itr = parameters.iterator();
		 		                    while(itr.hasNext()){
		 		                    	StringTokenizer stoken = new StringTokenizer(itr.next().toString());
		 		                    	String temp1=stoken.nextToken(),temp2=stoken.nextToken();
		 		                    	if(flag){ methodParams+="," +temp2 +" : "+temp1; continue;}
		 		                    	else{ methodParams = temp2 +" : "+temp1;}
		 		                    	flag = true;
		 		                    //	System.out.println(temp2 +" : "+temp1);
		 		                    }
		 		                  //  System.out.println(m.getName()+" params : "+methodParams);
		 		                  //  System.out.println("Type : "+m.getType());
		 		                    if(m.getName().toString().startsWith("get")){
		 		                    	getFlag=true;
		 		                //    	System.out.println("Get method Spotted : "+m.getName());       	
		 		                    }
		 		                    if(m.getName().toString().startsWith("set")){
		 		                    	setFlag=true;
		 		             //       	System.out.println("Set method Spotted : "+m.getName());
		 		                    }
		 		                    
		                        	 methodsList.add("+ "+m.getName()+"("+methodParams+")"+" : "+m.getType());
		                         }
		                     }
		                     if (bd instanceof FieldDeclaration) {
		                         FieldDeclaration m = (FieldDeclaration) bd;
		                         String modifier="";
		                       
		                         for(VariableDeclarator v : m.getVariables()){
		                        	 variablesList.add(cid.getName() +" "+ m.getType()+" "+ v.getId().getName());
		                         }
		                       
		                         if (ModifierSet.isPrivate(m.getModifiers()) || ModifierSet.isPublic(m.getModifiers())) {
		                             for (VariableDeclarator v : m.getVariables()) {
		                         //   	 System.out.println(v.getId().getName());
		                         //   	 System.out.println(m.getModifiers());
		                            	 switch(m.getModifiers()){
		                            	 case 0 : modifier="~"; break;
		     		                    case 1 : modifier="+"; break;
		     		                    case 2 : modifier="-";break;
		     		                    case 4 : modifier ="#"; break;
		                            	 }
				              //    	 variablesList.add(v.getId().getName());

		                             fieldsList.add(modifier+"  "+v.getId().getName()+" : "+m.getType());
		                             }
		                         }
		                     }
		                  }
		             }
		             if (getFlag && setFlag){
		            	 checkGetSet.add(cid.getName());
		            	 getFlag=false; setFlag=false; 
		            //	 System.out.println("Class added : "+cid.getName());
		             }
	            	 implementsMap.put(cid.getName(), implementsList);
		             extendsMap.put(cid.getName(), extendsList);
		             methodsMap.put(cid.getName(), methodsList);
		             fieldsMap.put(cid.getName(), fieldsList);
		             potentialDependancy.put(cid.getName(), depClass);
				}
			 }
		}
		catch(Exception e)	{		}
	}
	
	public void showData(){
		System.out.println("Classes : "+classList);
		System.out.println("Interfaces : "+interfaceList);
		System.out.println("Implements : "+implementsMap.toString());
		System.out.println("Extends : "+extendsMap.toString());
		System.out.println("Methods : "+methodsMap.toString());
		System.out.println("Fields : "+fieldsMap.toString());
		System.out.println("variables : "+variablesList.toString());
		System.out.println("Potential Dependancy : "+potentialDependancy.toString());
		}
	
	public void writeToFile(String pngName) throws IOException{
		
		File outputFile = new File(System.getProperty("user.dir"),pngName);
		String string = this.pasteTheLogic();
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {	 
		   writer.write("@startuml\nskinparam classAttributeIconSize 0\n");
		   writer.write(string);
		   writer.write("@enduml");
		 		}
	}
	
	public void CreatePNG(String pngName){
		try{
			File file = new File(System.getProperty("user.dir"),pngName);
			SourceFileReader reader = new SourceFileReader(file);
			List<GeneratedImage> list = reader.getGeneratedImages();
			File f1 = list.get(0).getPngFile();
		}
		catch(Exception e)
		{
			System.err.print(e);
		}
	}
	
	public String pasteTheLogic(){
		String write = "";
		for(int i=0;i<classList.size();i++){
			String methods = this.getMethods(classList.get(i));
			String fields = this.getFields(classList.get(i));
			write+="class "+classList.get(i)+" {\n";
			write+=fields+"\n";
			write+=methods+"\n";
			write+="}\n";			
		}
		for(int i=0;i<interfaceList.size();i++){
			String methods = this.getMethods(interfaceList.get(i));
			write+="interface "+interfaceList.get(i)+" {\n";
			write+=methods+"\n";
			write+="}\n";			
		}
		
		for(Map.Entry<String, List<String>> entry : extendsMap.entrySet()){
			
			
			if((entry.getValue().isEmpty())){
				continue;
			}
			String value = entry.getValue().toString();
			write+=value.substring(1, value.length()-1)+"<|-- "+entry.getKey()+"\n";
		}
		
		for(Map.Entry<String, List<String>> entry : implementsMap.entrySet()){
			if((entry.getValue().isEmpty())){
				continue;
			}
			if(entry.getValue().size()>1){
			//	System.out.println(entry.getValue());
				for(int i=0;i<entry.getValue().size();i++){
					//		System.out.println("Values : "+entry.getValue().get(i));
					write+=entry.getValue().get(i)+"<|.."+entry.getKey()+"\n";
				}
			continue;}
			String value = entry.getValue().toString();
		//	System.out.println(value);
			write+=value.substring(1, value.length()-1)+"<|.. "+entry.getKey()+"\n";
		}
		
		write+= this.findMultiplicity();
		write+= this.findDependancy();
		return write;
	}
	
	public String getMethods(String classname){
		String write = "";
		for(Map.Entry<String, List<String>> entry : methodsMap.entrySet()){
			if(entry.getKey().equals(classname)){
			if(entry.getValue().isEmpty()){
				continue;
			}
			if(entry.getValue().size()>1){
				//System.out.println(entry.getValue().size());
				for(int j=0;j<entry.getValue().size();j++){
			//		System.out.println(classname +" : " +entry.getValue().get(j));
					write+=entry.getValue().get(j)+"\n";
				}
			continue;}
		//	System.out.println(entry.getKey()+" : "+entry.getValue().toString().substring(1, entry.getValue().toString().length()-1));
			write+=entry.getValue().toString().substring(1, entry.getValue().toString().length()-1)+"\n";
				}
		}
		//System.out.println(write);
		return write;
	}
	
	public String getFields(String classname){
		String write = "";
		for(Map.Entry<String, List<String>> entry : fieldsMap.entrySet()){
			if(entry.getKey().equals(classname)){
				if(!(entry.getValue().isEmpty())){
		//			System.out.println("Field Members : "+entry.getKey()+"   "+entry.getValue());
					if(entry.getValue().size()>1){
						//System.out.println(entry.getValue().size());
						for(int j=0;j<entry.getValue().size();j++){
					//		System.out.println(classname +" : " +entry.getValue().get(j));
							write+=entry.getValue().get(j)+"\n";
						}
					continue;}
					write+=entry.getValue().toString().substring(1, entry.getValue().toString().length()-1)+"\n";

				}
			}
		}
		return write;
	}
	
	
	public void findIfPublic(){
		for(int i=0;i<checkGetSet.size();i++){
			String classname = checkGetSet.get(i);
			String temp = null;
		//	System.out.println(classname);
		//	Map.Entry<String, List<String>> itr = (Entry<String, List<String>>) methodsMap.entrySet().iterator();
			for(Map.Entry<String, List<String>> entry: methodsMap.entrySet()){
				if(entry.getKey().equals(classname)){
				//	System.out.println("Inside Spotted class : "+entry.getKey());
					List<String> mList = entry.getValue();
					for (int x=0;x<mList.size();x++){
					//		System.out.println(mList.get(x));	
						if(mList.get(x).contains("get")){
							String std = mList.get(x).substring(5,mList.get(x).indexOf("("));
							potentialFields.add(std.toLowerCase());
						}
						if(mList.get(x).contains("set")){
							String std = mList.get(x).substring(5,mList.get(x).indexOf("("));
							potentialFields.add(std.toLowerCase());
						}
					}
			//		System.out.println(potentialFields);	
					for(Map.Entry<String, List<String>> entrylist : fieldsMap.entrySet()){
						if(entrylist.getKey().equals(classname)){
						//	System.out.println(entrylist.getValue());
							for(int y=0;y<entrylist.getValue().size();y++){
						//		System.out.println(entrylist.getValue().get(y));
								temp =entrylist.getValue().get(y).substring(3, entrylist.getValue().get(y).indexOf(" :")).toLowerCase();
						//		System.out.println(temp);
								if(potentialFields.contains(temp)){
					//				System.out.println("Success !! "+entrylist.getValue().get(y).replace("-", "+"));
								  String tempp=  entrylist.getValue().get(y).replace("-", "+");
								  entrylist.getValue().remove(y); 
								  entrylist.getValue().add(tempp);
								   
								}
							}
				//			System.out.println(entrylist.getValue());
						}
					}
					
			//	Iterator itr  = entry.getValue().iterator();
				for(int h=0; h<entry.getValue().size();h++){
		//			System.out.println(entry.getValue().get(h).substring(2, entry.getValue().get(h).indexOf("(")));
		//			System.out.println("get"+temp);
						if(entry.getValue().get(h).substring(2, entry.getValue().get(h).indexOf("(")).equalsIgnoreCase("get"+temp)){
							
							entry.getValue().remove(h);
						}
						
						if(entry.getValue().get(h).substring(2, entry.getValue().get(h).indexOf("(")).equalsIgnoreCase("set"+temp)){
							entry.getValue().remove(h);
						}
				}
					
				}
				
			}
		}
	}
	
	public String findMultiplicity(){
		String string="";
		List<String[]> checkList = new ArrayList<String[]>();
		
		for(int i=0;i<variablesList.size();i++){
			String str = variablesList.get(i);
			String[] splits = str.split(" ");
			String array[] = new String[4];

		//	System.out.println(splits[1]);
			if(classList.contains(splits[1]) || interfaceList.contains(splits[1])){
		//		System.out.println("Match Found !!! As  "+splits[0]+" \"0\" "+"-"+ " \"1\""+" "+splits[1] );
			
				array[0] = splits[0]; array[1] = splits[1]; array[2] = "0"; array[3] = "1";
				checkList.add(array);
	//			string+=splits[0]+" \"0\" "+"-"+ " \"1\""+" "+splits[1]+"\n";
			}
			
			if(splits[1].startsWith("Collection")){
		//		System.out.println(splits[1].substring(splits[1].indexOf("<")+1, splits[1].indexOf(">")));
				if(classList.contains(splits[1].substring(splits[1].indexOf("<")+1, splits[1].indexOf(">"))) || interfaceList.contains(splits[1].substring(splits[1].indexOf("<")+1, splits[1].indexOf(">")))){
		//			System.out.println("Match Found !!! As  "+splits[0]+" \"0\" "+"-"+ " \"0..*\""+" "+splits[1].substring(splits[1].indexOf("<")+1, splits[1].indexOf(">")) );
					array[0] = splits[0]; array[1] = splits[1].substring(splits[1].indexOf("<")+1, splits[1].indexOf(">")); array[2] = "0"; array[3] = "*";
					checkList.add(array);
		//				string+=splits[0]+" \"0\" "+"-"+ " \"0..*\""+" "+splits[1].substring(splits[1].indexOf("<")+1, splits[1].indexOf(">"))+"\n" ;
				}
			}
		}
		
//Check if multiple tuples are there for Multiplicity
		
		for(int p=0;p<checkList.size();p++){
				System.out.println("\n");
					for(int q=p+1;q<checkList.size();q++){
						if(checkList.get(p)[0].equals(checkList.get(q)[1]) && checkList.get(p)[1].equals(checkList.get(q)[0])){
				//			System.out.println(checkList.get(p)[0]+" "+checkList.get(p)[1]+" "+checkList.get(p)[2]+" "+checkList.get(p)[3]+"   ->> " +checkList.get(q)[0]+" "+checkList.get(q)[1]+" "+checkList.get(q)[2]+" "+checkList.get(q)[3]);
				//			System.out.println("One instance spotted !!!");
							checkList.get(p)[2] = checkList.get(q)[3];
				//			System.out.println(checkList.get(p)[0]+" "+checkList.get(p)[1]+" "+checkList.get(p)[2]+" "+checkList.get(p)[3]);
							checkList.remove(q);
						}
				}
			}
		
	//	System.out.println("CHeckLIst : ");
		for(int p=0;p< checkList.size();p++){
			System.out.println("\n");
	/*		for(int pp=0;pp<checkList.get(p).length;pp++){
				System.out.print(checkList.get(p)[pp]+" ");
				string+=checkList.get(p)[pp]+" ";
			}
		*/
			if(checkList.get(p)[2].equals("*") || checkList.get(p)[3].equals("*") || checkList.get(p)[3].equals("1") || checkList.get(p)[3].equals("1")){
			string+=checkList.get(p)[0]+" "+" \""+checkList.get(p)[2]+"\" - \""+checkList.get(p)[3]+"\" "+checkList.get(p)[1]+"\n";
			}
		}
		return string;
	}
	
	public String findDependancy(){
		String string="";
		Boolean flag=false;
		Map<String,String> tempMap = new HashMap<String,String>();
			
		for(Map.Entry<String, List<String>> entry : potentialDependancy.entrySet()){
			if(interfaceList.contains(entry.getKey())){
				continue;
			}
			
			if(!(entry.getValue().isEmpty())){
			//	System.out.println(entry.getKey());
				
				for(int itr =0;itr<entry.getValue().size();itr++){
					Boolean mulFlag=false;
			//		System.out.println(entry.getKey()+" -> "+entry.getValue().get(itr));
							for(Map.Entry<String, List<String>> check : fieldsMap.entrySet()){
								if(check.getKey().equals(entry.getKey())){
								//	System.out.println(check.getKey()+" "+entry.getKey());
									if(!(check.getValue().isEmpty())){
										Iterator iterator = check.getValue().iterator();
									//	System.out.println(check.getValue());
									for(int in =0; in<check.getValue().size();in++){
										String temp = check.getValue().get(in);
										String[] compareWith = temp.split(" ");
									//	System.out.println("itr.next -> "+itr.next());
									//	System.out.println(check.getKey()+ " has -> "+compareWith[4]+" "+compareWith[2]);
											if(entry.getValue().get(itr).equals((compareWith)[4]+" "+compareWith[2])){
										//		System.out.println("Association Exists already !!!");
												flag=true;
											}
										}
									}
								}
							}
							if(flag){
					//			System.out.println("Association Exists already !!!"); flag=false;
							}else
							{	
								if(!(classList.contains(entry.getValue().get(itr).split(" ")[0]))){
									writeDependancy(entry.getKey(),entry.getValue().get(itr).split(" ")[0]);
								}
					
						//		System.out.println(entry.getKey()+" depends on "+entry.getValue().get(itr).split(" ")[0]); 
							}  
					}		
				}
			}
		Iterator jj =dependancySet.iterator();
		while(jj.hasNext()){
			String [] aray = jj.next().toString().split(" ");
//			string+=jj.next().toString().split(" ")[0]+" ..> "+jj.next().toString().split(" ")[1];
//			System.out.println(aray[0] + " ..> "+aray[1]);
			string+=aray[0] + " ..> "+aray[1]+"\n";
		}
		return string;
	}

	public void writeDependancy(String key, String string) {
			if(classList.contains(string) || interfaceList.contains(string)){
			//	System.out.println(key+ " depends on "+ string);
				dependancySet.add(key+" "+string);
			}
	}
	
	
}
