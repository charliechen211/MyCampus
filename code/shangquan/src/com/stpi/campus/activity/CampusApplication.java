/**
 *
 */
package com.stpi.campus.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.stpi.campus.activity.otherService.CrashHandler;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author yiwang
 */
public class CampusApplication extends Application {
    public static final String strKey = "hcTuvAMR8XVBjkGCT4IGDEZt";
    private static CampusApplication mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    private List<Activity> activityList = new LinkedList<Activity>();

    public static CampusApplication getInstance() {
        return mInstance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        int id = android.os.Process.myPid();
        if (id != 0) {
            android.os.Process.killProcess(id);
        }
    }

    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey, new MyGeneralListener())) {
            Toast.makeText(this.getInstance().getApplicationContext(), "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }

    private void initImageLoader(File cacheDir) {
        // to initialize the ImageLoader.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).memoryCacheExtraOptions(480, 800)
                // default = device screen dimensions
                .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75)
                        // .taskExecutor(...)
                        // .taskExecutorForCachedImages(...)
                .threadPoolSize(3)
                        // default
                .threadPriority(Thread.NORM_PRIORITY - 1)
                        // default
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                        // default
                .denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024).discCache(new UnlimitedDiscCache(cacheDir))
                        // default
                .discCacheSize(50 * 1024 * 1024).discCacheFileCount(100).discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .imageDecoder(new BaseImageDecoder()) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // initBaiduEngineManager(this);

        File cacheDir = new File(getCacheDir().toString() + "/images");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        initImageLoader(cacheDir);
        initEngineManager(this);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(CampusApplication.getInstance().getApplicationContext(), "您的网络出错啦！", Toast.LENGTH_LONG).show();
            } else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(CampusApplication.getInstance().getApplicationContext(), "输入正确的检索条件！", Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
                // 授权Key错误：
                Toast.makeText(CampusApplication.getInstance().getApplicationContext(), "请在输入正确的授权Key！", Toast.LENGTH_LONG).show();
                CampusApplication.getInstance().m_bKeyRight = false;
            }
        }
    }
}
