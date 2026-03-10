// Designer Chat Module - Ben Eklund 2026
package org.designerchat.common;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IChatAPI {
    CompletableFuture<Boolean> isHealthy();
    CompletableFuture<List<String>> listModels();
    CompletableFuture<ChatHistoryRecord> chatCompletion(String model, List<ChatHistoryRecord> history);
}

