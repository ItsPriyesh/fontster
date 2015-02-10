package com.chromium.fontinstaller;

import com.squareup.otto.Bus;

/**
 * Created by priyeshpatel on 15-02-10.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }
}