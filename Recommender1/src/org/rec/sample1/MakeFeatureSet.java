package org.rec.sample1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorization;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;
import org.apache.mahout.cf.taste.model.DataModel;

public class MakeFeatureSet extends ItemRecommender{
	double itemFeatures[][];
	double userFeatures[][];
	Factorization fact;
	public void getFeatures() throws TasteException, IOException{
		RatingSGDFactorizer factorizer = new RatingSGDFactorizer(model, 15, 10);
		fact = factorizer.factorize();
		 itemFeatures = fact.allItemFeatures();
		 userFeatures = fact.allUserFeatures();
		 FileWriter fw = new FileWriter(new File("Data/userLatentFeatures.csv"));
		 int nOfUsers = userFeatures.length;
		 for(int i=0; i<nOfUsers; i++){
			 String featureSet = "";
			 for(int j=3; j<18; j++){
				 featureSet = featureSet + (Math.round(userFeatures[i][j]*1000)) / 1000.0 + ",";
			 }
			 fw.write(featureSet+"\n");
		 }
		 fw.close();
		 fw = new FileWriter(new File("Data/itemLatentFeatures.csv"));
		 int nOfItems = itemFeatures.length;
		 for(int i=0; i<nOfItems; i++){
			 String featureSet = "";
			 for(int j=3; j<18; j++){
				 featureSet = featureSet + (Math.round(itemFeatures[i][j]*1000)) / 1000.0 + ",";
			 }
			 fw.write(featureSet+"\n");
		 }
		 fw.close();
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
		System.out.println(indx);
		int userlabel = labels[indx];
		LongPrimitiveIterator iter = model.getUserIDs();
		while(iter.hasNext()){
			long user = iter.nextLong();
			int useridx = fact.userIndex(user);
			if(labels[useridx] == userlabel){
				System.out.println(user+","+userlabel);
				similarUsers.add(user);
			}
		}
		similarUsersMap.put(userid, similarUsers);
		return similarUsers;
	}
	
	/*public static void main(String args[]) throws IOException, TasteException{
		MakeFeatureSet f1 = new MakeFeatureSet();
		f1.makeModel(new File("Data/movies.csv"));
		f1.getFeatures();
		f1.setLabels(new File("Data/labels.txt"));
		f1.getSimilarUsers(1);
		
	}/*/
}
