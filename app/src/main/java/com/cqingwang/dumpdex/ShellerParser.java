package com.cqingwang.dumpdex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * PackerInfo
 *
 * @author WrBug
 * @since 2018/3/29
 * <p>
 * 加壳类型
 */
public class ShellerParser {

    private static List<String> classesMap = new ArrayList<>();
    private static Map<String, Sheller> shellerMap = new HashMap<>();


    /**
     * 加固应用包含的包名，如果无法脱壳，请将application的包名，加到相应数组
     */
    //360加固
    private static final String[] QI_HOO = {"com.stub.StubApp"};
    //爱加密
    private static final String[] AI_JIA_MI = {"s.h.e.l.l.S"};
    //梆梆加固
    private static final String[] BANG_BANG = {"com.secneo.apkwrapper.ApplicationWrapper"};
    //腾讯加固
    private static final String[] TENCENT = {"com.tencent.StubShell.TxAppEntry"};
    //百度加固
    private static final String[] BAI_DU = {"com.baidu.protect.StubApplication"};

    static {
        classesMap.addAll(Arrays.asList(QI_HOO));
        classesMap.addAll(Arrays.asList(AI_JIA_MI));
        classesMap.addAll(Arrays.asList(BANG_BANG));
        classesMap.addAll(Arrays.asList(TENCENT));
        classesMap.addAll(Arrays.asList(BAI_DU));

        for (String s : QI_HOO)
            shellerMap.put(s, Sheller.QI_HOO);

        for (String s : AI_JIA_MI)
            shellerMap.put(s, Sheller.AI_JIA_MI);

        for (String s : BANG_BANG)
            shellerMap.put(s, Sheller.BANG_BANG);

        for (String s : TENCENT)
            shellerMap.put(s, Sheller.TENCENT);

        for (String s : BAI_DU)
            shellerMap.put(s, Sheller.BAI_DU);


    }

    public static void log(String txt) {
        CoreUtils.logRelease("packer-> " + txt);
    }

    public static Sheller parser(final XC_LoadPackage.LoadPackageParam lpparam) {
        for (String name : classesMap) {
            Class clazz = XposedHelpers.findClassIfExists(name, lpparam.classLoader);
            if (clazz != null) {
                Sheller sheller = getSheller(name);
                log("sheller :" + sheller.getName() + ",class :" + name);
                return sheller;
            }
        }
        return null;
    }


    private static Sheller getSheller(String packageName) {
        return shellerMap.get(packageName);
    }

    public enum Sheller {
        QI_HOO("360加固"), AI_JIA_MI("爱加密"), BANG_BANG("梆梆加固"), TENCENT("腾讯加固"), BAI_DU("百度加固");

        String name;

        Sheller(String s) {
            name = s;
        }

        public String getName() {
            return name;
        }
    }

}
