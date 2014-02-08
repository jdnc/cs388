package nlp.lm;

import java.io.*;
import java.util.*;

/** 
 * @author Ray Mooney
 * A simple bigram language model that uses simple fixed-weight interpolation
 * with a unigram model for smoothing.
*/

public class BackwardBigramModel extends BigramModel {

    public static String startToken = "</S>";
    public static String endToken = "<S>";

    public BackwardBigramModel() {
	super();
    }
    
    public static ArrayList<List<String>> reverse(List<List<String>> sentences){
    	ArrayList<List<String>> reverseSentences = new ArrayList<List<String>>();
	for(List<String> sentence : sentences){
	    ArrayList<String> rsentence = new ArrayList<>(sentence);
	    Collections.reverse(rsentence);
	    reverseSentences.add(rsentence);
	}
	return reverseSentences;
    }

    public void train (List<List<String>> sentences) {
    	
	// Accumulate unigram and bigram counts in maps
	trainSentences(reverse(sentences));
	// Compure final unigram and bigram probs from counts
	calculateProbs();
    }
    
    /** Use sentences as a test set to evaluate the model. Print out perplexity
     *  of the model for this test data */
    public void test (List<List<String>> sentences) {
	// Compute log probability of sentence to avoid underflow
	ArrayList<List<String>> rsentences = reverse(sentences);
	double totalLogProb = 0;
	// Keep count of total number of tokens predicted
	double totalNumTokens = 0;
	// Accumulate log prob of all test sentences
	for (List<String> sentence : rsentences) {
	    // Num of tokens in sentence plus 1 for predicting </S>
	    totalNumTokens += sentence.size() + 1;
	    // Compute log prob of sentence
	    double sentenceLogProb = sentenceLogProb(sentence);
	    //	    System.out.println(sentenceLogProb + " : " + sentence);
	    // Add to total log prob (since add logs to multiply probs)
	    totalLogProb += sentenceLogProb;
	}
	// Given log prob compute perplexity
	double perplexity = Math.exp(-totalLogProb / totalNumTokens);
	System.out.println("Perplexity = " + perplexity );
    }
    
     /** Like test1 but excludes predicting end-of-sentence when computing perplexity */
    public void test2 (List<List<String>> sentences) {
    	ArrayList<List<String>> rsentences = reverse(sentences);
	double totalLogProb = 0;
	double totalNumTokens = 0;
	for (List<String> sentence : rsentences) {
	    totalNumTokens += sentence.size();
	    double sentenceLogProb = sentenceLogProb2(sentence);
	    //	    System.out.println(sentenceLogProb + " : " + sentence);
	    totalLogProb += sentenceLogProb;
	}
	double perplexity = Math.exp(-totalLogProb / totalNumTokens);
	System.out.println("Word Perplexity = " + perplexity );
    }
    

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
	// Create a bigram model and train it.
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
