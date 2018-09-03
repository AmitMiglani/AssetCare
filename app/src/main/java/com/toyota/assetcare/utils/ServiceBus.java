package com.toyota.assetcare.utils;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class ServiceBus extends Bus {

    private final Handler mainThread;
    private static ServiceBus bus;

    public static ServiceBus getInstance() {
        if (bus == null) {
            bus = new ServiceBus();
        }
        return bus;
    }

    private ServiceBus() {
        mainThread = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }
}
