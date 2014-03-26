

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.EvaluateTreebank;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.trees.MemoryTreebank;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.Treebank;
import edu.stanford.nlp.util.Timing;


public class UnsupervisedParser {
	public static void main(String[] args){
		// parse cmd line and run experiments
		parseCmd(args);
	}
	
	public static void parseCmd(String[] args){
		CommandLineParser CliParser = new BasicParser();
		org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
		String testFile, seedFile, selftrainFile;
		// Create various command line options
		//1. -help
		Option help = new Option( "help", "print this message" );
		
		//2. -seed <file> 
		Option seed = OptionBuilder.withArgName( "file" )
                .hasArg()
                .withDescription(  "use given file as seed set" )
                .create( "seed" );
		
		//3. -selftrain <file>
		Option selftrain  = OptionBuilder.withArgName( "file" )
                .hasArg()
                .withDescription(  "use given file as self-training set" )
                .create( "selftrain" );
		
		//4. -test <file>
		Option test  = OptionBuilder.withArgName( "file" )
                .hasArg()
                .withDescription(  "use given file as test set for evaluation" )
                .create( "test" );
		
		// add the various options
		options.addOption(help);
		options.addOption(test);
		options.addOption(seed);
		options.addOption(selftrain);
		
		//check if self training is to be performed or not
		boolean doSelfTrain = true;
		
		// parse the user given input
		try{
			CommandLine line = CliParser.parse( options, args );
			if (args.length == 0 || line.hasOption("help") ){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "UnsupervisedParser", options );
			}
			else{
				if (line.hasOption("seed")){
					seedFile = line.getOptionValue("seed");
				}
				else{
					System.out.println("seed set must be specified.\nSee UnsupervisedParser -help for details.");
					return;
				}
				
				if (line.hasOption("selftrain")){
					selftrainFile = line.getOptionValue("selftrain");
				}
				else{
					System.out.println("No self training set provided. Parser will not perform self training.");
					selftrainFile = null;
					doSelfTrain = false;
				}
				if (line.hasOption("test")){
					testFile = line.getOptionValue("test");
				}
				else{
					System.out.println("test set must be specified.\nSee UnsupervisedParser -help for details.");
					return;
				}
				// now perform experiments with parser
				runParser(seedFile, selftrainFile, testFile, doSelfTrain);
			}			
		}
		catch ( Exception ex){
			 System.err.println( "Parsing failed.  Reason: " + ex.getMessage() );
		}
		
		return;
		
	}
	
	public static void runParser(String seed, String selftrain, String test, boolean doSelfTrain){
		// create options to feed in treebank
		Options op = new Options();
		op.doDep = false;
		op.doPCFG = true;
		op.setOptions("-goodPCFG", "-evals", "tsv");
		
		//store some size information for convenience
		int seedSize, selftrainSize, newTrainSize, testSize;
		seedSize = selftrainSize = newTrainSize = testSize = 0;
		
		// create treebank from the seed test to train parser initially
		MemoryTreebank seedBank = makeTreebank(seed, op);
		seedSize = seedBank.size();
		
		//train the parser on the seed set
		LexicalizedParser lp = LexicalizedParser.trainFromTreebank(seedBank, op);
		
		
		if (doSelfTrain) {
			//get the labeled trees for the self training set
			MemoryTreebank selfBank = makeTreebank(selftrain, op);
			selftrainSize = selfBank.size();
			List<Tree> selfTrees = new ArrayList<Tree>();
			Iterator<Tree> it = selfBank.iterator();
			while (it.hasNext()) {
				Tree labeledTree = lp.apply(getInputSentence(it.next()));
				seedBank.add(labeledTree);
			}
			// now retrain parser with both original + self trained data
			lp = LexicalizedParser.trainFromTreebank(seedBank, op);
		}
		
		newTrainSize = seedBank.size();
		// test on the test set
		// create test treebank
		MemoryTreebank testBank = makeTreebank(test, op);
		testSize = testBank.size();
		
		EvaluateTreebank et = new EvaluateTreebank(lp);
		double f1Score = et.testOnTreebank(testBank);
		System.out.println("-----Results-------");
		System.out.println("Seed size: "+seedSize);
		System.out.println("Self train size: "+selftrainSize);
		System.out.println("Combined Training size: "+newTrainSize);
		System.out.println("Test size: "+testSize);
		System.out.println("f1 score: "+f1Score);
		System.out.println("-------------------\n");
		return;
	}
	
	public static MemoryTreebank makeTreebank(String treebankPath, Options op) {
	    System.err.println("Training a parser from treebank dir: " + treebankPath);
	    MemoryTreebank treebank = op.tlpParams.memoryTreebank();
	    System.err.print("Reading trees..."); 
	    treebank.loadPath(treebankPath);
	    Timing.tick("done [read " + treebank.size() + " trees].");
	    return treebank;
	  }
	
	public static List<CoreLabel> getInputSentence(Tree t) {
	     return Sentence.toCoreLabelList(t.yieldWords());
	  }

}
