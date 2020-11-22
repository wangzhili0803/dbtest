package com.jerry.baselib.common.util;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

public class GBData {

    private static final String TAG = "GBData";
    public static ImageReader reader;

    /**
     * @param x
     * @param y
     * @return
     */
    public static int[] getColor(int x, int y) {
        if (reader == null) {
            Log.w(TAG, "getColor: reader is null");
            return null;
        }

        Image image = reader.acquireLatestImage();

        if (image == null) {
            Log.w(TAG, "getColor: image is null");
            return null;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        int enterColor = bitmap.getPixel(x, y);
        int[] rgb = new int[3];
        rgb[0] = Color.red(enterColor);
        rgb[1] = Color.green(enterColor);
        rgb[2] = Color.blue(enterColor);
        return rgb;
    }
}