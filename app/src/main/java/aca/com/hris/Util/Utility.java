package aca.com.hris.Util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentBreadCrumbs;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import aca.com.hris.Adapter.MyCursorAdapter;
import aca.com.hris.Database.FormHead;
import aca.com.hris.R;

/**
 * Created by stok on 11-Feb-15.
 */
public class Utility {
    private static final int DATABASE_VERSION = 1;


    private static final String emptyMessage = "Harus diisi";

    public static String getSqlFormat() {
        return sqlFormat;
    }

    private static final String sqlFormat = "yyyy-MM-dd";

    public static String getAndoridFormat() {
        return andoridFormat;
    }

    private static final String andoridFormat = "dd-MM-yyyy";

    public static int getDatabaseVersion() {
        return DATABASE_VERSION;
    }

    public static void setLocale (Context context) {
        Locale locale = new Locale("en");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            String h;

            for (int i=0; i<messageDigest.length; i++) {
                h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
