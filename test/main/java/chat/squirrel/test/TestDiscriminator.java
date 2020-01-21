package chat.squirrel.test;

import static org.junit.Assert.*;

import org.junit.Test;

import chat.squirrel.Squirrel;

public class TestDiscriminator {

    @Test
    public void test() {
        assertEquals("0069", Squirrel.formatDiscriminator(69));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fail() {
        Squirrel.formatDiscriminator(99999);
    }

}
