package com.granado.java.reference;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class GCUtils {

    public static final int CHECK_TIMES = 5;

    public static boolean ensureGC() {

        WeakReference<Object> checker = new WeakReference<>(new Object());
        int i = 0;
        while (i < CHECK_TIMES && checker.get() != null) {
            System.gc();
            i++;
        }

        if (i == CHECK_TIMES && checker.get() != null) {
            return  false;
        }

        return true;
    }

    public static boolean isLiving(Reference reference) {
        if (reference != null && reference.get() != null) {
            return true;
        } else {
            return false;
        }
    }
}
