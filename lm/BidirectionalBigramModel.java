package nlp.lm;

import java.io.*;
import java.util.*;

/** 
 * @author Ray Mooney
 * A simple bigram language model that uses simple fixed-weight interpolation
 * with a unigram model for smoothing.
*/

public class BidirectionalBigramModel {

    BigramModel forwardModel;
    BackwardBigramModel backwardModel;

    public BidirectionalBigramModel(){
    	forwardModel = new BigramModel();
    	backwardModel = new BackwardBigramModel();
    }

    /** Train the model on a List of sentences represented as
     *  Lists of String tokens */
    public void train (List<List<String>> sentences) {
	// Accumulate unigram and bigram counts in maps
	forwardModel.train(sentences);
	backwardModel.train(sentences);
    }

    /** Like test1 but excludes predicting end-of-sentence when computing perplexity */
    public void test2 (List<List<String>> sentences) {
	double totalLogProb = 0;
	double totalNumTokens = 0;
	for (List<String> sentence : sentences) {
	    totalNumTokens += sentence.size();
	    double sentenceLogProb = sentenceLogProb2(sentence);
	    //	    System.out.println(sentenceLogProb + " : " + sentence);
	    totalLogProb += sentenceLogProb;
	}
	double perplexity = Math.exp(-totalLogProb / totalNumTokens);
	System.out.println("Word Perplexity = " + perplexity );
    }
    
    /** Like sentenceLogProb but excludes predicting end-of-sentence when computing prob */
  public double sentenceLogProb2 (List<String> sentence) {
  	ArrayList<String> rsentence = new ArrayList<>(sentence);
	Collections.reverse(rsentence);
	String prevTokenForward = forwardModel.startToken;
	String prevTokenBackward = backwardModel.startToken;
	double sentenceLogProb = 0;
	for (int i = 0; i < sentence.size(); i++) {
	    String tokenForward = sentence.get(i);
	    String tokenBackward = rsentence.get(i);
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
	    String bigramForward = forwardModel.bigram(prevTokenForward, tokenForward);
	    String bigramBackward = backwardModel.bigram(prevTokenBackward, tokenBackward);
	    DoubleValue bigramValForward = forwardModel.bigramMap.get(bigramForward);
	    DoubleValue bigramValBackward = backwardModel.bigramMap.get(bigramBackward);
	    double logProb = Math.log(interpolatedProb(unigramValForward, unigramValBackward, bigramValForward, bigramValBackward));
	    sentenceLogProb += logProb;
	    prevTokenForward = tokenForward;
	    prevTokenBackward = tokenBackward;
	}
	return sentenceLogProb;
    }


    /** Interpolate bigram prob using bigram and unigram model predictions */	 
    public double interpolatedProb(DoubleValue unigramValForward, DoubleValue unigramValBackward, DoubleValue bigramValForward, DoubleValue bigramValBackward) {
    	double lambdaForward = 0.7;
    	double lambdaBackward = 0.3;
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

    /** Train and test a bigram model.
     *  Command format: "nlp.lm.BigramModel [DIR]* [TestFrac]" where DIR 
     *  is the name of a file or directory whose LDC POS Tagged files should be 
     *  used for input data; and TestFrac is the fraction of the sentences
     *  in this data that should be used for testing, the rest for training.
     *  0 < TestFrac < 1
     *  Uses the last fraction of the data for testing and the first part
     *  for training.
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
