import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class newOrthoFeatures{
	public static void main(String[] args){
		try{
			//String outFileName = args[0].substring(0, args[0].indexOf(".")) + ".ortho";
			boolean append = false;
			if (args.length > 2)
				append = args[2].equals("a");	
			FileWriter fstream = new FileWriter(args[1], append);
			BufferedWriter outFile = new BufferedWriter(fstream);
			BufferedReader inFile = new BufferedReader(new FileReader(args[0]));
			String line = null;
			String prevToken = null;
			Pattern suffix = Pattern.compile(".+(ing|ogy|ed|s|ly|ion|tion|ies)$");
			Pattern prefix = Pattern.compile("^(anti|non|un|re|in|pre|sub|de).+$");
			Pattern numStart = Pattern.compile("^[1-9].*");
			while((line = inFile.readLine()) != null){
				StringBuilder sb = new StringBuilder();
				if (!line.isEmpty()){
					String[] tokens = line.split("\\s+");
					sb.append(tokens[0]);
					Matcher matchSuff =  suffix.matcher(tokens[0]);
					Matcher matchNumStart = numStart.matcher(tokens[0]);
					Matcher matchPre =  prefix.matcher(tokens[0]);
					
					if(matchSuff.matches()){
						System.out.print(matchSuff.group(1));
						sb.append(" " + matchSuff.group(1));
					}
					
					if(matchPre.matches()){
						System.out.print(matchPre.group(1));
						sb.append(" " + matchPre.group(1));
					}

					if (tokens[0].contains("-")){
						sb.append(" " + "hyp");
					}
					
					if(Character.isUpperCase(tokens[0].charAt(0))){
						sb.append(" " + "caps");
					}
					
					if(matchNumStart.matches()){
						sb.append(" " + "nums");
					}

					int len = tokens[0].length();

					if(len <= 3) 
						sb.append(" " + "w_s");

					if(len > 3 && len <=6) 
						sb.append(" " + "w_m");

					if(len > 6) 
						sb.append(" " + "w_l");

					if(prevToken != null)
						sb.append(" " + prevToken);
					
					sb.append(" " + tokens[1]);

					prevToken = tokens[1];
					
					outFile.write(sb.toString());
					outFile.write("\n");
				}
				else{
					outFile.write("\n");
					prevToken = null;
				}
				//System.out.println(sb.toString());
			}
			inFile.close();
			outFile.close();
		}
		catch (Exception e){
		    e.printStackTrace(System.out);		
		}		
	}
};