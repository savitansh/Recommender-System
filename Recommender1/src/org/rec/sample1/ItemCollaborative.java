package org.rec.sample1;
/*
 * Item based collaborative filtering
 * The system generates a model for simmilarity between each pair of items
 * The simmilarity model is used to find weighted sum of ratings of items rated by user and which
 * are similar to the given item.
 * based on predicted ratings of all items the system gives top k rated items as recommendations
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

class Predictions implements Comparable<Predictions>{
	double rating;
	long item;
	public void setRating(double rating){
		this.rating = rating;
	}
	public double getRating(){
		return rating;
	}
	
	public void setItem(long itemid){
		item = itemid;
	}
	public long getItem(){
		return item;
	}
	public int compareTo(Predictions comparePredictions) {		 
		int compareQuantity = (int)((Predictions) comparePredictions).getRating();
 
		//descending order
		return (int) (compareQuantity - (int)this.rating);
 
	}	
}

public class ItemCollaborative {
	public DataModel model;
	public ItemSimilarity sim1;
	
	/* Generate item-item simmilarity model */
	public void makeModel(File file) throws IOException, TasteException{
		model = new FileDataModel(file);	
		
		sim1 = new PearsonCorrelationSimilarity(model);
	}
	
	public DataModel getModel(){
		return model;
	}
	
	/* Predict rating for a given userid-itemid pair */
	public double predictRatings(long userId, long itemId) throws TasteException{
		FastIDSet items = null;
		if(model.getItemIDsFromUser(userId) != null)
		items = model.getItemIDsFromUser(userId);
		else
			return 0.0;
		
		LongPrimitiveIterator iter = items.iterator();
		double total=0.0,totalWeight = 0.0, sim = 0.0;
		while(iter.hasNext()){
			long ratedItem = iter.nextLong();
			float rating = model.getPreferenceValue(userId, ratedItem);

			sim = sim1.itemSimilarity(itemId, ratedItem);
			if(sim < 0.0 || Double.isNaN(sim))
				sim = 0.0;
			
			total = total + sim * rating;
			totalWeight = totalWeight + sim;
		}
		if(totalWeight > 0.0)
		total = total / totalWeight;
		else
			total = 0.0;
		if(total > 5.0)
			total = 5.0;
		return total;
	}
	
	List<Predictions> predictedItems = new ArrayList<Predictions>();
	
	/* Get top k rated items not rated by user as a set of recommended items */ 
	public List<Predictions> getTopKItems(long userId, int k) throws TasteException{
		List<Predictions> recommendedItems = new ArrayList<Predictions>();
		
		LongPrimitiveIterator iter =  model.getItemIDs();
		FastIDSet ratedItemSet = model.getItemIDsFromUser(userId);
		
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
			
			double rating = predictRatings(userId, item);
			Predictions p1 = new Predictions();
			p1.item = item;
			p1.rating = rating;
			predictedItems.add(p1);
		}
		Collections.sort(predictedItems);
		
		int count = 0;
		for(Predictions p:predictedItems){
			
			if(count >= k)
				break;
		recommendedItems.add(p);
		count++;
		}
		return recommendedItems;
	}
	
	/* Update the dataset by adding new userid-itemid , rating pair */
	public void addRatings(long userId, long itemId, double rating, File dataset) throws IOException{
		 FileWriter fw = new FileWriter(dataset, true);
		 fw.write(userId+","+itemId+","+rating);
		 fw.close();
	}
	
	/* refresh the model with new dataset */
	public void refreshData(File file) throws IOException{
		model = new FileDataModel(file);
	}
	
}
