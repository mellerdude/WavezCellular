package com.example.wavezcellular.utils;

import java.util.Random;

public class CommentGenerator {
    private static final String[] COMMENT_TEMPLATES = {
            "Had a great time at {beach}!",
            "Beautiful beach!",
            "Lovely sunset at {beach}.",
            "{beach} is fantastic!",
            "Enjoyed the beach activities.",
            "Peaceful atmosphere at {beach}.",
            "Delicious seafood near {beach}.",
            "Hidden gem!",
    };

    private Random random;

    public CommentGenerator() {
        random = new Random();
    }

    public String generateComment(String beachName) {
        String commentTemplate = getRandomCommentTemplate();
        String comment = commentTemplate.replace("{beach}", beachName);
        return comment;
    }

    private String getRandomCommentTemplate() {
        int index = random.nextInt(COMMENT_TEMPLATES.length);
        return COMMENT_TEMPLATES[index];
    }
}