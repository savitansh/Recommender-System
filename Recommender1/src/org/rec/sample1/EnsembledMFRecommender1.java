package org.rec.sample1;

import java.util.Collection;
import java.util.List;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.RatingSGDFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.w3c.dom.views.AbstractView;

public class EnsembledMFRecommender1 extends AbstractRecommender implements Recommender{

	int modelNo = 0;
	SGDRecommender recommenders[] = new SGDRecommender[1000];
	RatingSGDFactorizer factorizer[] = new RatingSGDFactorizer[1000];
	protected EnsembledMFRecommender1(DataModel dataModel) throws TasteException {
		super(dataModel);
		// TODO Auto-generated constructor stub
		for(int numOfFactors = 1; numOfFactors <= 15; numOfFactors += 2){
			for(int nOfIters = 3; nOfIters <= 12; nOfIters++){
				factorizer[modelNo] = new RatingSGDFactorizer(dataModel, numOfFactors, nOfIters);
				recommenders[modelNo] = new SGDRecommender(dataModel, factorizer[modelNo]);
				//System.out.println(modelNo);
				modelNo++;
			}
		}
	}

	@Override
	public void refresh(Collection<Refreshable> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float estimatePreference(long userid, long itemid) throws TasteException {
		// TODO Auto-generated method stub
		float meanRating = 0.0f;
		for(int m = 0; m<modelNo; m++){
			float rating = recommenders[m].estimatePreference(userid, itemid);
			meanRating += rating;
		}
		meanRating = meanRating / modelNo;
		return meanRating;
	}

	@Override
	public List<RecommendedItem> recommend(long arg0, int arg1, IDRescorer arg2)
			throws TasteException {
		// TODO Auto-generated method stub
		return null;
	}

}
