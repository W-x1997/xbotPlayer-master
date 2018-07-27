package cn.iscas.xlab.xbotplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Size;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by lisongting on 2017/5/10.
 *
 */
public class ImageUtils {

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    //Get Path Image file
    public final static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    //Rotate Bitmap
    public final static Bitmap rotate(Bitmap b, float degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2,
                    (float) b.getHeight() / 2);

            Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                    b.getHeight(), m, true);
            if (b != b2) {
                b.recycle();
                b = b2;
            }

        }
        return b;
    }


    public static Bitmap getBitmap(String filePath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = ImageUtils.calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        if (bitmap != null) {
            try {
                ExifInterface ei = new ExifInterface(filePath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = ImageUtils.rotate(bitmap, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = ImageUtils.rotate(bitmap, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = ImageUtils.rotate(bitmap, 270);
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    public static Bitmap cropBitmap(Bitmap bitmap, Rect rect) {
        int w = rect.right - rect.left;
        int h = rect.bottom - rect.top;
        Bitmap ret = Bitmap.createBitmap(w, h, bitmap.getConfig());
        Canvas canvas = new Canvas(ret);
        canvas.drawBitmap(bitmap, -rect.left, -rect.top, null);
        bitmap.recycle();
        return ret;
    }

    //将Bitmap转为Base64编码
    public static String encodeBitmapToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }


    //将base64 String解码为Bitmap
    public static Bitmap decodeBase64ToBitmap(String base64Str,int inSampleSize,Size expectSize){
        byte[] bitmapBytes = Base64.decode(base64Str, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();


        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false


        options.inSampleSize = ImageUtils.calculateInSampleSize(options,128,128);
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
     /* 下面两个字段需要组合使用 */
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;

      //  options.inSampleSize = inSampleSize;

        Bitmap origin = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length,options);

        int width = expectSize.getWidth();
        int height = expectSize.getHeight();
        return Bitmap.createScaledBitmap(origin,width,height,true);
    }

    public static Bitmap decodeBase64ToBitmap(String base64Str){
        byte[] bitmapBytes = Base64.decode(base64Str, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);

        //获取到来自服务器的原始图像的宽高
        int width = options.outWidth;
        int height = options.outHeight;
//        options.inSampleSize = calculateInSampleSize(options,width/3,height/3);
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length,options);
    }


    /**
     * 通过inSampleSize设置缩放倍数，比如写2，即长宽变为原来的1/2，图片就是原来的1/4，如果不进行缩放的话设置为1 但是不能一味的压缩，
     * 毕竟这个值太小 的话，图片会很模糊，而且要避免图片的拉伸变形，所以需要我们在程序中动态的计算，
     * 这个 inSampleSize的合适值，而Options中又有这样一个方法：inJustDecodeBounds，将该参数设置为 true后，decodeFiel并不会分配内存空间，但
     * 是可以计算出原始图片的长宽，调用 options.outWidth/outHeight获取出图片的宽高，然后通过一定的算法，即可得到适合的 inSampleSize
     */
//added by wx
    public static int caculateInSampleSize2(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }


}
