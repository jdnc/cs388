package cc.mallet.fst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class OovUtils{
	public static String[] featureList = new String[] {"caps", "hyp", "nums", "ing", "ogy", "ed", "s",  "ly", "ion", "tion", "ies"};
	public static HashSet<String> features = new HashSet<String>(Arrays.asList(featureList));
	public static HashSet vocabulary = new HashSet();
	public static ArrayList<String> getWords(String s){
		System.out.println("SENTENCE "+s);
		//System.out.println("in getwords");
		ArrayList<String> words = new ArrayList();
		//s = s.substring(s.indexOf("\n") + 1, s.length());
		for(String t : s.split("[\\r\\n]+")){
			//System.out.println(t);
			if(!t.isEmpty()){
				if(t.contains(" ")){
					//words.add(t.split("\\s+")[1]);
					String[] wordies =  t.split("\\s+");
					for(int k = 1; k < wordies.length; k++){
						System.out.println("WORDIES "+wordies[k]);
						if(features.contains(wordies[k]))
							continue;
						else{
							words.add(wordies[k]);
							break;
						}
					}
					
				}
			//System.out.println(t.split("\\s+")[1]);
			}
		}
		return words;
	}
	
	public static void populateVocabulary(String sentence){
		 ArrayList<String> tokens = OovUtils.getWords(sentence);
	       for(String tok : tokens){
	          vocabulary.add(tok);
	       }		
	}
};
