package nlp.lm;

import java.io.*;
import java.util.*;

/** 
 * A play on the simple bigram language model so that it predicts sentences from right to left.
 * Interpolated with a unigram model for smoothing
*/

public class BackwardBigramModel extends BigramModel {
	
    /**
     * Declare the start and end tokens as variables, so that they may be
     * interchanged for the forward and backward model.
    */
    public static String startToken = "</S>";
    public static String endToken = "<S>";

    public BackwardBigramModel() {
	super();
    }
    
    /**
     * Static class method that given a list of sentences, reverses
     * the word order in each sentence, and returns a new list
     * containing the reversed sentences.
    */
    public static ArrayList<List<String>> reverse(List<List<String>> sentences){
    	ArrayList<List<String>> reverseSentences = new ArrayList<List<String>>();
	for(List<String> sentence : sentences){
	    ArrayList<String> rsentence = new ArrayList<>(sentence);
	    Collections.reverse(rsentence);
	    reverseSentences.add(rsentence);
	}
	return reverseSentences;
    }

    /**
     * Call the superclass train method, but with reversed sentences.
    */
    public void train (List<List<String>> sentences) {
	super.train(reverse(sentences));
    }
    
    /**
     * Call the superclass test method, but with reversed sentences.
    */
    public void test (List<List<String>> sentences) {
	ArrayList<List<String>> rsentences = reverse(sentences);
	super.test(rsentences);
    }
    
    /**
     * Call the superclass test2 method, but with reversed sentences.
    */
    public void test2 (List<List<String>> sentences) {
    	ArrayList<List<String>> rsentences = reverse(sentences);
	super.test2(rsentences);
    }
    
    /**
     * Similar to the forward model main method
    */
    public static void main(String[] args) throws IOException {
	// All but last arg is a file/directory of LDC tagged input data
	File[] files = new File[args.length - 1];
	for (int i = 0; i < files.length; i++) 
	    files[i] = new File(args[i]);
	// Last arg is the TestFrac
	double testFraction = Double.valueOf(args[args.length -1]);
	// Get list of sentences from the LDC POS tagged input files
	List<List<String>> sentences = 	POSTaggedFile.convertToTokenLists(files);
	int numSentences = sentences.size();
	// Compute number of test sentences based on TestFrac
	int numTest = (int)Math.round(numSentences * testFraction);
	// Take test sentences from end of data
	List<List<String>> testSentences = sentences.subList(numSentences - numTest, numSentences);
	// Take training sentences from start of data
	List<List<String>> trainSentences = sentences.subList(0, numSentences - numTest);
	System.out.println("# Train Sentences = " + trainSentences.size() + 
			   " (# words = " + wordCount(trainSentences) + 
			   ") \n# Test Sentences = " + testSentences.size() +
			   " (# words = " + wordCount(testSentences) + ")");
	// Create a backward bigram model and train it.
	BackwardBigramModel model = new BackwardBigramModel();
	System.out.println("Training...");
	model.train(trainSentences);
	// Test on training data using test and test2
	model.test(trainSentences);
	model.test2(trainSentences);
	System.out.println("Testing...");
	// Test on test data using test and test2
	model.test(testSentences);
	model.test2(testSentences);
    }
}
