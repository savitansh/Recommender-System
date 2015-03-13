package org.rec.sample1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.similarity.AbstractItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class AdjustedCosineSimmilarity extends AbstractItemSimilarity implements ItemSimilarity{

	Map<Long,Float> userAvgRating = new HashMap<Long, Float>();
	Map<Long,List<Long>> itemRatedUsers = new HashMap<Long, List<Long>>();
	
	DataModel model;
	protected AdjustedCosineSimmilarity(DataModel dataModel) throws TasteException {
		super(dataModel);
		// TODO Auto-generated constructor stub
		model = dataModel;
		LongPrimitiveIterator iter = dataModel.getUserIDs();
		while(iter.hasNext()){
			long userid = iter.nextLong();
			FastIDSet itemSet = dataModel.getItemIDsFromUser(userid);
			float avgrating = 0.0f;
			int numItemsRated = 0;
			for(long item:itemSet){
				avgrating += dataModel.getPreferenceValue(userid, item);
				numItemsRated++;
			}
			avgrating = avgrating / numItemsRated;
			userAvgRating.put(userid, avgrating);
		
		}
		iter = model.getUserIDs();
		while(iter.hasNext()){
			long userid = iter.nextLong();
			FastIDSet itemset = model.getItemIDsFromUser(userid);
			for(long item:itemset){
				if(itemRatedUsers.containsKey(item)){
					List<Long> users = itemRatedUsers.get(item);
					users.add(userid);
					itemRatedUsers.put(item, users);
				}else{
					List<Long> users = new ArrayList<Long>();
					users.add(userid);
					itemRatedUsers.put(item, users);
				}
			}
		}
	}

	@Override
	public double[] itemSimilarities(long item1, long[] items)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double itemSimilarity(long item1, long item2) throws TasteException {
		// TODO Auto-generated method stub
		List<Long> users1 = itemRatedUsers.get(item1);
		List<Long> users2 = itemRatedUsers.get(item2);
		Map<Long,Integer> commonUsers = new HashMap<Long, Integer>();
		for(long user1 : users1){
			for(long user2 : users2){
				if(user1 == user2){
					commonUsers.put(user1,1);
				}
			}
		}
		Iterator<Long> it = commonUsers.keySet().iterator();
		float dotProduct = 0.0f;
		while(it.hasNext()){
			long user = it.next();
			float rating1 = model.getPreferenceValue(user, item1);
			float rating2 = model.getPreferenceValue(user, item2);
			float avg1 = userAvgRating.get(user);
			dotProduct += (rating1 - avg1)*(rating2 - avg1);
		}
		it = commonUsers.keySet().iterator();
		float sum1 = 0.0f;
		while(it.hasNext()){
			long user = it.next();
			float rating1 = model.getPreferenceValue(user, item1);
			float avg = userAvgRating.get(user);
			sum1 += (rating1 - avg)*(rating1 - avg);
		}
		sum1 = (float) Math.sqrt(sum1);
		
		it = commonUsers.keySet().iterator();
		float sum2 = 0.0f;
		while(it.hasNext()){
			long user = it.next();
			float rating1 = model.getPreferenceValue(user, item2);
			float avg = userAvgRating.get(user);
			sum2 += (rating1 - avg)*(rating1 - avg);
		}
		sum2 = (float) Math.sqrt(sum2);
		
		float mag = sum1 * sum2;
		if(mag > 0.0f){
			mag = dotProduct / mag;
			return mag;	
		}
		return 0.0f;
	}

}
