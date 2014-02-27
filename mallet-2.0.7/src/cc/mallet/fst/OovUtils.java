package cc.mallet.fst;

import java.util.ArrayList;

public class OovUtils{
	public static ArrayList<String>getWords(String s){
		System.out.println("in getwords");
		ArrayList<String> words = new ArrayList();
		s = s.substring(s.indexOf("\n") + 1, s.length());
		for(String t : s.split("[\\r\\n]+")){
			System.out.println(t);
			words.add(t.split("\\s+")[1]);
			System.out.println(t.split("\\s+")[1]);
		}
		return words;
	}
};