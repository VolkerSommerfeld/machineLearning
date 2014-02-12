package com.zanox.sandbox.mahout.workshop;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class WorkshopPresentationRecommender {

    private DataModel model;

    private RecommenderType recommenderType;

    public WorkshopPresentationRecommender(RecommenderType recommenderType) {
        this.recommenderType = recommenderType;
        File dataFile = new File("src/main/resources/workshop.csv");
        try {
            model = new FileDataModel(dataFile);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        System.out.println("\n\n******** recommender type: " + this.recommenderType);
    }

    public static void main(String[] args) throws Exception {
        WorkshopPresentationRecommender.execute(RecommenderType.USER_BASED);
        WorkshopPresentationRecommender.execute(RecommenderType.ITEM_BASED);
        WorkshopPresentationRecommender.execute(RecommenderType.SLOPE_1);
    }

    public static void execute(RecommenderType recommenderType) throws IOException, TasteException {
        WorkshopPresentationRecommender recommender = new WorkshopPresentationRecommender(recommenderType);
        recommender.recommend();
        recommender.evaluateRecommendation();
    }

    private void recommend() throws IOException, TasteException {
        List<RecommendedItem> recommendations = createRecommender().recommend(0, 1);
        System.out.println("recommendations:");
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }

    private void evaluateRecommendation() throws IOException, TasteException {
        RandomUtils.useTestSeed();
        RecommenderBuilder builder = new RecommenderBuilder() {
            @Override
            public Recommender buildRecommender(DataModel evaluationDataModel) throws TasteException {
                model = evaluationDataModel;
                return createRecommender();
            }
        };

        // Evaluate precision and recall
        RecommenderIRStatsEvaluator iRStatsEvaluator = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats = iRStatsEvaluator.evaluate(builder,
                null, model, null, 2,
                GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
                1.0);
        System.out.println("precision=" + stats.getPrecision());
        System.out.println("recall=" + stats.getRecall());
        System.out.println("F1=" + stats.getF1Measure());
    }

    private Recommender createUserBasedRecommender() throws TasteException {
        UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
        //UserSimilarity userSimilarity = new EuclideanDistanceSimilarity(model);
        UserNeighborhood neighborhood = new NearestNUserNeighborhood (2, userSimilarity, model);
        return new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
    }

    private Recommender createItemBasedRecommender() throws TasteException {
        ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model);
        return new GenericItemBasedRecommender(model, itemSimilarity);
    }

    private Recommender createSlopeOneRecommender() throws TasteException {
        return new SlopeOneRecommender(model);
    }

    private Recommender createRecommender() {
        try {
            switch(recommenderType) {
                case USER_BASED:
                    return createUserBasedRecommender();
                case ITEM_BASED:
                    return createItemBasedRecommender();
                case SLOPE_1:
                    return createSlopeOneRecommender();
                default:
                    throw new IllegalStateException("incorrect recommender type");
            }
        } catch (TasteException e) {
            throw new IllegalStateException(e);
        }
    }

    private enum RecommenderType {
        USER_BASED, ITEM_BASED, SLOPE_1;
    }
}
