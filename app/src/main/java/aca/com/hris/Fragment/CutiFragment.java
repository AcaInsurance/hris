package aca.com.hris.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Adapter.ApprovalCutiAdapter;
import aca.com.hris.Adapter.MyCursorAdapter;
import aca.com.hris.BaseActivity;
import aca.com.hris.CutiActivity;
import aca.com.hris.Database.Employee;
import aca.com.hris.Database.Employee$Table;
import aca.com.hris.Database.Head;
import aca.com.hris.Database.MsCuti;
import aca.com.hris.Database.MsCuti$Table;
import aca.com.hris.Database.SetVar;
import aca.com.hris.Database.SetVar$Table;
import aca.com.hris.Database.SummaryPeriode;
import aca.com.hris.Database.SummaryPeriode$Table;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.Email;
import aca.com.hris.HelperClass.SysAuditLog;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.HelperClass.WebServicesNonQuery;
import aca.com.hris.PojoModel.EmployeeModel;
import aca.com.hris.R;
import aca.com.hris.Retrofit.HrisService;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.Var;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class CutiFragment extends BaseFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    public onCutiFormListener mListener;
    @Bind(R.id.txtTanggalTranskasi)
    EditText txtTanggalTranskasi;
    @Bind(R.id.txtApprover)
    EditText txtApprover;
    @Bind(R.id.btnTipeCuti)
    Button btnTipeCuti;
    @Bind(R.id.txtMaxCuti)
    EditText txtMaxCuti;
    @Bind(R.id.txtKeterangan)
    TextView txtKeterangan;
    @Bind(R.id.btnTanggalMulai)
    Button btnTanggalMulai;
    @Bind(R.id.btnTanggalBerakhir)
    Button btnTanggalBerakhir;
    @Bind(R.id.txtJumlahHari)
    EditText txtJumlahHari;
    @Bind(R.id.txtJumlahCuti)
    EditText txtJumlahCuti;
    @Bind(R.id.txtJumlahPotongCuti)
    EditText txtJumlahPotongCuti;
    @Bind(R.id.txtAlasan)
    EditText txtAlasan;
    @Bind(R.id.lblAlasan)
    TextInputLayout lblAlasan;
    @Bind(R.id.txtCuti)
    EditText txtCuti;
    @Bind(R.id.viewScrollView)
    NestedScrollView viewScrollView;
    @Bind(R.id.lblTanggalTransaksi)
    TextInputLayout lblTanggalTransaksi;
    @Bind(R.id.lblApprover)
    TextInputLayout lblApprover;
    @Bind(R.id.lblCuti)
    TextInputLayout lblCuti;
    @Bind(R.id.lblMaxCuti)
    TextInputLayout lblMaxCuti;
    @Bind(R.id.lblJumlahHari)
    TextInputLayout lblJumlahHari;
    @Bind(R.id.lblJumlahCuti)
    TextInputLayout lblJumlahCuti;
    @Bind(R.id.lblJumlahPotongCuti)
    TextInputLayout lblJumlahPotongCuti;
    @Bind(R.id.txtSisaCuti)
    EditText txtSisaCuti;
    @Bind(R.id.lblSisaCuti)
    TextInputLayout lblSisaCuti;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnRetry)
    Button btnRetry;

    private AlertDialog.Builder dialogTipeCuti;
    private MyCursorAdapter adapterTipeCuti;
    private String noTransaksi = BaseActivity.INVALID_FLAG + "";
    private ArrayList<HashMap<String, String>> arrList;
    private int action;
    private Boolean isLoad;
    private boolean inReviewed;

    public CutiFragment() {

    }

    public static CutiFragment newInstance(String noTransaksi, int action, boolean isLoad) {
        Bundle args = new Bundle();
        args.putString(ApprovalCutiAdapter.BUNDLE_NO_TRANSAKSI, noTransaksi);
        args.putInt(UtilBundle.ACTION_BUNDLE, action);
        args.putBoolean(UtilBundle.BUNDLE_ON_LOAD, isLoad);

        CutiFragment fragment = new CutiFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreate() {
        initLoad();
    }

    private void initLoad() {
        if (!isLoad) {
            fillApprover();
            fillCuti(User.getEmpCode());
        }
        bindTipeCuti();
        onLoad(noTransaksi);
    }

    @Override
    protected void getArgsData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            noTransaksi = bundle.getString(ApprovalCutiAdapter.BUNDLE_NO_TRANSAKSI, UtilBundle.BUNDLE_EMPTY_FLAG);
            action = bundle.getInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);
            isLoad = bundle.getBoolean(UtilBundle.BUNDLE_ON_LOAD, false);
        }
    }


    @Override
    public int getViewLayout() {
        return R.layout.fragment_cuti_form;
    }

    @Override
    protected void initInstance(View view) {

    }

    @Override
    protected void init(View view) {

    }

    @Override
    protected void registerListener(View view) {
        btnTipeCuti.setOnClickListener(btnTipeCutiOnClickListener());
        btnTanggalMulai.setOnClickListener(btnTanggalMulaiListener());
        btnTanggalBerakhir.setOnClickListener(btnTanggalBerakhirListener());
        btnRetry.setOnClickListener(btnRetryListener());

    }

    private SwipeRefreshLayout.OnRefreshListener swipeContainerRefresh() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initLoad();
            }
        };
    }

    private View.OnClickListener btnRetryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLoad();
            }
        };
    }

    @Override
    protected View getRootPanel() {
        return viewScrollView;
    }

    @Override
    protected Button getButtonRetry() {
        return btnRetry;
    }

    @Override
    protected ProgressBar getProgressLoading() {
        return pbLoading;
    }

    private void fillApprover() {
        try {
            Head head = new Select().from(Head.class).querySingle();
            String approver = head.FullName;

            txtApprover.setText(approver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindTipeCuti() {
        btnTipeCuti.setText("");
        btnTipeCuti.setTag("");

        Employee emp = new Select().from(Employee.class).querySingle();
        String kdGender = emp.KdGender;

        Cursor cur = new Select().from(MsCuti.class)
                .where(Condition.column(MsCuti$Table.GENDER).eq(kdGender))
                .or(Condition.column(MsCuti$Table.GENDER).isNull())
                .query();

        dialogTipeCuti = new AlertDialog.Builder(getActivity());
        adapterTipeCuti = new MyCursorAdapter(getActivity(), cur, false, MsCuti$Table.KODECUTI, MsCuti$Table.NAMACUTI);
        dialogTipeCuti.setAdapter(adapterTipeCuti, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = ((Cursor) adapterTipeCuti.getItem(which)).getString(adapterTipeCuti.getCursor().getColumnIndex(MsCuti$Table.KODECUTI));
                String value = ((Cursor) adapterTipeCuti.getItem(which)).getString(adapterTipeCuti.getCursor().getColumnIndex(MsCuti$Table.NAMACUTI));
                btnTipeCuti.setText(value);
                btnTipeCuti.setTag(id);

                onSelectedTipeCuti(id);
            }
        });
        dialogTipeCuti.setTitle("Pilih Cuti");
    }

    private void onSelectedTipeCuti(String kodeCuti) {
        fillDetailTipeCuti(kodeCuti);
        emptyKomponen();
    }

    private void fillDetailTipeCuti(String kodeCuti) {
        try {
            MsCuti msCuti = new Select().from(MsCuti.class)
                    .where(Condition.column(MsCuti$Table.KODECUTI).eq(kodeCuti))
                    .querySingle();
            if (msCuti == null)
                return;

            String keterangan = msCuti.Keterangan;
            String maxCuti = msCuti.JumlahHari;

            txtKeterangan.setText(keterangan);
            txtMaxCuti.setText(maxCuti);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void emptyKomponen() {
        btnTanggalMulai.setText("");
        btnTanggalBerakhir.setText("");
        txtJumlahHari.setText("");
        txtJumlahCuti.setText("");
        txtJumlahPotongCuti.setText("");
        txtAlasan.setText("");
//        txtCuti.setText("");
//        txtPengambilanCuti.setText("");
        txtSisaCuti.setText("");
    }

    private View.OnClickListener btnTipeCutiOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTipeCuti.show();
            }
        };
    }

    private View.OnClickListener btnTanggalMulaiListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocalDate date = LocalDate.now();
                int year = date.getYear();
                int month = date.getMonthOfYear();
                int day = date.getDayOfMonth();

                if (!btnTanggalMulai.getText().toString().isEmpty()) {
                    String tanggal = btnTanggalMulai.getText().toString();
                    LocalDate tanggalDate = UtilDate.toDate(tanggal);

                    year = tanggalDate.getYear();
                    month = tanggalDate.getMonthOfYear();
                    day = tanggalDate.getDayOfMonth();
                }

                DatePickerDialog dpp = DatePickerDialog.newInstance(
                        CutiFragment.this,
                        year,
                        --month,
                        day
                );


                dpp.show(getFragmentManager(), "tag");
                dpp.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        try {
                            month++;
                            LocalDate tanggalMulaiDate = UtilDate.mergeDate(year, month, day);
                            String tanggalMulai = tanggalMulaiDate.toString(UtilDate.DATE_PATTERN_DISPLAY_1);

                            btnTanggalMulai.setText(tanggalMulai);

                            fillTanggalBerakhir(tanggalMulaiDate);
                            fillDetailCuti();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }

    private void emptyCuti() {
        txtJumlahHari.setText("");
        txtJumlahCuti.setText("");
        txtJumlahPotongCuti.setText("");
//        txtAlasan.setText("");
        txtSisaCuti.setText("");
    }

    private void fillDetailCuti() {
        if (!btnTanggalMulai.getText().toString().isEmpty() && !btnTanggalBerakhir.getText().toString().isEmpty()) {
            emptyCuti();
            fillJumlahHari();

            inReviewed = false;
            fillJumlahCuti(inReviewed);
//            fillJumlahPotongCuti();
//            fillSisaCuti();
        }

    }

    private void fillDetailCutiReviewed(HashMap<String, String> map) {
        if (!btnTanggalMulai.getText().toString().isEmpty() && !btnTanggalBerakhir.getText().toString().isEmpty()) {
            inReviewed = true;

            emptyCuti();
            fillJumlahHari();
            fillJumlahCuti(inReviewed);

            String cuti = map.get("CurrentCuti");
            String sisaCuti = map.get("LatestCuti");
            String potongCuti = map.get("JmlhPtgCuti");

            txtCuti.setText(cuti);
            txtJumlahPotongCuti.setText(potongCuti);
            txtSisaCuti.setText(sisaCuti);
        }
    }


    private View.OnClickListener btnTanggalBerakhirListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocalDate date = LocalDate.now();
                int year = date.getYear();
                int month = date.getMonthOfYear();
                int day = date.getDayOfMonth();


                if (!btnTanggalBerakhir.getText().toString().isEmpty()) {
                    String tanggal = btnTanggalBerakhir.getText().toString();
                    LocalDate tanggalDate = UtilDate.toDate(tanggal);

                    year = tanggalDate.getYear();
                    month = tanggalDate.getMonthOfYear();
                    day = tanggalDate.getDayOfMonth();
                }

                DatePickerDialog dpp = DatePickerDialog.newInstance(
                        CutiFragment.this,
                        year,
                        --month,
                        day
                );

                if (!TextUtils.isEmpty(btnTanggalMulai.getText().toString())) {
                    LocalDate tanggalMulai = UtilDate.toDate(btnTanggalMulai.getText().toString());
                    Calendar c = Calendar.getInstance();
                    c.setTime(tanggalMulai.toDate());
                    dpp.setMinDate(c);

                }

                dpp.show(getFragmentManager(), "tag");
                dpp.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        try {
                            month++;
                            LocalDate dt = UtilDate.mergeDate(year, month, day);
                            String tanggal = dt.toString(UtilDate.DATE_PATTERN_DISPLAY_1);

                            btnTanggalBerakhir.setText(tanggal);

                            fillDetailCuti();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }

    private void fillTanggalBerakhir(LocalDate tanggalMulaiDate) {
        try {
            String tanggalMulai = tanggalMulaiDate.toString(UtilDate.DATE_PATTERN_DISPLAY_1);

            if (!btnTanggalBerakhir.getText().toString().isEmpty()) {
                LocalDate tanggalBerakhirDate = UtilDate.toDate(btnTanggalBerakhir.getText().toString());

                if (tanggalMulaiDate.isAfter(tanggalBerakhirDate)) {
                    btnTanggalBerakhir.setText(tanggalMulai);
                }
            } else {
                btnTanggalBerakhir.setText(tanggalMulai);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fillJumlahHari() {
        try {
            if (!TextUtils.isEmpty(btnTanggalMulai.getText().toString()) &&
                    !TextUtils.isEmpty(btnTanggalBerakhir.getText().toString())) {

                LocalDate tanggalMulai = UtilDate.toDate(btnTanggalMulai.getText().toString());
                LocalDate tanggalBerakhir = UtilDate.toDate(btnTanggalBerakhir.getText().toString());

                int dayDiff = UtilDate.dayDiff(tanggalMulai, tanggalBerakhir);
                dayDiff++;

                txtJumlahHari.setText(dayDiff + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillJumlahCuti(final boolean reviewed) {
        try {
            MsCuti msCuti = new Select().from(MsCuti.class)
                    .where(Condition.column(MsCuti$Table.KODECUTI).eq(btnTipeCuti.getTag().toString()))
                    .querySingle();

            if (msCuti == null) {
                return;
            }
            String isLihatCalendar = msCuti.IsLihatCalendar;
            String isSetengahHari = msCuti.IsSetengahHari;

            if (isSetengahHari.equalsIgnoreCase("true")) {
                txtJumlahCuti.setText(txtMaxCuti.getText().toString());

                if(!reviewed) {
                    fillJumlahPotongCuti();
                    fillSisaCuti();
                }
                return;
            }

            if (isLihatCalendar.equalsIgnoreCase("false")) {
                txtJumlahCuti.setText(txtJumlahHari.getText().toString());

                if(!reviewed) {
                    fillJumlahPotongCuti();
                    fillSisaCuti();
                }
                return;
            }

            HashMap<String, String> map = fillMapJumlahCuti();
            WebServices ws = new WebServices(
                    getActivity(),
                    "GetPotongCuti",
                    map,
                    getActivity().getResources().getStringArray(R.array.GetPotongCuti_post),
                    getActivity().getResources().getStringArray(R.array.GetPotongCuti_get)
            ) {
                @Override
                protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                    try {
                        if (arrList != null) {
                            String potongCuti = arrList.get(0).get("PotongCuti");
                            txtJumlahCuti.setText(potongCuti);

                            if(!reviewed) {
                                fillJumlahPotongCuti();
                                fillSisaCuti();
                            }
                        } else {
                            Snackbar.make(getView(), R.string.gagal_hitung_potong_cuti, Snackbar.LENGTH_INDEFINITE)
                                    .setAction(R.string.retry, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fillJumlahCuti(inReviewed);
                                        }
                                    }).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onFailed(String message) {
                    try {
                        Snackbar.make(getView(), R.string.gagal_hitung_potong_cuti, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        fillJumlahCuti(inReviewed);
                                    }
                                }).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected void onCancel() {

                }
            };
            ws.execute();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void fillJumlahPotongCuti() {
        try {
            MsCuti msCuti = new Select().from(MsCuti.class)
                    .where(Condition.column(MsCuti$Table.KODECUTI).eq(btnTipeCuti.getTag().toString()))
                    .querySingle();

            if (msCuti == null)
                return;

            String isPotong = msCuti.IsPotongCuti;
            if (isPotong.equalsIgnoreCase("false")) {
                txtJumlahPotongCuti.setText("0");
                return;
            }

            double jumlahCuti = Double.parseDouble(txtJumlahCuti.getText().toString());
            txtJumlahPotongCuti.setText(jumlahCuti + "");

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (txtJumlahCuti.getText().toString().isEmpty()) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                /**Write your logic here **/
//
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            MsCuti msCuti = new Select().from(MsCuti.class)
//                                    .where(Condition.column(MsCuti$Table.KODECUTI).eq(btnTipeCuti.getTag().toString()))
//                                    .querySingle();
//
//                            String isPotong = msCuti.IsPotongCuti;
//                            if (isPotong.equalsIgnoreCase("false")) {
//                                txtJumlahPotongCuti.setText("0");
//                                return;
//                            }
//
//                            double jumlahCuti = Double.parseDouble(txtJumlahCuti.getText().toString());
//                            txtJumlahPotongCuti.setText(jumlahCuti + "");
//
//                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//            }
//        }).start();
    }

    private void fillSisaCuti() {
        try {
            if (isLoad) {
                double cuti = Double.parseDouble(txtCuti.getText().toString());
                double ambilCuti = Double.parseDouble(txtJumlahPotongCuti.getText().toString());
                double sisa = cuti;
                cuti = sisa + ambilCuti;

                txtSisaCuti.setText(sisa + "");
                txtCuti.setText(cuti + "");

                isLoad = false;
                return;
            }

            double cuti = Double.parseDouble(txtCuti.getText().toString());
            double ambilCuti = Double.parseDouble(txtJumlahPotongCuti.getText().toString());
            double sisa = cuti - ambilCuti;

            txtSisaCuti.setText(sisa + "");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

//        try {
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    while (txtJumlahPotongCuti.getText().toString().isEmpty()) {
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    /**Write your logic here **/
//
//                    getActivity().runOnUiThread(new Runnable() {
//
//                        /** If needed to update the view**/
//                        @Override
//                        public void run() {
//
////                            if (action != UtilBundle.ACTION_BUNDLE_NEW) {
//                            if (isLoad) {
//                                double cuti = Double.parseDouble(txtCuti.getText().toString());
//                                double ambilCuti = Double.parseDouble(txtJumlahPotongCuti.getText().toString());
//                                double sisa = cuti;
//                                cuti = sisa + ambilCuti;
//
//                                txtSisaCuti.setText(sisa + "");
//                                txtCuti.setText(cuti + "");
//
//                                isLoad = false;
//                                return;
//                            }
//
//                            double cuti = Double.parseDouble(txtCuti.getText().toString());
//                            double ambilCuti = Double.parseDouble(txtJumlahPotongCuti.getText().toString());
//                            double sisa = cuti - ambilCuti;
//
//                            txtSisaCuti.setText(sisa + "");
//
//                        }
//                    });
//                }
//            }).start();
//
//
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
    }

    private void fillCuti(String idKary) {
        HashMap<String, String> map = fillMapCuti(idKary);

        WebServices ws = new WebServices(
                getActivity(),
                "GetSisaCuti",
                map,
                getActivity().getResources().getStringArray(R.array.GetSisaCuti_post),
                getActivity().getResources().getStringArray(R.array.GetSisaCuti_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                showProgress(false);
                if (arrList != null) {
                    try {
                        String sisaCuti = arrList.get(0).get("SisaCuti");
                        txtCuti.setText(sisaCuti);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    snackbarRetry(getString(R.string.gagal_mendapatkan_sisa_cuti));
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    showProgress(false);
                    snackbarRetry(getString(R.string.gagal_mendapatkan_sisa_cuti));
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

    private void snackbarRetry(String message){
        message = TextUtils.isEmpty(message) ? getString(R.string.check_your_connection) : message;

        Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initLoad();
                    }
                }).show();
    }
    private HashMap<String, String> fillMapCuti(String idKary) {
        HashMap<String, String> map = new HashMap<>();
        map.put("IdKaryawan", idKary);

        return map;
    }

    private HashMap<String, String> fillMapJumlahCuti() {
        HashMap<String, String> map = new HashMap<>();
        map.put("CutiFrom", UtilDate.formatServer(btnTanggalMulai.getText().toString()));
        map.put("CutiTo", UtilDate.formatServer(btnTanggalBerakhir.getText().toString()));

        return map;
    }

    public void onSave() {
        UtilDate.setAutoDateTime(this.getActivity());
        getKaryawanStatusAllowed(Var.SAVE);
    }

    private void onSaveToServer() {

        HashMap<String, String> map = fillMap();

        SysAuditLog sysAuditLog = new SysAuditLog();
        map = sysAuditLog.create(getActivity(), R.array.DoSaveAbsensiCuti_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoSaveAbsensiCuti",
                map,
                getResources().getStringArray(R.array.DoSaveAbsensiCuti_post),
                getResources().getStringArray(R.array.DoSaveAbsensiCuti_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String result = arrList.get(0).get(getResources().getStringArray(R.array.DoSaveAbsensiCuti_get)[0]);

                if (result.equalsIgnoreCase(BaseActivity.INVALID_FLAG + "")) {
                    Snackbar.make(getView(), getString(R.string.failed_create_form), Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(getView(), getString(R.string.success_create_form), Snackbar.LENGTH_SHORT).show();
                    mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                    disableView();
                    noTransaksi = result;
                    ((CutiActivity)getActivity()).setSaveSuccess(true);
                    sendEmail();
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
//                                            CutiFragment.this.sendEmail();
//                                        }
//                                    })
//                            .setCancelable(false)
//                            .show();
//                }
            }
        }.sendEmail(getActivity(),
                noTransaksi,
                "DoSendEmailCreateCuti",
                R.array.DoSendEmailCreateCuti_post,
                R.array.DoSendEmailCreateCuti_get,
                Email.IS_NOT_APPROVAL);

    }

    private void disableView() {
        btnTipeCuti.setEnabled(false);
        btnTanggalMulai.setEnabled(false);
        btnTanggalBerakhir.setEnabled(false);
        txtAlasan.setEnabled(false);
    }

    private void enableView() {
        btnTipeCuti.setEnabled(true);
        btnTanggalMulai.setEnabled(true);
        btnTanggalBerakhir.setEnabled(true);
        txtAlasan.setEnabled(true);


    }

    public void onEdit() {
        enableView();
        mListener.setAction(UtilBundle.ACTION_BUNDLE_UPDATE);
    }

    public void onDelete() {

        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);
        map.put("ModifyBy", User.getUserID());
        map.put("ModifyDate", UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER));

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoDeleteAbsensiCuti",
                map,
                getResources().getStringArray(R.array.DoDeleteAbsensiCuti_post),
                getResources().getStringArray(R.array.DoDeleteAbsensiCuti_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String result = arrList.get(0).get(getResources().getStringArray(R.array.DoDeleteAbsensiCuti_get)[0]);

                if (result.equalsIgnoreCase("-1")) {
                    Snackbar.make(getView(), getString(R.string.failed_delete_form), Snackbar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.success_delete_form), Toast.LENGTH_SHORT).show();
                    mListener.setAction(UtilBundle.ACTION_BUNDLE_EDIT);
                    disableView();
                    noTransaksi = result;
                    ((CutiActivity)getActivity()).onDeleteCompleted();

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

    public void onUpdate() {
        UtilDate.setAutoDateTime(this.getActivity());
        getKaryawanStatusAllowed(Var.UPDATE);
    }

    public void onUpdateToServer(){

        HashMap<String, String> map = fillMap();
        SysAuditLog sysAuditLog = new SysAuditLog();
        map = sysAuditLog.create(getActivity(), R.array.DoSaveAbsensiCuti_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoUpdateAbsensiCuti",
                map,
                getResources().getStringArray(R.array.DoUpdateAbsensiCuti_post),
                getResources().getStringArray(R.array.DoUpdateAbsensiCuti_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String result = arrList.get(0).get(getResources().getStringArray(R.array.DoUpdateAbsensiCuti_get)[0]);

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

    public void onLoad(String noTransaksi) {
        if (!isLoad)
            return;

        this.noTransaksi = noTransaksi;
        disableView();
        showProgress(true);

        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);

        WebServices ws = new WebServices(getActivity(),
                "LoadAbsensiCuti",
                map,
                getResources().getStringArray(R.array.LoadAbsensiCuti_post),
                getResources().getStringArray(R.array.LoadAbsensiCuti_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    showProgress(false);

                    if (arrList != null) {
                        fillKomponen(arrList);
                        checkOptionMenu();
                        mListener.setAction(action);
                    }
                    else {
                        snackbarRetry(getString(R.string.gagal_load_data));
                        mListener.setAction(UtilBundle.ACTION_BUNDLE_FAIL_LOADING);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    snackbarRetry(getString(R.string.gagal_load_data));
                    mListener.setAction(UtilBundle.ACTION_BUNDLE_FAIL_LOADING);
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


    public void onResendMail() {
        sendEmail();
    }


    private void checkOptionMenu() {
        if (action == UtilBundle.ACTION_BUNDLE_NEW)
            mListener.setAction(action);
    }

    public ArrayList<HashMap<String, String>> getList() {
        return arrList;
    }

    private void fillKomponen(ArrayList<HashMap<String, String>> arrList) {
        this.arrList = arrList;

        HashMap<String, String> map = arrList.get(0);

        String kodeTipeForm = map.get("TipeCuti");
        btnTipeCuti.setText(adapterTipeCuti.getValueById(kodeTipeForm));
        btnTipeCuti.setTag(kodeTipeForm);

        txtApprover.setText(map.get("NamaApprover1"));
        txtAlasan.setText(map.get("Alasan"));
        btnTanggalMulai.setText(UtilDate.parseUTC(map.get("CutiFrom")).toString(UtilDate.DATE_PATTERN_DISPLAY_1));
        btnTanggalBerakhir.setText(UtilDate.parseUTC(map.get("CutiTo")).toString(UtilDate.DATE_PATTERN_DISPLAY_1));
        txtCuti.setText(map.get("SisacCutiOutstanding"));

        fillDetailTipeCuti(kodeTipeForm);
        if (isReviewed(map)) {
            fillDetailCutiReviewed(map);
//            mListener.setAction(UtilBundle.ACTION_BUNDLE_NOT_EDIT);
        } else {
            fillDetailCuti();
        }

    }


    private boolean isReviewed(HashMap<String, String> map) {
        boolean approved = Boolean.parseBoolean(map.get("IsAppHead"));
        boolean rejected = Boolean.parseBoolean(map.get("IsRejectHead"));

        if (approved || rejected)
            return true;
        else
            return false;
    }

    private boolean validate() {

        if (!validateEmptyField()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_empty_field), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!validateJumlahHari()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_jumlah_hari_batas), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!validateSummaryPeriode()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_periode), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!validateTipeKerja()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_tipe_kerja), Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!validateSetengahHari()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_jumlah_hari_batas), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!validateSisaCuti()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_sisa_cuti), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private boolean validateTipeKerja() {
        try {
            SetVar sv = new Select().from(SetVar.class)
                    .where(Condition.column(SetVar$Table.PARAMETERCODE).eq("TpKerjaShift"))
                    .querySingle();

            Employee employee = new Select().from(Employee.class)
                    .querySingle();

            if (!employee.KdTpKerja.equalsIgnoreCase(sv.Value))
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validateSummaryPeriode() {

        try {
            SetVar setVar = new Select().from(SetVar.class)
                    .where(Condition.column(SetVar$Table.PARAMETERCODE).eq("BatasForm"))
                    .querySingle();

            int tanggalBatas = Integer.parseInt(setVar.Value);

            LocalDate dateFrom = UtilDate.toDate(btnTanggalMulai.getText().toString());
            LocalDate today = UtilDate.getDate();
            LocalDate dateBatas = today.withDayOfMonth(tanggalBatas);

//            today = today.withDayOfMonth(1);
            long count;

            count = cekDiSummaryPeriode(dateFrom);

            if (count != 0) {
                Employee employee = new Select().from(Employee.class).querySingle();
                boolean isAllow = employee.IsAllowBatasForm;
                return isAllow;
            }

            boolean valid;
            int diffMonth = UtilDate.monthDiff(today, dateFrom);
            if (diffMonth >= 0) {
                valid = true;
            }
            else if (diffMonth == -1){
                if (today.compareTo(dateBatas) <= 0)
                    valid = true;
                else
                    valid = false;
            }
            else
                valid = false;

            if (!valid) {
                Employee employee = new Select().from(Employee.class).querySingle();
                boolean isAllow = employee.IsAllowBatasForm;
                return isAllow;
            }
            return valid;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }


    private long cekDiSummaryPeriode(LocalDate myDate) {
        long count = 0;
        try {
            count = new Select()
                    .from(SummaryPeriode.class)
                    .where(Condition.column(SummaryPeriode$Table.BULAN).eq(myDate.getMonthOfYear()),
                            Condition.column(SummaryPeriode$Table.TAHUN).eq((myDate.getYear())))
                    .query().getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }



    private MsCuti getMsCuti() {
        MsCuti msCuti = new Select().from(MsCuti.class)
                .where(Condition.column(MsCuti$Table.KODECUTI).eq(btnTipeCuti.getTag().toString()))
                .querySingle();
        return msCuti;
    }

    private boolean validateJumlahHari() {
        MsCuti msCuti = getMsCuti();

        double maxCuti = Double.parseDouble(msCuti.JumlahHari);
        double ambilCuti = Double.parseDouble(txtJumlahCuti.getText().toString());

        if (ambilCuti > maxCuti) {
            lblJumlahCuti.setErrorEnabled(true);
            lblJumlahCuti.setError("Max cuti " + maxCuti + " hari");
            lblJumlahCuti.setFocusableInTouchMode(true);
            return false;
        }
        lblJumlahCuti.setFocusableInTouchMode(false);
        lblJumlahCuti.setError(null);
        lblJumlahCuti.setErrorEnabled(false);
        return true;
    }

    private boolean validateSetengahHari() {
        MsCuti msCuti = getMsCuti();
        double jumHari = Double.parseDouble(txtJumlahHari.getText().toString());

        if (msCuti.IsSetengahHari.equalsIgnoreCase("true") && jumHari > 1) {
            return false;
        }
        return true;
    }

    private boolean validateSisaCuti() {
        double sisaCuti = Double.parseDouble(txtSisaCuti.getText().toString());

        if (sisaCuti < 0) {
            lblSisaCuti.setErrorEnabled(true);
            lblSisaCuti.setError("Sisa Cuti tidak mencukupi");
            return false;
        }
        lblSisaCuti.setError(null);
        lblSisaCuti.setErrorEnabled(false);

        return true;
    }


    private HashMap<String, String> fillMap() {

        String userId = User.getUserID();
        String empCode = User.getEmpCode();
        String noTransaksi = this.noTransaksi;
        String tanggalTransaksi = UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER);
        String kodeTransaksi = "CUTI";

        Condition condition = Condition.column(Employee$Table.IDKARY).eq(empCode);
        Employee emp = new Select().from(Employee.class).where(condition).querySingle();

        HashMap<String, String> map = new HashMap<>();


        map.put("UserId", userId);
        map.put("NoTransaksi", noTransaksi);
        map.put("TanggalTransaksi", tanggalTransaksi);
        map.put("KodeTransaksi", kodeTransaksi);
        map.put("TipeCuti", btnTipeCuti.getTag().toString());
        map.put("CutiFrom", UtilDate.formatServer(btnTanggalMulai.getText().toString()));
        map.put("CutiTo", UtilDate.formatServer(btnTanggalBerakhir.getText().toString()));
        map.put("CurrentCuti", txtCuti.getText().toString());
        map.put("LatestCuti", txtSisaCuti.getText().toString());
        map.put("IsCancel", "0");
        map.put("JmlhPtgCuti", txtJumlahPotongCuti.getText().toString());
        map.put("Alasan", txtAlasan.getText().toString());
        map.put("KdLvlKary", User.getKodeLevel());
        map.put("KodeCabang", emp.KodeCabang);
        map.put("KodeRangking", emp.KdRanking);
        map.put("KdTpKerja", emp.KdTpKerja);
        map.put("IdKary", User.getEmpCode());


        map = fillApprover(map);


        map.put("IsAktifL1", "0");
        map.put("IsAktifL2", "0");
        map.put("IsAktifL3", "0");
        map.put("IsAktifL4", "0");
        map.put("IsAktifL5", "0");
        map.put("IsAppHead", "0");
        map.put("IdAppHead", "");
        map.put("TglAppHEad", "");
        map.put("IsRejectHead", "0");
        map.put("IsAppHRD", "0");
        map.put("IdAppHRD", "");
        map.put("TglAppHRD", "");
        map.put("IsRejectHRD", "0");
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

    private HashMap<String, String> fillApprover(HashMap<String, String> map) {
        List<Head> listHead = new Select().from(Head.class).queryList();

        map.put("L1KdLvl", "");
        map.put("L2KdLvl", "");
        map.put("L3KdLvl", "");
        map.put("L4KdLvl", "");
        map.put("L5KdLvl", "");

        String key = "";

        for (int i = 0; i < listHead.size(); i++) {
            key = "L" + (i + 1) + "KdLvl";
            map.put(key, listHead.get(i).HeadKodeLevel);
        }

        return map;
    }


    private boolean validateEmptyField() {
        if (
                isEmptyField(txtCuti, lblCuti) || isEmptyField(btnTipeCuti) ||
                isEmptyField(txtMaxCuti, lblMaxCuti) || isEmptyField(btnTanggalMulai) ||
                isEmptyField(btnTanggalBerakhir) || isEmptyField(txtJumlahHari, lblJumlahHari) ||
                isEmptyField(txtJumlahCuti, lblJumlahCuti) || isEmptyField(txtJumlahPotongCuti, lblJumlahPotongCuti) ||
                isEmptyField(txtAlasan, lblAlasan) || isEmptyField(txtSisaCuti, lblSisaCuti)
                ) {

             return false;
        } else
            return true;

    }


    public boolean isEmptyField(EditText view, TextInputLayout label) {
        if (view.getVisibility() == View.VISIBLE) {
            if (view.getText().toString().isEmpty()) {
                label.setErrorEnabled(true);
                label.setError(getString(R.string.prompt_error_empty_field));
                return true;

            }
            else {
                label.setError(null);
                label.setErrorEnabled(false);
                return false;
            }
        }
        label.setError(null);
        label.setErrorEnabled(false);
        return false;
    }


    public  boolean isEmptyField(TextView view) {
        if (view.getVisibility() == View.VISIBLE) {
            if (view.getText().toString().isEmpty()) {
                view.setError(null);
                return true;
            }
            view.setError(null);
            return false;
        }
        view.setError(null);
        return false;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onCutiFormListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onCutiFormListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    private void getKaryawanStatusAllowed(final int isSave) {
        RequestBody formBody = new FormBody.Builder()
                .add("IdKary", User.getEmpCode())
                .build();
        HrisService.createHrisService(formBody)
                .GetKaryawanStatusAllowed("GetKaryawanStatusAllowed")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<EmployeeModel>>() {
                    @Override
                    public void onCompleted() {
                        if (!validate())
                            return;

                        if (isSave == Var.SAVE)
                            onSaveToServer();
                        else
                            onUpdateToServer();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Snackbar.make(getView(), R.string.failed_create_form, Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<EmployeeModel> employeeModels) {

                        if (employeeModels.size() != 0) {
                            EmployeeModel employeeModel = employeeModels.get(0);
                            Employee employee = new Select().from(Employee.class).querySingle();
                            employee.IsAllowCutiMin = Boolean.parseBoolean(employeeModel.IsAllowCutiMin);
                            employee.IsAllowBatasForm = Boolean.parseBoolean(employeeModel.IsAllowBatasForm);
                            employee.IsApprovalKhusus = Boolean.parseBoolean(employeeModel.IsApprovalKhusus);
                            employee.save();
                        }

                    }
                });
    }

    public interface onCutiFormListener {
        public void setAction(int action);
    }
}
