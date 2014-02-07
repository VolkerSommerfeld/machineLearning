package com.zanox.sandbox.mahout.recommendation;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Recommender1 {

    private DataModel model;

    public Recommender1() {
        File dataFile = new File("src/main/resources/intro.csv");
        try {
            model = new FileDataModel(dataFile);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        Recommender1 recommender = new Recommender1();
        recommender.recommend();
    }

    private void recommend() throws IOException, TasteException {
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
        Recommender recommender = new GenericUserBasedRecommender(
                model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(1, 2);
        System.out.println("recommendations:");
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }

}
