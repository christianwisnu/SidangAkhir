package utilities;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

    public static final String TAG = AppController.class
            .getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;
    public static RequestQueue queue;
    private static RequestQueue mRequestQueuee;
    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        mInstance = this;
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public static RequestQueue getRequestQueuee() {
        if (queue != null) {
            return queue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

  /* handle get image loader
   * public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
    */

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public Request<?> setInsert(Request<?> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        getRequestQueue().add(request);
        return request;
    }

    public Request<?> GetData(Request<?> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(true);
        getRequestQueue().add(request);
        return request;
    }

    public Request<?> setUpload(Request<?> request) {
        //request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(true);
        getRequestQueue().add(request);
        return request;
    }

    public Request<?> setDefaultRetryPolicy(Request<?> request) {

        //request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        getRequestQueue().add(request);
        return request;
    }

    public Request<?> setDefaultPriority(Request<?> request) {

        //request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //   	request.setPriority( priorityHight);
        request.setShouldCache(false);
        getRequestQueue().add(request);
        return request;
    }

    public Request<?> setHightPriority(Request<?> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//	   	request.setPriority( priorityHight);
        //request.setShouldCache(true);
        getRequestQueue().add(request);
        return request;
    }

    public Request<?> setMediumtPriority(Request<?> request) {

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //   	request.setPriority( prioritymedium);
        request.setShouldCache(false);
        getRequestQueue().add(request);
        return request;
    }

    public Request<?> setLowtPriority(Request<?> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //   	request.setPriority( prioritylow);
        request.setShouldCache(false);
        getRequestQueue().add(request);
        return request;
    }
}
