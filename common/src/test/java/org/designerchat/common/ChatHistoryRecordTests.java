package org.designerchat.common;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatHistoryRecordTests {
    @Test
    void createChatHistoryRecord() {
        ChatRole userRole = ChatRole.USER;
        String userContent = "Hello chatbot!";

        ChatHistoryRecord userRecord = new ChatHistoryRecord(userRole, userContent);

        assertEquals(userRole, userRecord.role());
        assertEquals(userContent, userRecord.content());

        ChatRole assistantRole = ChatRole.ASSISTANT;
        String assistantContent = "Hello user!";

        ChatHistoryRecord assistantRecord = new ChatHistoryRecord(assistantRole, assistantContent);

        assertEquals(assistantRole, assistantRecord.role());
        assertEquals(assistantContent, assistantRecord.content());
    }

    @Test
    void chatHistoryRecordToString() {
        ChatHistoryRecord assistantRecord = new ChatHistoryRecord(ChatRole.ASSISTANT, "Hello user!");
        ChatHistoryRecord userRecord = new ChatHistoryRecord(ChatRole.USER, "Hello. How are you?");

        assertEquals("assistant: Hello user!\n\n", assistantRecord.toString());
        assertEquals("user: Hello. How are you?\n\n", userRecord.toString());
    }

    @Test
    void chatHistoryRecordFromJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("role", "assistant");
        jsonObject.addProperty("content", "Hello user!");
        ChatHistoryRecord parsedRecord = ChatHistoryRecord.fromJson(jsonObject);

        assertEquals(new ChatHistoryRecord(ChatRole.ASSISTANT, "Hello user!"), parsedRecord);

        JsonObject invalidJsonObject = new JsonObject();
        invalidJsonObject.addProperty("role", "model");
        invalidJsonObject.addProperty("content", "Hello user!");

        assertThrows(IllegalArgumentException.class, () -> ChatHistoryRecord.fromJson(invalidJsonObject));
    }
}
