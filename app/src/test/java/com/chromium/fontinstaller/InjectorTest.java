package com.chromium.fontinstaller;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class InjectorTest {

  @Test public void testInitializeAndGet_notNull() {
    Injector.initialize(null);
    assertNotNull(Injector.get());
  }

}