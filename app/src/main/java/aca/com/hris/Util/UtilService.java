package aca.com.hris.Util;

import android.support.annotation.NonNull;

import org.ksoap2.serialization.PropertyInfo;

/**
 * Created by Marsel on 8/13/2015.
 */
public class UtilService {
    private static final String namespace = "http://tempuri.org/";
    private static final String soapAction = "http://tempuri.org/";
    private static final String urlLive = "http://182.23.65.68/wshrismobile/service.asmx";
    private static final String urlTest = "http://172.16.88.31/wshrismobile/service.asmx";
    private static final String urlHowden = "http://182.23.65.68/wshowdenxmltest/service.asmx";

    private static final String imagesURL = "http://172.16.88.31/acahrisDemo/Images/SuratDokter/";
//    private static final String imagesURL = "http://117.102.85.67/AcaHris/Images/BerkasAbsenImages/";


    private static final String wsURL = urlTest;

    private static final String loadingMessage = "Please wait ...";


    public static String getNamespace() {
        return namespace;
    }

    public static String getSoapAction() {
        return soapAction;
    }

    public static String getUrlHowden() {
        return urlHowden;
    }


    public static String getWsURL() {
        return wsURL;
    }


    public static String getLoadingMessage() {
        return loadingMessage;
    }


    public static String getImagesURL() {
        return imagesURL;
    }

    public static PropertyInfo setPropertyInfo(String namaProperty, String valueProperty, Object typeProperty) {
        PropertyInfo pi = new PropertyInfo();

        pi.setName(namaProperty);
        pi.setValue(valueProperty);
        pi.setType(typeProperty);


        return pi;
    }


}
