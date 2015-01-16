import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.CompleteLinkageStrategy;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.PDistClusteringAlgorithm;
import com.apporiented.algorithm.clustering.SingleLinkageStrategy;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

public class GroupAvgClustering {
	public static final String graphFileName = "dataCreated\\graph.txt";
	public static final String trainingFileName = "dataCreated\\training.txt";
	public static final String testingInputFileName3 = "dataCreated\\test_data_q3.txt";
	public static final String testingAnsFileName3 = "dataCreated\\test_data_a3.txt";
	public static final String testingInputFileName2 = "dataCreated\\test_data_q2.txt";
	public static final String testingAnsFileName2 = "dataCreated\\test_data_a2.txt";
	public static final String testingInputFileName1 = "dataCreated\\test_data_q1.txt";
	public static final String testingAnsFileName1 = "dataCreated\\test_data_a1.txt";
	private static class Idea
	{
		public String nodeID;
		public String ideaID;
		public Date date;
		public double adoptDeg;
		public Idea(String nodeID, String ideaID, Date date, double adoptDeg)
		{
			this.nodeID = nodeID;
			this.ideaID = ideaID;
			this.date = date;
			this.adoptDeg = adoptDeg;
		}
		public String toString()
		{
			return ""+nodeID+ " "+ideaID+" "+date+" "+adoptDeg;
		}
		@Override
		public boolean equals(Object obj)
		{
			Idea idea = (Idea)obj;
			return ideaID.equals(idea.ideaID);
		}
		
		static class theComparator implements Comparator<Idea>{
			 
		    @Override
		    public int compare(Idea e1, Idea e2) {
		    	if (e1.ideaID.equals(e2.ideaID))
		    	{
		    		int dateCompare = e1.date.compareTo(e2.date);
		    		if (dateCompare == 0)
		    			return e1.nodeID.compareTo(e2.nodeID);
		    		else
		    			return dateCompare;
		    	}
		    	else
		    		return e1.ideaID.compareTo(e2.ideaID);
		    }
		}
	}
	
	private static class Edge
	{
		public static int count = 0;
	}

	private static class Vertex
	{
		public static int count = 0;
		public static enum State{INACTIVE, NEWLY_ACTIVE, ACTIVE}; 
		public int vertexIndex;
		public State state;
		public ArrayList<Idea> ideaList;
		public Vertex(int vertexIndex, State state)
		{
			count++;
			this.vertexIndex = vertexIndex;
			this.state = state;
			ideaList = new ArrayList<Idea>();
		}
	}
	
	public static void main(String[] args)
	{
		/*Graph<Vertex, Edge> g;
		HashMap<Integer, Vertex> vertices = new HashMap<Integer, Vertex>();
		g = createGraph(graphFileName, vertices);*/
		HashMap<String, SortedSet<Idea>> trainingElement = new HashMap<String, SortedSet<Idea>>();
		HashMap<String, HashSet<String>> ideaAdopterMap = new HashMap<String, HashSet<String>>();
		ideaTrainingInput(trainingFileName, ideaAdopterMap, trainingElement);
		int k3 = 50;
		int k2 = 70;
		int k1 = 55;
		
		runClustering(k3, ideaAdopterMap, true, 1000);
		/*ArrayList<HashSet<Integer>> clusteredIdea3 = runClustering(k3, ideaAdopterMap, false);
		ArrayList<HashSet<Integer>> clusteredIdea2 = runClustering(k2, ideaAdopterMap, false);
		ArrayList<HashSet<Integer>> clusteredIdea1 = runClustering(k1, ideaAdopterMap, false);*/
		
		/*testingQ3(testingInputFileName3, testingAnsFileName3, ideaAdopterMap, clusteredIdea3, trainingElement);
		testingQ2(testingInputFileName2, testingAnsFileName2, ideaAdopterMap, clusteredIdea2, trainingElement);
		testingQ1(testingInputFileName1, testingAnsFileName1, ideaAdopterMap, clusteredIdea1, trainingElement);*/
		//testing(testingInputFileName, testingAnsFileName, ideaAdopterMap, clusteredIdea, trainingElement, g, vertices);
	}
	
	private static void ideaTrainingInput(String trainingFileName,
			HashMap<String, HashSet<String>> ideaAdopterMap, HashMap<String, SortedSet<Idea>> trainingElement)
	{
		BufferedReader input = null;
		String line = null;
		String token[];
		String nodeID;
		String idea;
		Date date;
		double adoptDeg;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		
		HashSet<String> adopterList;
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
					date = dateFormater.parse(token[2]);
					adoptDeg = Double.parseDouble(token[3]);
					
					if (!trainingElement.containsKey(idea))
						trainingElement.put(idea, new TreeSet<Idea>(new Idea.theComparator()));
					trainingElement.get(idea).add(new Idea(nodeID, idea, date, adoptDeg));
					
					adopterList = ideaAdopterMap.get(idea);
					if (adopterList == null)
					{
						adopterList = new HashSet<String>();
						adopterList.add(nodeID);
						ideaAdopterMap.put(idea,  adopterList);
					}
					else
						adopterList.add(nodeID);
				}
				line = input.readLine();
			}
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/*
	private static void testingQ1(String testingInputFileName, String testingAnsFileName, 
			HashMap<Integer, HashSet<Integer>> ideaAdopterMap, ArrayList<HashSet<Integer>> clusteredIdea, HashMap<Integer, SortedSet<Idea>> trainingElement)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		double avgFscore = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			PrintStream outAnsFile = new PrintStream(new File("outAnswer.txt"));
			line = input.readLine();
			while(line != null)
			{
				HashSet<Integer> initialAdopterList = new HashSet<Integer>();
				nodes = line.split(" ");
				for (String nStr:nodes)
					if (!nStr.equals(""))
						initialAdopterList.add(Integer.parseInt(nStr));
				
				HashSet<Integer> anotherAdopterList;
				double distance;
				double biggestDistance;
				double smallestBiggestDistance = 2;
				HashSet<Integer> closestCluster = null;
				for (HashSet<Integer> c:clusteredIdea)
				{
					if (c.size() > 18)
					{
						biggestDistance = -1;
						for (int idea:c)
						{
							anotherAdopterList = ideaAdopterMap.get(idea);
							distance = getIdeaDistance(initialAdopterList, anotherAdopterList);
							if (distance > biggestDistance)
							{
								biggestDistance = distance;
							}
						}
						if (biggestDistance < smallestBiggestDistance)
						{
							smallestBiggestDistance = biggestDistance;
							closestCluster = c;
						}
					}
				}
				
				HashSet<Integer> secondCloseCluster = null;
				smallestBiggestDistance = 2;
				for (HashSet<Integer> c:clusteredIdea)
				{
					if (c.size() > 10 && c != closestCluster)
					{
						biggestDistance = -1;
						for (int idea:c)
						{
							anotherAdopterList = ideaAdopterMap.get(idea);
							distance = getIdeaDistance(initialAdopterList, anotherAdopterList);
							if (distance > biggestDistance)
							{
								biggestDistance = distance;
							}
						}
						if (biggestDistance < smallestBiggestDistance)
						{
							smallestBiggestDistance = biggestDistance;
							secondCloseCluster = c;
						}
					}
				}
				
				HashMap<Integer, Double> rankUser = new HashMap<Integer, Double>();
				double temporalOrder;
				Date dateNow;
				for (int idea:closestCluster)
				{
					temporalOrder = 0;
					dateNow = null;
					for (Idea eachIdea:trainingElement.get(idea))
					{
						if (temporalOrder < 450)
						{
							if (rankUser.containsKey(eachIdea.nodeID))
								rankUser.put(eachIdea.nodeID, rankUser.get(eachIdea.nodeID)+Math.exp(-temporalOrder*0));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.3+1:1));
							else
								rankUser.put(eachIdea.nodeID, Math.exp(-temporalOrder*0));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.3+1:1));
							if (!eachIdea.date.equals(dateNow))
							{
								temporalOrder+=1;
								dateNow = eachIdea.date;
							}
						}
						else
							break;
					}
				}
				
				for (int idea:secondCloseCluster)
				{
					temporalOrder = 0;
					dateNow = null;
					for (Idea eachIdea:trainingElement.get(idea))
					{
						if (temporalOrder < 450)
						{
							if (rankUser.containsKey(eachIdea.nodeID))
								rankUser.put(eachIdea.nodeID, rankUser.get(eachIdea.nodeID)+(Math.exp(-temporalOrder*0)));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1))*0.3);
							else
								rankUser.put(eachIdea.nodeID, (Math.exp(-temporalOrder*0)));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1))*0.3);
							if (!eachIdea.date.equals(dateNow))
							{
								temporalOrder+=1;
								dateNow = eachIdea.date;
							}
						}
						else
							break;
					}
				}
				
				HashSet<Integer> outAns = new HashSet<Integer>();
				double bestRank;
				int bestUser = 0;
				for (int i = 0; i < 100 && !rankUser.isEmpty(); i++)
				{
					bestRank = 0;
					for(int user:rankUser.keySet())
						if (rankUser.get(user) > bestRank)
						{
							bestUser = user;
							bestRank = rankUser.get(user);
						}
					if (!initialAdopterList.contains(bestUser))
						outAns.add(bestUser);
					else
						i--;
					rankUser.remove(bestUser);
				}
				
				//output our answer
				for (int ans:outAns)
					outAnsFile.print(""+ans+" ");
				outAnsFile.println();
				
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(Integer.parseInt(nodes[i])))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing\t");
				else if (tp == 0)
					System.out.print("no true positive\t");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore+=fScore;
					System.out.print(fScore + "\t");
				}
				System.out.println("recall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			System.out.println("Average F score: "+avgFscore);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testingQ2(String testingInputFileName, String testingAnsFileName, 
			HashMap<Integer, HashSet<Integer>> ideaAdopterMap, ArrayList<HashSet<Integer>> clusteredIdea, HashMap<Integer, SortedSet<Idea>> trainingElement)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		double avgFscore = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			PrintStream outAnsFile = new PrintStream(new File("outAnswer.txt"));
			line = input.readLine();
			while(line != null)
			{
				HashSet<Integer> initialAdopterList = new HashSet<Integer>();
				nodes = line.split(" ");
				for (String nStr:nodes)
					if (!nStr.equals(""))
						initialAdopterList.add(Integer.parseInt(nStr));
				
				HashSet<Integer> anotherAdopterList;
				double distance;
				double biggestDistance;
				double smallestBiggestDistance = 2;
				HashSet<Integer> closestCluster = null;
				for (HashSet<Integer> c:clusteredIdea)
				{
					if (c.size() > 10)
					{
						biggestDistance = -1;
						for (int idea:c)
						{
							anotherAdopterList = ideaAdopterMap.get(idea);
							distance = getIdeaDistance(initialAdopterList, anotherAdopterList);
							if (distance > biggestDistance)
							{
								biggestDistance = distance;
							}
						}
						if (biggestDistance < smallestBiggestDistance)
						{
							smallestBiggestDistance = biggestDistance;
							closestCluster = c;
						}
					}
				}
				
				HashSet<Integer> secondCloseCluster = null;
				smallestBiggestDistance = 2;
				for (HashSet<Integer> c:clusteredIdea)
				{
					if (c.size() > 10 && c != closestCluster)
					{
						biggestDistance = -1;
						for (int idea:c)
						{
							anotherAdopterList = ideaAdopterMap.get(idea);
							distance = getIdeaDistance(initialAdopterList, anotherAdopterList);
							if (distance > biggestDistance)
							{
								biggestDistance = distance;
							}
						}
						if (biggestDistance < smallestBiggestDistance)
						{
							smallestBiggestDistance = biggestDistance;
							secondCloseCluster = c;
						}
					}
				}
				
				HashMap<Integer, Double> rankUser = new HashMap<Integer, Double>();
				double temporalOrder;
				Date dateNow;
				for (int idea:closestCluster)
				{
					temporalOrder = 0;
					dateNow = null;
					for (Idea eachIdea:trainingElement.get(idea))
					{
						if (temporalOrder < 450)
						{
							if (rankUser.containsKey(eachIdea.nodeID))
								rankUser.put(eachIdea.nodeID, rankUser.get(eachIdea.nodeID)+Math.exp(-temporalOrder*0));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.1+1:1));
							else
								rankUser.put(eachIdea.nodeID, Math.exp(-temporalOrder*0));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.1+1:1));
							if (!eachIdea.date.equals(dateNow))
							{
								temporalOrder+=1;
								dateNow = eachIdea.date;
							}
						}
						else
							break;
					}
				}
				
				for (int idea:secondCloseCluster)
				{
					temporalOrder = 0;
					dateNow = null;
					for (Idea eachIdea:trainingElement.get(idea))
					{
						if (temporalOrder < 450)
						{
							if (rankUser.containsKey(eachIdea.nodeID))
								rankUser.put(eachIdea.nodeID, rankUser.get(eachIdea.nodeID)+(Math.exp(-temporalOrder*0)));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1))*0.3);
							else
								rankUser.put(eachIdea.nodeID, (Math.exp(-temporalOrder*0)));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1))*0.3);
							if (!eachIdea.date.equals(dateNow))
							{
								temporalOrder+=1;
								dateNow = eachIdea.date;
							}
						}
						else
							break;
					}
				}
				
				HashSet<Integer> outAns = new HashSet<Integer>();
				double bestRank;
				int bestUser = 0;
				for (int i = 0; i < 100 && !rankUser.isEmpty(); i++)
				{
					bestRank = 0;
					for(int user:rankUser.keySet())
						if (rankUser.get(user) > bestRank)
						{
							bestUser = user;
							bestRank = rankUser.get(user);
						}
					if (!initialAdopterList.contains(bestUser))
						outAns.add(bestUser);
					else
						i--;
					rankUser.remove(bestUser);
				}
				
				//output our answer
				for (int ans:outAns)
					outAnsFile.print(""+ans+" ");
				outAnsFile.println();
				
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(Integer.parseInt(nodes[i])))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing\t");
				else if (tp == 0)
					System.out.print("no true positive\t");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore+=fScore;
					System.out.print(fScore + "\t");
				}
				System.out.println("recall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			System.out.println("Average F score: "+avgFscore);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testingQ3(String testingInputFileName, String testingAnsFileName, 
			HashMap<Integer, HashSet<Integer>> ideaAdopterMap, ArrayList<HashSet<Integer>> clusteredIdea, HashMap<Integer, SortedSet<Idea>> trainingElement)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		double avgFscore = 0;
		try 
		{
			input = new BufferedReader(new FileReader(testingInputFileName));
			answer = new BufferedReader(new FileReader(testingAnsFileName));
			PrintStream outAnsFile = new PrintStream(new File("outAnswer.txt"));
			line = input.readLine();
			while(line != null)
			{
				HashSet<Integer> initialAdopterList = new HashSet<Integer>();
				nodes = line.split(" ");
				for (String nStr:nodes)
					if (!nStr.equals(""))
						initialAdopterList.add(Integer.parseInt(nStr));
				
				HashSet<Integer> anotherAdopterList;
				double distance;
				double biggestDistance;
				double smallestBiggestDistance = 2;
				HashSet<Integer> closestCluster = null;
				for (HashSet<Integer> c:clusteredIdea)
				{
					if (c.size() > 10)
					{
						biggestDistance = -1;
						for (int idea:c)
						{
							anotherAdopterList = ideaAdopterMap.get(idea);
							distance = getIdeaDistance(initialAdopterList, anotherAdopterList);
							if (distance > biggestDistance)
							{
								biggestDistance = distance;
							}
						}
						if (biggestDistance < smallestBiggestDistance)
						{
							smallestBiggestDistance = biggestDistance;
							closestCluster = c;
						}
					}
				}
				
				HashSet<Integer> secondCloseCluster = null;
				smallestBiggestDistance = 2;
				for (HashSet<Integer> c:clusteredIdea)
				{
					if (c.size() > 10 && c != closestCluster)
					{
						biggestDistance = -1;
						for (int idea:c)
						{
							anotherAdopterList = ideaAdopterMap.get(idea);
							distance = getIdeaDistance(initialAdopterList, anotherAdopterList);
							if (distance > biggestDistance)
							{
								biggestDistance = distance;
							}
						}
						if (biggestDistance < smallestBiggestDistance)
						{
							smallestBiggestDistance = biggestDistance;
							secondCloseCluster = c;
						}
					}
				}
				
				HashMap<Integer, Double> rankUser = new HashMap<Integer, Double>();
				double temporalOrder;
				Date dateNow;
				for (int idea:closestCluster)
				{
					temporalOrder = 0;
					dateNow = null;
					for (Idea eachIdea:trainingElement.get(idea))
					{
						if (temporalOrder < 400)
						{
							if (rankUser.containsKey(eachIdea.nodeID))
								rankUser.put(eachIdea.nodeID, rankUser.get(eachIdea.nodeID)+Math.exp(-temporalOrder*0));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1));
							else
								rankUser.put(eachIdea.nodeID, Math.exp(-temporalOrder*0));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1));
							if (!eachIdea.date.equals(dateNow))
							{
								temporalOrder+=1;
								dateNow = eachIdea.date;
							}
						}
						else
							break;
					}
				}
				
				for (int idea:secondCloseCluster)
				{
					temporalOrder = 0;
					dateNow = null;
					for (Idea eachIdea:trainingElement.get(idea))
					{
						if (temporalOrder < 400)
						{
							if (rankUser.containsKey(eachIdea.nodeID))
								rankUser.put(eachIdea.nodeID, rankUser.get(eachIdea.nodeID)+(Math.exp(-temporalOrder*0)));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1))*0.3);
							else
								rankUser.put(eachIdea.nodeID, (Math.exp(-temporalOrder*0)));//*(eachIdea.adoptDeg > 0.7 ? (eachIdea.adoptDeg-0.7)*0.5+1:1))*0.3);
							if (!eachIdea.date.equals(dateNow))
							{
								temporalOrder+=1;
								dateNow = eachIdea.date;
							}
						}
						else
							break;
					}
				}
				
				HashSet<Integer> outAns = new HashSet<Integer>();
				double bestRank;
				int bestUser = 0;
				for (int i = 0; i < 100 && !rankUser.isEmpty(); i++)
				{
					bestRank = 0;
					for(int user:rankUser.keySet())
						if (rankUser.get(user) > bestRank)
						{
							bestUser = user;
							bestRank = rankUser.get(user);
						}
					if (!initialAdopterList.contains(bestUser))
						outAns.add(bestUser);
					else
						i--;
					rankUser.remove(bestUser);
				}
				
				//output our answer
				for (int ans:outAns)
					outAnsFile.print(""+ans+" ");
				outAnsFile.println();
				
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					if (outAns.contains(Integer.parseInt(nodes[i])))
						tp++;
					else
						fn++;
				}
				fp = outAns.size() - tp;
				if (outAns.size() == 0)
					System.out.print("predict nothing\t");
				else if (tp == 0)
					System.out.print("no true positive\t");
				else
				{
					double recall = (double)tp/(tp+fn);
					double precision = (double)tp/(tp+fp);
					double fScore = 2*precision*recall/(precision+recall);
					avgFscore+=fScore;
					System.out.print(fScore + "\t");
				}
				System.out.println("recall: "+(double)tp/(tp+fn)+"\tprecision: "+(double)tp/(tp+fp));
				line = input.readLine();
			}
			avgFscore /= 100;
			System.out.println("Average F score: "+avgFscore);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	private static ArrayList<HashSet<String>> runClustering(int k, HashMap<String, HashSet<String>> ideaAdopterMap, boolean reCalculate, int numSampledIdea)
	{
		
		String[] names = new String[numSampledIdea];
		double[][] distances = new double[names.length][names.length];
		ArrayList<String> ideaShuffledList = new ArrayList<String>();
		ideaShuffledList.addAll(ideaAdopterMap.keySet());
		Collections.shuffle(ideaShuffledList, new Random(0));
		try {
			if (reCalculate)
			{
				PrintStream outDistancesNames = new PrintStream(new File("namesDistances2"));
	
				int count = 0;
				for (int i = 0; i < numSampledIdea; i++)
				{
					names[count] = ""+ideaShuffledList.get(i);
					outDistancesNames.print(names[count]+" ");
					count++;
				}
				outDistancesNames.println();
				
				HashSet<String> union = new HashSet<String>();
				HashSet<String> joint = new HashSet<String>();
				HashSet<String> iAdopterList = new HashSet<String>();
				HashSet<String> jAdopterList = new HashSet<String>();
				for (int i = 0; i < names.length; i++)
				{
					for (int j = 0; j < names.length; j++)
					{
						iAdopterList = ideaAdopterMap.get(names[i]);
						jAdopterList = ideaAdopterMap.get(names[j]);
						distances[i][j] = getIdeaDistance(iAdopterList, jAdopterList);
						outDistancesNames.print(""+distances[i][j]+" ");
					}
					outDistancesNames.println();
					System.out.println(i);
				}
			}
			else
			{
				BufferedReader input;
				input = new BufferedReader(new FileReader("namesDistances2"));
				String line = input.readLine();
				String []token = line.split(" ");
				for (int i = 0; i < names.length; i++)
					names[i] = token[i];
				
				for (int i = 0; i < names.length; i++)
				{
					line = input.readLine();
					token = line.split(" ");
					for (int j = 0; j < names.length; j++)
						distances[i][j] = 100*Math.pow(Double.parseDouble(token[j]), 1);
				}
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0 ; i < names.length; i++)
			names[i] = ""+i;
		
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster cluster = alg.performClustering(distances, names,
			//new SingleLinkageStrategy());
			new CompleteLinkageStrategy());
		    //new AverageLinkageStrategy());
		
		HashSet<Cluster> clusters = new HashSet<Cluster>();
		clusters.addAll(getKClusters(cluster, k));
		/*List<Cluster> removeList = new ArrayList<Cluster>();
		for (Cluster c:clusters)
		{
			if (c.countLeafs() < 8)
			{
				removeList.addAll(c.getParent().getChildren());
			}
		}
		clusters.removeAll(removeList);
		for (Cluster r:removeList)
			clusters.add(r.getParent());*/
		
		ArrayList<HashSet<String>> clusteredIdea = new ArrayList<HashSet<String>>();
		for (Cluster c:clusters)
		{
			clusteredIdea.add(getAllLeaves(c));
			
			if (getAllLeaves(c).size() > 10)
				System.out.println(c.countLeafs()+" "+Math.pow(c.getDistance() == null?0:c.getDistance(), 1));
			
		}
		
		DendrogramPanel dp = new DendrogramPanel();
		dp.setModel(cluster);
		JFrame frame = new JFrame();
		frame.add(dp);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		return clusteredIdea;
	}
	
	
	private static double getIdeaDistance(HashSet<String> iAdopterList, HashSet<String> jAdopterList)
	{
		double distance;
		HashSet<String> union = new HashSet<String>();
		HashSet<String> joint = new HashSet<String>();
		union.addAll(iAdopterList);
		union.addAll(jAdopterList);
		joint.addAll(iAdopterList);
		joint.retainAll(jAdopterList);
		//distance = 1 - (double)joint.size()/union.size();		//namesDistances
		distance = 1/(Math.pow((double)joint.size(), 0.5)+1);						//namesDistances2
		//distance = 1 - (double)joint.size()/Math.min(iAdopterList.size(), jAdopterList.size());		//namesDistances3
		
		return distance;
	}
	
	public static List<Cluster> getKClusters(Cluster root, int k)
	{
		List<Cluster> clusters = new ArrayList<Cluster>();
		clusters.add(root);
		double distance;
		double biggestD = root.getDistance();
		Cluster biggestDCluster = null;
		while (k > clusters.size())
		{
			biggestD = -1;
			for (Cluster c:clusters)
			{
				distance = c.getDistance() == null ? 0:c.getDistance();
				if (distance > biggestD)
				{
					biggestD = distance;
					biggestDCluster = c;
				}	
			}
			clusters.remove(biggestDCluster);
			clusters.addAll(biggestDCluster.getChildren());
		}
		return clusters;
	}
	
	public static HashSet<String> getAllLeaves(Cluster root)
	{
		HashSet<String> ideaIDSet = new HashSet<String>();
		for (Cluster c:root.getChildren())
		{
			if (c.isLeaf())
				ideaIDSet.add(c.getName());
			else
				getAllLeaves(c, ideaIDSet);
		}
		return ideaIDSet;
	}
	
	public static void getAllLeaves(Cluster root, HashSet<String> ideaIDSet)
	{
		for (Cluster c:root.getChildren())
		{
			if (c.isLeaf())
				ideaIDSet.add(c.getName());
			else
				getAllLeaves(c, ideaIDSet);
		}
	}
	
	private static Vertex getVertex(int vertexIndex, HashMap<Integer, Vertex> vertices)
	{
		if (vertices.containsKey(vertexIndex))
			return vertices.get(vertexIndex);
		else
			return new Vertex(vertexIndex, Vertex.State.INACTIVE);
	}
	
	
}
