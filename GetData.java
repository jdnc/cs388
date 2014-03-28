
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetData {
	public static String wsjSeed = "wsjSeed.mrg";
	public static String wsjTest = "wsjTest.mrg";
	public static String brownTrain = "brownTrain.mrg";
	public static String brownTest = "brownTest.mrg";
	public static Pattern endOfTree = Pattern.compile("^\\s*\\(. .\\) \\)\\)\\s*$");
	
	public static void createWsjSeed(String dirName){
		String outFileName = wsjSeed;
		FileWriter fstream;
		try {
			fstream = new FileWriter(outFileName, true);
			BufferedWriter outFile = new BufferedWriter(fstream);
			File wsjDir = new File(dirName);
			File[] wsjSub = wsjDir.listFiles();
			for (File f : wsjSub){
				if(f.getName() == "00"|| f.getName() == "01" || f.getName() == "23" || f.getName() == "24" || !f.isDirectory())
					continue;
				File[] actualFiles = f.listFiles();
				for (File df : actualFiles){
					BufferedReader inFile = new BufferedReader(new FileReader(df));
					String line = null;
					while((line = inFile.readLine()) != null){
						outFile.write(line);
						outFile.newLine();
					}
					inFile.close();
				}
		  }
		  outFile.close();
	
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return;
	}


	public static void createWsjTest(String dirName){
		String outFileName = wsjTest;
		FileWriter fstream;
		try {
			fstream = new FileWriter(outFileName, true);
			BufferedWriter outFile = new BufferedWriter(fstream);
			File wsjDir = new File(dirName);
			File[] wsjSub = wsjDir.listFiles();
			for (File f : wsjSub){
				BufferedReader inFile = new BufferedReader(new FileReader(f));
				String line = null;
				while((line = inFile.readLine()) != null){
					outFile.write(line);
					outFile.newLine();
				}
				inFile.close();				
		  }
		  outFile.close();
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return;
	}
	
	public static void getTrainTestFromBrown(String dirName){
		String trainFile = brownTrain;
		String testFile = brownTest;
		FileWriter ftrain, ftest;
		try {
			ftrain = new FileWriter(trainFile, true);
			ftest = new FileWriter(testFile, true);
			BufferedWriter outTrain = new BufferedWriter(ftrain);
			BufferedWriter outTest = new BufferedWriter(ftest);
			File brownDir = new File(dirName);
			File[] brownSub = brownDir.listFiles();
			for (File f : brownSub){
				if(!f.isDirectory())
					continue;
				List<String>trees = new ArrayList<String>();
				File[] actualFiles = f.listFiles();
				for (File df : actualFiles){
					BufferedReader inFile = new BufferedReader(new FileReader(df));
					String line = null;
					StringBuilder sb = new StringBuilder();
					while((line = inFile.readLine()) != null){
            if(line.startsWith("*x*"))
                continue;
						sb.append(line);
            sb.append("\n");
						Matcher endTree = endOfTree.matcher(line);
						if(endTree.find()){
							trees.add(sb.toString());
							sb.setLength(0);
						}
					}
					inFile.close();
				}	
				int numSentences = trees.size();
				// Compute number of test sentences based on TestFrac
				int numTest = (int)Math.round(numSentences * 0.1);
				// Take test sentences from end of data
				List<String> testSentences = trees.subList(numSentences - numTest, numSentences);
				// Take training sentences from start of data
				List<String> trainSentences = trees.subList(0, numSentences - numTest);
				for (String s: testSentences){
					outTest.write(s);
					outTest.newLine();
				}
				for(String s: trainSentences){
					outTrain.write(s);
					outTrain.newLine();
				}
			}
			outTrain.close();
			outTest.close();	
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		return;
	}
	
	public static void getKSentences(String outFileName, int n, String corpus){
		int numSentences = 0;
		try {
      String filename = null;
      if(corpus.equals("brown"))
        filename = brownTrain;
      else
        filename = wsjSeed;
			BufferedReader inFile = new BufferedReader(new FileReader(filename));
			FileWriter outFile = new FileWriter(outFileName);
			String line = null;
			StringBuilder sb = new StringBuilder();
			while((line = inFile.readLine()) != null){
				if(line.startsWith("*x*"))
                	continue;
				sb.append(line);
        		sb.append("\n");
				Matcher endTree = endOfTree.matcher(line);
				if(endTree.find()){
					outFile.write(sb.toString());
					numSentences++;
					if(numSentences == n)
						break;
					sb.setLength(0);
				}
			}
			inFile.close();
			outFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String args[]){
		// args[0] is which operation to perform
		// can be "brown"||"wsj"||"wsj-k"||wsjTest
		
		// for brown 
		// args[1] = name of brown directory
		
		// for wsj 
		// args[1] is name of wsj dir

		// for wsjTest
		// args[1] is name of dir
		
		// for wsj-k 
		// args[1] is the number of sentences (k)
		// args[2] is name of output file
		
		if (args[0].equals("brown")){
			getTrainTestFromBrown(args[1]);
		}
		else if (args[0].equals("wsj")){
			createWsjSeed(args[1]);
		}
		else if (args[0].equals("wsjTest")){
			createWsjTest(args[1]);
		}
		else if(args[0].equals("wsjk")){
     // System.out.println(args[1]);
     // System.out.println(args[2]);
     // args[3] is the either wsj or brown
			int n = Integer.parseInt(args[1]);
			getKSentences(args[2], n, args[3]);
		}
		else{
			System.out.println("Error: Unrecognized args to program.");
			System.out.println("Args must be one of 'brown' | 'wsj' | 'wsj-k'.");			
		}
		return;
	}
}
