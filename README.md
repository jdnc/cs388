Contents
========
the unzipped folder contains:

1. report.pdf  ---  the report discussing and comparing results for different models.
2. nlp  --- this is the folder that contains the source code.
3. backward-trace.txt --- the trace file for the backward model
4. bidirectional-trace.txt --- the trace file for the bidirectional model
5. README.md


Compiling and running the source code
======================================
Make sure that you are in the parent folder that contains the nlp folder.

To compile the code::

   javac nlp/lm/*.java

To run any model on any of the data::
   
   java nlp.lm.<MODEL> /projects/nlp/penn-treebank3/tagged/pos/<DATA>/ 0.1
  
where you should replace 
  <MODEL> := BigramModel | BackwardBigramModel | BidirectionalBigramModel
  <DATA> := atis | wsj | brown

Note : you may also replace 0.1 by your choice of test data fraction.