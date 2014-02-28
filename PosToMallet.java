import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PosToMallet {
	
	public static void main(String[] args){
		//System.out.println("hello World");
		//System.out.println(args[0]);
		try{
			Pattern keep = Pattern.compile("([^\\s\\[\\]]+)/([^\\s\\[\\]]+)");
			Pattern discard = Pattern.compile("\\[\\s*@.+/CD\\s*\\]");
			Pattern newLine = Pattern.compile("=+");
			//String outFileName = args[0].substring(0, args[0].indexOf(".")) + ".txt";
			FileWriter fstream = new FileWriter(args[1]);
			BufferedWriter outFile = new BufferedWriter(fstream);
			BufferedReader inFile = new BufferedReader(new FileReader(args[0]));
			String line = null;
			while((line = inFile.readLine()) != null){
				//System.out.println("here");
				System.out.println(line);
				Matcher mKeep = keep.matcher(line);
				Matcher mDiscard = discard.matcher(line);
				Matcher mNewLine = newLine.matcher(line);
				if(mNewLine.find()){
					System.out.println("in newline");
					outFile.write("\n");
				}
				else if(mDiscard.find()){
					System.out.println("in discard");
					continue;
				}
				else while (mKeep.find()){
                                        System.out.println("here");
                                        outFile.write(mKeep.group(1) + " " + mKeep.group(2) + "\n");                 
                                }

			}
			inFile.close();
			outFile.close();
		}
		catch (Exception e){
		    e.printStackTrace(System.out);		
		}
		
	}

}
