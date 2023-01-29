package com.cqingwang.dumpdex;

import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by wrbug on 2017/8/23.
 */
public class CoreUtils {

    private static int sdkInit = Build.VERSION.SDK_INT;

    public static boolean isNativeHook() {
        return isAndroid6() || isAndroid7() || isAndroid8() || isAndroid9() || isAndroid10();
    }

    public static boolean isAndroid6() {
        return sdkInit == 23;
    }

    public static boolean isAndroid7() {
        return CoreUtils.includes(sdkInit, new int[]{24, 25});
    }

    public static boolean isAndroid8() {
        return CoreUtils.includes(sdkInit, new int[]{26, 27});
    }

    public static boolean isAndroid9() {
        return sdkInit == 28;
    }

    public static boolean isAndroid10() {
        return sdkInit == 29;
    }

    public static void load(String parent, String soName) {
        String path = parent + soName;
        System.load(path);
        CoreUtils.logRelease("loaded: " + path);
    }

    public static void writeByteToFile(byte[] data, String path) {
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(path);
            localFileOutputStream.write(data);
            localFileOutputStream.close();
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    public static void makeDumpDir(String packageName) {
        File parent = new File("/data/data/" + packageName + "/dump");
        if (!parent.exists() || !parent.isDirectory()) parent.mkdirs();
    }

    public static boolean includes(int target, int[] values) {
        for (int val : values) {
            if (val == target) return true;
        }
        return false;
    }

    public static void logRelease(String txt) {
        XposedBridge.log("dumper-> " + txt);
    }

}
