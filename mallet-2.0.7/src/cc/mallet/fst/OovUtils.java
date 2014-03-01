package cc.mallet.fst;

import java.util.ArrayList;
import java.util.HashSet;

public class OovUtils{
	public static HashSet vocabulary = new HashSet();
	public static ArrayList<String> getWords(String s){
		System.out.println("SENTENCE "+s);
		//System.out.println("in getwords");
		ArrayList<String> words = new ArrayList();
		//s = s.substring(s.indexOf("\n") + 1, s.length());
		for(String t : s.split("[\\r\\n]+")){
			//System.out.println(t);
			if(!t.isEmpty()){
				if(t.contains(" "))
					words.add(t.split("\\s+")[1]);
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
