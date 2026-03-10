// Designer Chat Module - Ben Eklund 2026
package org.designerchat.common;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public record ChatHistoryRecord(
        ChatRole role,
        String content
) {
    public static ChatHistoryRecord fromJson(JsonObject json) {
        return new ChatHistoryRecord(
                ChatRole.fromString(json.get("role").getAsString()),
                json.get("content").getAsString()
        );
    }

    @NotNull
    @Override
    public String toString() {
        return this.role() +
                ": " +
                this.content() +
                "\n\n";
    }
}
