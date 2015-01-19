import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class BusinessCategoriesFeature {

	public static final String businessCategoryFileName = "dataCreated\\businessCategory.txt";
	public static final String trainingFileName = "dataCreated\\training.txt(else)";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, HashSet<String>> businessCategoryMap;
		HashMap<String, Integer> categoryBusinessCount;
		HashMap<String, HashSet<String>> adopterIdeasList;
		int featureIndex = 0;
		try {
			businessCategoryMap = new HashMap<String, HashSet<String>>();
			categoryBusinessCount = new HashMap<String, Integer>();
			createBusinessCategoryMap(businessCategoryMap, categoryBusinessCount, businessCategoryFileName);
			adopterIdeasList = createAdopterIdeasList(trainingFileName);
			
			HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>> trainingData = new HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>>();
			HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>> testingData = new HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>>();
			
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\BipartieCommonNeighbor_training2", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\BipartieCommonNeighbor_testing2", testingData);
			featureIndex++;
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\ICSpreadSample_training1", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\ICSpreadSample_testing1", testingData);
			featureIndex++;
			featureIndex = createBusinessCategoryFeatureInTraining(featureIndex, businessCategoryMap, categoryBusinessCount, adopterIdeasList, trainingData, testingData);
			/*FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_training1(f0).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_testing1(f0).txt", testingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_training1(f0).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_testing1(f0).txt", testingData);
			featureIndex++;
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_training1(f1).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_testing1(f1).txt", testingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_training1(f1).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_testing1(f1).txt", testingData);
			featureIndex++;
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_training1(f2).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_testing1(f2).txt", testingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_training1(f2).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_testing1(f2).txt", testingData);
			featureIndex++;
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_training1(f3).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\BipartieCommonNeighbor_testing1(f3).txt", testingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_training1(f3).txt", trainingData);
			FeaturesCombine.inputFeatureFile(featureIndex, "dataCreated\\svm_feature_merge\\ICSpreadSample_testing1(f3).txt", testingData);
			featureIndex++;
			*/
			FeaturesCombine.outputArffTrainingData(featureIndex, "dataCreated\\trainingData_nonNormalize_category2.arff", trainingData);
			FeaturesCombine.outputArffTrainingData(featureIndex, "dataCreated\\testingData_nonNormalize_category2.arff", testingData);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static HashMap<String, HashSet<String>> createAdopterIdeasList(String trainingFileName) throws IOException
	{
		BufferedReader input = null;
		String line = null;
		String token[];
		String nodeID;
		String idea;
		HashSet<String> ideasList;
		HashMap<String, HashSet<String>> adopterIdeasList = new HashMap<String, HashSet<String>>();
		
		input = new BufferedReader(new FileReader(trainingFileName));
		line = input.readLine();
		while(line != null)
		{
			if (!line.equals(""))
			{
				token = line.split(" ");
				nodeID = token[0];
				idea = token[1];
				
				
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
		
		return adopterIdeasList;
	}
	
	public static HashMap<String, HashSet<String>> createBusinessCategoryMap(HashMap<String, HashSet<String>> businessCategoryMap, 
			HashMap<String, Integer> categoryBusinessCount, String businessCategoryFileName) throws IOException
	{
		BufferedReader inputBusinessCategories;
		String line;
		String businessID;
		HashSet<String> categorySet;
		inputBusinessCategories= new BufferedReader(new FileReader(businessCategoryFileName));
		line = inputBusinessCategories.readLine();
		while (line != null && !line.equals(""))
		{
			businessID = line;
			categorySet = new HashSet<String>();
			line = inputBusinessCategories.readLine();
			if (!line.equals(""))
			{
				while (!line.equals(""))
				{
					if (categoryBusinessCount.containsKey(line))
						categoryBusinessCount.put(line, categoryBusinessCount.get(line)+1);
					else
						categoryBusinessCount.put(line, 1);
					categorySet.add(line);
					line = inputBusinessCategories.readLine();
				}
			}
			else
				line = inputBusinessCategories.readLine();
			businessCategoryMap.put(businessID, categorySet);
			line = inputBusinessCategories.readLine();
		}
		inputBusinessCategories.close();
		
		return businessCategoryMap;
	}
	
	public static int createBusinessCategoryFeatureInTraining(int featureIndex, 
			HashMap<String, HashSet<String>> businessCategoryMap, HashMap<String, Integer> categoryBusinessCount, HashMap<String, HashSet<String>> adopterIdeasList,
			HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>> trainingData,
			HashMap<FeaturesCombine.BusinessUserPair, HashMap<Integer, Double>> testingData)
	{
		HashSet<String> ideasList;
		HashMap<String, Integer> voteCategoryCount = new HashMap<String, Integer>();
		HashMap<String, Double> categoryNormalized;
		HashMap<String, HashMap<String, Double>> userCategoryNormalized = new HashMap<String, HashMap<String, Double>>();
		HashMap<String, Integer> categoryIndex = new HashMap<String, Integer>();
		int categoryCount = 0;
		for (FeaturesCombine.BusinessUserPair p:trainingData.keySet())
		{
			if (!userCategoryNormalized.containsKey(p.userID))	//create this user's category count normalized
			{
				voteCategoryCount.clear();
				ideasList = adopterIdeasList.get(p.userID);
				for (String idea:ideasList)
				{
					for (String category:businessCategoryMap.get(idea))
					{
						if (!categoryIndex.containsKey(category) && categoryBusinessCount.get(category) > 1000)
							categoryIndex.put(category, categoryCount++);
						if (!voteCategoryCount.containsKey(category))
							voteCategoryCount.put(category, 1);
						else
							voteCategoryCount.put(category, voteCategoryCount.get(category)+1);
					}
				}
				categoryNormalized = new HashMap<String, Double>();
				for (String category:voteCategoryCount.keySet())
					categoryNormalized.put(category, (double)voteCategoryCount.get(category) / ideasList.size());
				userCategoryNormalized.put(p.userID, categoryNormalized);
			}
		}
		System.out.println("Start BusinessCategoryFeature Input");
		int index;
		int count = 0;
		for (FeaturesCombine.BusinessUserPair p:trainingData.keySet())
		{
			categoryNormalized = userCategoryNormalized.get(p.userID);
			for (String category:businessCategoryMap.get(p.businessID))
			{
				if (categoryNormalized.containsKey(category) && categoryIndex.containsKey(category))
				{
					index = featureIndex+categoryIndex.get(category);
					trainingData.get(p).put(index, categoryNormalized.get(category));
				}
			}
			System.out.println(count++);
		}
		count = 0;
		for (FeaturesCombine.BusinessUserPair p:testingData.keySet())
		{
			categoryNormalized = userCategoryNormalized.get(p.userID);
			if (categoryNormalized != null)
			{
				for (String category:businessCategoryMap.get(p.businessID))
				{
					if (categoryNormalized.containsKey(category) && categoryIndex.containsKey(category))
					{
						index = featureIndex+categoryIndex.get(category);
						testingData.get(p).put(index, categoryNormalized.get(category));
					}
				}
			}
			System.out.println(count++);
		}
		return featureIndex + categoryCount;
	}
	
}
