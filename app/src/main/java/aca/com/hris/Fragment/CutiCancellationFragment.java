package aca.com.hris.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Adapter.CancellationCutiAdapter;
import aca.com.hris.BaseActivity;
import aca.com.hris.CutiCancellationActivity;
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
public class CutiCancellationFragment extends BaseFragment {

    @Bind(R.id.txtAlasanBatal)
    EditText txtAlasanBatal;
    @Bind(R.id.sectionTop)
    CardView sectionTop;
    @Bind(R.id.tilAlasanBatal)
    TextInputLayout tilAlasanBatal;
    private String noTransaksi = UtilBundle.BUNDLE_INVALID_FLAG + "";
    private ArrayList<HashMap<String, String>> arrList;
    private onCutiCancellationFragmentListener mListener;
    private int action;

    public static CutiCancellationFragment newInstance(String noTransaksi, int action) {
        Bundle args = new Bundle();
        args.putString(CancellationCutiAdapter.BUNDLE_NO_TRANSAKSI_ID, noTransaksi);
        args.putInt(UtilBundle.ACTION_BUNDLE, action);

        CutiCancellationFragment fragment = new CutiCancellationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public interface onCutiCancellationFragmentListener {
        public void setAction(int action);
    }

    public CutiCancellationFragment() {
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
            noTransaksi = bundle.getString(CancellationCutiAdapter.BUNDLE_NO_TRANSAKSI_ID, UtilBundle.BUNDLE_INVALID_FLAG + "");
            action = bundle.getInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_cuti_cancellation_form;
    }

    @Override
    protected void initInstance(View view) {

    }

    @Override
    protected void init(View view) {

    }

    @Override
    protected void registerListener(View view) {

    }

    @Override
    protected View getRootPanel() {
        return null;
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
//        mListener.setAction(action);
        disableView();

        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);

        WebServices ws = new WebServices(getActivity(),
                "LoadAbsensiCutiCancellation",
                map,
                getResources().getStringArray(R.array.LoadAbsensiCutiCancellation_post),
                getResources().getStringArray(R.array.LoadAbsensiCutiCancellation_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                if (arrList != null) {
                    fillKomponen(arrList);
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
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

//        if (isReviewed(map)) {
//            mListener.setAction(UtilBundle.ACTION_BUNDLE_NOT_EDIT);
//        }

    }


    private boolean isReviewed(HashMap<String, String> map) {
        boolean approved = Boolean.parseBoolean(map.get("IsAppHead"));
        boolean rejected = Boolean.parseBoolean(map.get("IsRejectHead"));

        if (approved || rejected)
            return true;
        else
            return false;
    }


    public void onSave(ArrayList<HashMap<String, String>> arrList) {
        if (!validate() || arrList == null)
            return;

        this.arrList = arrList;
        HashMap<String, String> map = fillMap();

        SysAuditLog sysAuditLog = new SysAuditLog();
        map = sysAuditLog.create(getActivity(), R.array.DoSaveAbsensiCutiCancellation_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoSaveAbsensiCutiCancellation",
                map,
                getResources().getStringArray(R.array.DoSaveAbsensiCutiCancellation_post),
                getResources().getStringArray(R.array.DoSaveAbsensiCutiCancellation_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String result = arrList.get(0).get(getResources().getStringArray(R.array.DoSaveAbsensiCutiCancellation_get)[0]);

                    if (result.equalsIgnoreCase(BaseActivity.INVALID_FLAG + "")) {
                        Snackbar.make(getView(), getString(R.string.failed_create_form), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(getView(), getString(R.string.success_create_form), Snackbar.LENGTH_SHORT).show();
                        mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                        disableView();
                        noTransaksi = result;
                        ((CutiCancellationActivity)getActivity()).setSaveSuccess(true);

                        sendEmail();
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    Snackbar.make(getView(),message, Snackbar.LENGTH_SHORT).show();
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
//                                            CutiCancellationFragment.this.sendEmail();
//                                        }
//                                    })
//                            .setCancelable(false)
//                            .show();
//                }
            }
        }.sendEmail(getActivity(),
                noTransaksi,
                "DoSendEmailCreateCutiCancellation",
                R.array.DoSendEmailCreateCutiCancellation_post,
                R.array.DoSendEmailCreateCutiCancellation_get,
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
        String kodeTransaksi = "CUCC";

        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> cutiList = arrList.get(0);

        map.put("UserId", userId);
        map.put("NoTransaksi", noTransaksi);
        map.put("KodeTransaksi", kodeTransaksi);
        map.put("Tanggal", tanggalTransaksi);
        map.put("NoTransaksiCuti", cutiList.get("NoTransaksi"));
        map.put("AlasanBatal", txtAlasanBatal.getText().toString());
        map.put("IsAppHead", "0");
        map.put("IsRejectHead", "0");
        map.put("TglAppHead", "");
        map.put("IdAppHead", "");
        map.put("IsAppHRD", "0");
        map.put("IsRejectHRD", "0");
        map.put("TglAppHRD", "");
        map.put("IdAppHRD", "");
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
        map = sysAuditLog.create(getActivity(), R.array.DoSaveAbsensiCuti_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoUpdateAbsensiCutiCancellation",
                map,
                getResources().getStringArray(R.array.DoUpdateAbsensiCutiCancellation_post),
                getResources().getStringArray(R.array.DoUpdateAbsensiCutiCancellation_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String result = arrList.get(0).get(getResources().getStringArray(R.array.DoUpdateAbsensiCutiCancellation_get)[0]);

                    if (result.equalsIgnoreCase("-1")) {
                        Snackbar.make(getView(), getString(R.string.failed_update_form), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(getView(), getString(R.string.success_update_form), Snackbar.LENGTH_SHORT).show();
                        mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                        disableView();
                        noTransaksi = result;
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
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

    public void onDelete() {

        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);
        map.put("ModifyBy", User.getUserID());
        map.put("ModifyDate", UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER));

//        SysAuditLog sysAuditLog = new SysAuditLog();
//        map = sysAuditLog.create(getActivity(), R.array.DoUpdateAbsensiForm_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoDeleteAbsensiCutiCancellation",
                map,
                getResources().getStringArray(R.array.DoDeleteAbsensiCutiCancellation_post),
                getResources().getStringArray(R.array.DoDeleteAbsensiCutiCancellation_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String result = arrList.get(0).get(getResources().getStringArray(R.array.DoDeleteAbsensiCutiCancellation_get)[0]);

                if (result.equalsIgnoreCase("-1")) {
                    Snackbar.make(getView(), getString(R.string.failed_delete_form), Snackbar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.success_delete_form), Toast.LENGTH_SHORT).show();
                    mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                    disableView();
                    noTransaksi = result;

                    ((CutiCancellationActivity)getActivity()).onDeleteCompleted();

                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
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


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onCutiCancellationFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onCutiCancellationFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
