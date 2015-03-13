package org.rec.sample1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorization;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class SGDRecommender extends AbstractRecommender implements Recommender{

	RatingSGDFactorizer factorizer;
	Factorization factorization;
	double userFeatures[],itemFeatures[];
	DataModel model;
	ItemSimilarity sim1;
	List<RecommendedItem> itemsRecommended;
	List<MovieRecommended> movieList = new ArrayList<MovieRecommended>();
	
	
	protected  SGDRecommender(DataModel dataModel, RatingSGDFactorizer factorizer) {
		// TODO Auto-generated constructor stub
		super(dataModel);
		try {
			this.factorizer = factorizer;
			factorization = factorizer.factorize();
			model = dataModel;
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float estimatePreference(long userid, long itemid) throws TasteException {
		// TODO Auto-generated method stub
		itemFeatures = factorization.getItemFeatures(itemid);
		userFeatures = factorization.getUserFeatures(userid);
		
		int len = itemFeatures.length;
		float preference = 0.0f;
		for(int i=0; i<len; i++){
			preference = preference + (float)(itemFeatures[i] * userFeatures[i]);
		}
		return preference;
	}

	public List<RecommendedItem> recommend(long userid, int k) throws TasteException{
		List<RecommendedItem> recommendedList = new ArrayList<RecommendedItem>();
		LongPrimitiveIterator iter =  model.getItemIDs();
		FastIDSet ratedItemSet = model.getItemIDsFromUser(userid);
		
		int i = 0;
		while(iter.hasNext()){
			long item = iter.nextLong();
			boolean ignore = false;
			for(long ratedItem:ratedItemSet){
				if(ratedItem == item){
					ignore = true;
					break;
				}
			}
			if(ignore == true)
				continue;
			
			double rating = estimatePreference(userid, item);
			MovieRecommended mv = new MovieRecommended();
			mv.setItemID(item);
			mv.setValue((float)rating);
			movieList.add(mv);
		}
		
		Collections.sort(movieList);
		
		int count = 0;
		List<RecommendedItem> finalList = new ArrayList<RecommendedItem>();
		for(MovieRecommended m:movieList){
			
			if(count >= k)
				break;
			RecommendedItem item = m;
		finalList.add(item);
		count++;
		}
		return finalList;
	}
	@Override
	public List<RecommendedItem> recommend(long arg0, int arg1, IDRescorer arg2)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

}
