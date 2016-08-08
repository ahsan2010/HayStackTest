package com.test;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class Test {

	public static void main ( String arg[] ){
		//final MaxentTagger tagger = new MaxentTagger("/home/amee/Downloads/Duplicate Detection MS Thesis Papers/stanford-postagger-2015-04-20/models/english-left3words-distsim.tagger");
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse,sentiment");
		props.setProperty("pos.model","/home/amee/git/haystack/HayStack/src/english-left3words-distsim.tagger");
		props.setProperty("ner.model", "/home/amee/Documents/Stamford CoreNLP Resources/stanford-ner-2014-06-16/classifiers/english.all.3class.distsim.crf.ser.gz");
		props.setProperty("parser.model", "/home/amee/Documents/Stamford CoreNLP Resources/englishPCFG.caseless.ser.gz");
		//props.setProperty("dcoref.demonym", "/home/amee/Documents/Stamford CoreNLP Resources/demonyms.txt");
		//props.setProperty("dcoref.state-abbreviations", "/home/amee/Documents/Stamford CoreNLP Resources/state-abbreviations.txt");
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		String text = "Ahsan is a very good boy. Hello this is me.";
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		System.out.println(document);
		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		
		System.out.println(sentences.size());
		
		for(CoreMap sentence : sentences){
			Tree tree = sentence.get(TreeAnnotation.class);
			  System.out.println("parse tree:\n" + tree);
		}
		
		
	    

		
	}
}
