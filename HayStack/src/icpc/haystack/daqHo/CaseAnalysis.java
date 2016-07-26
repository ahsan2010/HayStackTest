package icpc.haystack.daqHo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


public class CaseAnalysis{
	public static void main(String[] args)
	{
		Vector RSID=new Vector();
		Vector NSID=new Vector();//store the negtive sentence ID ;
		int CSID=0;
		Vector SwingDic=new Vector();
		Connection conn=PickDic.sqlConn();

		Map tempMap=CaseAnalysisCounterWithNeighbors .countWord();//consider subsets: attention to both neighbor or precedency;

		Map map1=tempMap;
		Set tempset=tempMap.entrySet();
		Set tempset1=map1.entrySet();
		Iterator itor=tempset.iterator();
		Iterator itor1=tempset1.iterator();
		ResultStore sorter;
		Vector temper=new Vector();
		CompareSupport cs1=new CompareSupport();
		while(itor.hasNext()) // get the identified features and its occurrence frequency.
		{
			Map.Entry mapentry1 = (Map.Entry) itor.next();  

			sorter=new ResultStore();
			sorter.setCandiFeature(mapentry1.getKey().toString());
			sorter.setSupport(Integer.parseInt(mapentry1.getValue().toString()));
		
			temper.add(sorter);
		}
		
		Collections.sort(temper, cs1); // sorting the identified features according to its occurrence frequency, then output is for generating the MasterList.
		 for(int t=0; t<temper.size(); t++)
			{
				sorter=new ResultStore();
				sorter=(ResultStore)temper.elementAt(t);
				System.out.println(sorter.getCandiFeature()+"--->"+(int)sorter.getSupport());
			}
		 
		 
		 
		try
		{
			Map Tmap=new HashMap();
			Map Tsidmap=new HashMap();

//			String sql="select distinct TID from retestrdpnsdatasource where NegFlag=1";
			String sql="select distinct TID from sdatasource where NegFlag=1";
			
			if(!conn.isClosed())
			{
				Statement statement1=conn.createStatement();
				Statement statement2=conn.createStatement();
				Statement statement3=conn.createStatement();
				Statement statement4=conn.createStatement();
				Statement statement=conn.createStatement();
				
				ResultSet rs=statement.executeQuery(sql);
				
				while(rs.next())// processing each thread which contains negative sentence;
				{
					Map map=new HashMap();
					Map sidmap=new HashMap();
					
					// identifying the post id of current thread;
//					String sql1="select distinct PID from retestrdpnsdatasource where NegFlag=1 and TID='"+rs.getString("TID")+"'";
					String sql1="select distinct PID from sdatasource where NegFlag=1 and TID='"+rs.getString("TID")+"'";
					
					ResultSet rs1=statement1.executeQuery(sql1);
					
					while(rs1.next())
					{
						// counting the sentences amount for figuring out the negative sentence's neighbors;
//						String sql2="select count(SID) as CSID from retestrdpnsdatasource where PID="+rs1.getInt("PID")+" and TID='"+rs.getString("TID")+"'";
						String sql2="select count(SID) as CSID from sdatasource where PID="+rs1.getInt("PID")+" and TID='"+rs.getString("TID")+"'";
						
						ResultSet rs2=statement2.executeQuery(sql2);
						while(rs2.next())
						{
							CSID=rs2.getInt("CSID");
						}
//					

						// figuring out the negative sentences in current post;
//						String sql3="select * from retestrdpnsdatasource where PID="+rs1.getInt("PID")+" AND NegFlag=1 and TID='"+rs.getString("TID")+"'";
						String sql3="select * from sdatasource where PID="+rs1.getInt("PID")+" AND NegFlag=1 and TID='"+rs.getString("TID")+"'";
						
						ResultSet rs3=statement3.executeQuery(sql3);
						
						Vector reSID=new Vector();
						Vector checkedSID=new Vector();//used to store which sentence has been added to RSID.
						
						while(rs3.next())
						{
							int k=0;
//								for(int i=-2; i<=0; i++) // only when processing both the negative sentence and its precede sentences using this "for" loop;
//								for(int i=-2; i<=2; i++) // processing both negative sentence and its neighbors
								for(int i=0; i<=2; i++) // processing both negative sentence and the following sentences.
								{
									if((rs3.getInt("SID")+i)>0 && (rs3.getInt("SID")+i)<=CSID)// avoid one sentence be processing in two negative sentence group;
									{
										if(!checkedSID.contains((rs3.getInt("SID")+i)))
										{

//											String checkNID="select NegFlag from retestrdpnsdatasource where PID="+rs1.getInt("PID")+" and SID="+(rs3.getInt("SID")+i)+" and TID='"+rs.getString("TID")+"'";
											String checkNID="select NegFlag from sdatasource where PID="+rs1.getInt("PID")+" and SID="+(rs3.getInt("SID")+i)+" and TID='"+rs.getString("TID")+"'";
											
											ResultSet flag=statement4.executeQuery(checkNID);
											
											// for identifying the negative sentence when output the analysis;
											int neg=-1;
											while(flag.next())
											{
												neg=flag.getInt("NegFlag");
											}
											
											// make sure one negative group just contains one negative sentence and its neighbors;
											if(neg==0)
											{
												RSID.add((rs3.getInt("SID")+i));
												checkedSID.add((rs3.getInt("SID")+i));
											}
											else if(neg==1 && k==0)
											{
												RSID.add((rs3.getInt("SID")+i));
												k=1;
												checkedSID.add((rs3.getInt("SID")+i));
											}
											// when one negative sentence is next to another negative one, then break;
											else if(neg==1 && k==1)
											{
												break;
											}
									}
								}
							}
							reSID.add(RSID); // reSID contains each negative sentences group;
							RSID=new Vector();
							NSID.add(rs3.getInt("SID"));
						}

						map.put(rs1.getInt("PID"), reSID); // ["PID", [negative group1; negative group2...]]
						sidmap.put(rs1.getInt("PID"), NSID); // ["PID", [negative SID1; negative SID2...]]

						RSID=new Vector();
						reSID=new Vector();
						NSID=new Vector();
					}
					

					Tmap.put(rs.getString("TID"), map);  // ["TID", ["PID", [negative group1; negative group2...]]]
					Tsidmap.put(rs.getString("TID"), sidmap); // ["TID", ["PID", [negative SID1; negative SID2...]]]
				}
			}

			Set Tset1=Tmap.entrySet();
			Set Tset2=Tsidmap.entrySet();
			Iterator Titerator1=Tset1.iterator();
			Iterator Titerator2=Tset2.iterator();
			
			
			Vector NounList=new Vector();
			String nounlist=null;

			ResultStore rs;
			ResultStore rsp;
			Vector tempStore=new Vector();
			CompareSupport cs=new CompareSupport();

			if(!conn.isClosed())
				{
					Statement statement1=conn.createStatement();
					Statement statement2=conn.createStatement();
					Statement statement3=conn.createStatement();
					
					while(Titerator1.hasNext()) // each thread;
					{
						
						
						
						Map.Entry Tmapentry1=(Map.Entry) Titerator1.next();
						Map.Entry Tmapentry2=(Map.Entry) Titerator2.next();
						
						Map m1=(Map)Tmapentry1.getValue();
						Set set1=m1.entrySet();
						Iterator iterator1=set1.iterator();
						
						Map m2=(Map)Tmapentry2.getValue();
						Set set2=m2.entrySet();
						Iterator iterator2=set2.iterator();
						
						
//						File newFile = new File("CaseAnalysis\\"+Tmapentry1.getKey()+".txt");
						File newFile = new File("AllCaseAnalysis\\"+Tmapentry1.getKey()+".txt");
						FileWriter write = new FileWriter(newFile,true);
						BufferedWriter bufferedWriter = new BufferedWriter(write);
					
						
						while (iterator1.hasNext())  //each paragraph
						{  
							Map.Entry mapentry1 = (Map.Entry) iterator1.next();  
					        Map.Entry mapentry2 = (Map.Entry) iterator2.next();  
					        Vector list=(Vector)mapentry1.getValue();//get all the sentence ID in each negtive sentence group.
					        Vector sidlist=(Vector)mapentry2.getValue();//get the negtive sentence ID in each negtive sentences group.
			       
					        for(int k=0; k<list.size();k++)//each negative sentences group(Vector)
					        {		
					        	Vector v=(Vector)list.elementAt(k);
					        	
					        	Vector NIDList=new Vector();
					        	Vector NNList=new Vector();
					        	

					        	for(int q=0;q<v.size();q++) //every negative sentences group(Vector)
						        {
							        	
//						        	String sql22="select NNList from retestrdpnstsentences where PID="+Integer.parseInt(mapentry1.getKey().toString())+" AND SID="+Integer.parseInt(v.elementAt(q).toString())+" and TID='"+Tmapentry1.getKey().toString()+"'";
					        		String sql22="select NNList from stsentences where PID="+Integer.parseInt(mapentry1.getKey().toString())+" AND SID="+Integer.parseInt(v.elementAt(q).toString())+" and TID='"+Tmapentry1.getKey().toString()+"'";
					        		
						        	ResultSet rs2=statement2.executeQuery(sql22);
						        	
						        	while(rs2.next())
						        	{
						        		if(!rs2.getString("NNList").equals("null"))
						        		{
							        		String test[] = rs2.getString("NNList").split("\\s+");
							        		for (int kk=0; kk<test.length; kk++) //NIDList stored all the unique NID of each negtive sentence group;
								        	{
								        		if(!NIDList.contains(test[kk]))
								        		{
								        			NIDList.add(test[kk]);
								        		}
								        	}
						        		}
						        	}
					        	
					        	
					        	String flag="";
//					        	bufferedWriter.write("~~~~"+Tmapentry1.getKey()+" / "+mapentry1.getKey() + " / " + v.elementAt(q)+"~~~~");
					        	bufferedWriter.write("~~~~"+mapentry1.getKey() + " / " + v.elementAt(q)+"~~~~");
					        	bufferedWriter.newLine();
								bufferedWriter.flush();
								
//								String sql11="select SenCon from retestrdpnsdatasource where PID="+Integer.parseInt(mapentry1.getKey().toString())+" AND SID="+Integer.parseInt(v.elementAt(q).toString())+" and TID='"+Tmapentry1.getKey().toString()+"'";
								String sql11="select SenCon from sdatasource where PID="+Integer.parseInt(mapentry1.getKey().toString())+" AND SID="+Integer.parseInt(v.elementAt(q).toString())+" and TID='"+Tmapentry1.getKey().toString()+"'";
								
								for(int id=0;id<sidlist.size();id++)
					        	{
					        		if(Integer.parseInt(mapentry1.getKey().toString())==Integer.parseInt(mapentry2.getKey().toString()))
					        		{
					        			if(Integer.parseInt(v.elementAt(q).toString())==Integer.parseInt(sidlist.elementAt(id).toString()))
					        			{
					        				flag="NEGSEN:";
					        			}
					        		}
					        	}
					        		ResultSet rs1=statement1.executeQuery(sql11);
					        		while(rs1.next())
					        		{
					        		bufferedWriter.write(flag+" "+rs1.getString("SenCon"));
					        		bufferedWriter.newLine();
									bufferedWriter.flush();
					        		}
					        }
					        
					        
					        for(int nn=0; nn<NIDList.size(); nn++)
					        {

//					        	String seeds="select items from retestrdpnsnonulist where NID="+Integer.parseInt(NIDList.elementAt(nn).toString());
					        	String seeds="select items from snonulist where NID="+Integer.parseInt(NIDList.elementAt(nn).toString());
					        	
					        	ResultSet seedList=statement2.executeQuery(seeds);
					        	while(seedList.next())
					        	{
					        		/*when process the "NP" relative class the following first "if" should be "//"*/
//						        	if((!NNList.contains(seedList.getString("items")))&&SwingDic.contains(seedList.getString("items")))
					        		if(!NNList.contains(seedList.getString("items")))
					        		{
						        		NNList.add(seedList.getString("items"));
						        	}
					        	}
					        }
					        
					        bufferedWriter.write("&&&&&&&");
					        bufferedWriter.newLine();
							bufferedWriter.flush();
					        for(int b=0; b<NNList.size();b++)
					        {
					        	bufferedWriter.write(NNList.elementAt(b).toString());
						        bufferedWriter.newLine();
								bufferedWriter.flush();
					        }
					        bufferedWriter.write("&&&&&&&");
					        bufferedWriter.newLine();
							bufferedWriter.flush();
					    	bufferedWriter.write("*******************");
							bufferedWriter.newLine();
							bufferedWriter.flush();
							
							
							Vector CountedList=new Vector();//check which NN in NNList have been counted;
					        
							 for(int id=0;id<NNList.size()-1; id++)
					        {
								 for(int id1=id+1; id1<NNList.size(); id1++)
					        	{
									 while(itor1.hasNext())
										{
										 
											Map.Entry mapentry11 = (Map.Entry) itor1.next(); 
											if((mapentry11.getKey().toString().equals(NNList.elementAt(id1).toString()+", "+NNList.elementAt(id).toString())) ||(mapentry11.getKey().toString().equals(NNList.elementAt(id).toString()+", "+NNList.elementAt(id1).toString())))
						        			{

//												String support1="select * from retestrdpnsitemsupport where itemsets='"+mapentry11.getKey().toString()+"'";
												String support1="select * from sitemsupport where itemsets='"+mapentry11.getKey().toString()+"'";
												
												ResultSet s1=statement2.executeQuery(support1);
													
							        				bufferedWriter.write(mapentry11.getKey().toString()+"-->"+ ((Integer)tempMap.get(mapentry11.getKey().toString())).intValue());
													bufferedWriter.newLine();
													bufferedWriter.flush();
													/*when process NP relative class, the following "while" should be '//'*/
//												

													
							        				bufferedWriter.write("------");
													bufferedWriter.newLine();
													bufferedWriter.flush();
													
													if(!CountedList.contains(NNList.elementAt(id).toString()))
													{
														CountedList.add(NNList.elementAt(id).toString());
													}
													if(!CountedList.contains(NNList.elementAt(id1).toString()))
													{
														CountedList.add(NNList.elementAt(id1).toString());
													}
													
												
						        			}
										}
									 itor1=tempset1.iterator();
					        		
					        	}
					        }
							 
							 
					        if(CountedList.size()!=NNList.size())
					        {
					        	for(int a=0; a<NNList.size();a++)
					        	{
					        		while(itor1.hasNext())
					        		{
					        			Map.Entry mapentry11 = (Map.Entry) itor1.next(); 
					        			if(!CountedList.contains(NNList.elementAt(a)))
						        		{
						        			if(mapentry11.getKey().toString().equals(NNList.elementAt(a).toString()))
						        			{
	//					        			
//						        				String support1="select * from retestrdpnsitemsupport where itemsets='"+NNList.elementAt(a).toString()+"'";
						        				String support1="select * from sitemsupport where itemsets='"+NNList.elementAt(a).toString()+"'";
						        				
						        				ResultSet s1=statement2.executeQuery(support1);
						        				bufferedWriter.write(NNList.elementAt(a).toString()+"-->"+ ((Integer)tempMap.get(NNList.elementAt(a).toString())).intValue());
												bufferedWriter.newLine();
												bufferedWriter.flush();
												/*when process NP relative class, the following "while" should be '//'*/
				
	
						        				bufferedWriter.write("------");
												bufferedWriter.newLine();
												bufferedWriter.flush();
						        			}
						        		}
					        		}
					        		itor1=tempset1.iterator();
					        	}
					        }
					        
					        
					    	bufferedWriter.write("*******************");
							bufferedWriter.newLine();
							bufferedWriter.flush();
			        }
					        bufferedWriter.write("--------------------------------");
							bufferedWriter.newLine();
							bufferedWriter.flush();
			    }
					
					NounList.removeAllElements();
					nounlist=null;
					
					 bufferedWriter.write("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
					bufferedWriter.newLine();
					bufferedWriter.flush();
				}

//				}
					
					
				
			}
				conn.close();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
		System.out.println("DONE");
	}
}
