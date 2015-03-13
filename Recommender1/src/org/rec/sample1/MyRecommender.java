package org.rec.sample1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;


public class MyRecommender extends AbstractRecommender implements Recommender{

	
	List<RecommendedItem> itemsRecommended;
	DataModel model;
	ItemSimilarity sim1;
	
	protected MyRecommender(DataModel dataModel) throws TasteException {
		super(dataModel);
		// TODO Auto-generated constructor stub
		model = dataModel;
		//sim1 = new PearsonCorrelationSimilarity(model);
		sim1 = new AdjustedCosineSimmilarity(model);
	}

	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float estimatePreference(long userid, long itemid) throws TasteException {
		// TODO Auto-generated method stub
		FastIDSet items = null;
		if(model.getItemIDsFromUser(userid) != null)
		items = model.getItemIDsFromUser(userid);
		else
			return (float) 0.0;
		
		LongPrimitiveIterator iter = items.iterator();
		float total=(float) 0.0,totalWeight = (float) 0.0;
		double sim = 0.0;
		while(iter.hasNext()){
			long ratedItem = iter.nextLong();
			float rating = model.getPreferenceValue(userid, ratedItem);
			
			sim = sim1.itemSimilarity(itemid, ratedItem);
			if(sim < 0.0 || Double.isNaN(sim))
				sim = (float) 0.0;
			
			total = (float) (total + sim * rating);
			totalWeight = (float) (totalWeight + sim);
		}
		if(totalWeight > 0.0)
		total = total / totalWeight;
		else
			total = (float) 0.0;
		if(total > 5.0)
			total = (float) 5.0;
		
		return total;
		
	}
	List<MovieRecommended> movieList = new ArrayList<MovieRecommended>();
	
	@Override
	public List<RecommendedItem> recommend(long userid, int k)
			throws TasteException {
		// TODO Auto-generated method stub
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
