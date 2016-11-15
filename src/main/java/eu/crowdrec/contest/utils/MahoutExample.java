package eu.crowdrec.contest.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.search.Similarity;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;

/*
 * Implements Example: http://blog.trifork.com/2009/12/09/mahout-taste-part-one-introduction/
 */
public class MahoutExample {
	
	private DataModel dataModel;
	
	private long CUSTOMER_A = 0;
	
	private long PRODUCT_A = 0;
	
	private static boolean moreInput = true;
	
	private static UserBasedRecommender recommender;
	
	public static void main(String[] args) throws TasteException{
		Map<long[], List<GenericPreference>> userPreferenceCollection = new HashMap<long[], List<GenericPreference>>();
		
		//put current users preference
		//PreferenceArray prefArray = new GenericUserPreferenceArray(new LinkedList<GenericPreference>());
		List<GenericPreference> prefs = Arrays.asList(new GenericPreference(0,3,2));
		List<GenericPreference> prefs1 = Arrays.asList(new GenericPreference(1,1,1),
				new GenericPreference(1,2,2),
				new GenericPreference(1,3,1));
		List<GenericPreference> prefs2 = Arrays.asList(new GenericPreference(2,1,1),
				new GenericPreference(2,2,2),
				new GenericPreference(2,3,1),
				new GenericPreference(2,4,2));
		List<GenericPreference> prefs3 = Arrays.asList(new GenericPreference(3,3,3));
		
		userPreferenceCollection.put(new long[] {0}, prefs);
		userPreferenceCollection.put(new long[] {1}, prefs1);
		userPreferenceCollection.put(new long[] {2}, prefs2);
		userPreferenceCollection.put(new long[] {3}, prefs3);
		
		calculateModel(userPreferenceCollection);
		
		for(int i = 0; i<4; i++){
			System.out.println(Arrays.toString(calcNearestUser(i, 3)));
			System.out.println(estimatePreference(i, 4));
			System.out.println(recommend(i,5).size());
		}

	}
	
	public static void calculateModel(Map<long[], List<GenericPreference>> userPreferenceCollection ) throws TasteException{
		FastByIDMap<PreferenceArray> fastMap = new FastByIDMap<PreferenceArray>();
		
		for(Entry<long[], List<GenericPreference>> entry : userPreferenceCollection.entrySet()){
			fastMap.put(entry.getKey()[0], new GenericUserPreferenceArray(entry.getValue()));
		}
		
		DataModel dataModel = new GenericDataModel(fastMap);
		
		TanimotoCoefficientSimilarity tanimoto = new TanimotoCoefficientSimilarity(dataModel);
		
		//System.out.print(dataModel.getNumItems());
		LogLikelihoodSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
		recommender = new GenericUserBasedRecommender(dataModel, new ThresholdUserNeighborhood(0.1, similarity, dataModel), tanimoto);
		
	}
	
	public static long[] calcNearestUser(long userID, int amount) throws TasteException{
		
		return recommender.mostSimilarUserIDs(userID, amount);
		
	}
	
	public static float estimatePreference(long userID, int documentID) throws TasteException{
		
		return recommender.estimatePreference(userID, documentID);
		
	}
	
	public static List<RecommendedItem> recommend(long userID, int amount) throws TasteException{
		
		return recommender.recommend(userID, amount);
		
	}
}
