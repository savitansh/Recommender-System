package org.rec.sample1;

import java.io.*;
import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ItemRecommender {
	static DataModel model;
	ItemSimilarity sim1;
	String fileName;
	public void makeModel(File f) throws IOException{
		model = new FileDataModel(f);
	}
	public void findlogSimmilarity(){
		sim1 = new LogLikelihoodSimilarity(model);
	}
	public void findPearsonSimmilarity() throws TasteException{
		sim1 = new PearsonCorrelationSimilarity(model);
	}

	public DataModelBuilder getModelBuilder(){
		DataModelBuilder modelBuilder;
		modelBuilder = new DataModelBuilder() {
	        public DataModel buildDataModel( FastByIDMap<PreferenceArray> trainingData ) {
	            return model;
	        }        
	    }; 
	    return modelBuilder;
	}
	
	public RecommenderBuilder getItemBasedRecommender(){
		RecommenderBuilder builder = new RecommenderBuilder() {
			
			  public Recommender buildRecommender(DataModel model) throws TasteException {
			    // build and return the Recommender to evaluate here
				//  ItemSimilarity sim1 = new LogLikelihoodSimilarity(model);
				  ItemSimilarity sim1 = new PearsonCorrelationSimilarity(model);
				//  ItemSimilarity sim1 = new AdjustedCosineSimmilarity(model);
				  return new GenericItemBasedRecommender(model, sim1);
				  	
			  }
			};
		return builder;	
	}
	
	
	public RecommenderBuilder getSVDRecommender(){
		RecommenderBuilder builder = new RecommenderBuilder() {
			
			  public Recommender buildRecommender(DataModel model) throws TasteException {
			    // build and return the Recommender to evaluate here
				  //ItemSimilarity sim1 = new LogLikelihoodSimilarity(model);
				  ItemSimilarity sim1 = new PearsonCorrelationSimilarity(model);
				//  ALSWRFactorizer factorizer = new ALSWRFactorizer(model, 35, 0.065, 10);
				  RatingSGDFactorizer factorizer = new RatingSGDFactorizer(model, 15, 10);
				  
				 // SVDPlusPlusFactorizer factorizer = new SVDPlusPlusFactorizer(model, 15, 10);
				return new SVDRecommender(model, factorizer);	
			  }
			};
		return builder;	
	}
	
	public RecommenderBuilder getSGDRecommender(){
		RecommenderBuilder builder = new RecommenderBuilder() {
			
			  public Recommender buildRecommender(DataModel model) throws TasteException {
			    // build and return the Recommender to evaluate here
				  //ItemSimilarity sim1 = new LogLikelihoodSimilarity(model);
				  ItemSimilarity sim1 = new PearsonCorrelationSimilarity(model);
				//  ALSWRFactorizer factorizer = new ALSWRFactorizer(model, 35, 0.065, 10);
				  RatingSGDFactorizer factorizer = new RatingSGDFactorizer(model, 2, 22);
				  
				 // SVDPlusPlusFactorizer factorizer = new SVDPlusPlusFactorizer(model, 15, 10);
				return new SGDRecommender(model, factorizer);	
			  }
			};
		return builder;	
	}
	
	public RecommenderBuilder getMyRecommender(){
		RecommenderBuilder builder = new RecommenderBuilder() {
			
			  public Recommender buildRecommender(DataModel model) throws TasteException {
			    // build and return the Recommender to evaluate here
				  //ItemSimilarity sim1 = new LogLikelihoodSimilarity(model);
				//  ItemSimilarity sim1 = new PearsonCorrelationSimilarity(model);
				  ItemSimilarity sim1 = new AdjustedCosineSimmilarity(model);  
				return new MyRecommender(model);	
			  }
			};
		return builder;
	}
	
	public RecommenderBuilder getEnsembledRecommender1(){
		RecommenderBuilder builder = new RecommenderBuilder() {
			
			  public Recommender buildRecommender(DataModel model) throws TasteException {
				return new EnsembledMFRecommender1(model);	
			  }
			};
		return builder;
	}
	
	public RecommenderBuilder getClusteringRecommender1(){
		RecommenderBuilder builder = new RecommenderBuilder() {
			
			  public Recommender buildRecommender(DataModel model) throws TasteException {
				try {
					return new ClusteringRecommender(model);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}	
			  }
			};
		return builder;
	}
	public RecommenderEvaluator getMAEEvaluator(){
		return new AverageAbsoluteDifferenceRecommenderEvaluator();
	}
	
	public RecommenderEvaluator getRMSEvaluator(){
		return new RMSRecommenderEvaluator();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{		
		String filename = "Data/movies.csv";
		File f = new File(filename);
		ItemRecommender ir1 = new ItemRecommender();
		ir1.makeModel(f);
//		RecommenderBuilder rec1 = ir1.getMyRecommender();
//		double prediction = rec1.buildRecommender(model).estimatePreference(196, 242);
//		System.out.println(prediction);
		
		RecommenderEvaluator maeEvaluator = ir1.getMAEEvaluator();
		
			/*String filename = "Data/movies.csv";
			File f = new File(filename);
			ItemRecommender ir1 = new ItemRecommender();
			ir1.makeModel(f);	
		Recommender rec = new ClusteringRecommender(model);	
		List<RecommendedItem> recommendations = rec.recommend(196, 4);
		System.out.println(recommendations.get(2).getItemID()); 
		
			ClusteringRecommender crc = new ClusteringRecommender(model);
			System.out.println(crc.estimatePreference(244, 377));*/
			
	//		RecommenderBuilder crcb = ir1.getClusteringRecommender1();
//			
			RecommenderBuilder rec2 = ir1.getEnsembledRecommender1();
//			
//			RecommenderBuilder itemBasedRecommender = ir1.getItemBasedRecommender();
			
			//RatingSGDFactorizer factorizer = new RatingSGDFactorizer(model, 25, 12);
//			RecommenderBuilder mySGDecommender = ir1.getSGDRecommender();
			
			//RecommenderBuilder svdRecommender = ir1.getSVDRecommender();
			
//			double error = maeEvaluator.evaluate(crcb, null, model, 0.9, 1);
			double error2 = maeEvaluator.evaluate(rec2, null, model, 0.9, 1);
		//	double error3 = maeEvaluator.evaluate(itemBasedRecommender, null, model, 0.95, 1);
		//	double error4 = maeEvaluator.evaluate(mySGDecommender, null, model, 0.9, 1);
			//double error5 = maeEvaluator.evaluate(svdRecommender, null, model, 0.9, 1);
			
//			System.out.println("clustering based err:"+error);
//			
			System.out.println("ensembled based err:"+error2);
//			
//			System.out.println("itembased based err:"+error3);
			
//			System.out.println("sgd based based err:"+error4);
			
			//System.out.println("svd based based err:"+error5);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
