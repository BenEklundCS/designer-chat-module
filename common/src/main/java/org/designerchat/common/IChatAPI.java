// Designer Chat Module - Ben Eklund 2026
package org.designerchat.common;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public interface IChatAPI {
    CompletableFuture<Boolean> isHealthy();
    CompletableFuture<ArrayList<String>> listModels();
    CompletableFuture<ChatHistoryRecord> chatCompletion(String model, ArrayList<ChatHistoryRecord> history);
}

