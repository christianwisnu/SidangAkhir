package utilities;

import android.content.Context;

import java.io.File;
import java.util.Calendar;

public class Utils {

    public static void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static java.util.Date getLastTimeOfDay(java.util.Date date){
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(date);
        tempCalendar.set(Calendar.HOUR_OF_DAY,23);
        tempCalendar.set(Calendar.MINUTE,59);
        tempCalendar.set(Calendar.SECOND,59);
        tempCalendar.set(Calendar.MILLISECOND,999);
        return tempCalendar.getTime();
    }
}
