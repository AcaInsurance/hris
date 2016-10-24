package aca.com.hris.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.AbsensiCancellationActivity;
import aca.com.hris.Adapter.CancellationAbsensiAdapter;
import aca.com.hris.Adapter.CancellationCutiAdapter;
import aca.com.hris.BaseActivity;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.Email;
import aca.com.hris.HelperClass.SysAuditLog;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.HelperClass.WebServicesNonQuery;
import aca.com.hris.R;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.Var;
import butterknife.Bind;

/**
 * A placeholder fragment containing a simple view.
 */
public class AbsensiCancellationFragment extends BaseFragment {

    @Bind(R.id.txtAlasanBatal)
    EditText txtAlasanBatal;
    @Bind(R.id.tilAlasanBatal)
    TextInputLayout tilAlasanBatal;

    @Bind(R.id.sectionTop)
    CardView sectionTop;
    @Bind(R.id.rootView)
    RelativeLayout rootView;

    private String noTransaksi = UtilBundle.BUNDLE_INVALID_FLAG + "";
    private ArrayList<HashMap<String, String>> arrList;
    private onFormCancellationFragmentListener mListener;
    private int action;

    public AbsensiCancellationFragment() {
    }

    public static AbsensiCancellationFragment newInstance(String noTransaksi, int action) {
        Bundle args = new Bundle();
        args.putString(CancellationCutiAdapter.BUNDLE_NO_TRANSAKSI_ID, noTransaksi);
        args.putInt(UtilBundle.ACTION_BUNDLE, action);

        AbsensiCancellationFragment fragment = new AbsensiCancellationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreate() {
        onLoad(noTransaksi);
    }

    @Override
    protected void getArgsData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        try {
            noTransaksi = bundle.getString(CancellationAbsensiAdapter.BUNDLE_NO_TRANSAKSI_ID, UtilBundle.BUNDLE_INVALID_FLAG + "");
            action = bundle.getInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_form_cancellation;
    }

    @Override
    protected void initInstance(View view) {

    }

    @Override
    protected void init(View view) {

    }

    @Override
    protected void registerListener(View view) {
//        btnRetry.setOnClickListener(btnRetryListener());
    }

    private View.OnClickListener btnRetryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoad(noTransaksi);
            }
        };
    }

    @Override
    protected View getRootPanel() {
        return rootView;
    }

    @Override
    protected Button getButtonRetry() {
        return null;
    }

    @Override
    protected ProgressBar getProgressLoading() {
        return null;
    }

    public void onLoad(String noTransaksi) {
        if (action == UtilBundle.ACTION_BUNDLE_NEW)
            return;

        this.noTransaksi = noTransaksi;

//        showProgress(true);
        disableView();

        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);

        WebServices ws = new WebServices(getActivity(),
                "LoadAbsensiFormCancellation",
                map,
                getResources().getStringArray(R.array.loadAbsensiFormCancellation_post),
                getResources().getStringArray(R.array.loadAbsensiFormCancellation_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                if (arrList != null) {
                    fillKomponen(arrList);
//                    showProgress(false);
//                    mListener.setAction(action);
                } else {
//                    showRetry(true);
//                    mListener.setAction(UtilBundle.ACTION_BUNDLE_FAIL_LOADING);
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
//                    showRetry(true);
//                    mListener.setAction(UtilBundle.ACTION_BUNDLE_FAIL_LOADING);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();

    }

    private void fillKomponen(ArrayList<HashMap<String, String>> arrList) {
        this.arrList = arrList;

        HashMap<String, String> map = arrList.get(0);
        txtAlasanBatal.setText(map.get("AlasanBatal"));
    }

    public void onSave(ArrayList<HashMap<String, String>> arrList) {
        if (!validate() || arrList == null)
            return;


        this.arrList = arrList;
        HashMap<String, String> map = fillMap();

        SysAuditLog sysAuditLog = new SysAuditLog();
        map = sysAuditLog.create(getActivity(), R.array.DoSaveAbsensiFormCancellation_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoSaveAbsensiFormCancellation",
                map,
                getResources().getStringArray(R.array.DoSaveAbsensiFormCancellation_post),
                getResources().getStringArray(R.array.DoSaveAbsensiFormCancellation_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String result = arrList.get(0).get(getResources().getStringArray(R.array.DoSaveAbsensiFormCancellation_get)[0]);

                if (result.equalsIgnoreCase(BaseActivity.INVALID_FLAG + "")) {
                    Snackbar.make(getView(), getString(R.string.failed_create_form), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(), getString(R.string.success_create_form), Snackbar.LENGTH_SHORT).show();
                    mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                    disableView();
                    noTransaksi = result;
                    ((AbsensiCancellationActivity)getActivity()).setSaveSuccess(true);

                    sendEmail();
                }
            }

            @Override
            protected void onFailed(String message) {
                Snackbar.make(getView(), getString(R.string.failed_create_form), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }

    private void sendEmail() {
        new Email() {
            @Override
            public void resultSending(boolean success) {
//                if (!success) {
//                    new AlertDialog.Builder(getActivity(), Var.dialogTheme)
//                            .setTitle(R.string.Email)
//                            .setMessage(R.string.gagal_kirim_email)
//                            .setPositiveButton(R.string.retry
//                                    , new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            AbsensiCancellationFragment.this.sendEmail();
//                                        }
//                                    })
//                            .setCancelable(false)
//                            .show();
//                }
            }
        }.sendEmail(getActivity(),
                noTransaksi,
                "DoSendEmailCreateAbsensiCancellation",
                R.array.DoSendEmailCreateAbsensiCancellation_post,
                R.array.DoSendEmailCreateAbsensiCancellation_get,
                Email.IS_NOT_APPROVAL);




    }

    private void disableView() {
        txtAlasanBatal.setEnabled(false);
    }

    private void enableView() {
        txtAlasanBatal.setEnabled(true);
    }

    private HashMap<String, String> fillMap() {

        String userId = User.getUserID();
        String tanggalTransaksi = UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER);
        String kodeTransaksi = "ABCN";

        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> detailList = arrList.get(0);

        map.put("UserId", userId);
        map.put("NoCancel", noTransaksi);
        map.put("NoTransaksi", detailList.get("NoTransaksi"));
        map.put("TglCancel", tanggalTransaksi);
        map.put("KodeTransaksi", kodeTransaksi);
        map.put("AlasanBatal", txtAlasanBatal.getText().toString());
        map.put("KodeLevelKary", User.getKodeLevel());
        map.put("ReviewBy", "");
        map.put("ReviewDate", "");
        map.put("IsApproved", "0");
        map.put("IsRejected", "0");
        map.put("HRDReviewBy", "");
        map.put("HRDReviewDate", "");
        map.put("IsHRDApproved", "0");
        map.put("IsHRDRejected", "0");
        map.put("IsAppMobile", "1");
        map.put("AlasanReject", "");
        map.put("IsEmailSent", "");
        map.put("EmailException", "");
        map.put("CreateBy", userId);
        map.put("CreateDate", UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER));
        map.put("ModifyBy", userId);
        map.put("ModifyDate", UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER));
        map.put("IsActive", "1");


        return map;
    }

    private boolean validate() {
        if (!validateEmpty()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_empty_field), Snackbar.LENGTH_SHORT).show();
            return false;

        }

        return true;
    }

    private boolean validateEmpty() {
        if (txtAlasanBatal.getText().toString().isEmpty()) {
            tilAlasanBatal.setError(getString(R.string.prompt_error_empty_field));
            return false;
        }

        return true;
    }

    public void onEdit() {
        enableView();
        mListener.setAction(UtilBundle.ACTION_BUNDLE_UPDATE);
    }

    public void onUpdate(ArrayList<HashMap<String, String>> list) {
        if (!validate())
            return;

        this.arrList = list;

        HashMap<String, String> map = fillMap();
        SysAuditLog sysAuditLog = new SysAuditLog();
        map = sysAuditLog.create(getActivity(), R.array.DoSaveAbsensiFormCancellation_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoUpdateAbsensiFormCancellation",
                map,
                getResources().getStringArray(R.array.DoUpdateAbsensiFormCancellation_post),
                getResources().getStringArray(R.array.DoUpdateAbsensiFormCancellation_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String result = arrList.get(0).get(getResources().getStringArray(R.array.DoUpdateAbsensiFormCancellation_get)[0]);

                if (result.equalsIgnoreCase("-1")) {
                    Snackbar.make(getView(), getString(R.string.failed_update_form), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(), getString(R.string.success_update_form), Snackbar.LENGTH_SHORT).show();
                    mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                    disableView();
                    noTransaksi = result;
                }
            }

            @Override
            protected void onFailed(String message) {
                Snackbar.make(getView(), getString(R.string.failed_update_form), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }

    public void onDelete() {

        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);
        map.put("ModifyBy", User.getUserID());
        map.put("ModifyDate", UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER));

//        SysAuditLog sysAuditLog = new SysAuditLog();
//        map = sysAuditLog.create(getActivity(), R.array.DoUpdateAbsensiForm_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoDeleteAbsensiFormCancellation",
                map,
                getResources().getStringArray(R.array.DoDeleteAbsensiFormCancellation_post),
                getResources().getStringArray(R.array.DoDeleteAbsensiFormCancellation_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String result = arrList.get(0).get(getResources().getStringArray(R.array.DoDeleteAbsensiFormCancellation_get)[0]);

                if (result.equalsIgnoreCase("-1")) {
                    Snackbar.make(getView(), getString(R.string.failed_delete_form), Snackbar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.success_delete_form), Toast.LENGTH_SHORT).show();
                    mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                    disableView();
                    noTransaksi = result;

                    ((AbsensiCancellationActivity)getActivity()).onDeleteCompleted();

                }
            }

            @Override
            protected void onFailed(String message) {
                Snackbar.make(getView(), getString(R.string.failed_delete_form), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onFormCancellationFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onFormCancellationFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onFormCancellationFragmentListener {
        public void setAction(int action);
    }
}
