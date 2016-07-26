package icpc.haystack.daqHo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class PickDic 
{
	public static void main(String args[])
	{
		try
		{
			File senFile=new File("SwingBase\\p42.xml");
			SAXReader reader=new SAXReader();
			Document doc=reader.read(senFile);
			Element root=doc.getRootElement();
			Element NounWords;
			Element foo;
			Element lem;
			Element pos;
			List elements = root.elements();
			List<Element> sentencesList=elements;
			Vector NonuList=new Vector();
			Vector newWords=new Vector();
			Connection cons=sqlConn();
			Connection cons1=sqlConn();
			String sql1="select * from testdnonulist";
			String indexlist=null;
			if(!cons.isClosed())
			{
				Statement statement1=cons.createStatement();
				ResultSet rs1=statement1.executeQuery(sql1);
				while(rs1.next())
				{
					NonuList.add(rs1.getString("Items"));
				}
				
				rs1.close();
			}
			BufferedReader rein = new BufferedReader(new InputStreamReader( 
	                new FileInputStream("filter.txt"))); 
			Vector filter=new Vector();
			LineNumberReader rereader = new LineNumberReader(rein); 
			String res=rereader.readLine();
			while(res!=null)
			{
				filter.add(res.trim());
				res=rereader.readLine();
			}
			for(int i=0; i<sentencesList.size(); i++)
			{
				NounWords=sentencesList.get(i).element("tokens");
				for(Iterator j=NounWords.elementIterator("token"); j.hasNext();)
				{
					foo=(Element)j.next();
					lem=foo.element("lemma");
					pos=foo.element("POS");
					
					if((pos.getText().contains("NN")||pos.getText().contains("JJ")||pos.getText().contains("VB"))&&lem.getTextTrim().length()>2 && lem.getTextTrim().length()<25)
					{
						if(!filter.contains(lem.getTextTrim().toLowerCase()))
						{
//							if(!newWords.contains(lem.getTextTrim().toLowerCase()))
//							{
								if(lem.getTextTrim().toLowerCase().length()>0 && !NonuList.contains(lem.getTextTrim().toLowerCase()))
								{
//									newWords.add(lem.getTextTrim().toLowerCase());
//									NonuList.add(lem.getTextTrim().toLowerCase());

									String insertSql="insert into testdnonulist(Items) values ('"+lem.getTextTrim().toLowerCase()+"')";
									PreparedStatement statement3=cons1.prepareStatement(insertSql);
									statement3.executeUpdate();
									NonuList.add(lem.getText().toLowerCase());
								}
//							}
								
								int wordid=0;
								if(!cons1.isClosed())
								{
									String wordIndex="select NID from testdnonulist where Items='"+lem.getTextTrim().toLowerCase()+"'";
									Statement statementIndex=cons1.createStatement();
									ResultSet rsIndex=statementIndex.executeQuery(wordIndex);
									while(rsIndex.next())
									{
										wordid=rsIndex.getInt("NID");
									}
								}
								
							if(indexlist==null)
							{
//								if(NonuList.indexOf(lem.getTextTrim().toLowerCase())!=0)
//								{
//									indexlist=NonuList.indexOf(lem.getTextTrim().toLowerCase())+" ";
									indexlist=wordid+" ";
//								}
							}
							else
							{
//								if(NonuList.indexOf(lem.getTextTrim().toLowerCase())!=0)
//								{
//									indexlist+=NonuList.indexOf(lem.getTextTrim().toLowerCase())+" ";
									indexlist+=wordid+" ";
//								}
							}
						}
					}
				}
				if(!indexlist.equals(null))
				{
					JDBCConnection(cons , "insert into testdstsentences(NNList) values ('"+indexlist+"')", 1);
					System.out.println("insert dsentences");
				}
				indexlist="";
				
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	public static Connection sqlConn()
	{
		String driver="com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://127.0.0.1:3306/sensource";
		String user = "root";
		String password = "zhang";
		try
		{
		Class.forName(driver);
		Connection conn=DriverManager.getConnection(url, user, password);
		return conn;
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	public static void JDBCConnection(Connection conn , String sqls, int flag) //flag=0: select; flag=1: insert;
	{
		try
		{
			if(!conn.isClosed())
			System.out.println("Succeeded connecting to the Database!");
			Statement statement1=conn.createStatement();
			String sql=sqls;
			if(flag==0)
			{
			ResultSet rs=statement1.executeQuery(sql);
			while(rs.next())
				
			rs.close();
			}
			else if(flag==1)
			{
				PreparedStatement statement2=conn.prepareStatement(sql);
				statement2.executeUpdate();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
