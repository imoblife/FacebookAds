package base.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();
    private static final String SP_NAME = "sp_name_" + TAG;
    private static final long TIME_LIMIT = 12 * 60 * 60 * 1000;


    public static boolean isTimeUp(Context context, String key, long timeLimit) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME,
                Context.MODE_PRIVATE);
        long current = System.currentTimeMillis();
        long last = sp.getLong(key, 0);
        long delta = current - last;

        if (delta > timeLimit) {
            sp.edit().putLong(key, current).commit();
            return true;
        } else {
            return false;
        }
    }


    public static final long MINUTE = 60 * 1000;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    public static boolean isUmengTimeUp(Context context) {
        boolean result = isTimeUp(context, "Umeng", 2 * HOUR);
        Log.i(TAG, "UC::isUmengTimeUp " + result);
        return result;
    }


    public static boolean isCountUp(Context context, String key, long countLimit) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        long last = sp.getLong(key, 0);

        long current = last + 1;
        sp.edit().putLong(key, current).commit();

        Log.i(TAG, "FB::isCountUp " + current + " % " + countLimit + " = " + (current % countLimit == 0) + " key="+key);
        if (current % countLimit == 0) {
            return true;
        } else {
            return false;
        }
    }
}
