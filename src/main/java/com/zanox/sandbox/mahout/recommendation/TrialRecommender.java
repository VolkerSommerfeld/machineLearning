package com.zanox.sandbox.mahout.recommendation;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrialRecommender {

    private DataModel model;

    public TrialRecommender() {
        File dataFile = new File("src/main/resources/ua.base");
//        File dataFile = new File("src/main/resources/intro.csv");
        try {
            model = new FileDataModel(dataFile);
            System.out.println("instatiated TrialRecommender");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        TrialRecommender recommender = new TrialRecommender();
        //recommender.recommend();
        recommender.evaluateRecommendation();
    }

    private void recommend() throws IOException, TasteException {
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood(2, similarity, model);
        Recommender recommender = new GenericUserBasedRecommender(
                model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(1, 1);
        System.out.println("recommendations:");
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }

    private void evaluateRecommendation() throws IOException, TasteException {
        RandomUtils.useTestSeed();
        RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
        RecommenderBuilder builder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel model) throws TasteException {
                UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
                UserNeighborhood neighborhood = new NearestNUserNeighborhood (2, similarity, model);
                return new SlopeOneRecommender(model);
                //return new GenericUserBasedRecommender(model, neighborhood, similarity);
            }
        };

        double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
        System.out.println("score: " + score);

        // Evaluate precision and recall "ate 2":
//        RecommenderIRStatsEvaluator iRStatsEvaluator =
//                new GenericRecommenderIRStatsEvaluator();
//        IRStatistics stats = iRStatsEvaluator.evaluate(builder,
//                null, model, null, 2,
//                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
//                1.0);
//        System.out.println(stats.getPrecision());
//        System.out.println(stats.getRecall());

    }
}
