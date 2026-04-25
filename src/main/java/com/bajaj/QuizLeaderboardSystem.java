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
    private static final long DELAY_MS = 5000L;

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {

        try {
            Set<String> seenKeys = new HashSet<>();
            Map<String, Integer> scoreMap = new LinkedHashMap<>();

            System.out.println("=== Quiz Leaderboard System Started ===\n");

            int totalFetchedEvents = 0;

            for (int poll = 0; poll < TOTAL_POLLS; poll++) {

                System.out.println("Fetching poll " + poll + "...");

                HttpResponse<String> response = fetchPoll(poll);

                if (response.statusCode() != 200) {
                    System.out.println("Failed to fetch poll " + poll + " | Status: " + response.statusCode());
                    continue;
                }

                System.out.println("Poll " + poll + " fetched successfully");

                JsonNode root = objectMapper.readTree(response.body());
                JsonNode events = root.get("events");

                if (events != null && events.isArray()) {

                    for (JsonNode event : events) {

                        totalFetchedEvents++;

                        String roundId = event.get("roundId").asText();
                        String participant = event.get("participant").asText();
                        int score = event.get("score").asInt();

                        String dedupKey = roundId + "::" + participant;

                        if (seenKeys.contains(dedupKey)) {
                            System.out.println("Duplicate skipped: " + dedupKey);
                        } else {
                            seenKeys.add(dedupKey);
                            scoreMap.merge(participant, score, Integer::sum);
                            System.out.println("Added: " + participant + " +" + score);
                        }
                    }
                }

                // Required delay (handled safely)
                if (poll < TOTAL_POLLS - 1) {
                    System.out.println("Waiting 5 seconds...\n");
                    try {
                        Thread.sleep(DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.out.println("Sleep interrupted");
                    }
                    
                }
            }

            // Summary
            System.out.println("\n=== DATA SUMMARY ===");
            System.out.println("Total events fetched: " + totalFetchedEvents);
            System.out.println("Unique events after dedup: " + seenKeys.size());
            System.out.println("Unique participants: " + scoreMap.size());

            // Sort leaderboard
            List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scoreMap.entrySet());
            leaderboard.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            // Display leaderboard
            System.out.println("\n=== FINAL LEADERBOARD ===");

            int totalScore = 0;
            int rank = 1;

            for (Map.Entry<String, Integer> entry : leaderboard) {
                System.out.println(rank + ". " + entry.getKey() + " - " + entry.getValue());
                totalScore += entry.getValue();
                rank++;
            }

            System.out.println("\nTotal Score Across All Users: " + totalScore);

            submitLeaderboard(leaderboard);

        } catch (Exception e) {
            // Clean error handling (no stack trace dump)
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    private static HttpResponse<String> fetchPoll(int poll) throws Exception {

        String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void submitLeaderboard(List<Map.Entry<String, Integer>> leaderboard) throws Exception {

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

        String payload = objectMapper.writeValueAsString(submitBody);

        System.out.println("\nSubmitting leaderboard...");
        System.out.println("Payload: " + payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("\n=== SUBMISSION RESULT ===");
        System.out.println("Status: " + response.statusCode());
        System.out.println("Response: " + response.body());

        JsonNode result = objectMapper.readTree(response.body());

        if (result.has("isCorrect")) {
            System.out.println("Correct: " + result.get("isCorrect").asBoolean());
        }

        if (result.has("message")) {
            System.out.println("Message: " + result.get("message").asText());
        }
    }
}