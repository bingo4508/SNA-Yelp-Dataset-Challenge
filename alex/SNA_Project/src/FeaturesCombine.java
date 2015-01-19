import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class FeaturesCombine {
	
	static class BusinessUserPair
	{
		String businessID;
		String userID;
		int label;
		public BusinessUserPair(String businessID, String userID, int label)
		{
			this.businessID = businessID;
			this.userID = userID;
			this.label = label;
		}
		@Override
		public boolean equals(Object obj)
		{
			BusinessUserPair p = (BusinessUserPair)obj;
			return businessID.equals(p.businessID) && userID.equals(p.userID); 
		}
		@Override
		public int hashCode()
		{
			return userID.hashCode();
		}
		@Override
		public String toString()
		{
			return "\'"+businessID+"\'\t\'"+userID+"\'";
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		HashMap<BusinessUserPair, HashMap<Integer, Double>> trainingData = new HashMap<BusinessUserPair, HashMap<Integer, Double>>();
		HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>> testingData = new HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>>();
		
		int featureIndex = 0;
		
		try {
			inputFeatureFile(featureIndex, "dataCreated\\BipartieCommonNeighbor_training1", trainingData);
			inputFeatureFile(featureIndex, "dataCreated\\BipartieCommonNeighbor_testing1", testingData);
			featureIndex++;
			inputFeatureFile(featureIndex, "dataCreated\\ICSpreadSample_training1", trainingData);
			inputFeatureFile(featureIndex, "dataCreated\\ICSpreadSample_testing1", testingData);
			featureIndex++;
			
			
			/*
			//outputTrainingData("dataCreated\\trainingData_order", trainingData);
			outputTrainingData("dataCreated\\testingData_order", testingData);
			*/
			outputArffTrainingData(featureIndex, "dataCreated\\trainingData_nonNormalize.arff", trainingData);
			outputArffTrainingData(featureIndex, "dataCreated\\testingData_nonNormalize.arff", testingData);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void inputFeatureFile(int featureIndex, String featureFileName, HashMap<BusinessUserPair, HashMap<Integer, Double>> trainingData) throws IOException
	{
		BufferedReader inputFeature = null;
		String line = null;
		String token[];
		BusinessUserPair instance;
		HashMap<Integer, Double> features;
		
		inputFeature = new BufferedReader(new FileReader(featureFileName));
		line = inputFeature.readLine();
		while(line != null && !line.equals(""))
		{
			token = line.split(" ");
			instance = new BusinessUserPair(token[0], token[1], Integer.parseInt(token[3]));
			features = trainingData.get(instance);
			if (features == null)
			{
				features = new HashMap<Integer, Double>();
				features.put(featureIndex, Double.parseDouble(token[2]));
				trainingData.put(instance, features);
			}
			else
				features.put(featureIndex, Double.parseDouble(token[2]));
			
			line = inputFeature.readLine();
		}
		inputFeature.close();
		System.out.println("Finish input "+featureFileName);
	}

	public static void outputTrainingData(int numFeatures, String outputTrainingFileName, HashMap<BusinessUserPair, HashMap<Integer, Double>> trainingData) throws FileNotFoundException
	{
		PrintStream outputTraining = new PrintStream(new File(outputTrainingFileName));
		HashMap<Integer, Double> features;
		for (BusinessUserPair instance:trainingData.keySet())
		{
			outputTraining.print(""+instance.label);
			features = trainingData.get(instance);
			for (int i = 0; i < numFeatures; i++)
				if (features.containsKey(i))
					outputTraining.print(" "+(i+1)+":"+features.get(i));
			outputTraining.println();
		}
		outputTraining.close();
	}
	
	public static void outputArffTrainingData(int numFeatures, String outputTrainingFileName, HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>> trainingData) throws FileNotFoundException
	{
		String head = "@RELATION reviewOrNot\r\n\r\n";
		head += "@ATTRIBUTE bipartieCommonNeighbor NUMERIC\r\n";
		head += "@ATTRIBUTE ICSpreadSample NUMERIC\r\n";
		for (int i = 0; i < numFeatures - 2; i++)
			head += "@ATTRIBUTE category_"+(i+1)+" NUMERIC\r\n";
		/*head += "@ATTRIBUTE positionFeature_0 NUMERIC\r\n";
		head += "@ATTRIBUTE positionFeature_1 NUMERIC\r\n";
		head += "@ATTRIBUTE positionFeature_2 NUMERIC\r\n";
		head += "@ATTRIBUTE positionFeature_3 NUMERIC\r\n";*/
		head += "@ATTRIBUTE class {1, 0}\r\n\r\n@DATA";
		String outputPairFileName = outputTrainingFileName+".pairName.txt";
		PrintStream output = new PrintStream(new File(outputTrainingFileName));
		PrintStream outputPair = new PrintStream(new File(outputPairFileName));
		output.println(head);
		HashMap<Integer, Double> features;
		for (FeaturesCombine.BusinessUserPair instance:trainingData.keySet())
		{
			outputPair.println(instance);
			features = trainingData.get(instance);
			output.print("{");
			for (int i = 0; i < numFeatures; i++)
			{
				if (features.containsKey(i))
					output.print(""+i+" "+features.get(i)+", ");
			}		
			output.print(""+numFeatures+" "+instance.label+"}");
			output.println();
		}
		output.close();
		outputPair.close();
	}
	
}
