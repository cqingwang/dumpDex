package com.wrbug.dumpdex;

import de.robv.android.xposed.XposedBridge;

/**
 * Native
 *
 * @author WrBug
 * @since 2018/3/23
 */
public class Native {
    public static void log(String txt) {
        XposedBridge.log("dumpdex.Native-> " + txt);
    }
    static {
        try {
            log("loaded libnativeDump.so");
            System.load("/data/local/tmp/libnativeDump.so");
        } catch (Throwable t) {
            log("failed:"+t.getMessage());
            log("loaded libnativeDump64.so");
            System.load("/data/local/tmp/libnativeDump64.so");
        }
//        System.loadLibrary("nativeDump");
    }

    public static native void dump(String packageName);
}
