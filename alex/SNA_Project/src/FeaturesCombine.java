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

	private final static int numFeatures = 2;
	
	static class BusinessUserPair
	{
		int businessOrder;
		String userID;
		int label;
		public BusinessUserPair(int businessOrder, String userID, int label)
		{
			this.businessOrder = businessOrder;
			this.userID = userID;
			this.label = label;
		}
		@Override
		public boolean equals(Object obj)
		{
			BusinessUserPair p = (BusinessUserPair)obj;
			return businessOrder == p.businessOrder && userID == p.userID; 
		}
		@Override
		public int hashCode()
		{
			return userID.hashCode();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		HashMap<BusinessUserPair, HashMap<Integer, Double>> trainingData = new HashMap<BusinessUserPair, HashMap<Integer, Double>>();
		
		int featureIndex = 0;
		
		try {
			//inputFeatureFile(featureIndex++, "dataCreated\\BipartieCommonNeighbor_training1", trainingData);
			inputFeatureFile(featureIndex++, "dataCreated\\BipartieCommonNeighbor_testing1", trainingData);
			//inputFeatureFile(featureIndex++, "dataCreated\\ICSpreadSample_training1", trainingData);
			inputFeatureFile(featureIndex++, "dataCreated\\ICSpreadSample_testing1", trainingData);
			
			/*
			//outputTrainingData("dataCreated\\trainingData_order", trainingData);
			outputTrainingData("dataCreated\\testingData_order", trainingData);
			*/
			//outputArffTrainingData("dataCreated\\trainingData_nonNormalize.arff", trainingData);
			outputArffTrainingData("dataCreated\\testingData_nonNormalize.arff", trainingData);
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
			instance = new BusinessUserPair(Integer.parseInt(token[0]), token[1], Integer.parseInt(token[3]));
			features = trainingData.get(instance);
			if (features == null)
			{
				features = new HashMap<Integer, Double>(numFeatures);
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

	public static void outputTrainingData(String outputTrainingFileName, HashMap<BusinessUserPair, HashMap<Integer, Double>> trainingData) throws FileNotFoundException
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
	
	public static void outputArffTrainingData(String outputTrainingFileName, HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>> trainingData) throws FileNotFoundException
	{
		String head = "@RELATION reviewOrNot\r\n\r\n@ATTRIBUTE bipartieCommonNeighbor NUMERIC\r\n@ATTRIBUTE ICSpreadSample NUMERIC\r\n@ATTRIBUTE class {1, 0}\r\n\r\n@DATA";
		PrintStream output = new PrintStream(new File(outputTrainingFileName));
		output.println(head);
		HashMap<Integer, Double> features;
		for (FeaturesCombine.BusinessUserPair instance:trainingData.keySet())
		{
			features = trainingData.get(instance);
			for (int i = 0; i < numFeatures; i++)
			{
				if (features.containsKey(i))
					output.print(""+features.get(i)+",");
				else
					output.print("0,");
			}		
			output.print(""+instance.label);
			output.println();
		}
	}
	
}
