package chat.squirrel.test;

import static org.junit.Assert.*;

import org.junit.Test;

import chat.squirrel.Squirrel;
import chat.squirrel.auth.MongoAuthHandler;

public class TestFormatting {

    @Test
    public void testDiscriminator() {
        assertEquals("0069", Squirrel.formatDiscriminator(69));
        assertEquals("0420", Squirrel.formatDiscriminator(420));
        assertEquals("0121", Squirrel.formatDiscriminator(121));
        assertEquals("0000", Squirrel.formatDiscriminator(0));
        assertEquals("0001", Squirrel.formatDiscriminator(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDiscriminatorFail() {
        Squirrel.formatDiscriminator(99999);
    }

    @Test
    public void testUsernames() {
        assertTrue(MongoAuthHandler.isValidUsername("Charles Hatant"));
        assertFalse(MongoAuthHandler.isValidUsername("    owo spaces \t"));
        assertFalse(MongoAuthHandler.isValidUsername("\bo\n\t\rols"));
        assertFalse(MongoAuthHandler.isValidUsername("hayyaya#0003"));
        assertFalse(MongoAuthHandler.isValidUsername("line\nb\reak"));
    }

}
