package org.rec.sample1;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class UserInterface {
	static DataModel model;
	
	public static void main(String[] args) throws IOException, TasteException {
		// TODO Auto-generated method stub
		ItemRecommender ir1 = new ItemRecommender();
		String filename = "Data/movies.csv";
		//filename = "Data/movies_1m.csv";
		File f = new File(filename);
		DataModel model = new FileDataModel(f);
		RecommenderBuilder builder=null;
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter recommender type\n");
		System.out.println("1. Generic item based");
		System.out.println("2. MF based");
		System.out.println("3. Ensembled MF based");
		System.out.println("4. User clustering based");
		int recType = Integer.parseInt(sc.next());
		
		
		if(recType == 1){
		builder = ir1.getItemBasedRecommender();	
		}else if(recType == 2){
			builder = ir1.getSGDRecommender();
		}else if(recType == 3){
			builder = ir1.getEnsembledRecommender1();
		}else if(recType == 4){
			builder = ir1.getClusteringRecommender1();
		}
		
		Recommender rec = builder.buildRecommender(model);
		
	System.out.println("Enter 1 to get predicted rating \n 2 to get top k recommendations \n 3 to get evaluation score \n 4 to add new rating");
		int choice = Integer.parseInt(sc.next());
		if(choice == 1){
			
		System.out.println("Enter useid");
		long userid = Long.parseLong(sc.next());
		System.out.println("Enter itemid");
		long itemid = Long.parseLong(sc.next());
		float rating = rec.estimatePreference(userid, itemid);
		System.out.println("Predicted rating :" + rating);
		
		}else if(choice == 2){
			
		System.out.println("Enter userid");
		long userid = Long.parseLong(sc.next());
		System.out.println("Enter no. of recommendations to get");
		int k = Integer.parseInt(sc.next());
		List<RecommendedItem> recommendations = rec.recommend(userid, k);
		for(RecommendedItem movie:recommendations){
			System.out.println(movie.getItemID()+",rating="+movie.getValue());
		}
		}else if(choice == 3){
		
		RecommenderEvaluator evaluator = ir1.getMAEEvaluator();
		float trainingRatio = 0.9f;
		double error= evaluator.evaluate(builder, null, model, trainingRatio, 1);
		System.out.println("MAE error for training ratio "+trainingRatio+" = "+error);
		}
	}

}
