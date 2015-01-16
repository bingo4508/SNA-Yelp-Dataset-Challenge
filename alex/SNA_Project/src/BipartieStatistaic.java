import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

public class BipartieStatistaic {

	public static final String graphFileName = "dataCreated\\graph.txt";
	public static final String trainingFileName = "dataCreated\\training.txt(else)";
	public static final String testingInputFileName3 = "dataCreated\\test_data_q3.txt";
	public static final String testingAnsFileName3 = "dataCreated\\test_data_a3.txt";
	public static final String testingInputFileName2 = "dataCreated\\test_data_q2.txt";
	public static final String testingAnsFileName2 = "dataCreated\\test_data_a2.txt";
	public static final String testingInputFileName1 = "dataCreated\\test_data_q1.txt";
	public static final String testingAnsFileName1 = "dataCreated\\test_data_a1.txt";
	public static final String SVMtrainingInputFileName1 = "dataCreated\\SVM_training_data_q1.txt";
	public static final String SVMtrainingAnsFileName1 = "dataCreated\\SVM_training_data_a1.txt";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, HashSet<String>> ideaAdoptersList = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> adopterIdeasList = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> friendsList = new HashMap<String, HashSet<String>>();
		training(trainingFileName, ideaAdoptersList, adopterIdeasList);
		inputSocialNetwork(graphFileName, friendsList);
		
		outputCommonNeighborFeatures("dataCreated\\BipartieCommonNeighbor_training2",SVMtrainingInputFileName1, SVMtrainingAnsFileName1, ideaAdoptersList, adopterIdeasList);
		outputCommonNeighborFeatures("dataCreated\\BipartieCommonNeighbor_testing2", testingInputFileName1, testingAnsFileName1, ideaAdoptersList, adopterIdeasList);
		/*testingCommonNeighbor(testingInputFileName1, testingAnsFileName1, ideaAdoptersList, adopterIdeasList);//0.090940
		testingCommonNeighbor(testingInputFileName2, testingAnsFileName2, ideaAdoptersList, adopterIdeasList);//0.077299
		testingCommonNeighbor(testingInputFileName3, testingAnsFileName3, ideaAdoptersList, adopterIdeasList);//0.068896*/
		/*testingJaccard(testingInputFileName1, testingAnsFileName1, ideaAdoptersList, adopterIdeasList);//0.044728
		testingJaccard(testingInputFileName2, testingAnsFileName2, ideaAdoptersList, adopterIdeasList);//0.042603
		testingJaccard(testingInputFileName3, testingAnsFileName3, ideaAdoptersList, adopterIdeasList);//0.042170*/
		/*testingAdar(testingInputFileName1, testingAnsFileName1, ideaAdoptersList, adopterIdeasList);//0.090447
		testingAdar(testingInputFileName2, testingAnsFileName2, ideaAdoptersList, adopterIdeasList);//0.078008
		testingAdar(testingInputFileName3, testingAnsFileName3, ideaAdoptersList, adopterIdeasList);//0.068665*/
		/*testingGatherStrength(testingInputFileName1, testingAnsFileName1, ideaAdoptersList, adopterIdeasList);//exp: 0.076415, pow_1.2: 0.089335, ifElse_>4*2: 0.090245, ifElse_>10*2: 0.088031
		testingGatherStrength(testingInputFileName2, testingAnsFileName2, ideaAdoptersList, adopterIdeasList);//pow_1.2: 0.076488, ifElse_>10*2: 0.076276
		testingGatherStrength(testingInputFileName3, testingAnsFileName3, ideaAdoptersList, adopterIdeasList);//pow_1.2: 0.067575, ifElse_>10*2: 0.068896*/
		/*testingFriendEnhanced(testingInputFileName1, testingAnsFileName1, ideaAdoptersList, adopterIdeasList, friendsList);//0.091141
		testingFriendEnhanced(testingInputFileName2, testingAnsFileName2, ideaAdoptersList, adopterIdeasList, friendsList);//0.077304
		testingFriendEnhanced(testingInputFileName3, testingAnsFileName3, ideaAdoptersList, adopterIdeasList, friendsList);//0.068796*/
	}

	private static void training(String trainingFileName, HashMap<String, HashSet<String>> ideaAdoptersList, HashMap<String, HashSet<String>> adopterIdeasList)
	{
		BufferedReader input = null;
		String line = null;
		String token[];
		String nodeID;
		String idea;
		HashSet<String> adoptersList;
		HashSet<String> ideasList;
		
		try 
		{
			input = new BufferedReader(new FileReader(trainingFileName));
			line = input.readLine();
			while(line != null)
			{
				if (!line.equals(""))
				{
					token = line.split(" ");
					nodeID = token[0];
					idea = token[1];
					
					if (ideaAdoptersList.containsKey(idea))
						ideaAdoptersList.get(idea).add(nodeID);
					else
					{
						adoptersList = new HashSet<String>();
						adoptersList.add(nodeID);
						ideaAdoptersList.put(idea, adoptersList);
					}
					if (adopterIdeasList.containsKey(nodeID))
						adopterIdeasList.get(nodeID).add(idea);
					else
					{
						ideasList = new HashSet<String>();
						ideasList.add(idea);
						adopterIdeasList.put(nodeID, ideasList);
					}
				}
				line = input.readLine();
			}
			input.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void inputSocialNetwork(String graphFileName, HashMap<String, HashSet<String>> friendsList)
	{
		BufferedReader input = null;
		String line = null;
		String nodes[] = new String[3];
		HashSet<String> friends;
		
		try 
		{
			input = new BufferedReader(new FileReader(graphFileName));
			
			line = input.readLine();
			while (line != null)
			{
				if (!line.equals(""))
				{
					nodes = line.split(" ");
					friends = new HashSet<String>();
					for (int i = 1; i < nodes.length; i++)
						friends.add(nodes[i]);
					friendsList.put(nodes[0], friends);
				}
				line = input.readLine();
			}
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void outputCommonNeighborFeatures(String outputFileName, String testingInputFileName, String testingAnsFileName, 
			HashMap<String, HashSet<String>> ideaAdoptersList, HashMap<String, HashSet<String>> adopterIdeasList)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		HashMap<String, Integer> rankAdopters = new HashMap<String, Integer>();
		HashSet<String> outAns = new HashSet<String>();
		HashSet<String> initialAdopters = new HashSet<String>();
		HashSet<String> groundTruth = new HashSet<String>();
		PrintStream featuresOutput = null;
		int businessOrder = 1;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			featuresOutput = new PrintStream(new File(outputFileName));
			line = input.readLine();
			while(line != null)
			{
				//collect initial adopters
				nodes = line.split(" ");
				initialAdopters.clear();
				for (String nStr:nodes)
					initialAdopters.add(nStr);
				//rank adopters based on initial adopters
				rankAdopters.clear();
				for (String nStr:initialAdopters)
				{
					if (!nStr.equals(""))
					{
						if (adopterIdeasList.containsKey(nStr))
						{
							for (String idea : adopterIdeasList.get(nStr))
								for (String otherAdopter : ideaAdoptersList.get(idea))
								{
									if (rankAdopters.containsKey(otherAdopter))
										rankAdopters.put(otherAdopter, rankAdopters.get(otherAdopter)+1);
									else
										rankAdopters.put(otherAdopter, 1);
								}
						}
					}
				}
				//collect top 200
				outAns.clear();
				int bestRank = 0;
				String best;
				for (int i = 0; i < 200; i++)
				{
					best = null;
					bestRank = 0;
					for (String otherAdopter : rankAdopters.keySet())
					{
						if (!initialAdopters.contains(otherAdopter) && !outAns.contains(otherAdopter) && rankAdopters.get(otherAdopter) > bestRank)
						{
							bestRank = rankAdopters.get(otherAdopter);
							best = otherAdopter;
						}
					}
					if (best != null)
					{
						outAns.add(best);
						//rankAdopters.remove(best);
					}
					else
						i = 200;
				}
				//find the best rank to normalize
				/*bestRank = 0;
				for (String otherAdopter : outAns)
					if (rankAdopters.get(otherAdopter) > bestRank)
						bestRank = rankAdopters.get(otherAdopter);*/
				//collect the groundTruth
				line = answer.readLine();
				nodes = line.split(" ");
				groundTruth.clear();
				for (int i = 0; i < 100 && i < nodes.length; i++)
					groundTruth.add(nodes[i]);
				
				int index = 0;
				for (String user:outAns)
				{
					if (groundTruth.contains(user))
						featuresOutput.println(""+businessOrder+" "+user+" "+(200-index)+" 1");
					else
						featuresOutput.println(""+businessOrder+" "+user+" "+(200-index)+" 0");
					index++;
				}
				
				System.out.println(businessOrder++);
				line = input.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testingCommonNeighbor(String testingInputFileName, String testingAnsFileName, 
			HashMap<String, HashSet<String>> ideaAdoptersList, HashMap<String, HashSet<String>> adopterIdeasList)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		HashMap<String, Integer> rankAdopters = new HashMap<String, Integer>();
		HashSet<String> outAns = new HashSet<String>();
		HashSet<String> initialAdopters = new HashSet<String>();
		double avgFscore = 0;
		double avgPrecision = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			line = input.readLine();
			while(line != null)
			{
				//collect initial adopters
				nodes = line.split(" ");
				initialAdopters.clear();
				for (String nStr:nodes)
					initialAdopters.add(nStr);
				//rank adopters based on initial adopters
				rankAdopters.clear();
				for (String nStr:initialAdopters)
				{
					if (!nStr.equals(""))
					{
						if (adopterIdeasList.containsKey(nStr))
						{
							for (String idea : adopterIdeasList.get(nStr))
								for (String otherAdopter : ideaAdoptersList.get(idea))
								{
									if (rankAdopters.containsKey(otherAdopter))
										rankAdopters.put(otherAdopter, rankAdopters.get(otherAdopter)+1);
									else
										rankAdopters.put(otherAdopter, 1);
								}
						}
					}
				}
				//collect top 100 adopters
				outAns.clear();
				int bestRank = 0;
				String best;
				for (int i = 0; i < 100; i++)
				{
					best = null;
					bestRank = 0;
					for (String otherAdopter : rankAdopters.keySet())
					{
						if (!initialAdopters.contains(otherAdopter) && rankAdopters.get(otherAdopter) > bestRank)
						{
							bestRank = rankAdopters.get(otherAdopter);
							best = otherAdopter;
						}
					}
					if (best != null)
					{
						outAns.add(best);
						rankAdopters.remove(best);
					}
					else
						i = 100;
				}
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(nodes[i]))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing");
				else if (tp == 0)
					System.out.print("no true positive");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore += fScore;
					avgPrecision += precision;
					//System.out.print(fScore);
				}
				//System.out.println("\trecall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			avgPrecision /= 100;
			System.out.println();
			System.out.println("Average F score: "+avgFscore);
			System.out.println("Average Precision: "+avgPrecision);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testingJaccard(String testingInputFileName, String testingAnsFileName, 
			HashMap<String, HashSet<String>> ideaAdoptersList, HashMap<String, HashSet<String>> adopterIdeasList)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		HashMap<String, Double> rankAdopters = new HashMap<String, Double>();
		HashMap<String, Double> jointSize = new HashMap<String, Double>();
		HashSet<String> union = new HashSet<String>();
		HashSet<String> outAns = new HashSet<String>();
		HashSet<String> initialAdopters = new HashSet<String>();
		double avgFscore = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			line = input.readLine();
			while(line != null)
			{
				//collect initial adopters
				nodes = line.split(" ");
				initialAdopters.clear();
				for (String nStr:nodes)
					initialAdopters.add(nStr);
				//rank adopters based on initial adopters
				rankAdopters.clear();
				for (String nStr:initialAdopters)
				{
					if (!nStr.equals(""))
					{
						if (adopterIdeasList.containsKey(nStr))
						{
							jointSize.clear();
							for (String idea : adopterIdeasList.get(nStr))
								for (String otherAdopter : ideaAdoptersList.get(idea))
								{
									if (jointSize.containsKey(otherAdopter))
										jointSize.put(otherAdopter, jointSize.get(otherAdopter)+1);
									else
										jointSize.put(otherAdopter, (double)1);
								}
							for (String otherAdopter : jointSize.keySet())
							{
								union.clear();
								union.addAll(adopterIdeasList.get(nStr));
								union.addAll(adopterIdeasList.get(otherAdopter));
								if (rankAdopters.containsKey(otherAdopter))
									rankAdopters.put(otherAdopter, rankAdopters.get(otherAdopter) + jointSize.get(otherAdopter) / union.size());
								else
									rankAdopters.put(otherAdopter, jointSize.get(otherAdopter) / union.size());
							}
						}
					}
				}
				//collect top 100 adopters
				outAns.clear();
				double bestRank = 0;
				String best;
				for (int i = 0; i < 100; i++)
				{
					best = null;
					bestRank = 0;
					for (String otherAdopter : rankAdopters.keySet())
					{
						if (!initialAdopters.contains(otherAdopter) && rankAdopters.get(otherAdopter) > bestRank)
						{
							bestRank = rankAdopters.get(otherAdopter);
							best = otherAdopter;
						}
					}
					if (best != null)
					{
						outAns.add(best);
						rankAdopters.remove(best);
					}
					else
						i = 100;
				}
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(nodes[i]))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing");
				else if (tp == 0)
					System.out.print("no true positive");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore += fScore;
					System.out.print(fScore);
				}
				System.out.println("\trecall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			System.out.println("Average F score: "+avgFscore);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testingAdar(String testingInputFileName, String testingAnsFileName, 
			HashMap<String, HashSet<String>> ideaAdoptersList, HashMap<String, HashSet<String>> adopterIdeasList)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		HashMap<String, Double> rankAdopters = new HashMap<String, Double>();
		HashSet<String> outAns = new HashSet<String>();
		HashSet<String> initialAdopters = new HashSet<String>();
		double avgFscore = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			line = input.readLine();
			while(line != null)
			{
				//collect initial adopters
				nodes = line.split(" ");
				initialAdopters.clear();
				for (String nStr:nodes)
					initialAdopters.add(nStr);
				//rank adopters based on initial adopters
				rankAdopters.clear();
				for (String nStr:initialAdopters)
				{
					if (!nStr.equals(""))
					{
						if (adopterIdeasList.containsKey(nStr))
						{
							for (String idea : adopterIdeasList.get(nStr))
								for (String otherAdopter : ideaAdoptersList.get(idea))
								{
									if (rankAdopters.containsKey(otherAdopter))
										rankAdopters.put(otherAdopter, rankAdopters.get(otherAdopter)+1./Math.log10(ideaAdoptersList.get(idea).size()));
									else
										rankAdopters.put(otherAdopter, 1./Math.log10(ideaAdoptersList.get(idea).size()));
								}
						}
					}
				}
				//collect top 100 adopters
				outAns.clear();
				double bestRank = 0;
				String best;
				for (int i = 0; i < 100; i++)
				{
					best = null;
					bestRank = 0;
					for (String otherAdopter : rankAdopters.keySet())
					{
						if (!initialAdopters.contains(otherAdopter) && rankAdopters.get(otherAdopter) > bestRank)
						{
							bestRank = rankAdopters.get(otherAdopter);
							best = otherAdopter;
						}
					}
					if (best != null)
					{
						outAns.add(best);
						rankAdopters.remove(best);
					}
					else
						i = 100;
				}
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(nodes[i]))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing");
				else if (tp == 0)
					System.out.print("no true positive");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore += fScore;
					System.out.print(fScore);
				}
				System.out.println("\trecall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			System.out.println("Average F score: "+avgFscore);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testingGatherStrength(String testingInputFileName, String testingAnsFileName, 
			HashMap<String, HashSet<String>> ideaAdoptersList, HashMap<String, HashSet<String>> adopterIdeasList)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		HashMap<String, Double> rankAdopters = new HashMap<String, Double>();
		HashMap<String, Integer> togetherTimes = new HashMap<String, Integer>();
		HashSet<String> outAns = new HashSet<String>();
		HashSet<String> initialAdopters = new HashSet<String>();
		double avgFscore = 0;
		double avgPrecision = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			line = input.readLine();
			while(line != null)
			{
				//collect initial adopters
				nodes = line.split(" ");
				initialAdopters.clear();
				for (String nStr:nodes)
					initialAdopters.add(nStr);
				//rank adopters based on initial adopters
				rankAdopters.clear();
				for (String nStr:initialAdopters)
				{
					if (!nStr.equals(""))
					{
						togetherTimes.clear();
						if (adopterIdeasList.containsKey(nStr))
						{
							for (String idea : adopterIdeasList.get(nStr))
								for (String otherAdopter : ideaAdoptersList.get(idea))
								{
									if (togetherTimes.containsKey(otherAdopter))
										togetherTimes.put(otherAdopter, togetherTimes.get(otherAdopter)+1);
									else
										togetherTimes.put(otherAdopter, 1);
								}
						}
						for (String otherAdopter : togetherTimes.keySet())
						{
							if (rankAdopters.containsKey(otherAdopter))
								rankAdopters.put(otherAdopter, rankAdopters.get(otherAdopter) + (togetherTimes.get(otherAdopter) > 10 ? 2.*togetherTimes.get(otherAdopter) : togetherTimes.get(otherAdopter)));
							else
								rankAdopters.put(otherAdopter, (togetherTimes.get(otherAdopter) > 10 ? 2.*togetherTimes.get(otherAdopter) : togetherTimes.get(otherAdopter)));
						}
					}
				}
				//collect top 100 adopters
				outAns.clear();
				double bestRank = 0;
				String best;
				for (int i = 0; i < 100; i++)
				{
					best = null;
					bestRank = 0;
					for (String otherAdopter : rankAdopters.keySet())
					{
						if (!initialAdopters.contains(otherAdopter) && rankAdopters.get(otherAdopter) > bestRank)
						{
							bestRank = rankAdopters.get(otherAdopter);
							best = otherAdopter;
						}
					}
					if (best != null)
					{
						outAns.add(best);
						rankAdopters.remove(best);
					}
					else
						i = 100;
				}
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(nodes[i]))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing");
				else if (tp == 0)
					System.out.print("no true positive");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore += fScore;
					avgPrecision += precision;
					//System.out.print(fScore);
				}
				//System.out.println("\trecall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			avgPrecision /= 100;
			System.out.println();
			System.out.println("Average F score: "+avgFscore);
			System.out.println("Average Precision: "+avgPrecision);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testingFriendEnhanced(String testingInputFileName, String testingAnsFileName, 
			HashMap<String, HashSet<String>> ideaAdoptersList, HashMap<String, HashSet<String>> adopterIdeasList, HashMap<String, HashSet<String>> friendsList)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		HashMap<String, Double> rankAdopters = new HashMap<String, Double>();
		HashSet<String> outAns = new HashSet<String>();
		HashSet<String> initialAdopters = new HashSet<String>();
		double avgFscore = 0;
		double avgPrecision = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			line = input.readLine();
			while(line != null)
			{
				//collect initial adopters
				nodes = line.split(" ");
				initialAdopters.clear();
				for (String nStr:nodes)
					initialAdopters.add(nStr);
				//rank adopters based on initial adopters
				rankAdopters.clear();
				for (String nStr:initialAdopters)
				{
					if (!nStr.equals(""))
					{
						if (adopterIdeasList.containsKey(nStr))
						{
							for (String idea : adopterIdeasList.get(nStr))
								for (String otherAdopter : ideaAdoptersList.get(idea))
								{
									if (rankAdopters.containsKey(otherAdopter))
										rankAdopters.put(otherAdopter, rankAdopters.get(otherAdopter)+(friendsList.containsKey(nStr) && friendsList.get(nStr).contains(otherAdopter) ? 1.1 : 1));
									else
										rankAdopters.put(otherAdopter, (friendsList.containsKey(nStr) && friendsList.get(nStr).contains(otherAdopter) ? 1.1 : 1));
								}
						}
						/*if (friendsList.containsKey(nStr))
						{
							for (String friend : friendsList.get(nStr))
							{
								if (rankAdopters.containsKey(friend))
									rankAdopters.put(friend, rankAdopters.get(friend)+1);
								else
									rankAdopters.put(friend, 1);
							}
						}*/
					}
				}
				//collect top 100 adopters
				outAns.clear();
				double bestRank = 0;
				String best;
				for (int i = 0; i < 100; i++)
				{
					best = null;
					bestRank = 0;
					for (String otherAdopter : rankAdopters.keySet())
					{
						if (!initialAdopters.contains(otherAdopter) && rankAdopters.get(otherAdopter) > bestRank)
						{
							bestRank = rankAdopters.get(otherAdopter);
							best = otherAdopter;
						}
					}
					if (best != null)
					{
						outAns.add(best);
						rankAdopters.remove(best);
					}
					else
						i = 100;
				}
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(nodes[i]))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing");
				else if (tp == 0)
					System.out.print("no true positive");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore += fScore;
					avgPrecision += precision;
					//System.out.print(fScore);
				}
				//System.out.println("\trecall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			avgPrecision /= 100;
			System.out.println();
			System.out.println("Average F score: "+avgFscore);
			System.out.println("Average Precision: "+avgPrecision);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}