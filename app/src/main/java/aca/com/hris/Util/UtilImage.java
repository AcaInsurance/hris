package aca.com.hris.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.IllegalFormatWidthException;

import aca.com.hris.R;
import aca.com.hris.Widget.TouchImageView;

/**
 * Created by Marsel on 25/11/2015.
 */
public class UtilImage {
    public static final int width = 1024;
    public static final int height = 768;
    public static final int maxByte = 1000000;

    public static String convertBase64 (ImageView imageView) {
//        imageView.buildDrawingCache();
//        Bitmap bm = imageView.getDrawingCache();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//        byte[] b = baos.toByteArray();
//
//        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//        return encodedImage;


        Bitmap bitmap = resizeImage(imageView);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
        byte[] bb = bos.toByteArray();
        String image = Base64.encodeToString(bb, Base64.DEFAULT);
        return image;


    }

    public static Bitmap resizeImage (ImageView imgView) {
        BitmapDrawable drawable = (BitmapDrawable) imgView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();


        while (bitmap.getByteCount() > maxByte) {
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.8), (int) (bitmap.getHeight() * 0.8), true);
        }
        return bitmap;
    }

    public static void popupImage(final Context context, final Drawable imageDwb)
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_touch_image_view);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        TouchImageView touchImageView = (TouchImageView)dialog.findViewById(R.id.imgPhoto);
        touchImageView.setImageDrawable(imageDwb);
    }


    public static String ImageToString(String path)
    {
        String sourceFileUri = path;
        File file = new File(sourceFileUri);
        String retValue = null;

        if (file.isFile())
        {
            FileInputStream objFileIS = null;
            ByteArrayOutputStream objByteArrayOS = null;
            try
            {

                //resizeImageFile(file, 500);

                file = new File(sourceFileUri);

                objFileIS = new FileInputStream(file);

                objByteArrayOS = new ByteArrayOutputStream();
                byte[] byteBufferString = new byte[1024];

                for (int readNum; (readNum = objFileIS.read(byteBufferString)) != -1;)
                    objByteArrayOS.write(byteBufferString, 0, readNum);

                byte[] byteBinaryData = Base64.encode((objByteArrayOS.toByteArray()), Base64.DEFAULT);
                String strAttachmentCoded = new String(byteBinaryData);

                retValue = strAttachmentCoded;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finally
            {
                if (objByteArrayOS != null)
                    try {
                        objByteArrayOS.close();
                        objFileIS.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        }

        return retValue;

    }
}
