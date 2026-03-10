package org.designerchat.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatRoleTests {
    @Test
    void chatRoleFromString() {
        assertEquals(ChatRole.ASSISTANT, ChatRole.fromString("assistant"));
        assertEquals(ChatRole.USER, ChatRole.fromString("user"));
        assertThrows(IllegalArgumentException.class, () -> {
            ChatRole.fromString("model");
        });
    }

    @Test
    void chatRoleToString() {
        assertEquals("assistant", ChatRole.ASSISTANT.toString());
        assertEquals("user", ChatRole.USER.toString());
    }
}
