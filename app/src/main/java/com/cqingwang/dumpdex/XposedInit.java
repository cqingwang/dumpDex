package com.cqingwang.dumpdex;

import android.os.Build;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (Helpers.whiteListApp(lpparam.packageName)) return;
        CoreUtils.logPrint("loaded app: " + lpparam.packageName);

        ShellerParser.Sheller type = ShellerParser.parser(lpparam);
        if (type == null) return;


        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
//            XposedBridgeHookAllMethods(XposedHelpers.findClass("com.sollyu.android.appenv.commons.Application", lpparam.classLoader), "isXposedWork", new MethodHookValue(true));
            return;
        }

        final String packageName = lpparam.packageName;
        CoreUtils.logPrint("handleLoadPackage:Enter -> " + packageName);
        if (!lpparam.packageName.equals(packageName)) return;

        CoreUtils.logPrint("sdk version:" + Build.VERSION.SDK_INT);
        CoreUtils.makeDumpDir(packageName);
        if (CoreUtils.isNativeHook()) {
            DexDumper.native_parser(lpparam);
        } else {
            DexDumper.java_parser(lpparam);
        }

    }
}
