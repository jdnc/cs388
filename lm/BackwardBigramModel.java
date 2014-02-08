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

    /*
    public void trainSentences (List<List<String>> sentences) {
	for (List<String> sentence : sentences) {
		ArrayList<String> rsentence = new ArrayList<>(sentence);
		Collections.reverse(rsentence);
	    trainSentence(rsentence);
	}
    }*/

    /** Return bigram string as two tokens separated by a newline */
    /*public String bigram (String prevToken, String token) {
	if(prevToken.equals("<S>") || token.equals("</S>"))
	    return token + "\n" + prevToken;
	return prevToken + "\n" + token;
    }*/
    public void train (List<List<String>> sentences) {
    	ArrayList<List<String>> reverseSentences = new ArrayList<List<String>>();
	for(List<String> sentence : sentences){
	    ArrayList<String> rsentence = new ArrayList<>(sentence);
	    Collections.reverse(rsentence);
	    reverseSentences.add(rsentence);
	}
	// Accumulate unigram and bigram counts in maps
	trainSentences(reverseSentences);
	// Compure final unigram and bigram probs from counts
	calculateProbs();
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
