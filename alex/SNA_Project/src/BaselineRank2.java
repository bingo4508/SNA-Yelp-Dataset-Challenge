import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

public class BaselineRank2 {

	public static final String graphFileName = "dataCreated\\graph.txt";
	public static final String trainingFileName = "dataCreated\\training.txt";
	public static final String testingInputFileName1 = "dataCreated\\test_data_q1.txt";
	public static final String testingAnsFileName1 = "dataCreated\\test_data_a1.txt";
	public static final String testingInputFileName2 = "dataCreated\\test_data_q2.txt";
	public static final String testingAnsFileName2 = "dataCreated\\test_data_a2.txt";
	public static final String testingInputFileName3 = "dataCreated\\test_data_q3.txt";
	public static final String testingAnsFileName3 = "dataCreated\\test_data_a3.txt";
	public static void main(String[] args)
	{
		Graph<Vertex, Edge> g;
		HashMap<String, Vertex> vertices = new HashMap<String, Vertex>();
		g = createGraph(graphFileName, vertices);
		//System.out.println(g.getVertexCount());
		//visualizeGraph(g);
		training(trainingFileName, g, vertices);
		
		testing(testingInputFileName1, testingAnsFileName1, g, vertices);//0.029057
		testing(testingInputFileName2, testingAnsFileName2, g, vertices);//0.026541
		testing(testingInputFileName3, testingAnsFileName3, g, vertices);//0.024864
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
		public double activatedProb;
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
		
		static class theComparator implements Comparator<Edge>{
			 
		    @Override
		    public int compare(Edge e1, Edge e2) {
		    	if (e1 == e2)
		    		return 0;
		    	else
		    		return ((ICEdge)e1).icProbability - ((ICEdge)e2).icProbability >= 0 ? 1 : -1;
		    }
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
				nodes = line.split(" ");
				Vertex v;
				HashSet<Vertex> seeds = new HashSet<Vertex>();
				HashSet<Vertex> neighbors = new HashSet<Vertex>();
				for (String nStr:nodes)
				{
					if (!nStr.equals(""))
					{
						v = getVertex(nStr, vertices);
						seeds.add(v);
						if (g.containsVertex(v))
							neighbors.addAll(g.getSuccessors(v));
						/*
						for (Edge oe:g.getOutEdges(v))
							if (((ICEdge)oe).icProbability != 0)
								neighborsEdge.add(oe);
						*/
					}
				}
				neighbors.removeAll(seeds);
				
				HashSet<Vertex> outAns = new HashSet<Vertex>();
				Vertex best = null;
				double bestProb = 0;
				ICEdge e;
				for (int i = 0; i < 100; i++)
				{
					best = null;
					bestProb = 0;
					for (Vertex n:neighbors)
					{
						n.activatedProb = 1;
						for (Vertex s:seeds)
						{
							e = (ICEdge)g.findEdge(s, n);
							if (e != null)
							{
								n.activatedProb *= (1-e.icProbability);
							}
						}
						n.activatedProb = 1- n.activatedProb;
						if (n.activatedProb > bestProb)
						{
							bestProb = n.activatedProb;
							best = n;
						}
					}
					if (best != null)
					{
						outAns.add(best);
						seeds.add(best);
						if (g.containsVertex(best))
							neighbors.addAll(g.getSuccessors(best));
						neighbors.removeAll(seeds);
					}
					else
						i = 100;
					/*
					for (Edge oe:g.getOutEdges(v))
						if (((ICEdge)oe).icProbability != 0)
							neighborsEdge.add(oe);
					*/
				}
				//compare to the answer
				line = answer.readLine();
				nodes = line.split(" ");
				int tp = 0, fp = 0, fn = 0;
				for (int i = 0; i < 100 && i < nodes.length; i++)
				{
					v = getVertex(nodes[i], vertices);
					if (outAns.contains(v))
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
