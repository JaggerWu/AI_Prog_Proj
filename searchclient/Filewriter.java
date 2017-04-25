package searchclient;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Filewriter {
	 private static String tar;
	
	  public static void printtotxt(){
		 try{
			 File file = new File("plan.txt");//
		        if (!file.exists()) {//  
		            file.createNewFile();  
		        }  
		        // 
		        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));  
		        //  
		        bufferedWriter.write(tar);  
		        //bufferedWriter.newLine();// 
		        bufferedWriter.close();
		    }catch(Exception e){
		    	System.out.println(e);
		    }
		 
	 }
	 
	 public static void giveittostring(String s){
		 tar = s + tar;
	 }
}
