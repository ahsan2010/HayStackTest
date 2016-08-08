package com.test;

import java.io.File;
import java.io.IOException;

import com.aliasi.classify.Classification;
import com.aliasi.classify.Classified;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.Files;

public class LingePipeSentiment {

	File mPolarityDir;
	String[] mCategories;
	DynamicLMClassifier<NGramProcessLM> mClassifier;

	public void loadData() {
		String path = "/home/amee/Documents/Lib/Training Testing DataSet/Sentiment/review_polarity/";
		mPolarityDir = new File(path, "txt_sentoken");
		mCategories = mPolarityDir.list();
		for (String s : mCategories) {
			System.out.println(s);
		}
		int nGram = 8;
		mClassifier = DynamicLMClassifier.createNGramProcess(mCategories, nGram);
	}

	boolean isTrainingFile(File file) {
		return file.getName().charAt(2) != '9'; // test on fold 9
	}

	void train() throws IOException {

		for (int i = 0; i < mCategories.length; ++i) {
			String category = mCategories[i];
			Classification classification = new Classification(category);
			File dir = new File(mPolarityDir, mCategories[i]);
			File[] trainFiles = dir.listFiles();
			for (int j = 0; j < trainFiles.length; ++j) {
				File trainFile = trainFiles[j];
				if (isTrainingFile(trainFile)) {
					String review = Files.readFromFile(trainFile, "ISO-8859-1");
					Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
					mClassifier.handle(classified);
				}
			}
		}
	}

	void evaluate() throws IOException {
		int numTests = 0;
		int numCorrect = 0;
		for (int i = 0; i < mCategories.length; ++i) {
			String category = mCategories[i];
			File file = new File(mPolarityDir, mCategories[i]);
			File[] testFiles = file.listFiles();
			for (int j = 0; j < testFiles.length; ++j) {
				File testFile = testFiles[j];
				if (!isTrainingFile(testFile)) {
					String review = Files.readFromFile(testFile, "ISO-8859-1");
					++numTests;
					Classification classification = mClassifier.classify(review);
					String resultCategory = classification.bestCategory();
					if (resultCategory.equals(category))
						++numCorrect;
				}
			}
		}

		System.out.println("  # Test Cases=" + numTests);
		System.out.println("  # Correct=" + numCorrect);
		System.out.println("  % Correct=" + ((double) numCorrect) / (double) numTests);

	}

	public LingePipeSentiment() throws Exception{
		loadData();
		System.out.println("Load Data Complete");
		train();
		System.out.println("Complete Training Data");
		evaluate();
		System.out.println("Finish");
	}
	
	public static void main(String arg[]) {
		try{
			new LingePipeSentiment();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
