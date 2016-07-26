package icpc.haystack.daqHo;

import java.io.BufferedReader;  

import java.io.FileInputStream; //import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStreamReader;  
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;



public class PostJson {
	public static void main(String[] args) throws IOException
	{
		{  
			try 
			{  
				JSONObject js1=new JSONObject();
				JSONObject js2=new JSONObject();
				JSONArray ja=new JSONArray();
				JSONArray array=new JSONArray();
				String temp=null;
				
				Connection conn=PickDic.sqlConn();
//				String sql1="select * from retestrdpnsdatasource";
				String sql1="select * from allnsdatasource";
				if(!conn.isClosed())
				{
					Statement statement1=conn.createStatement();
					ResultSet rs1=statement1.executeQuery(sql1);
						while(rs1.next())
						{
							if(rs1.getString("SenCon").length()>2)
							{
								js1.put("text", rs1.getString("SenCon"));
								temp=rs1.getString("TID")+"["+rs1.getString("PID")+"]"+rs1.getString("SID");
			                	js1.put("id",temp);
			                	ja.add(js1);
							}
						}
						js2.element("data", ja);
						rs1.close();
				}
				
				
				JSONObject object = JSONObject.fromObject(generateRequest(js2.toString()));
				array = object.getJSONArray("data");

				int pid=0;
				int sid=0;
				int result=0;
				Connection conn1=PickDic.sqlConn();
				if(!conn1.isClosed())
				{
					Statement statement2=conn.createStatement();

					for(int j=0; j<array.size();j++)
					{
						JSONObject obj = array.getJSONObject(j);
						if(Integer.parseInt(obj.getString("polarity"))<1)
						{
							

							String kk=obj.getString("id").trim();
							
							String rtid=kk.substring(0, kk.lastIndexOf("["));
							String rpid=kk.substring(kk.lastIndexOf("[")+1, kk.lastIndexOf("]"));
							String rsid=kk.substring(kk.lastIndexOf("]")+1);
							
//							String sql2="update retestrdpnsdatasource set NegFlag=1 where TID='"+rtid+"' and PID="+rpid+" AND SID="+rsid;
							String sql2="update allnsdatasource set NegFlag=1 where TID='"+rtid+"' and PID="+rpid+" AND SID="+rsid;
							
							result=statement2.executeUpdate(sql2);
							if(result==1)
							{
								System.out.println(sql2);
								System.out.println("update success");
							}
						}
					}
						conn1.close();
				}
				
				
				} 
			catch (Exception e) 
			{  
				e.printStackTrace();  
			}  
			}  
	
	}
	
	public static String generateRequest(String ja)
	{
		DefaultHttpClient client = new DefaultHttpClient();
		String responseJSON=null;
		try
		{
			
			StringEntity entity = new StringEntity(ja);
			HttpPost httpost = new HttpPost("http://www.sentiment140.com/api/bulkClassifyJson?appid=zyynanyang@gmail.com");
            httpost.setEntity(entity);
            HttpResponse response = client.execute(httpost);
//            System.out.println(EntityUtils.toString(response.getEntity()));
            responseJSON=EntityUtils.toString(response.getEntity());
            
		}
		catch(Exception ex)
		{
			
		}
		finally
		{
			client.getConnectionManager().shutdown();
			if(responseJSON!=null)
			{
				return responseJSON;
			}
			else
			{
				return "-1";
			}
		}
	}
}
