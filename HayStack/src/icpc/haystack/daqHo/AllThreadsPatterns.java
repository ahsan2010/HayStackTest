package icpc.haystack.daqHo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;


public class AllThreadsPatterns {
	public static void IdentifyPatterns(String inputFile, String TID, int PID)
	{
		try
		{
			Connection conn1=sqlConn();
			File senFile=new File(inputFile);
//			File senFile=new File("ForumBase\\10cases.xml");
			SAXReader reader=new SAXReader();
			Document doc=reader.read(senFile);
			
			Element root=doc.getRootElement(); 
			
			Element docdiv=root.element("document");
			
			Element SenRoot=docdiv.element("sentences");// for original XML; delete it when processing cut XML
			
			Element sentence;
			Element NounWords;
			Element foo;
			Element lem;
			Element pos;
			Element tword;
			String Sentence=null;
			List elements=SenRoot.elements(); // for original XML; delete it when processing cut XML
			List<Element> sentencesList=elements;
			String[] PosSentences=new String[sentencesList.size()];
			Vector SwingDic=new Vector();
			Vector NonuList=new Vector();
			Vector EachIndex=new Vector();
			int index=0;
			String indexList=null;
			Connection conn=sqlConn();
			Connection cons=sqlConn();
			int counter=0;
			String sql15="select itemsets from ditemsupport";
			if(!conn.isClosed())
			{
				Statement statement1=conn.createStatement();
				ResultSet rs1=statement1.executeQuery(sql15);
					while(rs1.next())
					{
						if(!rs1.getString("itemsets").equals(null))
						{
							SwingDic.add(rs1.getString("itemsets").toLowerCase()); // get the dictionary;
						}
					}
			}
			Vector replaceList=new Vector();
			replaceList.add("jframe");
			replaceList.add("jwindow");
			replaceList.add("jlabel");
			replaceList.add("jtextarea");
			replaceList.add("jbutton");
			replaceList.add("jradiobutton");
			replaceList.add("jcheckbox");
			replaceList.add("jcombobox");
			replaceList.add("jlist");
			replaceList.add("jmenubar");
			replaceList.add("jpopupmenu");
			replaceList.add("jtoolbar");
			replaceList.add("jpanel");
			replaceList.add("jtable");
			BufferedReader rein = new BufferedReader(new InputStreamReader( 
	                new FileInputStream("filter.txt"))); 
			Vector filter=new Vector();
			LineNumberReader rereader = new LineNumberReader(rein); 
			String res=rereader.readLine();
			while(res.length()>0)
			{
				filter.add(res.trim().toLowerCase());// get another filter;
				res=rereader.readLine();
			}


			String sql1="select * from allnsnonulist";
			if(!cons.isClosed())
			{
				Statement statement1=cons.createStatement();
				Statement statement2=cons.createStatement();
				ResultSet rs1=statement1.executeQuery(sql1);
				while(rs1.next())
				{
						NonuList.add(rs1.getString("Items"));// NonuList contains all the identified features;
						counter++; // used as the beginner when adding new feature into database;
				}
				rs1.close();
			}
			
			
//			int lastIndex=-1;
			String indexconst=null;
			int sid=1;
			
			for(int i=0; i<sentencesList.size(); i++)
			{
				int nnflag=0;// record whether the current sentence contains noun;
				
				NounWords=sentencesList.get(i).element("tokens");
				sentence=sentencesList.get(i).element("parse");	
				PosSentences[i]=sentence.getTextTrim();
					
				
				String s=sentence.getTextTrim();
				TreeReader tr=new PennTreeReader(new StringReader(s));
				Vector<Tree> tempbank=new Vector(); // used when some subtree node needed be cutted;
				Vector<Tree> bank=new Vector(); // used to collect all the matched subtrees;
				Vector checkedTree=new Vector();
				Vector cand=new Vector();
				Vector temps=new Vector();
				
				Tree tree=tr.readTree();

//				int test=0;
				TregexPattern pattern1=TregexPattern.compile("/S.?/<(VP<</VB.?/)<(NP <</NN.?/ )");
				TregexMatcher matcher1=pattern1.matcher(tree);
				TregexPattern pattern2=TregexPattern.compile("VP</VB.?/<(NP<</NN.?/)");
				TregexMatcher matcher2=pattern2.matcher(tree);
				TregexPattern pattern3=TregexPattern.compile("VP</VB.?/<(PP<</NN.?/)");
				TregexMatcher matcher3=pattern3.matcher(tree);
				
				while(matcher1.findNextMatchingNode())
				{
					bank.add(matcher1.getMatch());
				}
				while(matcher2.findNextMatchingNode())
				{
//					System.out.println(1);
					bank.add(matcher2.getMatch());
				}
				while(matcher3.findNextMatchingNode())
				{
//					System.out.println(matcher3.getMatch().pennString());
					bank.add(matcher3.getMatch());
				}
				
				HashMap ItemPattern=new HashMap();
				int patternFlag=0;
				for(int in=0; in<bank.size(); in++) // to process each matched subtree;
				{
					int k1=bank.size();
					ArrayList<String> words = new ArrayList();
					String candiWord="";
					Tree match=bank.elementAt(in);
					if(!match.equals("null"))
					{
							//for case 1: S--->NP+VP
							if(match.nodeString().equals("S") || match.nodeString().equals("SINV") || match.nodeString().equals("SQ"))
							{
								List<Tree> childList=match.getChildrenAsList();
								for(int id2=0; id2<childList.size();id2++) // to process each child node of current matched subtree;
									{
										Tree currentChild=childList.get(id2);
										if(currentChild.nodeString().equals("VP"))
										{
											List<Tree> tempchildList=currentChild.getChildrenAsList();
											List<Tree> childList1=currentChild.getChildrenAsList();
											for(int id=0;id<tempchildList.size();id++)
											{
												Tree child=tempchildList.get(id);
												Iterator<Tree> i1=child.iterator();
												
												//processing "Be Done" type
												if(child.nodeString().equals("VP"))
												{
													List<Tree> VPchilds=child.getChildrenAsList();
													for(int childid=0; childid<VPchilds.size();childid++)
													{
														Tree subchild=VPchilds.get(childid);
														if(subchild.nodeString().contains("VB") || subchild.nodeString().contains("JJ"))
														{
															if(!words.contains(subchild.getLeaves().toString().toLowerCase()))
															{
																words.add(subchild.getLeaves().toString().toLowerCase());
															}
														}
													}
												}
												
												
												while(i1.hasNext()) // removing the conpound subtree node.
												{
													Tree spring=(Tree)i1.next();
													String springstr=spring.flatten().toString();
													for(int cc=0; cc<bank.size(); cc++)
													{
														String bankmumber=bank.elementAt(cc).flatten().toString().substring(3, bank.elementAt(cc).flatten().toString().length()-2);
														if(springstr.contains(bankmumber))
														{
															childList1.remove(tempchildList.get(id));
														}
													}
												}
											}
											for(int index1=0; index1<childList1.size();index1++)
											{
												Tree realChild=childList1.get(index1);
												Iterator<Tree> i1=realChild.iterator();
												while(i1.hasNext())
												{
													Tree kk=(Tree)i1.next();
														if(kk.nodeString().contains("NN")|| kk.nodeString().contains("JJ") ||kk.nodeString().contains("VB"))//judge whether the nodestring=NN/NNS/NNP
														{
															if(!words.contains(kk.getLeaves().toString().toLowerCase()))
															{
																words.add(kk.getLeaves().toString().toLowerCase());
															}
														}
														if(kk.nodeString().contains("NN"))
														{
															nnflag=1; // nnflag used to record whether the current sentence contains noun.
														}
												}
											}
										}
										else if(currentChild.nodeString().equals("NP"))
										{
											List<Tree> tempchildList=currentChild.getChildrenAsList();
											List<Tree> childList1=currentChild.getChildrenAsList();
											for(int id=0;id<tempchildList.size();id++)
											{
												Tree child=tempchildList.get(id);
												Iterator<Tree> i1=child.iterator();
												while(i1.hasNext())
												{
													Tree spring=(Tree)i1.next();

													String springstr=spring.flatten().toString();
													for(int cc=0; cc<bank.size(); cc++)
													{
														String bankmumber=bank.elementAt(cc).flatten().toString().substring(3, bank.elementAt(cc).flatten().toString().length()-2);
														if(springstr.contains(bankmumber))
														{
															childList1.remove(tempchildList.get(id));
														}
													}
												}
											}
											for(int index1=0; index1<childList1.size();index1++)
											{
												Tree realChild=childList1.get(index1);
												Iterator<Tree> i1=realChild.iterator();
												while(i1.hasNext())
												{
													Tree kk=(Tree)i1.next();
														if(kk.nodeString().contains("NN")|| kk.nodeString().contains("JJ") ||kk.nodeString().contains("VB"))//judge whether the nodestring=NN/NNS/NNP
														{
															if(!words.contains(kk.getLeaves().toString().toLowerCase()))
															{
																words.add(kk.getLeaves().toString().toLowerCase());
															}
														}
														if(kk.nodeString().contains("NN"))
														{
															nnflag=1;
														}
												}
											}
										}
									}
								patternFlag=1;
							}
							
							//for case 2: VP--->/VB?/+NP || for case 3; VP--->/VB?/+(PP--->NP)
							else if(match.nodeString().equals("VP"))
							{
								List<Tree> childList=match.getChildrenAsList();
									for(int id2=0; id2<childList.size();id2++)
									{
										Tree currentChild=childList.get(id2);
										if(currentChild.nodeString().contains("VB"))
										{
											if(!words.contains(currentChild.getLeaves().toString().toLowerCase()))
											{
												words.add(currentChild.getLeaves().toString().toLowerCase());
											}
										}
										else if(currentChild.nodeString().contains("PP"))
										{
											List<Tree> tempchildList=currentChild.getChildrenAsList();
											for(int k=0; k<tempchildList.size(); k++)
											{
												if(tempchildList.get(k).nodeString().equals("NN"))
												{
													if(!words.contains(tempchildList.get(k).getLeaves().toString().toLowerCase()))
													{
														words.add(tempchildList.get(k).getLeaves().toString().toLowerCase());
														nnflag=1;
													}
												}
//												if(tempchildList.get(k).nodeString().equals("NN"))
////												System.out.println(tempchildList.get(k).pennString());
////												if(tempchildList.get(k).pennString().contains("NN"))
//												{
//////													List<Tree> childList1=tempchildList.get(k).getChildrenAsList();
//													List<Tree> childList1=tempchildList.get(k).getLeaves();
//													for(int cid=0; cid<childList1.size(); cid++)
//													{
//														Tree currentChild1=childList1.get(cid);
//														
////														if(currentChild1.nodeString().equals("NN"))
////														{
//															if(!words.contains(currentChild1.getLeaves().toString()))
////															if(!words.contains(tempchildList.get(k).getLeaves().toString().toLowerCase()))
//															{
////																String test=tempchildList.get(k).getLeaves().toString().toLowerCase();
////																words.add(tempchildList.get(k).getLeaves().toString().toLowerCase());
//																String test=currentChild1.getLeaves().toString().toLowerCase();
//																words.add(currentChild1.getLeaves().toString().toLowerCase());
//																nnflag=1;
////															}
//														}
//													}
//												}
//												String test11=tempchildList.get(k).nodeString();
//												if(tempchildList.get(k).nodeString().contains("NP"))
												if(tempchildList.get(k).pennString().contains("NP"))
												{
													
													List<Tree> childList1=tempchildList.get(k).getChildrenAsList();
													List<Tree> tempchildList1=tempchildList.get(k).getChildrenAsList();
													for(int id=0;id<tempchildList1.size();id++)
													{
														Tree child=tempchildList1.get(id);
														Iterator<Tree> i1=child.iterator();
														while(i1.hasNext())
														{
															Tree spring=(Tree)i1.next();
															String springstr=spring.flatten().toString();
															for(int cc=0; cc<bank.size(); cc++)
															{
																String bankmumber=bank.elementAt(cc).flatten().toString().substring(3, bank.elementAt(cc).flatten().toString().length()-2);
																if(springstr.contains(bankmumber))
																{
																	childList1.remove(tempchildList1.get(id));
																}
															}
														}
													}
													for(int index1=0; index1<childList1.size();index1++)
													{
														Tree realChild=childList1.get(index1);
														Iterator<Tree> i1=realChild.iterator();
														while(i1.hasNext())
														{
															Tree kk=(Tree)i1.next();
																if(kk.nodeString().contains("NN")|| kk.nodeString().contains("JJ") ||kk.nodeString().contains("VB"))//judge whether the nodestring=NN/NNS/NNP
																{
																	if(!words.contains(kk.getLeaves().toString().toLowerCase()))
																	{
																		words.add(kk.getLeaves().toString().toLowerCase());
																	}
																}
																if(kk.nodeString().contains("NN"))
																{
																	nnflag=1;
																}
														}
													}
												}
											}
											patternFlag=3;
										}
										else if(currentChild.nodeString().contains("NP"))
										{
											List<Tree> tempchildList=currentChild.getChildrenAsList();
											List<Tree> childList1=currentChild.getChildrenAsList();
											for(int id=0;id<tempchildList.size();id++)
											{
												Tree child=tempchildList.get(id);
												Iterator<Tree> i1=child.iterator();
												while(i1.hasNext())
												{
													Tree spring=(Tree)i1.next();
													String springstr=spring.flatten().toString();
													for(int cc=0; cc<bank.size(); cc++)
													{
														String bankmumber=bank.elementAt(cc).flatten().toString().substring(3, bank.elementAt(cc).flatten().toString().length()-2);
														if(springstr.contains(bankmumber))
														{
															childList1.remove(tempchildList.get(id));
														}
													}
												}
											}
											for(int index1=0; index1<childList1.size();index1++)
											{
												Tree realChild=childList1.get(index1);
												Iterator<Tree> i1=realChild.iterator();
												while(i1.hasNext())
												{
													Tree kk=(Tree)i1.next();
														if(kk.nodeString().contains("NN")|| kk.nodeString().contains("JJ") ||kk.nodeString().contains("VB"))//judge whether the nodestring=NN/NNS/NNP
														{
															if(!words.contains(kk.getLeaves().toString().toLowerCase()))
															{
																words.add(kk.getLeaves().toString().toLowerCase());
															}
														}
														if(kk.nodeString().contains("NN"))
														{
															nnflag=1;
														}
												}
											}
											patternFlag=2;
										}
									}

							}
					}
							
							Collections.sort(words, new CompareString()); // sorting all the identified words in dictionary order.
							
							Iterator<String> it=words.iterator();
							
							while(it.hasNext())
							{
								String next=it.next().toString().replace("[", "");
								next=next.replace("]", "");
								
										for(Iterator j=NounWords.elementIterator("token"); j.hasNext();)
											{
												foo=(Element)j.next();
												lem=foo.element("lemma");
												tword=foo.element("word");
												pos=foo.element("POS");
												if(tword.getTextTrim().toLowerCase().equals(next.toLowerCase()))
												{
													if(!filter.contains(lem.getText().toLowerCase()))
													{
														if(SwingDic.contains(lem.getText().toLowerCase()))
														{
															if(replaceList.contains(lem.getText().toLowerCase()))
															{
																String restring=lem.getText().toLowerCase().substring(1);
																if(!candiWord.contains(restring))
																{
																	candiWord+=restring+"; ";
																}
															}
															
															else
															{
																if(!candiWord.contains(lem.getText().toLowerCase()))
																{
																	candiWord+=lem.getTextTrim().toLowerCase()+"; ";
																}
															}
														}
													}
												}
											}	
									
							}
							int co=0;
							
							temps=cand;//temps used to remove some element;
							for(int c=0; c<cand.size(); c++)
							{
								if(cand.elementAt(c).toString().contains(candiWord))
								{
									co++;
								}
								else if(candiWord.contains(cand.elementAt(c).toString()))
								{
									temps.remove(cand.elementAt(c).toString());
								}
							}
							cand=temps;//after temps remove elements, cand update the element list;
							if(co==0)
							{
								cand.add(candiWord);
							}
							ItemPattern.put(candiWord, patternFlag);
					}
				for(int candid=0; candid<cand.size(); candid++)
				{
					String item=cand.elementAt(candid).toString();
					if(item.length()>0)
					{
						if(!NonuList.contains(item))
						{
							NonuList.add(item);
							
							
							Set set=ItemPattern.entrySet();
							Iterator iterator=set.iterator();
							while(iterator.hasNext())
							{
								Map.Entry mapentry=(Map.Entry)iterator.next();
								if(mapentry.getKey().toString().equals(item))
								{
//									JDBCConnection(conn1 , "insert into allnsnonulist(Items, TreePattern) values ('"+item+"',"+Integer.parseInt(mapentry.getValue().toString())+")", 1);
								}
							}
						}
						index=NonuList.indexOf(item)+1;
							if(indexList!=null)
								indexList+=" "+index;
							else
								indexList=String.valueOf(index);
							indexconst=null;
					}
					
					
					
					System.out.println(item);
					System.out.println("--------");
				}
				
				for(Iterator j=NounWords.elementIterator("token"); j.hasNext();)
				{
					foo=(Element)j.next();
					tword=foo.element("word");
					if(Sentence==null)
						Sentence=tword.getText();
					else
						Sentence+=" "+tword.getText();
				}
				
//				JDBCConnection(conn , "INSERT INTO allnstsentences(NNList, TID, PID, SID) VALUES ('"+indexList+"','"+TID+"',"+PID+","+sid+")",1);
				
				if(Sentence.contains("'"))
				{
					Sentence=Sentence.replaceAll("\\'", "\\\\'");
				}
				
//				JDBCConnection(conn, "insert into allnsdatasource (TID, PID, SID, NegFlag, SenCon, NNFlag) values ('"+TID+"',"+PID+","+sid+",0,'"+Sentence+"',"+nnflag+")",1);
				
				sid++;
				EachIndex.add(indexList);
				indexList=null;
				Sentence=null;
				
				System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
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
