package org.designerchat.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatHistoryRecordTests {
    @Test
    void createChatHistoryRecord() {
        ChatRole role = ChatRole.ASSISTANT;
        String content = "Hello user!";

        ChatHistoryRecord record = new ChatHistoryRecord(role, content);

        assertEquals(role, record.role());
        assertEquals(content, record.content());
    }

    @Test
    void chatHistoryRecordToString() {
        ChatRole role = ChatRole.ASSISTANT;
        String content = "Hello user!";

        ChatHistoryRecord record = new ChatHistoryRecord(role, content);

        assertEquals("assistant: Hello user!\n\n", record.toString());
    }
}
