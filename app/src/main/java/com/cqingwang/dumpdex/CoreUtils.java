package com.cqingwang.dumpdex;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by wrbug on 2017/8/23.
 */
public class CoreUtils {

    public static boolean isNativeHook() {
        return Build.VERSION.SDK_INT >= 23; //23=android 6.0
    }

    public static void load(String parent, String soName) {
        String path = parent + "/" + soName;
        logPrint("core load:" + path);
        System.load(path);
        logPrint("core loaded: " + path);
    }


    /**
     * 从assets目录中复制整个文件夹内容
     *
     * @param context Context 使用CopyFiles类的Activity
     * @param oldPath String  原文件路径  如：/aa
     * @param newPath String  复制后路径  如：xx:/bb/cc
     */
    public static void copyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                new File(newPath).mkdirs(); //如果文件夹不存在，则递归
                for (String fileName : fileNames) copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                return;
            }

            //如果是文件
            InputStream is = context.getAssets().open(oldPath);
            FileOutputStream fos = new FileOutputStream(newPath);
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
            fos.flush();
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    public static void logPrint(String txt) {
        String msg = "dumper-> " + txt;
        try {
            XposedBridge.log(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
