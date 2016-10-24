package aca.com.hris.Fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import aca.com.hris.AbsensiActivity;
import aca.com.hris.Adapter.ApprovalCutiAdapter;
import aca.com.hris.Adapter.MyCursorAdapter;
import aca.com.hris.BaseActivity;
import aca.com.hris.Database.Employee;
import aca.com.hris.Database.Employee$Table;
import aca.com.hris.Database.FormDetail;
import aca.com.hris.Database.FormDetail$Table;
import aca.com.hris.Database.FormHead;
import aca.com.hris.Database.FormHead$Table;
import aca.com.hris.Database.Head;
import aca.com.hris.Database.ImageSource;
import aca.com.hris.Database.ImageSource$Table;
import aca.com.hris.Database.SetVar;
import aca.com.hris.Database.SetVar$Table;
import aca.com.hris.Database.StandardField;
import aca.com.hris.Database.StandardField$Table;
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
import aca.com.hris.Util.UtilImage;
import aca.com.hris.Util.UtilService;
import aca.com.hris.Util.Var;
import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.EasyImage;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.os.Build.VERSION_CODES.M;

/**
 * A placeholder fragment containing a simple view.
 */
public class AbsensiFragment extends BaseFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    final String[] irisan = {""};
    @Bind(R.id.txtTanggalTranskasi)
    EditText txtTanggalTranskasi;
    @Bind(R.id.txtApprover)
    EditText txtApprover;
    @Bind(R.id.txtKeterangan)
    TextView txtKeterangan;
    @Bind(R.id.btnTanggalMulai)
    Button btnTanggalMulai;
    @Bind(R.id.btnTanggalBerakhir)
    Button btnTanggalBerakhir;
    @Bind(R.id.txtJumlahHari)
    EditText txtJumlahHari;
    @Bind(R.id.lblJumlahHari)
    TextInputLayout lblJumlahHari;
    @Bind(R.id.btnJamMulai)
    Button btnJamMulai;
    @Bind(R.id.btnJamBerakhir)
    Button btnJamBerakhir;
    @Bind(R.id.txtAlasan)
    EditText txtAlasan;
    @Bind(R.id.lblAlasan)
    TextInputLayout lblAlasan;
    @Bind(R.id.btnPickImage)
    Button btnPickImage;
    @Bind(R.id.imgSuratDokter)
    ImageView imgSuratDokter;
    @Bind(R.id.viewScrollView)
    NestedScrollView viewScrollView;
    @Bind(R.id.viewSuratDokter)
    LinearLayout viewSuratDokter;
    @Bind(R.id.btnJenisForm)
    Button btnJenisForm;
    @Bind(R.id.btnTipeForm)
    Button btnTipeForm;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnRetry)
    Button btnRetry;

    private onAbsensiFormListener mListener;
    private String noTransaksi = BaseActivity.INVALID_FLAG + "";
    private boolean isEmptyKomponen = true;
    private AlertDialog.Builder jenisFormBuilder;
    private MyCursorAdapter jenisFormAdapter = null;
    private AlertDialog.Builder tipeFormBuilder;
    private MyCursorAdapter tipeFormAdapter;
    private ArrayList<HashMap<String, String>> arrList;
    private int action;
    private boolean isLoad;
    private Object o;

    public AbsensiFragment() {

    }

    public static AbsensiFragment newInstance(String noTransaksi, int action, boolean isLoad) {
        Bundle args = new Bundle();
        args.putString(ApprovalCutiAdapter.BUNDLE_NO_TRANSAKSI, noTransaksi);
        args.putInt(UtilBundle.ACTION_BUNDLE, action);
        args.putBoolean(UtilBundle.BUNDLE_ON_LOAD, isLoad);

        AbsensiFragment fragment = new AbsensiFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public ArrayList<HashMap<String, String>> getList() {
        return arrList;
    }

    @Override
    protected void onCreate() {
        initLoad();
    }

    private void initLoad() {
        fillApprover();
        bindJenisFormulir();
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

    private void fillApprover() {
        try {
            Head head = new Select().from(Head.class).querySingle();
            String approver = head.FullName;

            txtApprover.setText(approver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void bindJenisFormulir() {
        btnJenisForm.setText("");
        btnJenisForm.setTag("");

        Condition conditions = Condition.column(StandardField$Table.FIELDCODE).eq("GrpFormAbsensi");
        Cursor cAbsensiFormTempHead = new Select().from(StandardField.class).where(conditions).query();

        jenisFormBuilder = new AlertDialog.Builder(getActivity());
        jenisFormAdapter = new MyCursorAdapter(getActivity(), cAbsensiFormTempHead, false, StandardField$Table.FIELDCODEDT, StandardField$Table.FIELDNAMEDT);
        jenisFormBuilder.setAdapter(jenisFormAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = ((Cursor) jenisFormAdapter.getItem(which)).getString(jenisFormAdapter.getCursor().getColumnIndex(StandardField$Table.FIELDCODEDT));
                String value = ((Cursor) jenisFormAdapter.getItem(which)).getString(jenisFormAdapter.getCursor().getColumnIndex(StandardField$Table.FIELDNAMEDT));
                btnJenisForm.setText(value);
                btnJenisForm.setTag(id);
                bindTipeFormulir(id);
            }
        });
        jenisFormBuilder.setTitle("Pilih Form");
    }


    private void bindTipeFormulir(String grpFormAbsensi) {
        btnTipeForm.setText("");
        btnTipeForm.setTag("");

        Condition condition = Condition.column(FormHead$Table.GRPFORMABSENSI).eq(grpFormAbsensi);
        Cursor cursor = new Select().from(FormHead.class).where(condition).query();

        tipeFormBuilder = new AlertDialog.Builder(getActivity());
        tipeFormAdapter = new MyCursorAdapter(getActivity(), cursor, false, FormHead$Table.KODEFORM, FormHead$Table.NAMAFORM);
        tipeFormBuilder.setAdapter(tipeFormAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = ((Cursor) tipeFormAdapter.getItem(which)).getString(tipeFormAdapter.getCursor().getColumnIndex(FormHead$Table.KODEFORM));
                String value = ((Cursor) tipeFormAdapter.getItem(which)).getString(tipeFormAdapter.getCursor().getColumnIndex(FormHead$Table.NAMAFORM));
                btnTipeForm.setText(value);
                btnTipeForm.setTag(id);

                fillKeterangan(id);
                showKomponen(id);
                emptyKomponen();
            }
        });
        tipeFormBuilder.setTitle("Pilih Form");

    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_absensi_form;
    }


    @Override
    protected void initInstance(View view) {
    }

    @Override
    protected void init(@NonNull View view) {

    }


    @Override
    protected void registerListener(View view) {
        btnJenisForm.setOnClickListener(btnJenisFormListener());
        btnTipeForm.setOnClickListener(btnTipeFormListener());

        btnTanggalMulai.setOnClickListener(btnTanggalMulaiListener());
        btnTanggalBerakhir.setOnClickListener(btnTanggalBerakhirListener());

        btnTanggalMulai.addTextChangedListener(btnTanggalTextListener());
        btnTanggalBerakhir.addTextChangedListener(btnTanggalTextListener());

        btnJamMulai.setOnClickListener(btnJamMasukListener());
        btnJamBerakhir.setOnClickListener(btnJamKeluarListener());
        btnPickImage.setOnClickListener(btnPickImageListener());
        imgSuratDokter.setOnClickListener(imgSuratDokterListener());

        btnRetry.setOnClickListener(btnRetryListener());

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


    private void disableView() {

        btnJenisForm.setEnabled(false);
        btnTipeForm.setEnabled(false);
        btnTanggalMulai.setEnabled(false);
        btnTanggalBerakhir.setEnabled(false);
        btnJamMulai.setEnabled(false);
        btnJamBerakhir.setEnabled(false);


        txtAlasan.setEnabled(false);

        viewScrollView.setEnabled(false);
        viewSuratDokter.setEnabled(false);

        btnPickImage.setClickable(false);

    }

    private void enableView() {
        btnJenisForm.setEnabled(true);
        btnTipeForm.setEnabled(true);
        btnTanggalMulai.setEnabled(true);
        btnTanggalBerakhir.setEnabled(true);
        btnJamMulai.setEnabled(true);
        btnJamBerakhir.setEnabled(true);

        txtAlasan.setEnabled(true);

        viewScrollView.setEnabled(true);
        viewSuratDokter.setEnabled(true);

        btnPickImage.setClickable(true);
    }

    private TextWatcher btnTanggalTextListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(btnTanggalMulai.getText().toString()) &&
                        !TextUtils.isEmpty(btnTanggalBerakhir.getText().toString())) {
                    LocalDate tanggalMulai = UtilDate.toDate(btnTanggalMulai.getText().toString());
                    LocalDate tanggalBerakhir = UtilDate.toDate(btnTanggalBerakhir.getText().toString());

                    int dayDiff = UtilDate.dayDiff(tanggalMulai, tanggalBerakhir);
                    dayDiff++;
                    txtJumlahHari.setText(dayDiff + "");
                }
            }
        };
    }

    private View.OnClickListener btnPickImageListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooser(AbsensiFragment.this, "Pick an ImageSource", true);
            }
        };
    }

    private View.OnClickListener imgSuratDokterListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilImage.popupImage(getActivity(), imgSuratDokter.getDrawable());
            }
        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                Snackbar.make(getView(), getResources().getString(R.string.prompt_error_photo), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource) {
                onPhotoReturned(file);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource) {
                if (imageSource == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getActivity());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    private void onPhotoReturned(final File photoFile) {
        Picasso.with(getActivity())
                .load(photoFile)
                .fit()
                .centerCrop()
                .into(imgSuratDokter, new Callback() {
                    @Override
                    public void onSuccess() {

                        new Delete().from(ImageSource.class)
                                .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.SURAT_DOKTER))
                                .queryClose();

                        ImageSource cImage = new ImageSource();
                        cImage.key = ImageSource.SURAT_DOKTER;
                        cImage.imageName = photoFile.getName();
                        cImage.imagePath = photoFile.getPath();
                        cImage.save();
                    }

                    @Override
                    public void onError() {
                        new Delete().from(ImageSource.class)
                                .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.SURAT_DOKTER))
                                .queryClose();

                    }
                });
    }


    private View.OnClickListener btnJenisFormListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisFormBuilder.show();
            }
        };
    }

    private View.OnClickListener btnTipeFormListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tipeFormBuilder == null) {
                    Toast.makeText(getActivity(), "Silahkan pilih jenis form", Toast.LENGTH_SHORT).show();
                    return;
                }

                tipeFormBuilder.show();
            }
        };
    }

    private AdapterView.OnItemSelectedListener spnJenisFormulirListener() {
        return new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String grpFormAbsensi = Utility.getIDSpinner(spnJenisFormulir);
//                bindTipeFormulir(grpFormAbsensi);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }


    private AdapterView.OnItemSelectedListener spnTipeFormulirListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
//                    String kodeForm = Utility.getIDSpinner(spnTipeFormulir);

//                    fillKeterangan(kodeForm);
//                    showKomponen(kodeForm);
                    if (isEmptyKomponen) {
                        emptyKomponen();
                    } else {
                        isEmptyKomponen = true;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }


    private void fillKeterangan(String kodeForm) {
        Condition condition = Condition.column(FormHead$Table.KODEFORM).eq(kodeForm);
        FormHead aFormHead = new Select().from(FormHead.class).where(condition).querySingle();

        String keterangan = aFormHead.Keterangan;
        txtKeterangan.setText(keterangan);

    }


    private View.OnClickListener btnTanggalMulaiListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocalDate date = LocalDate.now();
                int year = date.getYear();
                int month = date.getMonthOfYear();
                int day = date.getDayOfMonth();

//                Calendar calendar = Calendar.getInstance();
//                int year = calendar.get(Calendar.YEAR);
//                int month = calendar.get(Calendar.MONTH);
//                int day = calendar.get(Calendar.DAY_OF_MONTH);

                if (!btnTanggalMulai.getText().toString().isEmpty()) {
                    String tanggal = btnTanggalMulai.getText().toString();
                    LocalDate tanggalDate = UtilDate.toDate(tanggal);

                    year = tanggalDate.getYear();
                    month = tanggalDate.getMonthOfYear();
                    day = tanggalDate.getDayOfMonth();
                }

                DatePickerDialog dpp = DatePickerDialog.newInstance(
                        AbsensiFragment.this,
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

                            if (!btnTanggalBerakhir.getText().toString().isEmpty()) {
                                LocalDate tanggalBerakhirDate = UtilDate.toDate(btnTanggalBerakhir.getText().toString());

                                if (tanggalMulaiDate.isAfter(tanggalBerakhirDate)) {
                                    btnTanggalBerakhir.setText(tanggalMulai);
                                }
                            } else {
                                btnTanggalBerakhir.setText(tanggalMulai);
                            }

                            btnTanggalMulai.setText(tanggalMulai);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
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
                        AbsensiFragment.this,
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }

    private View.OnClickListener btnJamMasukListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnJamMulai.setError(null);

                int hour, minute;

                if (btnJamMulai.getText().toString().isEmpty()) {
                    hour = UtilDate.getTime().getHourOfDay();
                    minute = UtilDate.getTime().getMinuteOfHour();
                } else {
                    String jamMulai = btnJamMulai.getText().toString();
                    LocalTime localTime = UtilDate.toTime(jamMulai);
                    hour = localTime.getHourOfDay();
                    minute = localTime.getMinuteOfHour();
                }

                TimePickerDialog tpd = TimePickerDialog.newInstance(AbsensiFragment.this,
                        hour,
                        minute,
                        true);

                tpd.show(getFragmentManager(), "tag");
                tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                        try {
                            LocalTime timeJamMulai = UtilDate.mergeTime(hour, minute);
                            String jamMulai = timeJamMulai.toString(UtilDate.TIME_PATTERN);
                            btnJamMulai.setText(jamMulai);

                            if (btnJamBerakhir.getVisibility() == View.VISIBLE) {

                                if (!btnJamBerakhir.getText().toString().isEmpty()) {
                                    LocalTime timeJamBerakhir = UtilDate.toTime(btnJamBerakhir.getText().toString());

                                    if (timeJamMulai.isAfter(timeJamBerakhir)) {
                                        btnJamBerakhir.setText(jamMulai);
                                    }
                                } else {
                                    btnJamBerakhir.setText(jamMulai);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
    }


    private View.OnClickListener btnJamKeluarListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;

                if (btnJamMulai.getText().toString().isEmpty()) {
                    hour = UtilDate.getTime().getHourOfDay();
                    minute = UtilDate.getTime().getMinuteOfHour();
                } else {
                    String jamMulai = btnJamMulai.getText().toString();
                    LocalTime localTime = UtilDate.toTime(jamMulai);
                    hour = localTime.getHourOfDay();
                    minute = localTime.getMinuteOfHour();
                }

                TimePickerDialog tpd = TimePickerDialog.newInstance(AbsensiFragment.this,
                        hour,
                        minute,
                        true);

                tpd.show(getFragmentManager(), "tag");
                tpd.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hour, int minute) {
                        try {
                            LocalTime localTime = UtilDate.mergeTime(hour, minute);
                            String jamMulai = localTime.toString(UtilDate.TIME_PATTERN);
                            btnJamBerakhir.setText(jamMulai);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        };
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    private void showKomponen(String kodeForm) {
        FormHead formHead = new Select().from(FormHead.class)
                .where(Condition.column(FormHead$Table.KODEFORM).eq(kodeForm))
                .querySingle();


        Condition colKodeForm = Condition.column(FormDetail$Table.KODEFORM).eq(kodeForm);
        Cursor c = new Select().from(FormDetail.class).as("FD")
                .join(StandardField.class, Join.JoinType.INNER).as("SF")
                .on(
                        Condition.column(ColumnAlias.columnWithTable("FD", FormDetail$Table.KODEKOMPONEN))
                                .eq((ColumnAlias.columnWithTable("SF", StandardField$Table.FIELDCODEDT)))
                )
                .where(colKodeForm).query();

        c.moveToFirst();

        showPeriode(false, "");
        showAlasan(false, "");
        showJamMasuk(false, "");
        showJamKeluar(false, "");
        showImagePick(Boolean.parseBoolean(formHead.IsAttachMan));

        do {
            String caption = c.getString(c.getColumnIndex(FormDetail$Table.CAPTION));
            String fieldName = c.getString(c.getColumnIndex(StandardField$Table.FIELDNAMEDT));

            switch (fieldName) {
                case "Periode":
                    showPeriode(true, caption);
                    break;
                case "Alasan":
                    showAlasan(true, caption);
                    break;
                case "Jam Masuk":
                    showJamMasuk(true, caption);
                    break;
                case "Jam Keluar":
                    showJamKeluar(true, caption);
                    break;
            }

        } while (c.moveToNext());
    }

    private void emptyKomponen() {

        btnTanggalMulai.setText("");
        btnTanggalBerakhir.setText("");
        btnJamMulai.setText("");
        btnJamBerakhir.setText("");
        txtAlasan.setText("");
    }

    private void showPeriode(boolean show, String caption) {
        btnTanggalMulai.setVisibility(show ? View.VISIBLE : View.GONE);
        btnTanggalBerakhir.setVisibility(show ? View.VISIBLE : View.GONE);

        btnTanggalMulai.setHint(caption + " Mulai");
        btnTanggalBerakhir.setHint(caption + " Berakhir");

    }

    private void showJamMasuk(boolean show, String caption) {
        btnJamMulai.setVisibility(show ? View.VISIBLE : View.GONE);
        btnJamMulai.setHint(caption);
    }

    private void showJamKeluar(boolean show, String caption) {
        btnJamBerakhir.setVisibility(show ? View.VISIBLE : View.GONE);
        btnJamBerakhir.setHint(caption);
    }


    private void showAlasan(boolean show, String caption) {
        txtAlasan.setVisibility(show ? View.VISIBLE : View.GONE);
        lblAlasan.setHint(caption);
    }


    private void showImagePick(Boolean isAttachMan) {
        viewSuratDokter.setVisibility(isAttachMan == true ? View.VISIBLE : View.GONE);
        if (noTransaksi.isEmpty() || noTransaksi.equalsIgnoreCase(UtilBundle.BUNDLE_INVALID_FLAG + ""))
            return;

        String uri = UtilService.getImagesURL() + noTransaksi + ".jpg";
        Picasso.with(getActivity())
                .load(uri)
                .fit()
                .centerCrop()
                .into(imgSuratDokter, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

    }


    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onAbsensiFormListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onAbsensiFormListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

    }

    private void cekIrisan(final int action) {
        HashMap<String, String> map = fillMapCekIrisan();
        WebServices ws = new WebServices(
                getActivity(),
                "DoCekIrisan",
                map,
                getActivity().getResources().getStringArray(R.array.DoCekIrisan_post),
                getActivity().getResources().getStringArray(R.array.DoCekIrisan_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String irisan = "-1";

                    if (arrList != null) {

                        irisan = arrList.get(0).get("IRISAN");

                        if (validateIrisan(irisan)) {

                            if (action == Var.SAVE)
                                finallyDoSave();
                            else if (action == Var.UPDATE)
                                finallyDoUpdate();
                        } else
                            Snackbar.make(getView(), R.string.prompt_error_irisan, Snackbar.LENGTH_INDEFINITE)
                                    .show();
                    } else {
                        Snackbar.make(getView(), R.string.gagal_load_data, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cekIrisan(action);
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
                    Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cekIrisan(action);
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

    }

    private HashMap<String, String> fillMapCekIrisan() {
        HashMap<String, String> map = new HashMap<>();

        map.put("TanggalMulai", UtilDate.formatServer(btnTanggalMulai.getText().toString()));
        map.put("TanggalBerakhir", UtilDate.formatServer(btnTanggalBerakhir.getText().toString()));
        map.put("JamMulai", btnJamMulai.getText().toString());
        map.put("JamSelesai", btnJamBerakhir.getText().toString());
        map.put("TableName", "FORM");
        map.put("NoTransaksi", noTransaksi);
        map.put("IdKaryawan", User.getEmpCode());
        map.put("TipeForm", btnTipeForm.getTag().toString());

        return map;
    }

    public void onSave() {
        UtilDate.setAutoDateTime(this.getActivity());
        getKaryawanStatusAllowed(Var.SAVE);

/*
        if (!validate())
            return;

        cekIrisan(Var.SAVE);
*/
    }

    private void finallyDoSave() {
        HashMap<String, String> map = fillMap();
        SysAuditLog sysAuditLog = new SysAuditLog();
        map = sysAuditLog.create(getActivity(), R.array.DoSaveAbsensiForm_sysAuditLog, 3, map);


        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoSaveAbsensiForm",
                map,
                getResources().getStringArray(R.array.DoSaveAbsensiForm_post),
                getResources().getStringArray(R.array.DoSaveAbsensiForm_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String result = arrList.get(0).get(getResources().getStringArray(R.array.DoSaveAbsensiForm_get)[0]);

                    if (result.equalsIgnoreCase("-1")) {
                        Snackbar.make(getView(), getString(R.string.failed_create_form), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(getView(), getString(R.string.success_create_form), Snackbar.LENGTH_SHORT).show();
                        mListener.onSetAction(UtilBundle.ACTION_BUNDLE_EDIT);
                        disableView();
                        noTransaksi = result;

                        ((AbsensiActivity) getActivity()).setSaveSuccess(true);
                        sendEmail();
                        syncAttachment();
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

    private void finallyDoUpdate() {

        HashMap<String, String> map = fillMap();
        SysAuditLog sysAuditLog = new SysAuditLog();
        map = sysAuditLog.create(getActivity(), R.array.DoUpdateAbsensiForm_sysAuditLog, 3, map);

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoUpdateAbsensiForm",
                map,
                getResources().getStringArray(R.array.DoUpdateAbsensiForm_post),
                getResources().getStringArray(R.array.DoUpdateAbsensiForm_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String result = arrList.get(0).get(getResources().getStringArray(R.array.DoUpdateAbsensiForm_get)[0]);

                    if (result.equalsIgnoreCase("-1")) {
                        Snackbar.make(getView(), getString(R.string.failed_update_form), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(getView(), getString(R.string.success_update_form), Snackbar.LENGTH_SHORT).show();
                        mListener.onSetAction(UtilBundle.ACTION_BUNDLE_EDIT);
                        disableView();
                        noTransaksi = result;

                        syncAttachment();
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    Snackbar.make(getView(), getString(R.string.failed_update_form), Snackbar.LENGTH_SHORT).show();
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

    private boolean validateIrisan(String irisan) {
        int jumlahIrisan = Integer.parseInt(irisan);
        if (jumlahIrisan == -1 || jumlahIrisan > 0) {
            return false;
        }
        return true;

    }


    private void sendEmail() {
        new Email() {
            @Override
            public void resultSending(boolean success) {
//                String message = success ? getString(R.string.berhasil_kirim_email) : getString(R.string.gagal_kirim_email);
//                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

//                if (!success) {
//                    new AlertDialog.Builder(getActivity(), Var.dialogTheme)
//                            .setTitle(R.string.Email)
//                            .setMessage(R.string.gagal_kirim_email)
//                            .setPositiveButton(R.string.retry
//                                    , new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            AbsensiFragment.this.sendEmail();
//                                        }
//                                    })
//                            .setCancelable(false)
//                            .show();
//                }
            }
        }.sendEmail(getActivity(),
                noTransaksi,
                "DoSendEmailCreateAbsensi",
                R.array.DoSendEmailCreateAbsensi_post,
                R.array.DoSendEmailCreateAbsensi_get,
                Email.IS_NOT_APPROVAL);

    }

    public void onEdit() {
        enableView();
        mListener.onSetAction(UtilBundle.ACTION_BUNDLE_UPDATE);
    }

    public void onDelete() {

        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);
        map.put("ModifyBy", User.getUserID());
        map.put("ModifyDate", UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER));

        WebServicesNonQuery ws = new WebServicesNonQuery(getActivity(),
                "DoDeleteAbsensiForm",
                map,
                getResources().getStringArray(R.array.DoDeleteAbsensiForm_post),
                getResources().getStringArray(R.array.DoDeleteAbsensiForm_get),
                true
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String result = arrList.get(0).get(getResources().getStringArray(R.array.DoDeleteAbsensiForm_get)[0]);

                    if (result.equalsIgnoreCase("-1")) {
                        Snackbar.make(getView(), getString(R.string.failed_delete_form), Snackbar.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.success_delete_form), Toast.LENGTH_SHORT).show();
                        mListener.onSetAction(UtilBundle.ACTION_BUNDLE_EDIT);
                        disableView();
                        noTransaksi = result;
                        ((AbsensiActivity) getActivity()).onDeleteCompleted();
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

    public void onUpdate() {
        UtilDate.setAutoDateTime(this.getActivity());
        getKaryawanStatusAllowed(Var.UPDATE);
/*
        if (!validate())
            return;

        cekIrisan(Var.UPDATE);
*/

    }


    public void onLoad(String noTransaksi) {
        if (!isLoad)
            return;

        this.noTransaksi = noTransaksi;

        showProgress(true);
        disableView();
        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", noTransaksi);

        WebServices ws = new WebServices(getActivity(),
                "LoadAbsensiForm",
                map,
                getResources().getStringArray(R.array.LoadAbsensiForm_post),
                getResources().getStringArray(R.array.LoadAbsensiForm_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    showProgress(false);

                    if (arrList != null) {
                        fillKomponen(arrList);
                        checkOptionMenu();
                        mListener.onSetAction(action);
                    } else {
                        snackbarRetry(getString(R.string.gagal_load_data));
                        mListener.onSetAction(UtilBundle.ACTION_BUNDLE_FAIL_LOADING);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    mListener.onSetAction(UtilBundle.ACTION_BUNDLE_FAIL_LOADING);
                    snackbarRetry(getString(R.string.gagal_load_data));
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

    private void snackbarRetry(String message) {
        message = TextUtils.isEmpty(message) ? getString(R.string.check_your_connection) : message;

        Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initLoad();
                    }
                }).show();
    }


    private void checkOptionMenu() {
        if (action == UtilBundle.ACTION_BUNDLE_NEW)
            mListener.onSetAction(action);
    }


    private void fillKomponen(ArrayList<HashMap<String, String>> arrList) {
        this.arrList = arrList;

        HashMap<String, String> map = arrList.get(0);

        String kodeTipeForm = map.get("KdTpFormulir");
        String kodeJenisForm = getJenisFormulirPos(kodeTipeForm);

        btnJenisForm.setText(jenisFormAdapter.getValueById(kodeJenisForm));
        bindTipeFormulir(kodeJenisForm);
        btnTipeForm.setText(tipeFormAdapter.getValueById(kodeTipeForm));
        btnJenisForm.setTag(kodeJenisForm);
        btnTipeForm.setTag(kodeTipeForm);

        fillKeterangan(kodeTipeForm);
        showKomponen(kodeTipeForm);

        txtApprover.setText(map.get("NamaApprover1"));
        btnTanggalMulai.setText(UtilDate.parseUTC(map.get("DateFrom")).toString(UtilDate.DATE_PATTERN_DISPLAY_1));
        btnTanggalBerakhir.setText(UtilDate.parseUTC(map.get("DateTo")).toString(UtilDate.DATE_PATTERN_DISPLAY_1));
        btnJamMulai.setText(map.get("TimeFrom"));
        btnJamBerakhir.setText(map.get("TimeTo"));
        txtAlasan.setText(map.get("Alasan"));

//        if (isReviewed(map)){
//            mListener.onSetAction(UtilBundle.ACTION_BUNDLE_NOT_EDIT);
//        }
    }


    private boolean isReviewed(HashMap<String, String> map) {
        boolean approved = Boolean.parseBoolean(map.get("IsApproved"));
        boolean rejected = Boolean.parseBoolean(map.get("IsRejected"));

        if (approved || rejected)
            return true;
        else
            return false;
    }

    private String getJenisFormulirPos(String kodeForm) {
        FormHead formHead = new Select().from(FormHead.class)
                .where(Condition.column(FormHead$Table.KODEFORM).eq(kodeForm))
                .querySingle();

        String kodeTipeForm = formHead.GrpFormAbsensi;
        return kodeTipeForm;
    }

    private HashMap<String, String> fillMap() {

        String userId = User.getUserID();
        String empCode = User.getEmpCode();
        String noTransaksi = this.noTransaksi;
        String tanggalTransaksi = UtilDate.getDateTime().toString(UtilDate.DATE_TIME_PATTERN_SERVER);
        String kodeTransaksi = "ABFR";

        Condition condition = Condition.column(Employee$Table.IDKARY).eq(empCode);
        Employee emp = new Select().from(Employee.class).where(condition).querySingle();

        HashMap<String, String> map = new HashMap<>();

        map.put("UserId", userId);
        map.put("NoTransaksi", noTransaksi);
        map.put("TanggalTransaksi", tanggalTransaksi);
        map.put("KodeTransaksi", kodeTransaksi);
        map.put("KdTpFormulir", btnTipeForm.getTag().toString());
        map.put("DateFrom", UtilDate.formatServer(btnTanggalMulai.getText().toString()));
        map.put("DateTo", UtilDate.formatServer(btnTanggalBerakhir.getText().toString()));
        map.put("TimeFrom", btnJamMulai.getText().toString());
        map.put("TimeTo", btnJamBerakhir.getText().toString());
        map.put("Alasan", txtAlasan.getText().toString());
        map.put("KodeLevelKary", User.getKodeLevel());
        map.put("KodeCabang", emp.KodeCabang);
        map.put("KodeRangking", emp.KdRanking);
        map.put("KdTpKerja", emp.KdTpKerja);
        map.put("IdKary", empCode);

        map = fillApprover(map);
        String imageBerkas = getImageBerkas();

        map.put("ReviewBy", "");
        map.put("ReviewDate", "");
        map.put("KodeLevelReviewer", "");
        map.put("KodeShift", "");
        map.put("IsCancelled", "0");
        map.put("IsApproved", "0");
        map.put("IsRejected", "0");
        map.put("HRDReviewBy", "");
        map.put("HRDReviewDate", "");
        map.put("IsHRDApproved", "0");
        map.put("IsHRDRejected", "0");
        map.put("IsAppMobile", "1");
        map.put("IsInsertAbsensiView", "0");
        map.put("ImageBerkas", imageBerkas);
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

    private String getImageBerkas() {
        if (viewSuratDokter.getVisibility() == View.VISIBLE) {
            ImageSource image = new Select().from(ImageSource.class)
                    .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.SURAT_DOKTER))
                    .querySingle();

            return image.imageName;
        }
        return "";
    }

    public void syncAttachment() {
        if (viewSuratDokter.getVisibility() == View.GONE)
            return;

        HashMap<String, String> map = fillMapSyncAttachment();

        WebServicesNonQuery ws = new WebServicesNonQuery(
                getActivity(),
                "DoSaveImage64",
                map,
                getActivity().getResources().getStringArray(R.array.DoSaveImage64_post),
                getActivity().getResources().getStringArray(R.array.DoSaveImage64_get),
                true,
                "Uploading Image..."
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                try {
                    String result = arrList.get(0).get(getActivity().getResources().getStringArray(R.array.DoSaveImage64_get)[0]);

                    if (result.equalsIgnoreCase("true")) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.success_send_image), Toast.LENGTH_SHORT).show();
                        mListener.onSetAction(UtilBundle.ACTION_BUNDLE_EDIT);
                    } else {
                        retrySyncImage();
                    }
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    retrySyncImage();
//                    mListener.onSetAction(UtilBundle.ACTION_BUNDLE_IMAGE);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }


    private void retrySyncImage() {
        try {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity(), Var.dialogTheme);
            builder.setMessage(R.string.gagal_upload_foto);
            builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AbsensiFragment.this.syncAttachment();
                }
            });
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> fillMapSyncAttachment() {
        String fileName = noTransaksi + ".jpg";
        String binaryImage = UtilImage.convertBase64(imgSuratDokter);

        HashMap<String, String> map = new HashMap<>();
        map.put("filename", fileName);
        map.put("picbyte", binaryImage);

        return map;
    }

    private HashMap<String, String> fillApprover(HashMap<String, String> map) {
        List<Head> listHead = new Select().from(Head.class).queryList();

        map.put("Approver1", "");
        map.put("Approver2", "");
        map.put("Approver3", "");
        map.put("Approver4", "");
        map.put("Approver5", "");

        String key = "";

        for (int i = 0; i < listHead.size(); i++) {
            key = "Approver" + (i + 1);
            map.put(key, listHead.get(i).HeadKodeLevel);
        }

        return map;
    }

    public boolean isEmptyField(EditText view, TextInputLayout label) {
        if (view.getVisibility() == View.VISIBLE) {
            if (view.getText().toString().isEmpty()) {
                label.setErrorEnabled(true);
                label.setError(getString(R.string.prompt_error_empty_field));
                return true;

            } else {
                label.setError(null);
                label.setErrorEnabled(false);
                return false;
            }
        }
        label.setError(null);
        label.setErrorEnabled(false);
        return false;
    }


    public boolean isEmptyField(TextView view) {
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

    private boolean validate() {

        if (!validateEmptyField()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_empty_field), Snackbar.LENGTH_SHORT).show();
            return false;
        }


        String kodeForm = btnTipeForm.getTag().toString();
        Condition condition = Condition.column(FormHead$Table.KODEFORM).eq(kodeForm);
        FormHead aFormHead = new Select().from(FormHead.class).where(condition).querySingle();

        if (!validateApprovalKhusus(Boolean.parseBoolean(aFormHead.IsApprovalKhusus))) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_approval_khusus), Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!validateJumlahHari(aFormHead.MaxHari)) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_jumlah_hari_maksimal) + " " + aFormHead.MaxHari + " hari", Snackbar.LENGTH_SHORT).show();
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
        if (!validatePassingToday()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_passing_today), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!validateJam()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_jam), Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!validateJamMinMax()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_jam_min_max), Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!validateIsAttachmentAdded()) {
            Snackbar.make(getView(), getString(R.string.prompt_error_invalid_no_attachment), Snackbar.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    private boolean validateJam() {
        if (btnJamMulai.getVisibility() == View.GONE ||
                btnJamBerakhir.getVisibility() == View.GONE)
            return true;

        String jamMulai = btnJamMulai.getText().toString();
        String jamAkhir = btnJamBerakhir.getText().toString();

        if (UtilDate.toTime(jamMulai).isAfter(UtilDate.toTime(jamAkhir)))
            return false;

        return true;
    }

    private boolean validateJamMinMax() {
        String kodeForm = btnTipeForm.getTag().toString();

        FormHead fh = new Select().from(FormHead.class)
                .where(Condition.column(FormHead$Table.KODEFORM).eq(kodeForm))
                .querySingle();

        SetVar sv = new Select().from(SetVar.class)
                .where(Condition.column(SetVar$Table.PARAMETERCODE).eq("RegulerMsk"))
                .querySingle();

        SetVar sv2 = new Select().from(SetVar.class)
                .where(Condition.column(SetVar$Table.PARAMETERCODE).eq("RegulerKlr"))
                .querySingle();

        LocalTime regulerJamMasuk = UtilDate.toTime(sv.Value);
        LocalTime regulerJamKeluar = UtilDate.toTime(sv2.Value);

        int jamMin = Integer.parseInt(fh.JamMin);
        int jamMax = Integer.parseInt(fh.JamMax);

        if (jamMin < 0) {
            if (jamMin < -(regulerJamMasuk.getHourOfDay()))
                jamMin = -(regulerJamMasuk.getHourOfDay());
        }

        if (btnJamMulai.getVisibility() == View.VISIBLE &&
                btnJamBerakhir.getVisibility() == View.VISIBLE) {
            LocalTime jamMasuk = UtilDate.toTime(btnJamMulai.getText().toString());
            LocalTime jamKeluar = UtilDate.toTime(btnJamBerakhir.getText().toString());


            int bedaMenit = Minutes.minutesBetween(jamMasuk, jamKeluar).getMinutes();
            float bedaJam = (float) bedaMenit / Var.pembagiJam;

            if (bedaJam > jamMax)
                return false;
            else
                return true;

//            if (jamMasuk.plusHours(jamMax).isAfter(LocalTime.MIDNIGHT))
//                return true;
//            else if (jamMasuk.plusHours(jamMax).isAfter(jamKeluar))
//                return true;
//            else
//                return false;

        } else {
            if (btnJamMulai.getVisibility() == View.VISIBLE) {
                LocalTime jamMasuk = UtilDate.toTime(btnJamMulai.getText().toString());

                int bedaMenit = Minutes.minutesBetween(regulerJamMasuk, jamMasuk).getMinutes();
                float bedaJam = (float) bedaMenit / Var.pembagiJam;

                if (bedaJam > jamMax || bedaJam < jamMin)
                    return false;
                else
                    return true;

//                if (jamMasuk.isAfter(regulerJamMasuk.plusHours(jamMax)) ||
//                    jamMasuk.isBefore(regulerJamMasuk.plusHours(jamMin))) {
//                    return false;
//                }
            } else if (btnJamBerakhir.getVisibility() == View.VISIBLE) {
                LocalTime jamKeluar = UtilDate.toTime(btnJamBerakhir.getText().toString());

//                int bedaJam = Hours.hoursBetween(jamKeluar, regulerJamKeluar).getHours();
                int bedaMenit = Minutes.minutesBetween(jamKeluar, regulerJamKeluar).getMinutes();
                float bedaJam = (float) bedaMenit / Var.pembagiJam;

//                                                    15.01, 17.00 -> 17-15.01 = 1
//                                                                      17 - 14.50 = 2
//                                                                     17 - 19 = -2

                if (bedaJam > jamMax || bedaJam < jamMin)
                    return false;
                else
                    return true;


//                if (bedaJam > jamMax || bedaJam < jamMin)
//                    return false;
//                else
//                    return true;

//                if (regulerJamKeluar.plusHours(jamMax).isAfter(LocalTime.MIDNIGHT))
//                    pembatas = LocalTime.MIDNIGHT.minusSeconds(1);
//                else
//                    pembatas = regulerJamKeluar.plusHours(jamMax);
//
//                if (jamKeluar.isAfter(pembatas) ||
//                        jamKeluar.isBefore(regulerJamKeluar.plusHours(jamMin))
//                        ) {
//                    return false;
//                }
            }
//
//            else if (btnJamBerakhir.getVisibility() == View.VISIBLE) {
//                LocalTime jamKeluar = UtilDate.toTime(btnJamBerakhir.getText().toString());
//                if (jamKeluar.isAfter(regulerJamMasuk.plusHours(jamMax)) ||
//                        jamKeluar.isBefore(regulerJamMasuk.plusHours(jamMin))
//                        ) {
//                    return false;
//                }
//            }
        }

        return true;
    }

    private boolean validateIsAttachmentAdded() {
        String kodeForm = btnTipeForm.getTag().toString();

        FormHead fh = new Select().from(FormHead.class)
                .where(Condition.column(FormHead$Table.KODEFORM).eq(kodeForm))
                .querySingle();

        boolean isAttachFile = Boolean.parseBoolean(fh.IsAttachMan);

        if (isAttachFile) {
            if (imgSuratDokter.getDrawable() == null)
                return false;
            return true;
        }
        return true;
    }

    private boolean validatePassingToday() {
        String kodeForm = btnTipeForm.getTag().toString();

        FormHead fh = new Select().from(FormHead.class)
                .where(Condition.column(FormHead$Table.KODEFORM).eq(kodeForm))
                .querySingle();

        boolean isPassingToday = Boolean.parseBoolean(fh.IsPassingToday);
        LocalDate today = UtilDate.getDate();
        LocalDate dateFrom = UtilDate.toDate(btnTanggalMulai.getText().toString());

        if (!isPassingToday) {
            if (dateFrom.isAfter(today))
                return false;
            else
                return true;
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
            boolean valid;

            if (count != 0) {
                Employee employee = new Select().from(Employee.class).querySingle();
                boolean isAllow = employee.IsAllowBatasForm;
                return isAllow;
            }

            int diffMonth = UtilDate.monthDiff(today, dateFrom);
            if (diffMonth >= 0) {
                valid = true;
            } else if (diffMonth == -1) {
                if (today.compareTo(dateBatas) <= 0)
                    valid = true;
                else
                    valid = false;
            } else
                valid = false;

            if(!valid) {
                Employee employee = new Select().from(Employee.class).querySingle();
                boolean isAllow = employee.IsAllowBatasForm;
                valid = isAllow;
            }
            return valid ;
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


    private boolean validateEmptyField() {
        if (
                isEmptyField(btnJenisForm) || isEmptyField(btnTipeForm) ||
                        isEmptyField(btnTanggalMulai) || isEmptyField(btnTanggalBerakhir) ||
                        isEmptyField(btnJamMulai) || isEmptyField(btnJamBerakhir) ||
                        isEmptyField(txtAlasan, lblAlasan)
                ) {
            return false;
        } else
            return true;

    }

    private boolean validateApprovalKhusus (boolean needApprovalKhusus){
        try {
            if (needApprovalKhusus) {
                Employee employee = new Select().from(Employee.class).querySingle();
                boolean isApprovalKhusus  = employee.IsApprovalKhusus;
                return isApprovalKhusus;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean validateJumlahHari(String max) {
        try {
            int jumlahHari = Integer.parseInt(txtJumlahHari.getText().toString());
            int maxhari = Integer.parseInt(max);

            if (jumlahHari > maxhari) {
                lblJumlahHari.setErrorEnabled(true);
                lblJumlahHari.setError("Max hari " + maxhari + " hari");
                lblJumlahHari.setFocusableInTouchMode(true);
                return false;
            }
            lblJumlahHari.setFocusableInTouchMode(false);
            lblJumlahHari.setError(null);
            lblJumlahHari.setErrorEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
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

                            cekIrisan(isSave);
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


    public interface onAbsensiFormListener {
        void onSetAction(int action);
    }

}
