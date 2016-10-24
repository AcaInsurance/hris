package aca.com.hris.HelperClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Util.UtilException;
import aca.com.hris.Util.UtilService;

/**
 * Created by Marsel on 16/11/2015.
 */

public abstract class WebServices extends AsyncTask {
    private String NAMESPACE = UtilService.getNamespace();
    private String URL = UtilService.getWsURL();
    private String SOAP_ACTION = UtilService.getSoapAction();
    private String METHOD_NAME = "";


    private SoapObject requestretrive = null;
    private SoapSerializationEnvelope envelope = null;
    private ProgressDialog progressBar;
    private HttpTransportSE androidHttpTransport = null;

    private Context context;
    private boolean error = true;
    private String errorMessage;
    private String stackTrace;

    private ArrayList<HashMap<String, String>> responseList = null;
    private String[] propertyName = null;
    private HashMap<String, String> propertyParam = null;
    private String[] responseArray;
    private boolean loadingDialog;
    private String loadingMessage;

    private static final String TAG = "WebServices";


    public WebServices(@NonNull Context context
            , @NonNull String methodName
            , @NonNull HashMap<String, String> propertyParam
            , @NonNull String[] propertyName
            , @NonNull String[] responseArray) {
        this.context = context;
        this.SOAP_ACTION += methodName;
        this.METHOD_NAME = methodName;
        this.propertyParam = propertyParam;
        this.propertyName = propertyName;
        this.responseArray = responseArray;
    }



    public WebServices(@NonNull Context context
            , @NonNull String methodName
            , @NonNull HashMap<String, String> propertyParam
            , @NonNull String[] propertyName
            , @NonNull String[] responseArray
            , boolean loadingDialog) {
        this.context = context;
        this.SOAP_ACTION += methodName;
        this.METHOD_NAME = methodName;
        this.propertyParam = propertyParam;
        this.propertyName = propertyName;
        this.responseArray = responseArray;
        this.loadingDialog = loadingDialog;
        this.loadingMessage = UtilService.getLoadingMessage();

    }

    public WebServices(@NonNull Context context
            , @NonNull String methodName
            , @NonNull HashMap<String, String> propertyParam
            , @NonNull String[] propertyName
            , @NonNull String[] responseArray
            , boolean loadingDialog
            , String loadingMessage) {
        this.context = context;
        this.SOAP_ACTION += methodName;
        this.METHOD_NAME = methodName;
        this.propertyParam = propertyParam;
        this.propertyName = propertyName;
        this.responseArray = responseArray;
        this.loadingDialog = loadingDialog;
        this.loadingMessage = loadingMessage;
    }


    public WebServices(@NonNull Context context
            , @NonNull String methodName
            , @NonNull HashMap<String, String> propertyParam
            , @NonNull String[] propertyName
            , @NonNull String[] responseArray
            , boolean loadingDialog
            , int ws) {
        this.context = context;
        this.SOAP_ACTION += methodName;
        this.METHOD_NAME = methodName;
        this.propertyParam = propertyParam;
        this.propertyName = propertyName;
        this.responseArray = responseArray;
        this.loadingDialog = loadingDialog;
        this.loadingMessage = loadingMessage;

        if (ws == 99) {
            this.URL = UtilService.getUrlHowden();
        }
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (loadingDialog) {
            if (context != null) {
                progressBar = new ProgressDialog(context);
                progressBar.setCancelable(false);
                progressBar.setMessage(loadingMessage);
                progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressBar.show();
            }
        }

        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.implicitTypes = true;
        envelope.dotNet = true;    //used only if we use the webservice from a dot net file (asmx)
        requestretrive = new SoapObject(NAMESPACE, METHOD_NAME);
        androidHttpTransport = new HttpTransportSE(URL);
    }


    @Override
    protected Object doInBackground(Object[] params) {

        SoapObject table;                        // Contains table of dataset that returned through SoapObject
        SoapObject tableRow;                     // Contains row of table
        SoapObject responseBody;                    // Contains XML content of dataset
        try {
            String namaParam;

            for (int i = 0; i < propertyParam.size(); i++) {
                namaParam = propertyName[i];

                Log.d(TAG, namaParam);
                Log.d(TAG, propertyParam.get(namaParam));

                requestretrive.addProperty(UtilService.setPropertyInfo(namaParam, propertyParam.get(namaParam), String.class));
            }


            envelope.setOutputSoapObject(requestretrive);
            envelope.bodyOut = requestretrive;
            androidHttpTransport.call(SOAP_ACTION, envelope);


            responseBody = (SoapObject) envelope.getResponse();
            responseBody = (SoapObject) responseBody.getProperty(1);

            if (responseBody.getPropertyCount() != 0) {

                responseList = new ArrayList<>();
                table = (SoapObject) responseBody.getProperty(0);

                int iTotalDataFromWebService = table.getPropertyCount();
                HashMap<String, String> hashMap;


                for (int i = 0; i < iTotalDataFromWebService; i++) {
                    tableRow = (SoapObject) table.getProperty(i);
                    hashMap = new HashMap<String, String>();

                    for (int j = 0; j < responseArray.length; j++) {
                        hashMap.put(responseArray[j], trimAnyType((tableRow.getPropertySafelyAsString(responseArray[j]))));
                    }
                    responseList.add(hashMap);
                }
            }
            error = false;

        } catch (Exception ex) {
            UtilException exClass =  new UtilException();

            ex.printStackTrace();
            error = true;
            errorMessage = exClass.getException(ex);
            responseList = null;
            stackTrace = ex.getStackTrace().toString();
        }

        return null;
    }

    private String trimAnyType(String propertySafelyAsString) {
        if (TextUtils.isEmpty(propertySafelyAsString))
            return "";

        return propertySafelyAsString.equalsIgnoreCase("anytype{}") ? "" : propertySafelyAsString;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if (loadingDialog) {
            if (context != null) {
                progressBar.dismiss();
                progressBar.hide();
            }
        }

        if (error)
            onFailed(errorMessage);
        else
            onSuccess(responseList);

    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        onCancel();
    }

    protected abstract void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList);
    protected abstract void onFailed(String message);
    protected abstract void onCancel();



}
