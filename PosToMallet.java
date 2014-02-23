import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PosToMallet {
	
	public static void main(String[] args){
		try{
			Pattern keep = Pattern.compile("\\s+()/()\\s+");
			Pattern discard = Pattern.compile("\\[\\s*@.+/CD\\s*\\]");
			Pattern newLine = Pattern.compile("=+");
			String outFileName = args[0].substring(0, args[0].indexOf("."));
			FileWriter fstream = new FileWriter(outFileName);
			BufferedWriter outFile = new BufferedWriter(fstream);
			BufferedReader inFile = new BufferedReader(new FileReader(args[0]));
			String line = null;
			while((line = inFile.readLine()) != null){
				Matcher mKeep = keep.matcher(line);
				Matcher mDiscard = discard.matcher(line);
				Matcher mNewLine = newLine.matcher(line);
				if(mNewLine.find())
					outFile.write("\n");
				else if(mDiscard.find())
					continue;
				else while(mKeep.find()){
					outFile.write(mKeep.group(1) + " " + mKeep.group(2) + "\n");
				}
			}
			inFile.close();
			outFile.close();
		}
		catch (Exception e){
			
		}
		
	}

}
