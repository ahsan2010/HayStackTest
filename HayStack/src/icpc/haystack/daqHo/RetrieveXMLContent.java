package icpc.haystack.daqHo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class RetrieveXMLContent 
{
	public static void main(String[] args) throws IOException
	{
		File file = new File("AllThreadsXML");//give path of a directory.
		
		if (file.isDirectory()) 
		{
		        String[] filelist = file.list();
		        for (int i = 0; i < filelist.length; i++) // process each XML file
		        {
		        	
		        	String TID=filelist[i].substring(0, filelist[i].lastIndexOf("---"));// define the thread id
		        	int PID=Integer.parseInt(filelist[i].substring(filelist[i].lastIndexOf("-")+1, filelist[i].lastIndexOf(".")));// define the post id.
		        	
//		        	RetriveWords.IdentifyPatterns("xml\\"+filelist[i], TID, PID);
		        	AllThreadsPatterns.IdentifyPatterns("AllThreadsXML\\"+filelist[i], TID, PID);
		        	
		        }

		}

	}
}
