package org.rec.sample1;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;

public class Recommender {

	/* Driver function */
	public static void main(String args[]) throws IOException, TasteException{
		ItemCollaborative i1 = new ItemCollaborative();
		File f = new File("Data/movies.csv");
		i1.makeModel(f);
		
		//Evaluator evaluator = new Evaluator();
		//evaluator.generateModel(f);
		//double error = evaluator.RMSError();
		//double error = evaluator.MeanAbsoluteError();
		//System.out.println(error);
//		int choice = 0;
//		Scanner sc = new Scanner(System.in);
//		while(true){
//			System.out.println("Enter options : 1. View recommendations"
//					+ "\n 2. Add new user"
//					+ "\n 3. Rate a movie"
//					+ "\n 4. Exit"
//					);
//			choice = sc.nextInt();
//			if(choice == 1){
//				System.out.println("Enter userid");
//				long userid = sc.nextLong();
//				System.out.println("Enter Number of items");
//				int k = sc.nextInt();
//				List<Predictions> recommendations = i1.getTopKItems(userid, k);
//				for(Predictions item:recommendations){
//					System.out.println("itemid="+item.getItem() + ",rating="+item.getRating());
//				}			
//			}	else if(choice == 4){
//				break;
//			}
//		}
		
	}

}
