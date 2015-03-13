package org.rec.sample1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorization;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

public class ClusteringRecommender extends AbstractRecommender implements Recommender{

	double itemFeatures[][];
	double userFeatures[][];
	Factorization fact;
	DataModel model;
	ItemSimilarity sim1;
	
	public void getFeatures() throws TasteException, IOException{
		RatingSGDFactorizer factorizer = new RatingSGDFactorizer(model, 15, 10);
		fact = factorizer.factorize();
		 itemFeatures = fact.allItemFeatures();
		 userFeatures = fact.allUserFeatures();
	}
	
	int labels[];
	int nOfLabels;
	public void setLabels(File f) throws FileNotFoundException{
		labels = new int[10000];
		Scanner sc = new Scanner(f);
		int i =0;
		while(sc.hasNext()){
			int label = Integer.parseInt(sc.next());
			labels[i++] = label;
		}
		nOfLabels = i;
		sc.close();
	}
	Map<Long,List<Long>> similarUsersMap = new HashMap<Long, List<Long>>();
	
	public List<Long> getSimilarUsers(long userid) throws TasteException{
		List<Long> similarUsers = new ArrayList<Long>();
		int indx = fact.userIndex(userid);
		int userlabel = labels[indx];
		LongPrimitiveIterator iter = model.getUserIDs();
		while(iter.hasNext()){
			long user = iter.nextLong();
			int useridx = fact.userIndex(user);
			if(labels[useridx] == userlabel){
				//System.out.println(user+","+userlabel);
				similarUsers.add(user);
			}
		}
		similarUsersMap.put(userid, similarUsers);
		return similarUsers;
	}
	
	protected ClusteringRecommender(DataModel dataModel) throws TasteException, IOException {
		super(dataModel);
		// TODO Auto-generated constructor stub
		model = dataModel;
		getFeatures();
		setLabels(new File("Data/labels.txt"));
		sim1 = new AdjustedCosineSimmilarity(model);
	}

	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float estimatePreference(long userid, long itemid) throws TasteException {
		// TODO Auto-generated method stub
		List<Long> similarUsers;
		if(similarUsersMap.get(userid) != null){
			similarUsers = similarUsersMap.get(userid);
		}else
			similarUsers = getSimilarUsers(userid);
		
		PreferenceArray arr = model.getPreferencesFromUser(userid);
		Iterator<Preference> iter = arr.iterator();
		float s = 0.0f , itemCount = 0;
		while(iter.hasNext()){
			float val = iter.next().getValue();
			s += val;
			itemCount++;
		}
		float avgRating = s / itemCount;
		//System.out.println(avgRating);
		float finalRating = 0.0f;
		int noOfNeighbours = 0;
		for(long neighbour : similarUsers){
			float pref = 0.0f;
			if(model.getItemIDsFromUser(userid).contains(itemid))
				pref = model.getPreferenceValue(userid, itemid);
			if(pref > 0.0f){
				finalRating = finalRating + (pref - avgRating);
				noOfNeighbours++;
			}
		}
		if(noOfNeighbours > 0)
		finalRating = avgRating + finalRating / noOfNeighbours;
		else
			finalRating = avgRating;	
		return finalRating;
	}

	@Override
	public List<RecommendedItem> recommend(long arg0, int arg1, IDRescorer arg2)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	List<MovieRecommended> movieList = new ArrayList<MovieRecommended>();
	
	public List<RecommendedItem> recommend(long userid, int nOfItems)
			throws TasteException {
		// TODO Auto-generated method stub
		List<Long> similarUsers = getSimilarUsers(userid);
		Iterator<Long> iter = similarUsers.iterator();
		while(iter.hasNext()){
			long similarUser = iter.next();
			FastIDSet itemsRated = model.getItemIDsFromUser(similarUser);
			for(long item:itemsRated){
				float rating = model.getPreferenceValue(similarUser, item);
				MovieRecommended movie = new MovieRecommended();
				movie.itemid = item;
				movie.value = rating;
				movieList.add(movie);
			}
		}
		Collections.sort(movieList);
		List<RecommendedItem> recommended = new ArrayList<RecommendedItem>();
		int i = 0;
		for(MovieRecommended m:movieList){
			recommended.add(m);
			if(i>nOfItems)
				break;
			i++;
		}
		return recommended;
	}

}
