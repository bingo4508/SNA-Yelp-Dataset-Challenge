import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dataClean {
	public final static String inputFileName = "../dataAnalysis/yelp_dataset_challenge_academic_dataset";
	public final static String outBusinessFileName = "../dataAnalysis/Business.txt";
	public final static String outCheckInFileName = "../dataAnalysis/Checkin.txt";
	public final static String outTipFileName = "../dataAnalysis/Tip.txt";
	public final static String outReviewFileName= "../dataAnalysis/Review.txt";
	public final static String outUserFileName= "../dataAnalysis/User.txt";
	public static void main(String[] args)
	{
		BufferedReader input = null;
		PrintStream outBusiness = null;
		PrintStream outCheckIn = null;
		PrintStream outTip = null;
		PrintStream outReview = null;
		PrintStream outUser = null;
		String line = null;
		String []token;
		int index;
		String type;
		boolean foundType;
		try 
		{
			outBusiness = new PrintStream(new File(outBusinessFileName));
			outCheckIn = new PrintStream(new File(outCheckInFileName));
			outTip = new PrintStream(new File(outTipFileName));
			outReview = new PrintStream(new File(outReviewFileName));
			outUser = new PrintStream(new File(outUserFileName));
			input = new BufferedReader(new FileReader(inputFileName));
			line = input.readLine();
			while (line != null)
			{
				if (!line.equals(""))
				{
					token = line.split("\"");
					foundType = false;
					for (index = 0; index < token.length; index++)
						if (token[index].equals("type"))
							break;
					if (index < token.length)
					{
						type = token[index+2];
						if (type.equals("business"))
						{
							outBusiness.println(line.substring(line.indexOf("{")));
						}
						else if (type.equals("checkin"))
						{
							outCheckIn.println(line.substring(line.indexOf("{")));
						}
						else if (type.equals("review"))
						{
							outReview.println(line.substring(line.indexOf("{")));
						}
						else if (type.equals("tip"))
						{
							outTip.println(line.substring(line.indexOf("{")));
						}
						else if (type.equals("user"))
						{
							outUser.println(line.substring(line.indexOf("{")));
						}
					}
				}
				line = input.readLine();
			}
			input.close();
			outBusiness.close();
			outCheckIn.close();
			outReview.close();
			outTip.close();
			outUser.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
