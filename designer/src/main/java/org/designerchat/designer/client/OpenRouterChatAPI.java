// Designer Chat Module - Ben Eklund 2026
package org.designerchat.designer.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inductiveautomation.ignition.common.util.LoggerEx;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.designerchat.common.BuildConfig;
import org.designerchat.common.ChatHistoryRecord;
import org.designerchat.common.ChatRole;
import org.designerchat.common.IChatAPI;

public class OpenRouterChatAPI implements IChatAPI {
    private final HttpClient client;
    private static final LoggerEx logger = LoggerEx.newBuilder().build("designerchat.openrouterchatapi");

    public OpenRouterChatAPI() {
        this.client =
                HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    @Override
    public CompletableFuture<Boolean> isHealthy() {
        HttpRequest request = authRequestBuilder("/api/v1/models").GET().build();
        try {
            return this.client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 200);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    public CompletableFuture<List<String>> listModels() {
        HttpRequest request = authRequestBuilder("/api/v1/models").GET().build();
        try {
            return this.client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(OpenRouterChatAPI::getModelsFromResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }

    @Override
    public CompletableFuture<ChatHistoryRecord> chatCompletion(String model, List<ChatHistoryRecord> history) {
        String requestBodyStr = getCompletionRequestString(model, history);

        HttpRequest request = authRequestBuilder("/api/v1/chat/completions")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyStr))
                .build();

        try {
            return this.client
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(OpenRouterChatAPI::getChatHistoryRecordFromResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return CompletableFuture.completedFuture(
                    new ChatHistoryRecord(ChatRole.ASSISTANT, "Failed to generate chat completion."));
        }
    }

    private static List<String> getModelsFromResponse(HttpResponse<String> response) {
        String body = response.body();
        JsonObject jsonBody = JsonParser.parseString(body).getAsJsonObject();
        JsonArray data = jsonBody.getAsJsonArray("data");

        List<String> res = new ArrayList<>();
        for (JsonElement element : data) {
            String id = element.getAsJsonObject().get("id").getAsString();
            if (id.endsWith(":free")) {
                res.add(id);
            }
        }
        return res;
    }

    private static String getCompletionRequestString(String model, List<ChatHistoryRecord> history) {
        JsonObject jsonRequestBody = new JsonObject();
        jsonRequestBody.addProperty("model", model);

        JsonArray messages = new JsonArray();
        for (ChatHistoryRecord record : history) {
            JsonObject message = new JsonObject();
            message.addProperty("role", record.role().toString());
            message.addProperty("content", record.content());
            messages.add(message);
        }
        jsonRequestBody.add("messages", messages);

        return jsonRequestBody.toString();
    }

    private static ChatHistoryRecord getChatHistoryRecordFromResponse(HttpResponse<String> response) {
        String body = response.body();

        if (response.statusCode() != 200) {
            logger.error("Chat completion failed with status: " + response.statusCode() + ": " + body);
            return new ChatHistoryRecord(
                    ChatRole.ASSISTANT, "Failed to generate chat completion (HTTP " + response.statusCode() + ")");
        }

        JsonObject jsonResponseBody = JsonParser.parseString(body).getAsJsonObject();
        JsonArray choices = jsonResponseBody.getAsJsonArray("choices");

        if (choices == null || choices.isEmpty()) {
            logger.error("Response missing `choices` array: " + body);
            return new ChatHistoryRecord(
                    ChatRole.ASSISTANT, "Failed to generate chat completion (no choices in response.)");
        }

        JsonObject messageJson = choices.get(0).getAsJsonObject().getAsJsonObject("message");

        return ChatHistoryRecord.fromJson(messageJson);
    }

    private HttpRequest.Builder authRequestBuilder(String path) {
        try {
            return HttpRequest.newBuilder()
                    .uri(new URI(BuildConfig.OPENROUTER_HOST + path))
                    .header("Authorization", "Bearer " + BuildConfig.OPENROUTER_KEY)
                    .timeout(Duration.ofMinutes(2));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
