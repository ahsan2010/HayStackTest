package icpc.haystack.daqHo;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;


public class getpurepost 
{
	public static void main(String... args) throws Exception 
	{
		
		BufferedReader links = new BufferedReader(new InputStreamReader( 
                new FileInputStream("alllinks.txt"))); 
		LineNumberReader rereader = new LineNumberReader(links);
		String link=rereader.readLine();
		while(link!=null)
		{
			String testreg = "[^a-zA-Z0-9]";        
			Pattern matchsip = Pattern.compile(testreg);        
			Matcher mp = matchsip.matcher(link);        
			String linkstring = mp.replaceAll("-");// used as file name;
			
			String htmlStr=readPage(new URL(link));
			Parser parser=new Parser();  
			parser.setInputHTML(htmlStr);
			NodeFilter filter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class","jive-message-body"));
			NodeList divTags=parser.parse(filter);
		
			
			
			for(int i=0; i<divTags.size(); i++)
			{
				Div message=(Div)divTags.elementAt(i);
				String cleanedStr=Html2Text(message.getStringText());
				
				StringReader in=new StringReader(cleanedStr);
				LineNumberReader reader = new LineNumberReader(in);
				String s = reader.readLine(); 
				File newFile = new File("136Threads\\"+linkstring+"---"+(i+1)+".txt");
//				System.out.println("AllThreads\\"+linkstring+"---"+(i+1)+".txt");
				FileWriter write = new FileWriter(newFile,true);
				BufferedWriter bufferedWriter = new BufferedWriter(write);
				
				while(s!=null)
				{
					if(s.trim().length()>0)
					{
						bufferedWriter.write(s.trim());
						bufferedWriter.newLine();
						bufferedWriter.flush();
					}
					s=reader.readLine();
				}
				
				write.close();
				bufferedWriter.close();
			}
			link=rereader.readLine();
		}
	}

    private static String readPage(URL url) throws Exception 
    {

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url.toURI());
        HttpResponse response = client.execute(request);

        Reader reader = null;
        try {
            reader = new InputStreamReader(response.getEntity().getContent());

            StringBuffer sb = new StringBuffer();
            {
                int read;
                char[] cbuf = new char[1024];
                while ((read = reader.read(cbuf)) != -1)
                    sb.append(cbuf, 0, read);
            }

            return sb.toString();

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static String Html2Text(String inputString)
    {
        String htmlStr = inputString; 
        String textStr ="";
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;

       try
       {
             String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //<script[^>]*?>[\\s\\S]*?<\\/script> 
             String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //<style[^>]*?>[\\s\\S]*?<\\/style>
             String regEx_html = "<[^>]+>"; //Regext for HTML

             p_script = Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
             m_script = p_script.matcher(htmlStr);
             htmlStr = m_script.replaceAll(""); 

             p_style = Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
             m_style = p_style.matcher(htmlStr);
             htmlStr = m_style.replaceAll(""); 

             p_html = Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
             m_html = p_html.matcher(htmlStr);
             htmlStr = m_html.replaceAll(""); 

             textStr = htmlStr;
        }
       catch(Exception e)
       	{
             System.out.println(e.toString());
        }
        return textStr;
    }   
}

