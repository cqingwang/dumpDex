/**
 * Copyright (c) Kuaibao (Shanghai) Network Technology Co., Ltd. All Rights Reserved
 * User: chan
 * Date: 2023/1/29
 * Created by chan on 2023/1/29
 */


package com.cqingwang.dumpdex;

import android.app.AndroidAppHelper;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Helpers {
    private static ArrayList<String> whites_apps = new ArrayList<>();

    static {
        whites_apps.addAll(Arrays.asList(
                "android",
                "de.robv.android.xposed.installer",
                "org.meowcat.edxposed.manager",
                "com.topjohnwu.magisk",
                "com.android.providers.settings",
                "com.android.server.telecom",
                "com.android.networkstack.inprocess",
                "com.qualcomm.location"
        ));
    }

    public static boolean whiteListApp(String pkg) {
        return whites_apps.contains(pkg);
    }

    public static synchronized Context getContext(final XC_LoadPackage.LoadPackageParam lparams) {
        Context context = (Context) AndroidAppHelper.currentApplication();
        return context;
    }
}
