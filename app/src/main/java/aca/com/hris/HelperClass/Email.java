package aca.com.hris.HelperClass;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Database.Head;
import aca.com.hris.Database.User;
import aca.com.hris.Fragment.AbsensiFragment;
import aca.com.hris.R;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by Marsel on 26/1/2016.
 */
public abstract class Email {
    private static final String TAG = "Email";
    public static final int IS_APPROVAL = 1;
    public static final int IS_NOT_APPROVAL  = 0;


    private static boolean success = false;
    private static boolean finish = false;
    private static Activity activity = null;

    private static String loadingMessage = "Sending Email...";


    public static boolean isSuccess () {
        return success;
    }
    public static boolean isFinish () {
        return finish;
    }


    private static HashMap<String, String> fillMapEmail(String noTransaksi){
        HashMap<String, String> map = new HashMap<>();
        map.put("IdKary", User.getEmpCode());
        map.put("IdApprover", Head.getIdApprover() );
        map.put("NoTransaksi", noTransaksi);

        return map;
    }

    private static HashMap<String, String> fillMapApproverEmail(String noTransaksi){
        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);

        return map;
    }

    public abstract void resultSending(boolean success);


    public void sendEmail(
            final Activity act,
            String noTransaksi,
            final String wsName,
            final int post,
            final int get,
            int isApproval) {
        activity = act;

        final HashMap<String, String> map;
        ArrayList<HashMap<String, String>> response;

        if (isApproval == IS_NOT_APPROVAL)
            map = fillMapEmail(noTransaksi);
        else
            map = fillMapApproverEmail(noTransaksi);

        final WebServicesNonQueryNonAsync wsn = new WebServicesNonQueryNonAsync(
                activity,
                wsName,
                map,
                activity.getResources().getStringArray(post),
                activity.getResources().getStringArray(get)
//                true,
//                loadingMessage
        );
        wsn.onPreExecute();

        Observable.create(
                new Observable.OnSubscribe<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<HashMap<String, String>>> subscriber) {
                        subscriber.onNext(wsn.doInBackground());
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public void onCompleted() {
                        finish = true;
                        wsn.onPostExecute();
                        resultSending(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        finish = true;
                        Log.d(TAG, "onError " + e.getMessage());
                        wsn.onPostExecute();
                        resultSending(success);
                    }

                    @Override
                    public void onNext(ArrayList<HashMap<String, String>> arrList) {
                        if(arrList == null) {
                            return;
                        }
                        String result = arrList.get(0).get(activity.getResources().getStringArray(get)[0]);
                        if (result.equalsIgnoreCase("1")) {
                            success = true;
                            Log.d(TAG, "onSuccess berhasil kirim email");
                        } else {
                            Log.d(TAG, "onSuccess gagal kirim email");
                        }
                    }
                });

    }





}
