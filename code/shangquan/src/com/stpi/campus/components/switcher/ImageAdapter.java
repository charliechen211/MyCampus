package com.stpi.campus.components.switcher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import com.stpi.campus.HomeActivity;
import com.stpi.campus.R;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    public static Integer[] imgs = {R.drawable.demo_ad1, R.drawable.demo_ad2, R.drawable.demo_ad3};
    ImageView imageView;
    Intent intent;
    Uri uri;
    private Context context;
    private List<String> imageUrls; // 图片地址list
    @SuppressWarnings("unused")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0: {
                        self.notifyDataSetChanged();
                    }
                    break;
                }

                super.handleMessage(msg);
            } catch (Exception e) {
            }
        }
    };
    private String[] myuri = {"", "", ""};
    private ImageAdapter self;

    public ImageAdapter(/* List<String> imageUrls, */Context context) {
        // this.imageUrls = imageUrls;
        this.context = context;
        this.self = this;
    }

    public int getCount() {
        return imgs.length;
    }

    public Object getItem(int position) {
        return imageUrls.get(position % imgs.length);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Bitmap image;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.pic_switcher_item, null); // 实例化convertView
            Gallery.LayoutParams params = new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT);
            convertView.setLayoutParams(params);
            // image =
            // ((ImageActivity)context).imagesCache.get(imageUrls.get(position %
            // imageUrls.size())); //从缓存中读取图片
            /*
			 * if(image==null){
			 * //当缓存中没有要使用的图片时，先显示默认的图片
			 * image =
			 * ((ImageActivity)context).imagesCache.get("background_non_load");
			 * //异步加载图片
			 * LoadImageTask task = new LoadImageTask(convertView);
			 * task.execute(imageUrls.get(position % imageUrls.size()));
			 * }
			 */
            convertView.setTag(imgs);

        } else {
            // image = (Bitmap) convertView.getTag();
        }
        imageView = (ImageView) convertView.findViewById(R.id.gallery_image);
        imageView.setImageResource(imgs[position % imgs.length]);
        // 设置缩放比例：保持原样
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        ((HomeActivity) context).changePointView(position % imgs.length);
        return convertView;

    }

	/*
	 * //加载图片的异步任务
	 * class LoadImageTask extends AsyncTask<String,Void,Bitmap>{
	 * private View resultView;
	 * LoadImageTask(View resultView) {
	 * this.resultView = resultView;
	 * }
	 * // doInBackground完成后才会被调用
	 * @Override
	 * protected void onPostExecute(Bitmap bitmap) {
	 * //调用setTag保存图片以便于自动更新图片
	 * resultView.setTag(bitmap);
	 * }
	 * //从网上下载图片
	 * @Override
	 * protected Bitmap doInBackground(String... params) {
	 * Bitmap image=null;
	 * try {
	 * //new URL对象 把网址传入
	 * URL url = new URL(params[0]);
	 * //取得链接
	 * URLConnection conn = url.openConnection();
	 * conn.connect();
	 * //取得返回的InputStream
	 * InputStream is = conn.getInputStream();
	 * //将InputStream变为Bitmap
	 * image = BitmapFactory.decodeStream(is);
	 * is.close();
	 * ((ImageActivity)context).imagesCache.put(params[0],image);
	 * //把下载好的图片保存到缓存中
	 * Message m = new Message();
	 * m.what = 0;
	 * mHandler.sendMessage(m);
	 * } catch (Exception e) {
	 * e.printStackTrace();
	 * }
	 * return image;
	 * }
	 * }
	 */
}
