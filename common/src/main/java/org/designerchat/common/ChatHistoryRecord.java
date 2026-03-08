// Designer Chat Module - Ben Eklund 2026
package org.designerchat.common;
import com.google.gson.JsonObject;

public record ChatHistoryRecord(
        String role,
        String content
) {
    public static ChatHistoryRecord fromJson(JsonObject json) {
        return new ChatHistoryRecord(
                json.get("role").getAsString(),
                json.get("content").getAsString()
        );
    }
}
