// Designer Chat Module - Ben Eklund 2026
package org.designerchat.common;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/*
Interface designed to wrap an AI Chat Provider.
All methods return CompletableFuture for non-blocking UI usage.
 */
public interface IChatAPI {
    CompletableFuture<Boolean> isHealthy();
    CompletableFuture<ArrayList<String>> listModels();
    CompletableFuture<ChatHistoryRecord> chatCompletion(String model, ArrayList<ChatHistoryRecord> history);
}

