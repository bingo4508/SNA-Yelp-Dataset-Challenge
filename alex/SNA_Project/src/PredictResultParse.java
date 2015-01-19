import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;


public class PredictResultParse {
	
	static class PredictResult
	{
		public int truePositive;
		public int predictNumber;
		public String businessID;
		public PredictResult(String businessID)
		{
			this.businessID = businessID;
			truePositive = 0;
			predictNumber = 0;
		}
		public PredictResult(int tp, int pn)
		{
			truePositive = tp;
			predictNumber = pn;
		}
	}
	
	public static String resultFileName = "dataCreated\\testingData_nonNormalize_category2_result2.txt";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String, PredictResult> resultMap;
		double avgTP = 0;
		try {
			resultMap = createResultMap(resultFileName);
			System.out.println(resultMap.size());
			
			for (PredictResult r:resultMap.values())
			{
				avgTP += r.truePositive;
			}
			avgTP /= resultMap.size();
			System.out.println(avgTP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static HashMap<String, PredictResult> createResultMap(String resultFileName) throws IOException
	{
		BufferedReader inputResult = new BufferedReader(new FileReader(resultFileName));
		String line;
		String token[];
		PredictResult oneBusinessResult;
		String businessID;
		HashMap<String, PredictResult> resultMap = new HashMap<String, PredictResult>();
		Stack<Boolean> predictFalseNotSure = new Stack<Boolean>();
		
		line = inputResult.readLine();
		token = line.split("\t");
		businessID = token[0].substring(1, token[0].length()-2);
		while (line != null && !line.equals(""))
		{
			oneBusinessResult = new PredictResult(businessID);
			predictFalseNotSure.clear();
			while (line != null && !line.equals("") && oneBusinessResult.businessID.equals(businessID))
			{
				if (Integer.parseInt(token[3]) == 1 && oneBusinessResult.predictNumber < 100)
				{
					oneBusinessResult.predictNumber++;
					if (Integer.parseInt(token[2]) == 1)
						oneBusinessResult.truePositive++;
				}
				if (Integer.parseInt(token[3]) == 0 && oneBusinessResult.predictNumber < 100)
				{
					predictFalseNotSure.push(Integer.parseInt(token[2]) == 1);
				}
				if (oneBusinessResult.predictNumber > 100)
					System.out.println("reach coupon limit!");
				line = inputResult.readLine();
				if (line != null && !line.equals(""))
				{
					token = line.split("\t");
					businessID = token[0].substring(1, token[0].length()-2);
				}
			}
			while (oneBusinessResult.predictNumber < 100 && !predictFalseNotSure.empty())
			{
				oneBusinessResult.predictNumber++;
				if (predictFalseNotSure.pop() == true)
					oneBusinessResult.truePositive++;
			}		
			resultMap.put(oneBusinessResult.businessID, oneBusinessResult);
		}
		inputResult.close();
		return resultMap;
	}
	
}
