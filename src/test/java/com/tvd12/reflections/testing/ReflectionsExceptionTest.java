package com.tvd12.reflections.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.tvd12.reflections.ReflectionsException;

public class ReflectionsExceptionTest {

    @Test
    public void constructorWithMessage() {
        ReflectionsException e = new ReflectionsException("msg");
        assertEquals("msg", e.getMessage());
    }

    @Test
    public void constructorWithMessageAndCause() {
        Throwable cause = new RuntimeException("cause");
        ReflectionsException e = new ReflectionsException("msg", cause);
        assertEquals("msg", e.getMessage());
        assertSame(cause, e.getCause());
    }

    @Test
    public void constructorWithCauseOnly() {
        Throwable cause = new RuntimeException("root");
        ReflectionsException e = new ReflectionsException(cause);
        assertSame(cause, e.getCause());
    }
}
