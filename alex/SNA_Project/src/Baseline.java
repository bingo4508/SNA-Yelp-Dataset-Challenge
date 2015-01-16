import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

//training:	no use of temporal length and adoption degree of idea
//testing:	assume the initial nodes are newly active in the beginning

public class Baseline {

	public static final String graphFileName = "dataCreated\\graph.txt";
	public static final String trainingFileName = "dataCreated\\training.txt(else)";
	public static final String testingInputFileName1 = "dataCreated\\test_data_q1.txt";
	public static final String testingAnsFileName1 = "dataCreated\\test_data_a1.txt";
	public static final String testingInputFileName2 = "dataCreated\\test_data_q2.txt";
	public static final String testingAnsFileName2 = "dataCreated\\test_data_a2.txt";
	public static final String testingInputFileName3 = "dataCreated\\test_data_q3.txt";
	public static final String testingAnsFileName3 = "dataCreated\\test_data_a3.txt";
	public static final String SVMtrainingInputFileName1 = "dataCreated\\SVM_training_data_q1.txt";
	public static final String SVMtrainingAnsFileName1 = "dataCreated\\SVM_training_data_a1.txt";
	
	public static void main(String[] args)
	{
		Graph<Vertex, Edge> g;
		HashMap<String, Vertex> vertices = new HashMap<String, Vertex>();
		g = createGraph(graphFileName, vertices);
		//System.out.println(g.getVertexCount());
		//visualizeGraph(g);
		training(trainingFileName, g, vertices);
		
		//outputICSpreadSample("dataCreated\\ICSpreadSample_training1", SVMtrainingInputFileName1, SVMtrainingAnsFileName1, g, vertices);
		outputICSpreadSample("dataCreated\\ICSpreadSample_testing1", testingInputFileName1, testingAnsFileName1, g, vertices);
		
		/*testing(testingInputFileName1, testingAnsFileName1, g, vertices);//0.0036006
		testing(testingInputFileName2, testingAnsFileName2, g, vertices);//0.0022018
		testing(testingInputFileName3, testingAnsFileName3, g, vertices);//0.0040053*/
		/*
		int[] degree = new int [10000];
		int maxDegree = 0;
		for (Vertex n:g.getVertices())
		{
			if (maxDegree < g.inDegree(n))
				maxDegree = g.inDegree(n);
			degree[g.inDegree(n)]++;
		}
		
		for (int i = 1; i < maxDegree+1; i++)
		{
			if (degree[i] != 0)
				System.out.println(i+"\t"+degree[i]);
		}*/
	}
	
	private static class Edge
	{
		public static int count = 0;
	}

	private static class Vertex
	{
		public static int count = 0;
		public static enum State{INACTIVE, NEWLY_ACTIVE, ACTIVE}; 
		public String vertexIndex;
		public State state;
		public ArrayList<Idea> ideaList;
		public Vertex(String vertexIndex, State state)
		{
			count++;
			this.vertexIndex = vertexIndex;
			this.state = state;
			ideaList = new ArrayList<Idea>();
		}
	}

	private static class ICEdge extends Edge
	{
		public double icProbability;
		public ICEdge()
		{
			count++;
			this.icProbability = 0;
		}
		
		public ICEdge(double icProbability)
		{
			count++;
			this.icProbability = icProbability;
		}
	}

	private static class ICVertex extends Vertex
	{
		public ICVertex(String vertexIndex, State state)
		{
			super(vertexIndex, state);
		}
	}
	
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
		    	if (e1.date.compareTo(e2.date) == 0)
		    		return 1;
		    	else
		    		return e1.date.compareTo(e2.date);
		    }
		}
	}
	
	  
	
	private static Graph<Vertex, Edge> createGraph(String inputFileName, HashMap<String, Vertex> vertices)
	{
		BufferedReader input = null;
		String line = null;
		String nodes[] = new String[3];
		Vertex vertex1, vertex2;
		Graph<Vertex, Edge> g = null;
		//Create Graph g
		try 
		{
			input = new BufferedReader(new FileReader(inputFileName));
			g = new DirectedSparseGraph<Vertex, Edge>();
			
			line = input.readLine();
			while (line != null)
			{
				if (!line.equals(""))
				{
					nodes = line.split(" ");
					vertex1 = getVertex(nodes[0], vertices);
					addVertex(vertex1, g, vertices);
					for (int i = 1; i < nodes.length; i++)
					{
						vertex2 = getVertex(nodes[1], vertices);
						addVertex(vertex2, g, vertices);
						addEdge(vertex1.vertexIndex, vertex2.vertexIndex, g, vertices);
						addEdge(vertex2.vertexIndex, vertex1.vertexIndex, g, vertices);
					}
				}
				line = input.readLine();
			}
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return g;
	}
	
	private static void training(String trainingFileName, Graph<Vertex, Edge> g, HashMap<String, Vertex> vertices)
	{
		BufferedReader input = null;
		String line = null;
		String token[];
		String nodeID;
		String idea;
		Date date;
		double adoptDeg;
		SortedSet<Idea> trainingElement = new TreeSet<Idea>(new Idea.theComparator());
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
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
					
					trainingElement.add(new Idea(nodeID, idea, date, adoptDeg));
				}
				line = input.readLine();
			}
			input.close();
			
			Vertex vt;
			ICEdge e;
			int countNotInGraph = 0;
			System.out.println(trainingElement.size());
			for (Idea ele:trainingElement)
			{
				vt = getVertex(ele.nodeID, vertices);
				if (!vt.ideaList.contains(ele.ideaID) && g.containsVertex(vt))
				{
					for (Vertex p:g.getPredecessors(vt))
						if (p.ideaList.contains(ele) && p.ideaList.get(p.ideaList.indexOf(ele)).date.before(ele.date))
						{
							e = (ICEdge)getEdge(p.vertexIndex, vt.vertexIndex, g, vertices);
							e.icProbability++;
						}
					
					vt.ideaList.add(ele);
				}
				else if (!g.containsVertex(vt))
					countNotInGraph++;
				else
				{
					System.out.println("Duplicate idea spread");
				}
			}
			System.out.println(""+countNotInGraph+" Not in graph");
			
			//normalize probability
			int nonZeroEdge = 0;
			for (Vertex v:vertices.values())
			{
				if (v.ideaList.size() != 0)
				{
					for (Vertex s:g.getSuccessors(v))
					{
						e = (ICEdge)getEdge(v.vertexIndex, s.vertexIndex, g, vertices);
						if (e.icProbability != 0)
							nonZeroEdge++;
						e.icProbability /= v.ideaList.size();
					}
				}
			}
			System.out.println(nonZeroEdge);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static void outputICSpreadSample(String outputFileName, String testingInputFileName, String testingAnsFileName, Graph<Vertex, Edge> g, HashMap<String, Vertex> vertices)
	{
		BufferedReader input = null;
		BufferedReader answer = null;
		String line = null;
		String nodes[];
		HashMap<String, Double> rankUser = new HashMap<String, Double>();
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
				rankUser.clear();
				for (int i = 0; i < 100; i++)	//sample 100 times
				{
					//initialize state
					for (Vertex v:vertices.values())
						v.state = Vertex.State.INACTIVE;
					//set seeds
					nodes = line.split(" ");
					Vertex v;
					HashSet<Vertex> newlyActiveVertices = new HashSet<Vertex>();
					for (String nStr:nodes)
					{
						if (!nStr.equals(""))
						{
							v = getVertex(nStr, vertices);
							v.state = Vertex.State.NEWLY_ACTIVE;
							newlyActiveVertices.add(v);
						}
					}
					//start spreading
					ArrayList<Vertex> activatedVertices = new ArrayList<Vertex>();
					HashSet<Vertex> newerActiveVertices = new HashSet<Vertex>();
					ICEdge e;
					while (!newlyActiveVertices.isEmpty() && activatedVertices.size() < 100)
					{
						for (Vertex newlyActive:newlyActiveVertices)
						{
							newlyActive.state = Vertex.State.ACTIVE;
							if (g.containsVertex(newlyActive))
							{
								for (Vertex s:g.getSuccessors(newlyActive))
								{
									e = (ICEdge)getEdge(newlyActive.vertexIndex, s.vertexIndex, g, vertices);
									if (s.state == Vertex.State.INACTIVE && java.lang.Math.random() < e.icProbability)
									{
										s.state = Vertex.State.NEWLY_ACTIVE;
										newerActiveVertices.add(s);
										activatedVertices.add(s);
									}
								}
							}//end if
						}
						newlyActiveVertices.clear();
						newlyActiveVertices.addAll(newerActiveVertices);
						newerActiveVertices.clear();
					}
					if (activatedVertices.size() > 100)
						activatedVertices.retainAll(activatedVertices.subList(0, 100));
					for (int j = 0; j < activatedVertices.size(); j++)
					{
						if (rankUser.containsKey(activatedVertices.get(j).vertexIndex))
							rankUser.put(activatedVertices.get(j).vertexIndex, rankUser.get(activatedVertices.get(j).vertexIndex)+1);
						else
							rankUser.put(activatedVertices.get(j).vertexIndex, (double)1);
					}		
				}
				//collect the groundTruth
				line = answer.readLine();
				nodes = line.split(" ");
				groundTruth.clear();
				for (int i = 0; i < 100 && i < nodes.length; i++)
					groundTruth.add(nodes[i]);
				
				//output
				for (String user:rankUser.keySet())
				{
					if (groundTruth.contains(user))
						featuresOutput.println(""+businessOrder+" "+user+" "+(double)rankUser.get(user)+" 1");
					else
						featuresOutput.println(""+businessOrder+" "+user+" "+(double)rankUser.get(user)+" 0");
				}
				System.out.println(businessOrder++);
				line = input.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void testing(String testingInputFileName, String testingAnsFileName, Graph<Vertex, Edge> g, HashMap<String, Vertex> vertices)
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
			line = input.readLine();
			while(line != null)
			{
				//initialize state
				for (Vertex v:vertices.values())
					v.state = Vertex.State.INACTIVE;
				//set seeds
				nodes = line.split(" ");
				Vertex v;
				HashSet<Vertex> newlyActiveVertices = new HashSet<Vertex>();
				for (String nStr:nodes)
				{
					if (!nStr.equals(""))
					{
						v = getVertex(nStr, vertices);
						v.state = Vertex.State.NEWLY_ACTIVE;
						newlyActiveVertices.add(v);
					}
				}
				//start spreading
				ArrayList<Vertex> activatedVertices = new ArrayList<Vertex>();
				HashSet<Vertex> newerActiveVertices = new HashSet<Vertex>();
				ICEdge e;
				while (!newlyActiveVertices.isEmpty() && activatedVertices.size() < 100)
				{
					for (Vertex newlyActive:newlyActiveVertices)
					{
						newlyActive.state = Vertex.State.ACTIVE;
						if (g.containsVertex(newlyActive))
						{
							for (Vertex s:g.getSuccessors(newlyActive))
							{
								e = (ICEdge)getEdge(newlyActive.vertexIndex, s.vertexIndex, g, vertices);
								if (s.state == Vertex.State.INACTIVE && java.lang.Math.random() < e.icProbability)
								{
									s.state = Vertex.State.NEWLY_ACTIVE;
									newerActiveVertices.add(s);
									activatedVertices.add(s);
								}
							}
						}//end if
					}
					newlyActiveVertices.clear();
					newlyActiveVertices.addAll(newerActiveVertices);
					newerActiveVertices.clear();
				}
				if (activatedVertices.size() > 100)
					activatedVertices.retainAll(activatedVertices.subList(0, 100));
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					v = getVertex(nodes[i], vertices);
					if (activatedVertices.contains(v))
						tp++;
					else
						fn++;
				}
				fp = activatedVertices.size() - tp;
				if (activatedVertices.size() == 0)
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
	
	private static Vertex getVertex(String vertexIndex, HashMap<String, Vertex> vertices)
	{
		if (vertices.containsKey(vertexIndex))
			return vertices.get(vertexIndex);
		else
			return new ICVertex(vertexIndex, Vertex.State.INACTIVE);
	}
	
	private static void addVertex(Vertex v, Graph<Vertex, Edge> g, HashMap<String, Vertex> vertices)
	{
		if (!vertices.containsKey(v.vertexIndex))
		{
			g.addVertex(v);
			vertices.put(v.vertexIndex, v);
		}
	}
	
	private static Edge getEdge(String vertex1Index, String vertex2Index, Graph<Vertex, Edge> g, HashMap<String, Vertex> vertices)
	{
		Vertex v1 = getVertex(vertex1Index, vertices);
		Vertex v2 = getVertex(vertex2Index, vertices);
		Edge e = g.findEdge(v1, v2);
		return e;
	}
	
	private static void addEdge(String vertex1Index, String vertex2Index, Graph<Vertex, Edge> g, HashMap<String, Vertex> vertices)
	{
		Vertex v1 = getVertex(vertex1Index, vertices);
		Vertex v2 = getVertex(vertex2Index, vertices);
		Edge e = g.findEdge(v1, v2);
		if (e == null)
			g.addEdge(new ICEdge(), v1, v2);
	}
	
	private static void visualizeGraph(Graph<Vertex, Edge> g)
	{
		// The Layout<V, E> is parameterized by the vertex and edge types
        //Layout<Vertex, Edge> layout = new FRLayout<Vertex,Edge>(g);
		//Layout<Vertex, Edge> layout = new FRLayout2<Vertex,Edge>(g);
		Layout<Vertex, Edge> layout = new ISOMLayout<Vertex,Edge>(g);
		//Layout<Vertex, Edge> layout = new KKLayout<Vertex,Edge>(g);
        layout.setSize(new Dimension(1400,700)); // sets the initial size of the layout space
        // The BasicVisualizationServer<V,E> is parameterized by the vertex and edge types
        BasicVisualizationServer<Vertex,Edge> vv = new BasicVisualizationServer<Vertex,Edge>(layout);
        vv.setPreferredSize(new Dimension(1450,750)); //Sets the viewing area size
        
        JFrame frame = new JFrame("Simple Graph View");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(vv); 
        frame.pack();
        frame.setVisible(true);
	}
	
}
