package org.drinkless.td.libcore.telegram.parser;

import android.util.Log;

class BuildVars {

    public static final boolean LOGS_ENABLED = true;
}

class FileLog {

    public static void e(String s) {
        Log.e("LibTg", s);
    }

    public static void e(Exception e) {
        Log.e("LibTg", e.getLocalizedMessage());
    }
}
