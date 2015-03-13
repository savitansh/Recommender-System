package org.rec.sample1;

import org.apache.mahout.cf.taste.recommender.RecommendedItem;

public class MovieRecommended implements RecommendedItem,Comparable<MovieRecommended>{
	
	long itemid;
	float value;
	@Override
	public long getItemID() {
		// TODO Auto-generated method stub
		return itemid;
	}

	@Override
	public float getValue() {
		// TODO Auto-generated method stub
		return value;
	}
	
	public void setItemID(long itemid){
		this.itemid = itemid;
	}
	
	public void setValue(float value){
		this.value = value;
	}
	
	    public int compareTo(MovieRecommended comparemv) {
	        int comparerate=(int)((MovieRecommended)comparemv).getValue();
	       
	        /* For Descending order do like this */
	        return  (int) ((int)comparerate-(int)this.getValue());
	    }
}
