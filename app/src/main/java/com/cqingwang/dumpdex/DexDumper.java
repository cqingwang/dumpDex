package com.cqingwang.dumpdex;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class DexDumper {
    private static synchronized boolean loadArch() {
        for (String so : new String[]{"libnativeDump.so", "libnativeDump64.so"}) {
            try {
                CoreUtils.load("/data/local/tmp", so);
                return true;
            } catch (Throwable t) {
                t.printStackTrace();
                CoreUtils.logPrint("loadArch failed:" + t.getMessage());
            }
        }
        return false;
    }

    public static void native_parser(XC_LoadPackage.LoadPackageParam lparams) {
        loadArch();
        native_dump(lparams.packageName);
    }

    private static native void native_dump(String packageName);


    public static void logPrint(String txt) {
        CoreUtils.logPrint("lowSDK: " + txt);
    }

    public static void java_parser(final XC_LoadPackage.LoadPackageParam lparams) {
        logPrint("start hook [Instrumentation.newApplication]");

        XposedHelpers.findAndHookMethod("android.app.Instrumentation", lparams.classLoader, "newApplication", ClassLoader.class, String.class, Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                logPrint("Application=" + param.getResult());
                dump(lparams.packageName, param.getResult().getClass());
                attachBaseContextHook(lparams, ((Application) param.getResult()));
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
