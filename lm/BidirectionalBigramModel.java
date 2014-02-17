package nlp.lm;

import java.io.*;
import java.util.*;

/** 
 * An interpolation of both the forward and backward bigram model
*/

public class BidirectionalBigramModel {

    /**
     *  Has a forward model class object and a backward model class object as members
    */
    BigramModel forwardModel;
    BackwardBigramModel backwardModel;

    public BidirectionalBigramModel(){
    	forwardModel = new BigramModel();
    	backwardModel = new BackwardBigramModel();
    }

    /**
     * Call the train method of the individual models to 
     * accumulate unigram/bigram counts
    */
    public void train (List<List<String>> sentences) {
	forwardModel.train(sentences);
	backwardModel.train(sentences);
    }

    /** Keep only test2 that considers just the word perplexity metric*/
    public void test2 (List<List<String>> sentences) {
	double totalLogProb = 0;
	double totalNumTokens = 0;
	for (List<String> sentence : sentences) {
	    totalNumTokens += sentence.size();
	    double sentenceLogProb = sentenceLogProb2(sentence);
	    totalLogProb += sentenceLogProb;
	}
	double perplexity = Math.exp(-totalLogProb / totalNumTokens);
	System.out.println("Word Perplexity = " + perplexity );
    }
    
    /** Interpolate probabilities from both the forward and backward model 
     * for every token with each weight = 0.5.
    */
  public double sentenceLogProb2 (List<String> sentence) {
  	ArrayList<String> rsentence = new ArrayList<>(sentence);
  	// reverse the sentences for the backward model
	Collections.reverse(rsentence);
	// the forward and backward model have different start and end tokens
	String prevTokenForward = forwardModel.startToken;
	String prevTokenBackward = backwardModel.startToken;
	double sentenceLogProb = 0;
	for (int i = 0; i < sentence.size(); i++) {
	    String tokenForward = sentence.get(i);
	    String tokenBackward = rsentence.get(i);
	    // use unigram maps from the individual models
	    DoubleValue unigramValForward = forwardModel.unigramMap.get(tokenForward);
	    DoubleValue unigramValBackward = backwardModel.unigramMap.get(tokenBackward);
	    if (unigramValForward == null) {
		tokenForward = "<UNK>";
		unigramValForward = forwardModel.unigramMap.get(tokenForward);
	    }
	    if (unigramValBackward == null) {
		tokenBackward = "<UNK>";
		unigramValBackward = backwardModel.unigramMap.get(tokenBackward);
	    }
	    //use bigram maps from the individual models
	    String bigramForward = forwardModel.bigram(prevTokenForward, tokenForward);
	    String bigramBackward = backwardModel.bigram(prevTokenBackward, tokenBackward);
	    DoubleValue bigramValForward = forwardModel.bigramMap.get(bigramForward);
	    DoubleValue bigramValBackward = backwardModel.bigramMap.get(bigramBackward);
	    //return an interpolated probability from both the models
	    double logProb = Math.log(interpolatedProb(unigramValForward, unigramValBackward, bigramValForward, bigramValBackward));
	    sentenceLogProb += logProb;
	    prevTokenForward = tokenForward;
	    prevTokenBackward = tokenBackward;
	}
	return sentenceLogProb;
    }


    /** Interpolate individual model probabilities with weights lambdaForward and lambdaBackward = 0.5 */	 
    public double interpolatedProb(DoubleValue unigramValForward, DoubleValue unigramValBackward, DoubleValue bigramValForward, DoubleValue bigramValBackward) {
    	double lambdaForward = 0.5;
    	double lambdaBackward = 0.5;
    	double forwardProb = forwardModel.interpolatedProb(unigramValForward, bigramValForward);
    	double backwardProb = backwardModel.interpolatedProb(unigramValBackward, bigramValBackward);
    	return lambdaForward * forwardProb + lambdaBackward * backwardProb;
    }
    
    public static int wordCount (List<List<String>> sentences) {
	int wordCount = 0;
	for (List<String> sentence : sentences) {
	    wordCount += sentence.size();
	}
	return wordCount;
    }

    /** Train and test the bidirectional model, similar to the forward model main method.*/
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
	// Create a bidirectional bigram model and train it.
	BidirectionalBigramModel model = new BidirectionalBigramModel();
	System.out.println("Training...");
	model.train(trainSentences);
	// Test on training data using test2
	model.test2(trainSentences);
	System.out.println("Testing...");
	// Test on test data using test2
	model.test2(testSentences);
    }

}
