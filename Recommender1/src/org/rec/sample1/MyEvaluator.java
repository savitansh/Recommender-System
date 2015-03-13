package org.rec.sample1;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;

import sun.org.mozilla.javascript.EvaluatorException;

public class MyEvaluator {
	File dataFile;
	DataModel model;
	ItemCollaborative col = new ItemCollaborative();
	
	
	public void generateModel(File file) throws IOException, TasteException{
		dataFile = file;
		col.makeModel(dataFile);
		model = col.getModel();
	}
	
	public double RMSError() throws TasteException{
		LongPrimitiveIterator iter = model.getUserIDs();
		double rmsError = 0.0;
		double meanRmsError = 0.0;
		double numUsers = 0.0;
		while(iter.hasNext()){
			rmsError = 0.0;
			long userid = iter.nextLong();
			FastIDSet idSet = model.getItemIDsFromUser(userid);
			LongPrimitiveIterator iter2 = idSet.iterator();
			double numItems = 0.0;
			while(iter2.hasNext()){
				long id = iter2.nextLong();
				double predictedRating = col.predictRatings(userid, id);
				double actualRating = model.getPreferenceValue(userid, id);
				rmsError += (predictedRating - actualRating) * (predictedRating - actualRating);
				numItems++;
			}
			rmsError = Math.sqrt(rmsError / numItems);
			meanRmsError += rmsError;
			numUsers++;
		}
		meanRmsError = meanRmsError / numUsers;
		return meanRmsError;
	}
	
	public double MeanAbsoluteError() throws TasteException{
		double mae = 0.0, numItems = 0.0;
		
		LongPrimitiveIterator iter = model.getUserIDs();
		while(iter.hasNext()){
			long userid = iter.nextLong();
			System.out.println("processing userid:"+userid);
			FastIDSet idSet = model.getItemIDsFromUser(userid);
			LongPrimitiveIterator iter2 = idSet.iterator();
			while(iter2.hasNext()){
				long id = iter2.nextLong();
				double predictedRating = col.predictRatings(userid, id);
				double actualRating = model.getPreferenceValue(userid, id);
				mae += Math.abs(predictedRating - actualRating);
				numItems++;
			}
		}
		mae = mae / numItems;
		return mae;
	}
	
	double evaluateMeanAbsError(RecommenderBuilder builder, DataModel model) throws TasteException{
		
		org.apache.mahout.cf.taste.recommender.Recommender rec1 = builder.buildRecommender(model);
		
		double mae = 0.0, numItems = 0.0;
		
		LongPrimitiveIterator iter = model.getUserIDs();
		while(iter.hasNext()){
			long userid = iter.nextLong();
			System.out.println("processing userid:"+userid);
			FastIDSet idSet = model.getItemIDsFromUser(userid);
			LongPrimitiveIterator iter2 = idSet.iterator();
			while(iter2.hasNext()){
				long id = iter2.nextLong();
				double predictedRating = rec1.estimatePreference(userid, id);
				double actualRating = model.getPreferenceValue(userid, id);
				double err = Math.abs(predictedRating - actualRating);
			//	System.out.println(err+","+predictedRating);
				mae += err;
				numItems++;
			}
			}
		mae = mae / numItems;
		return mae;
	}
}
