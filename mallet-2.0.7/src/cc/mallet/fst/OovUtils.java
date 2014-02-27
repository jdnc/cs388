package cc.mallet.fst;

import java.util.ArrayList;

public class OovUtils{
	public static ArrayList<String>getWords(String s){
		System.out.println("in getwords");
		ArrayList<String> words = null;
		for(String t : s.split("[\\r\\n]+")){
			words.add(t.split("\\s+")[1]);
			System.out.println(t.split("\\s+")[1]);
		}
		return words;
	}
};