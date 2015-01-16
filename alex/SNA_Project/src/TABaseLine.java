import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;


public class TABaseLine {
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

		run(testingInputFileName1, testingAnsFileName1, g, vertices);//0.0070109
		run(testingInputFileName2, testingAnsFileName2, g, vertices);//0.0070894
		run(testingInputFileName3, testingAnsFileName3, g, vertices);//0.0071259
	}
	
	private static void run(String testingInputFileName, String testingAnsFileName, Graph<Vertex, Edge> g, HashMap<String, Vertex> vertices)
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
				//set seeds
				nodes = line.split(" ");
				Vertex v;
				HashSet<Vertex> C = new HashSet<Vertex>();
				HashSet<Vertex> seeds = new HashSet<Vertex>();
				for (String nStr:nodes)
				{
					if (!nStr.equals(""))
					{
						v = getVertex(nStr, vertices);
						seeds.add(v);
						if (g.containsVertex(v))
							C.addAll(g.getNeighbors(v));
					}
				}
				HashSet<Vertex> Cprime = new HashSet<Vertex>();
				for (Vertex c:C)
				{
					if (g.containsVertex(c))
						Cprime.addAll(g.getNeighbors(c));
				}
				
				Cprime.addAll(C);
				Cprime.removeAll(seeds);
				ArrayList<Vertex> CprimeArr = new ArrayList<Vertex>();
				CprimeArr.addAll(Cprime);
				HashSet<Vertex> neighbors = new HashSet<Vertex>();
				int score[] = new int[CprimeArr.size()];
				Vertex cp;
				for (int i = 0; i < CprimeArr.size(); i++)
				{
					cp = CprimeArr.get(i);
					neighbors.clear();
					neighbors.addAll(g.getNeighbors(cp));
					neighbors.retainAll(seeds);
					score[i] = neighbors.size();
				}
				
				int sortedIndex[] = new int[Cprime.size()];
				for (int i = 0; i < sortedIndex.length; i++)
					sortedIndex[i] = i;
				quickSort(sortedIndex, score, 0, sortedIndex.length-1);
				
				HashSet<Vertex> outAns = new HashSet<Vertex>();
				for (int i = 0; i < 100 && i < sortedIndex.length; i++)
				{
					//System.out.println(score[sortedIndex[sortedIndex.length-1-i]]);
					outAns.add(CprimeArr.get(sortedIndex[sortedIndex.length-1-i]));
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
	
	private static class Edge
	{
		public static int count = 0;
		public Edge()
		{
			count++;
		}
	}

	private static class Vertex
	{
		public static int count = 0;
		public String vertexIndex;
		public Vertex(String vertexIndex)
		{
			count++;
			this.vertexIndex = vertexIndex;
		}
		@Override
		public boolean equals(Object obj)
		{
			return ((Vertex)obj).vertexIndex == this.vertexIndex;
		}
		@Override
		public int hashCode()
		{
			return this.vertexIndex.hashCode();
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
			g = new UndirectedSparseGraph<Vertex, Edge>();
			
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
	private static Vertex getVertex(String vertexIndex, HashMap<String, Vertex> vertices)
	{
		if (vertices.containsKey(vertexIndex))
			return vertices.get(vertexIndex);
		else
			return new Vertex(vertexIndex);
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
			g.addEdge(new Edge(), v1, v2);
	}
	
	private static int partition(int index[], int arr[], int left, int right)
	{
	      int i = left, j = right;
	      int tmp;
	      int pivot = arr[index[(left + right) / 2]];
	     
	      while (i <= j) {
	            while (arr[index[i]] < pivot)
	                  i++;
	            while (arr[index[j]] > pivot)
	                  j--;
	            if (i <= j) {
	                  tmp = index[i];
	                  index[i] = index[j];
	                  index[j] = tmp;
	                  i++;
	                  j--;
	            }
	      };
	     
	      return i;
	}
	 
	private static void quickSort(int index[], int arr[], int left, int right) {
	      int indexI = partition(index, arr, left, right);
	      if (left < indexI - 1)
	            quickSort(index, arr, left, indexI - 1);
	      if (indexI < right)
	            quickSort(index, arr, indexI, right);
	}
}
