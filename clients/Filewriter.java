package clients;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Filewriter {
	 private static String tar;
	
	  public static void printtotxt(){
		 try{
			 File file = new File("plan.txt");// ָ��Ҫд����ļ�  
		        if (!file.exists()) {// ����ļ��������򴴽�  
		            file.createNewFile();  
		        }  
		        // ��ȡ���ļ��Ļ��������  
		        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));  
		        // д����Ϣ  
		        bufferedWriter.write(tar);  
		        //bufferedWriter.newLine();// ��ʾ���� 
		        bufferedWriter.close();
		    }catch(Exception e){
		    	System.out.println(e);
		    }
		 
	 }
	 
	 public static void giveittostring(String s){
		 tar = s + tar;
	 }
}
