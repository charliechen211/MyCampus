package com.stpi.campus.util;

import android.content.ContentResolver;
import android.graphics.*;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class ImageFileUtils {

    public static String parse(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);

        byte[] b = baos.toByteArray();
        try {
            return new String(b, "ISO-8859-1");
        } catch (Exception e) {
            return null;
        }
    }

    // 计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(ContentResolver cr, Uri uri) throws FileNotFoundException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(cr
                .openInputStream(uri), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeStream(cr
                .openInputStream(uri), null, options);
    }

    // 给原始图片加圆角
    public static Bitmap getRoundCornerImage(Bitmap bitmap, float roundPixels) {

        // 创建一个和原始图片一样大小位图
        Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // 创建带有位图roundConcerImage的画布
        Canvas canvas = new Canvas(roundConcerImage);
        // 创建画笔
        Paint paint = new Paint();
        // 创建一个和原始图片一样大小的矩形
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        // 去锯齿
        paint.setAntiAlias(true);
        // 画一个和原始图片一样大小的圆角矩形
        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        // 设置相交模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 把图片画到矩形去
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }

}
