package aca.com.hris.Util;

import android.support.annotation.NonNull;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by Marsel on 8/13/2015.
 */
public class UtilMath {
    private static NumberFormat nf = NumberFormat.getCurrencyInstance();

    private static double toDouble (@NonNull String angka) throws ParseException {
        return nf.parse(angka).doubleValue();
    }

    private static double toInt (@NonNull String angka) throws ParseException {
        return nf.parse(angka).intValue();
    }

    private static double toLong (@NonNull String angka) throws ParseException {
        return nf.parse(angka).longValue();
    }

    private static String toString (@NonNull double angka) throws ParseException {
        return nf.format(angka);
    }
    private static String toString (@NonNull int angka) throws ParseException {
        return nf.format(angka);
    }
    private static String toString (@NonNull long angka) throws ParseException {
        return nf.format(angka);
    }

}
