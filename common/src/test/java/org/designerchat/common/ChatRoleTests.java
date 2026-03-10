package org.designerchat.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChatRoleTests {
    @Test
    void chatRoleFromString() {
        assertEquals(ChatRole.ASSISTANT, ChatRole.fromString("assistant"));
        assertEquals(ChatRole.USER, ChatRole.fromString("user"));
        assertThrows(IllegalArgumentException.class, () -> ChatRole.fromString("model"));
    }

    @Test
    void chatRoleFromStringCaseInsensitive() {
        assertEquals(ChatRole.ASSISTANT, ChatRole.fromString("aSSisTanT"));
        assertEquals(ChatRole.ASSISTANT, ChatRole.fromString("ASSISTANT"));

        assertEquals(ChatRole.USER, ChatRole.fromString("UsER"));
        assertEquals(ChatRole.USER, ChatRole.fromString("USER"));
    }

    @Test
    void chatRoleToString() {
        assertEquals("assistant", ChatRole.ASSISTANT.toString());
        assertEquals("user", ChatRole.USER.toString());
    }
}
