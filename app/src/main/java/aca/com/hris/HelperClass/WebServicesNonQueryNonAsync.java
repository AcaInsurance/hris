package aca.com.hris.HelperClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Util.UtilException;
import aca.com.hris.Util.UtilService;
import rx.Observable;

/**
 * Created by Marsel on 16/11/2015.
 */

public class WebServicesNonQueryNonAsync  {
    private static final String TAG = "WebServices";
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


    public WebServicesNonQueryNonAsync(@NonNull Context context
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


    public WebServicesNonQueryNonAsync(@NonNull Context context
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


    public WebServicesNonQueryNonAsync(@NonNull Context context
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


    public void onPreExecute() {

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


    public ArrayList<HashMap<String,String>> doInBackground() {
        String resultSoap = "FALSE";
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
            responseBody = (SoapObject) envelope.bodyIn;

            if (responseBody.getPropertyCount() == 0) {
                error = true;
                errorMessage = "No data found";
                return null;
            }

            resultSoap = responseBody.getPropertySafelyAsString(responseArray[0]).toString();
            Log.d(TAG, "result:  " + resultSoap);

            responseList = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();

            map.put(responseArray[0], resultSoap);
            responseList.add(map);

            error = false;

        } catch (Exception ex) {
            UtilException exClass = new UtilException();

            ex.printStackTrace();
            error = true;
            errorMessage = exClass.getException(ex);
            responseList = null;
            stackTrace = ex.getStackTrace().toString();
        }

        return responseList;
    }

    public void onPostExecute() {
        if (loadingDialog) {
            if (context != null) {
                progressBar.dismiss();
                progressBar.hide();
            }
        }

    }

    public ArrayList<HashMap<String, String>> execute () {

        doInBackground();
        onPostExecute();

        return responseList;
    }

}
