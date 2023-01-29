package com.cqingwang.dumpdex;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        CoreUtils.logRelease("load:" + lpparam.packageName);
        ShellerParser.Sheller type = ShellerParser.parser(lpparam);
        if (type == null) return;

        final String packageName = lpparam.packageName;
        CoreUtils.logRelease("handle hook" + packageName);
        if (!lpparam.packageName.equals(packageName)) return;

        CoreUtils.logRelease("sdk version:" + Build.VERSION.SDK_INT);
        CoreUtils.makeDumpDir(packageName);
        if (CoreUtils.isNativeHook()) {
            DexDumper.native_parser(lpparam.packageName);
        } else {
            DexDumper.java_parser(lpparam);
        }

    }
}
