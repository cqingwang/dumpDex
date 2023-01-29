package com.cqingwang.dumpdex;

import android.app.Application;
import android.content.Context;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DexDumper {

    private static boolean loaded = false;

    private static void native_init() {
        if (!loaded) {
            loaded = true;
            String path = "/data/local/tmp/";
            try {
                CoreUtils.load(path, "libnativeDump.so");
            } catch (Throwable t) {
                CoreUtils.load(path, "libnativeDump64.so");
            }
//        System.loadLibrary("nativeDump");
        }
    }

    public static void native_parser(String packageName) {
        native_init();
        native_dump(packageName);
    }

    private static native void native_dump(String packageName);


    public static void logPrint(String txt) {
        CoreUtils.logRelease("lowSDK: " + txt);
    }

    public static void java_parser(final XC_LoadPackage.LoadPackageParam lpparam) {
        logPrint("start hook [Instrumentation.newApplication]");

        XposedHelpers.findAndHookMethod("android.app.Instrumentation", lpparam.classLoader, "newApplication", ClassLoader.class, String.class, Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                logPrint("Application=" + param.getResult());
                dump(lpparam.packageName, param.getResult().getClass());
                attachBaseContextHook(lpparam, ((Application) param.getResult()));
            }
        });
    }

    private static void dump(String packageName, Class<?> aClass) {
        Object dexCache = XposedHelpers.getObjectField(aClass, "dexCache");
        logPrint("decCache=" + dexCache);
        Object o = XposedHelpers.callMethod(dexCache, "getDex");
        byte[] bytes = (byte[]) XposedHelpers.callMethod(o, "getBytes");
        String path = "/data/data/" + packageName + "/dump";
        File file = new File(path, "source-" + bytes.length + ".dex");
        if (file.exists()) {
            logPrint(file.getName() + " exists");
            return;
        }
        CoreUtils.bytesToFile(bytes, file.getAbsolutePath());
    }


    private static void attachBaseContextHook(final XC_LoadPackage.LoadPackageParam lpparam, final Application application) {
        ClassLoader classLoader = application.getClassLoader();
        XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                logPrint("loadClass->" + param.args[0]);
                Class result = (Class) param.getResult();
                if (result != null) {
                    dump(lpparam.packageName, result);
                }
            }
        });
        XposedHelpers.findAndHookMethod("java.lang.ClassLoader", classLoader, "loadClass", String.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                logPrint("loadClassWithclassLoader->" + param.args[0]);
                Class result = (Class) param.getResult();
                if (result != null) {
                    dump(lpparam.packageName, result);
                }
            }
        });
    }
}
