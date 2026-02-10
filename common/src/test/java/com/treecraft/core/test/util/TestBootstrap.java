package com.treecraft.core.test.util;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

public class TestBootstrap {
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            initialized = true;
        }
    }
}
