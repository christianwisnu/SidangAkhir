package utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.project.sidangakhir.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.File;
import java.util.Calendar;

public class Utils {

    private static final ImageLoader imgloader = ImageLoader.getInstance();

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

    public static java.util.Date getFirstTimeOfDay(java.util.Date date){
        Calendar tempCalendar = Calendar.getInstance();
        tempCalendar.setTime(date);
        tempCalendar.set(Calendar.HOUR_OF_DAY,00);
        tempCalendar.set(Calendar.MINUTE,00);
        tempCalendar.set(Calendar.SECOND,00);
        tempCalendar.set(Calendar.MILLISECOND,000);
        return tempCalendar.getTime();
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

    public static void getCycleImage(String url, ImageView img, Context context){
        DisplayImageOptions options;
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer((int) 50.5f))
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .build();
        imgloader.init(ImageLoaderConfiguration.createDefault(context));
        imgloader.displayImage(url, img, options);
        return;
    }
}
