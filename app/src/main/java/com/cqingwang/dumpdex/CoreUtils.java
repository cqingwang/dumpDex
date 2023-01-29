package com.cqingwang.dumpdex;

import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by wrbug on 2017/8/23.
 */
public class CoreUtils {

    public static boolean isNativeHook() {
        return Build.VERSION.SDK_INT >= 23; //23=android 6.0
    }

    public static void load(String parent, String soName) {
        String path = parent + soName;
        System.load(path);
        CoreUtils.logRelease("loaded: " + path);
    }

    public static void bytesToFile(byte[] data, String path) {
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
