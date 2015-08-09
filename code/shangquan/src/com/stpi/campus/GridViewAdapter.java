package com.stpi.campus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.stpi.campus.R;

public class GridViewAdapter extends BaseAdapter {

    private Context mContext = null;

    GridViewAdapter(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 101;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        Display display = ((Activity) this.mContext).getWindowManager()
                .getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        ImageView imageView = new ImageView(this.mContext);
        GridView.LayoutParams layoutParams = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);

        imageView.setLayoutParams(layoutParams);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ScaleType.CENTER_INSIDE);
        imageView.setMinimumWidth(100);
        imageView.setMinimumHeight(100);
        imageView.setBackgroundColor(Color.BLACK);

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_image_loading)
                .showImageForEmptyUri(R.drawable.ic_image_empty)
                .showImageOnFail(R.drawable.ic_image_error)
                .resetViewBeforeLoading().delayBeforeLoading(1000)
                .cacheInMemory().cacheOnDisc()
                        // .preProcessor(...)
                        // .postProcessor(...)
                        // .extraForDownloader(...)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                        // .decodingOptions(...)
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();

        String imageUri = "http://72pi.com/bimei/images/IMG_3079.jpg";
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Load image, decode it to Bitmap and display Bitmap in ImageView
        imageLoader.displayImage(imageUri, imageView, options,
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                }
        );
        return imageView;
    }
}
