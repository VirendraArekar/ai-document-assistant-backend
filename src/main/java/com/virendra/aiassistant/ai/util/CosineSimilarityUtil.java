package com.virendra.aiassistant.ai.util;

import java.util.List;

public class CosineSimilarityUtil {

    public static double cosineSimilarity(

            List<Float> vector1,

            List<Float> vector2

    ) {

        double dot = 0;

        double normA = 0;

        double normB = 0;

        for (int i = 0; i < vector1.size(); i++) {

            dot += vector1.get(i) * vector2.get(i);

            normA += Math.pow(vector1.get(i), 2);

            normB += Math.pow(vector2.get(i), 2);

        }

        return dot /

                (Math.sqrt(normA) * Math.sqrt(normB));

    }

}