// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inductiveautomation.ignition.common.util.LoggerEx;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonParser;
import org.designerchat.common.BuildConfig;
import org.designerchat.common.ChatHistoryRecord;
import org.designerchat.common.IChatAPI;

// IChatAPI implementation for Open WebUI (Ollama backend)
public class OllamaChatAPI implements IChatAPI {
    private final HttpClient client;
    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.ollamachatapi");

    public OllamaChatAPI() {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public CompletableFuture<Boolean> isHealthy() {
        HttpRequest request = authRequestBuilder("/health")
                .GET()
                .build();
        try {
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply((response) -> response.statusCode() == 200);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public CompletableFuture<ArrayList<String>> listModels() {
        HttpRequest request = authRequestBuilder("/api/v1/models")
                .GET()
                .build();
        try {
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(OllamaChatAPI::getModelsFromResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }

    @Override
    public CompletableFuture<ChatHistoryRecord> chatCompletion(String model, ArrayList<ChatHistoryRecord> history) {
        String requestBodyStr = getCompletionRequestString(model, history);
        logger.info("Request URL: " + BuildConfig.OPENWEBUI_HOST + "/api/chat/completions");
        logger.info("Request Body: " + requestBodyStr);

        HttpRequest request = authRequestBuilder("/api/chat/completions")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyStr))
                .build();

        try {
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(OllamaChatAPI::getChatHistoryRecordFromResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.completedFuture(new ChatHistoryRecord("assistant", "Failed to generate chat completion."));
        }
    }

    // response parsers

    private static ArrayList<String> getModelsFromResponse(HttpResponse<String> response) {
        String body = response.body();
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
        JsonArray data = jsonBody.getAsJsonArray("data");

        ArrayList<String> res = new ArrayList<String>();
        for (JsonElement element : data) {
            res.add(element.getAsJsonObject().get("name").getAsString());
        }
        return res;
    }

    private static String getCompletionRequestString(String model, ArrayList<ChatHistoryRecord> history) {
        JsonObject jsonRequestBody = new JsonObject();
        jsonRequestBody.addProperty("model", model);

        JsonArray messages = new JsonArray();
        for (ChatHistoryRecord record : history) {
            JsonObject message = new JsonObject();
            message.addProperty("role", record.role());
            message.addProperty("content", record.content());
            messages.add(message);
        }
        jsonRequestBody.add("messages", messages);

        return jsonRequestBody.toString();
    }

    private static ChatHistoryRecord getChatHistoryRecordFromResponse(HttpResponse<String> response) {
        String body = response.body();
        logger.info("Status: " + response.statusCode());
        logger.info("Body: " + body);

        if (response.statusCode() != 200) {
            logger.error("Chat completion failed with status: " + response.statusCode() + ": " + body);
            return new ChatHistoryRecord("assistant", "Failed to generate chat completion (HTTP " + response.statusCode() + ")");
        }

        JsonObject jsonResponseBody = JsonParser.parseString(body).getAsJsonObject();
        JsonArray choices = jsonResponseBody.getAsJsonArray("choices");

        if (choices == null || choices.isEmpty()) {
            logger.error("Response missing `choices` array: " + body);
            return new ChatHistoryRecord("assistant", "Failed to generate chat completion (no choices in response.)");
        }

        JsonObject messageJson = choices.get(0)
                .getAsJsonObject()
                .getAsJsonObject("message");
        ChatHistoryRecord historyRecord = ChatHistoryRecord.fromJson(messageJson);

        logger.info(historyRecord.role());
        logger.info(historyRecord.content());

        return historyRecord;
    }

    private HttpRequest.Builder authRequestBuilder(String path) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(BuildConfig.OPENWEBUI_HOST + path))
                    .header("Authorization", "Bearer " + BuildConfig.OPENWEBUI_KEY)
                    .timeout(Duration.ofMinutes(2));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
