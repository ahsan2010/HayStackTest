package icpc.haystack.daqHo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


public class CaseAnalysisCounterWithNeighbors 
{
	public static Map countWord()
	{
		Map tempMap=new HashMap();
		Map map=new HashMap();
		Vector RSID=new Vector();
		int CSID=0;
		Vector SwingDic=new Vector();
		Connection conn=PickDic.sqlConn();
		
		try
		{
			// figuring out all the threads that contain negative sentence.
//			String sql="select distinct TID from retestrdpnsdatasource where NegFlag=1";
			String sql="select distinct TID from allnsdatasource where NegFlag=1";
			
			if(!conn.isClosed())
			{
				Statement statement=conn.createStatement();
				Statement statement1=conn.createStatement();
				Statement statement2=conn.createStatement();
				Statement statement3=conn.createStatement();
				Statement statement4=conn.createStatement();
				
				ResultSet rs=statement.executeQuery(sql);
				
				
				while(rs.next())
				{
					// identifying the posts that contain negative sentence in the current sentence.
//					String sql1="select distinct PID from retestrdpnsdatasource where NegFlag=1 and TID='"+rs.getString("TID")+"'";
					String sql1="select distinct PID from allnsdatasource where NegFlag=1 and TID='"+rs.getString("TID")+"'";
					
					ResultSet rs1=statement1.executeQuery(sql1);
					
					while(rs1.next())
					{
//						String sql2="select count(SID) as CSID from retestrdpnsdatasource where PID="+rs1.getInt("PID")+" and TID='"+rs.getString("TID")+"'";
						String sql2="select count(SID) as CSID from allnsdatasource where PID="+rs1.getInt("PID")+" and TID='"+rs.getString("TID")+"'";
						
						ResultSet rs2=statement2.executeQuery(sql2);
						while(rs2.next())
						{
							CSID=rs2.getInt("CSID");
						}
						
//						String sql3="select SID from retestrdpnsdatasource where PID="+rs1.getInt("PID")+" AND NegFlag=1"+" and TID='"+rs.getString("TID")+"'";
						String sql3="select SID from allnsdatasource where PID="+rs1.getInt("PID")+" AND NegFlag=1"+" and TID='"+rs.getString("TID")+"'";
						
						
						ResultSet rs3=statement3.executeQuery(sql3);
						Vector reSID=new Vector();
						Vector checkedSID=new Vector();//used to store which sentence has been added to RSID.
						while(rs3.next())
						{

							int k=0;
//							for(int i=0; i<=0; i++)
//								for(int i=-1; i<=1; i++)
//								for(int i=-1; i<=0; i++)
//								for(int i=0; i<=1; i++)
//								for(int i=-2; i<=0; i++) // only when processing both the negative sentence and its precede sentences using this "for" loop;
//								for(int i=-2; i<=2; i++) // processing both negative sentence and its neighbors
//								for(int i=0; i<=2; i++) // processing both negative sentence and the following sentences.
//							for(int i=-3; i<=3; i++)
//							for(int i=-3; i<=0; i++)
							for(int i=0; i<=3; i++)
//							for(int i=-4; i<=4; i++)
//							for(int i=-4; i<=0; i++)
//							for(int i=0; i<=4; i++)
//							for(int i=-5; i<=5; i++)
//							for(int i=-5; i<=0; i++)
//							for(int i=0; i<=5; i++)
							{
								if((rs3.getInt("SID")+i)>0 && (rs3.getInt("SID")+i)<=CSID)
								{
									if(!checkedSID.contains((rs3.getInt("SID")+i)))
									{
//										String checkNID="select NegFlag from retestrdpnsdatasource where PID="+rs1.getInt("PID")+" and SID="+(rs3.getInt("SID")+i)+" and TID='"+rs.getString("TID")+"'";
										String checkNID="select NegFlag from allnsdatasource where PID="+rs1.getInt("PID")+" and SID="+(rs3.getInt("SID")+i)+" and TID='"+rs.getString("TID")+"'";
										
										ResultSet flag=statement4.executeQuery(checkNID);
										
										int neg=-1;
										while(flag.next())
										{
											neg=flag.getInt("NegFlag");
										}
										if(neg==0)
										{
											RSID.add((rs3.getInt("SID")+i));// RSID used to store which sentences belongs to the current "PID", "NegFlag" group;
											checkedSID.add((rs3.getInt("SID")+i));
										}
										else if(neg==1 && k==0) //set k=1 to avoid the sentence which following the NegFlag sentence is also a NegFlag sentence.
										{
											RSID.add((rs3.getInt("SID")+i));
											k=1;
											checkedSID.add((rs3.getInt("SID")+i));
										}
										else if(neg==1 && k==1)
										{
											break;
										}
									}
								}
							}
							reSID.add(RSID);
							RSID=new Vector();
						}
						map.put(rs.getString("TID")+"["+rs1.getInt("PID"), reSID);
						RSID=new Vector();
						reSID=new Vector();
					}
				}
			}

			
				Set set1 = map.entrySet();  
				Iterator iterator1 = set1.iterator(); 

				if(!conn.isClosed())
					{
						Statement statement1=conn.createStatement();
						Statement statement2=conn.createStatement();
						Statement statement3=conn.createStatement();
						while (iterator1.hasNext())  //each paragraph(thread)
						{  
				        Map.Entry mapentry1 = (Map.Entry) iterator1.next();  
				        Vector list=(Vector)mapentry1.getValue();
				        
				        
				        
				        for(int k=0; k<list.size();k++)//each negtive sentences group(Vector)
				        {
					        	Vector v=(Vector)list.elementAt(k);
					        	Vector NIDList=new Vector();
					        	Vector NNList=new Vector();
					        	Vector tempS=new Vector();
					        	
						        for(int q=0;q<v.size();q++) //every negtive sentences group(Vector)
						        {
						        	String tid=mapentry1.getKey().toString().substring(0, mapentry1.getKey().toString().lastIndexOf("["));
						        	String pid=mapentry1.getKey().toString().substring(mapentry1.getKey().toString().lastIndexOf("[")+1);
//						        	String sql22="select NNList from retestrdpnstsentences where PID="+Integer.parseInt(pid)+" AND SID="+Integer.parseInt(v.elementAt(q).toString())+" and TID='"+tid+"'";
						        	String sql22="select NNList from allnstsentences where PID="+Integer.parseInt(pid)+" AND SID="+Integer.parseInt(v.elementAt(q).toString())+" and TID='"+tid+"'";
						        	
						        	ResultSet rs2=statement2.executeQuery(sql22);
						        	while(rs2.next())
						        	{
						        		if(!rs2.getString("NNList").equals("null"))
						        		{
								        	String test[] = rs2.getString("NNList").split("\\s+");
								        	for (int kk=0; kk<test.length; kk++)
								        	{
								        		if(!NIDList.contains(test[kk]))
								        		{
								        			NIDList.add(test[kk]);//NIDList used to store all NID in NNList in each negative sentence group;
								        		}
								        	}
						        		}
						        	}
						        }
						        for(int nn=0; nn<NIDList.size(); nn++)
						        {
//						        	String seeds="select items from retestrdpnsnonulist where NID="+Integer.parseInt(NIDList.elementAt(nn).toString());
						        	String seeds="select items from allnsnonulist where NID="+Integer.parseInt(NIDList.elementAt(nn).toString());
						        	
						        	ResultSet seedList=statement2.executeQuery(seeds);
						        	while(seedList.next())
						        	{
						        		/*when process the "NP" relative class the following first "if" should be "//"*/
//						        		if((!NNList.contains(seedList.getString("items")))&&SwingDic.contains(seedList.getString("items")))
						        		if(!NNList.contains(seedList.getString("items")))
						        		{
						        			NNList.add(seedList.getString("items"));// NNList used to store all the noun which was found in each negative sentence group;
						        		}
						        	}
						        }
						        
						        // get the current MasterList;
						        	Vector q=new Vector();
						        	Vector checkList=new Vector();
							        Set set2 = tempMap.entrySet();  
					        		Iterator iterator2 = set2.iterator(); 
					        		while(iterator2.hasNext())
					        		{
					        			Map.Entry mapentry2 = (Map.Entry) iterator2.next(); 
					        			Object key=mapentry2.getKey();
					        			q.add(key);
					        		}
					        			HashSet hashsetObject=new HashSet(q);
					        	       Iterator iteratorObject=hashsetObject.iterator();
					        	       while(iteratorObject.hasNext())
					        	       {
					        	           String str=(String)iteratorObject.next();
					        	           checkList.add(str);
					        	       }

					        		
							        
							        	for(int a=0; a<NNList.size();a++)
							        	{
							        		tempS.add(NNList.elementAt(a).toString());
							        		Vector nwords=new Vector();
							        		if(!NNList.elementAt(a).toString().equals(null))
							        		{
								        		String[] nlist=NNList.elementAt(a).toString().trim().split("\\s+");
								        		for(int i=0; i<nlist.length; i++)
								        		{
								        			nwords.add(nlist[i]);
								        		}
							        		}
							        		
							        		String existFlag2="T";

							        		
							        		if(checkList.contains(NNList.elementAt(a).toString()))
							        		{
							        			existFlag2="YES";
							        		}
							        		
							        		// Current Itemset didn't exist in current MasterList;
							        		if(existFlag2=="T")
							        		{
								        			for(int in=0; in<checkList.size(); in++)
													{  
								        				int comm=0;
								        				Vector mwords=new Vector();
								        				
								        				if(!checkList.elementAt(in).toString().equals(null))
								        				{
									        				String[] mlist=checkList.elementAt(in).toString().trim().split("\\s+");
									        				for(int i=0; i<mlist.length; i++)
									        				{
									        					mwords.add(mlist[i]);
									        				}
								        				}
									        				if(mwords.size()!=nwords.size())
									        				{
									        					for(int i1=0; i1<mwords.size(); i1++)
										        				{
										        					for(int i2=0; i2<nwords.size(); i2++)
										        					{
										        						if(mwords.elementAt(i1).toString().trim().equals(nwords.elementAt(i2).toString().trim()))
										        						{
										        							comm++;
										        						}
										        					}
										        				}
										        					if(comm==mwords.size())
											        				{
										        						tempS.add(checkList.elementAt(in).toString());
											        				}
										        					else if(comm==nwords.size())
										        					{
										        						int value=((Integer)tempMap.get(checkList.elementAt(in).toString())).intValue();
										        						for(int k1=0; k1<value; k1++)
											        					{
										        							tempS.add(NNList.elementAt(a).toString());
											        					}
										        					}
									        				}
													}
							        		}
							        		
							        		
							        		// current Itemset exist in the current MasterList;
							        		else
							        		{
							        				for(int in=0; in<checkList.size(); in++)
													{  
								        				int comm=0;
								        				Vector mwords=new Vector();
								        				
								        				if(!checkList.elementAt(in).toString().equals(null))
								        				{
									        				String[] mlist=checkList.elementAt(in).toString().trim().split("\\s+");
									        				for(int i=0; i<mlist.length; i++)
									        				{
									        					mwords.add(mlist[i]);
									        				}
								        				}
									        				
									        				if(mwords.size()<nwords.size())
									        				{
									        					for(int i1=0; i1<mwords.size(); i1++)
										        				{
										        					for(int i2=0; i2<nwords.size(); i2++)
										        					{
										        						if(mwords.elementAt(i1).toString().trim().equals(nwords.elementAt(i2).toString().trim()))
										        						{
										        							comm++;
										        						}
										        					}
										        				}
									        					
										        					if(comm==mwords.size())
											        				{
										        						tempS.add(checkList.elementAt(in).toString());
											        				}
									        				}
													}
							        		}
							        	}
					        			
						        			for(int aid=0; aid<tempS.size();aid++)
							        		{
						        				if(tempMap.get(tempS.elementAt(aid).toString())==null)
												{
													tempMap.put(tempS.elementAt(aid).toString(), 1);
												}
												else
												{
													int value=((Integer)tempMap.get(tempS.elementAt(aid).toString())).intValue();
													value++;
													tempMap.put(tempS.elementAt(aid).toString(), value);
												}
							        		}
					        		
						        NNList=new Vector();
						        tempS=new Vector();
				        }
				    }  
				}
				conn.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		return tempMap;
	}
}
