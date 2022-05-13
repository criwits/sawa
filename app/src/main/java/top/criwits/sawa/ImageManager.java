package top.criwits.sawa;

import android.content.res.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageManager {
    public static Bitmap BG_IMG;

    public static void loadImages(Resources r) {
        BG_IMG = BitmapFactory.decodeResource(r, R.drawable.bg);
    }
}
