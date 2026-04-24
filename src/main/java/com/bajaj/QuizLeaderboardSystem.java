package com.bajaj;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class QuizLeaderboardSystem {

    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final String REG_NO = "AP23110010570";
    private static final int TOTAL_POLLS = 10;
    private static final long DELAY_MS = 5000L; // ← fixed: long not int

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {

        Set<String> seenKeys = new HashSet<>();
        Map<String, Integer> scoreMap = new LinkedHashMap<>();

        System.out.println("=== Bajaj Finserv Quiz Leaderboard System ===\n");

        for (int poll = 0; poll < TOTAL_POLLS; poll++) {
            System.out.println("Fetching poll " + poll + "...");

            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Poll " + poll + " Response: " + response.body());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode events = root.get("events");

                if (events != null && events.isArray()) {
                    for (JsonNode event : events) {
                        String roundId = event.get("roundId").asText();
                        String participant = event.get("participant").asText();
                        int score = event.get("score").asInt();

                        String dedupKey = roundId + "::" + participant;

                        if (seenKeys.contains(dedupKey)) {
                            System.out.println("  [DUPLICATE SKIPPED] " + dedupKey);
                        } else {
                            seenKeys.add(dedupKey);
                            scoreMap.merge(participant, score, Integer::sum);
                            System.out.println("  [ADDED] " + participant + " +" + score + " (Round: " + roundId + ")");
                        }
                    }
                }
            } else {
                System.out.println("  [ERROR] Status code: " + response.statusCode());
            }

            if (poll < TOTAL_POLLS - 1) {
                System.out.println("  Waiting 5 seconds...\n");
                Thread.sleep(DELAY_MS);
            }
        }

        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scoreMap.entrySet());
        leaderboard.sort((a, b) -> b.getValue() - a.getValue());

        System.out.println("\n=== FINAL LEADERBOARD ===");
        int totalScore = 0;
        for (Map.Entry<String, Integer> entry : leaderboard) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
            totalScore += entry.getValue();
        }
        System.out.println("Total Score: " + totalScore);

        // Build submit payload
        ObjectNode submitBody = objectMapper.createObjectNode();
        submitBody.put("regNo", REG_NO);

        ArrayNode leaderboardArray = objectMapper.createArrayNode();
        for (Map.Entry<String, Integer> entry : leaderboard) {
            ObjectNode item = objectMapper.createObjectNode();
            item.put("participant", entry.getKey());
            item.put("totalScore", entry.getValue());
            leaderboardArray.add(item);
        }
        submitBody.set("leaderboard", leaderboardArray);

        String submitPayload = objectMapper.writeValueAsString(submitBody);
        System.out.println("\n=== SUBMITTING LEADERBOARD ===");
        System.out.println("Payload: " + submitPayload);

        HttpRequest submitRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submitPayload))
                .build();

        HttpResponse<String> submitResponse = httpClient.send(submitRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n=== SUBMISSION RESULT ===");
        System.out.println("Status: " + submitResponse.statusCode());
        System.out.println("Response: " + submitResponse.body());
        
        JsonNode result = objectMapper.readTree(submitResponse.body());

if (result.has("isCorrect")) {
    boolean isCorrect = result.get("isCorrect").asBoolean();
    System.out.println("\nCorrect: " + isCorrect);
} else if (result.has("submittedTotal")) {
    System.out.println("\nSubmitted Total: " + result.get("submittedTotal").asLong());
}

if (result.has("message")) {
    System.out.println("Message: " + result.get("message").asText());
}

if (result.has("expectedTotal")) {
    long submitted = result.get("submittedTotal").asLong();
    long expected = result.get("expectedTotal").asLong();
    System.out.println("Correct: " + (submitted == expected));
}  
    }
}