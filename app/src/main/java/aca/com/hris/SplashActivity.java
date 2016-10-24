package aca.com.hris;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Database.FormDetail;
import aca.com.hris.Database.FormHead;
import aca.com.hris.Database.MsCuti;
import aca.com.hris.Database.SetVar;
import aca.com.hris.Database.StandardField;
import aca.com.hris.Database.SummaryPeriode;
import aca.com.hris.Database.User;
import aca.com.hris.Database.Versioning;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.PojoModel.FormDetailModel;
import aca.com.hris.PojoModel.FormHeadModel;
import aca.com.hris.PojoModel.MsCutiModel;
import aca.com.hris.PojoModel.SetVarModel;
import aca.com.hris.PojoModel.StandardFieldModel;
import aca.com.hris.PojoModel.SummaryPeriodeModel;
import aca.com.hris.PojoModel.Version;
import aca.com.hris.PojoModel.VersioningModel;
import aca.com.hris.Retrofit.HrisAPI;
import aca.com.hris.Retrofit.HrisService;
import aca.com.hris.Util.UtilDate;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func6;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Marsel on 5/11/2015.
 */
public class SplashActivity extends AppCompatActivity {
    boolean getAbsensiFormTempDet,
            getAbsensiFormTempHead,
            getStandardFieldNameForm,
            getSetVar,
            getSummaryPeriode,
            getMsCuti;

    @Bind(R.id.imgBackground)
    ImageView imgBackground;
    @Bind(R.id.imgBackgroundReverse)
    ImageView imgBackgroundReverse;
    @Bind(R.id.rootView)
    RelativeLayout rootView;

    private HrisAPI getAbsensiFormTempDetService,
            getAbsensiFormTempHeadService,
            getStandardFieldNameFormService,
            getSetVarService,
            getSummaryPeriodeService,
            getMsCutiService,
            getVersionApp,
            getVersioning    ;

    private CompositeSubscription _subscriptions = new CompositeSubscription();
    private Subscription subscription;
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
//        init();
        wsGetVersion();
    }

    @Override
    protected void onDestroy() {
        if (subscription != null)
            subscription.unsubscribe();

        super.onDestroy();
    }

    private void initializing() {
        Log.d("SplashActivity", "initializing ");
        getAbsensiFormTempDetService = HrisService.createHrisService(null);
        getAbsensiFormTempHeadService = HrisService.createHrisService(null);
        getStandardFieldNameFormService = HrisService.createHrisService(null);
        getSetVarService = HrisService.createHrisService(null);
        getSummaryPeriodeService = HrisService.createHrisService(null);
        getMsCutiService = HrisService.createHrisService(null);


        subscription = Observable.zip(getAbsensiFormTempDetService.FormDetail("GetAbsensiFormTempDetJson").subscribeOn(Schedulers.newThread()),
                getAbsensiFormTempHeadService.FormHead("GetAbsensiFormTempHeadJson").subscribeOn(Schedulers.newThread()),
                getStandardFieldNameFormService.StandardField("GetStandardFieldNameFormJson").subscribeOn(Schedulers.newThread()),
                getSetVarService.SetVar("GetSetVarJson").subscribeOn(Schedulers.newThread()),
                getMsCutiService.MsCuti("GetMsCutiJson").subscribeOn(Schedulers.newThread()),
                getSummaryPeriodeService.SummaryPeriode("GetAbsensiViewSummaryPeriodJson").subscribeOn(Schedulers.newThread()),
                new Func6<List<FormDetailModel>, List<FormHeadModel>, List<StandardFieldModel>, List<SetVarModel>, List<MsCutiModel>, List<SummaryPeriodeModel>, Object>() {
                    @Override
                    public Object call(List<FormDetailModel> formDetailModels, List<FormHeadModel> formHeadModels, List<StandardFieldModel> standardFieldModels, List<SetVarModel> setVarModels, List<MsCutiModel> msCutiModels, List<SummaryPeriodeModel> summaryPeriodeModels) {
                        insertFormDetail(formDetailModels);
                        insertFormHead(formHeadModels);
                        insertStandardField(standardFieldModels);
                        insertSetVar(setVarModels);
                        insertMsCuti(msCutiModels);
                        insertSummaryPeriode(summaryPeriodeModels);

                        return null;
                    }
                }
        ).subscribe(
                new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                        autoLogin();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        retry();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });


    }

    private void retry() {
        Snackbar.make(rootView, R.string.gagal_load_data, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wsGetVersion();
                    }
                })
                .show();
    }

    private void autoLogin() {
        User user = new Select().from(User.class).querySingle();

        if (user == null || User.getIdRole() == null ) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            SplashActivity.this.finish();
        } else if (user.LoginStatus.equalsIgnoreCase(User.LOGOUT)) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            SplashActivity.this.finish();
        } else {
            String email = user.UserId;
            String password = user.UserPass;

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("userID", email);
            map.put("userPass", password);

            WebServices ws = new WebServices(SplashActivity.this,
                    "ValidateLoginUser",
                    map,
                    getResources().getStringArray(R.array.ValidateLoginUser_post),
                    getResources().getStringArray(R.array.ValidateLoginUser_get)
            ) {
                @Override
                protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                    if (arrList != null) {
                        startActivity(new Intent(SplashActivity.this, FirstActivity.class));
                        SplashActivity.this.finish();
                        return;
                    }
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    SplashActivity.this.finish();
                }

                @Override
                protected void onFailed(String message) {
                    retry();
                }

                @Override
                protected void onCancel() {
                }
            };
            ws.execute();
        }

    }

    private void insertSummaryPeriode(List<SummaryPeriodeModel> summaryPeriodeModels) {
        try {
            new Delete().from(SummaryPeriode.class).queryClose();

            SummaryPeriode sp = null;

            for (SummaryPeriodeModel s : summaryPeriodeModels) {
                sp = new SummaryPeriode();
                sp.Tahun = s.Tahun;
                sp.Bulan = s.Bulan;
                sp.save();
            }

            getSummaryPeriode = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertMsCuti(List<MsCutiModel> msCutiModels) {
        try {
            new Delete().from(MsCuti.class).queryClose();
            MsCuti f;

            for (MsCutiModel c : msCutiModels) {

                f = new MsCuti();

                f.KodeCuti = c.KodeCuti;
                f.NamaCuti = c.NamaCuti;
                f.TipeCuti = c.TipeCuti;
                f.Gender = c.Gender;
                f.JumlahHari = c.JumlahHari;
                f.IsGaji = c.IsGaji;
                f.IsFix = c.IsFix;
                f.IsPotongCuti = c.IsPotongCuti;
                f.IsLihatCalendar = c.IsLihatCalendar;
                f.IsSetengahHari = c.IsSetengahHari;
                f.IsTelat = c.IsTelat;
                f.JamMasuk = c.JamMasuk;
                f.Keterangan = c.Keterangan;
                f.save();
            }

            getMsCuti = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertSetVar(List<SetVarModel> setVarModels) {
        try {
            new Delete().from(SetVar.class).queryClose();
            SetVar f;

            for (SetVarModel c : setVarModels) {

                f = new SetVar();

                f.ParameterCode = c.ParameterCode;
                f.ParameterName = c.ParameterName;
                f.Value = c.Value;

                f.save();
            }

            getSetVar = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertStandardField(List<StandardFieldModel> standardFieldModels) {
        try {
            new Delete().from(StandardField.class).queryClose();
            StandardField f;

            for (StandardFieldModel c : standardFieldModels) {

                f = new StandardField();

                f.FieldCode = c.FieldCode;
                f.FieldCodeDt = c.FieldCodeDt;
                f.FieldNameDt = c.FieldNameDt;
                f.Value = c.Value;
                f.Description = c.Description;

                f.save();
            }

            getStandardFieldNameForm = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertFormHead(List<FormHeadModel> formHeadModels) {
        try {
            new Delete().from(FormHead.class).queryClose();
            FormHead f;

            for (FormHeadModel c : formHeadModels) {

                f = new FormHead();

                f.KodeForm = c.KodeForm;
                f.GrpFormAbsensi = c.GrpFormAbsensi;
                f.NamaForm = c.NamaForm;
                f.MaxHari = c.MaxHari;
                f.JamMin = c.JamMin;
                f.JamMax = c.JamMax;
                f.IsAttachMan = c.IsAttachMan;
                f.IsApprovalKhusus = c.IsApprovalKhusus;
                f.IsPassingToday = c.IsPassingToday;
                f.IsTelat = c.IsTelat;
                f.Keterangan = c.Keterangan;
                f.KetWidth = c.KetWidth;
                f.KetHeight = c.KetHeight;

                f.save();
            }

            getAbsensiFormTempHead = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertFormDetail(List<FormDetailModel> formDetailModels) {
        try {
            new Delete().from(FormDetail.class).queryClose();
            FormDetail f;

            for (FormDetailModel c : formDetailModels) {

                f = new FormDetail();
                f.KodeKomponen = c.KodeKomponen;
                f.KodeForm = c.KodeForm;
                f.Caption = c.Caption;

                f.save();
            }

            getAbsensiFormTempDet = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void wsGetVersion() {

        try {
            getVersionApp = HrisService.createHrisService(null);
            getVersionApp.Version("GetNewVersionAndroidJson")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Observer<List<Version>>() {

                                @Override
                                public void onCompleted() {
                                    Timber.d("Retrofit call 1 completed");
                                }


                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    retry();
                                }

                                @Override
                                public void onNext(List<Version> versions) {
                                    if (verifyVersion(versions)) {
                                        checkingVersion();
    //                                    initializing();
                                    }
                                }


                            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        HashMap<String, String> map = fillMapGetVersion();
        WebServices ws = new WebServices(
                this,
                "GetNewVersionAndroid",
                map,
                getResources().getStringArray(R.array.GetNewVersionAndroid_post),
                getResources().getStringArray(R.array.GetNewVersionAndroid_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                if (arrList != null) {
//                    if (verifyVersion(arrList.get(0))) {
//                        initializing();
//                    }
                } else {
                    Toast.makeText(SplashActivity.this, getString(R.string.gagal_load_data), Toast.LENGTH_SHORT).show();
                    SplashActivity.this.finish();
                }
            }

            @Override
            protected void onFailed(String message) {
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
                SplashActivity.this.finish();
            }

            @Override
            protected void onCancel() {

            }
        };
//        ws.execute();
    }

    private void checkingVersion() {
        HrisService.createHrisService(null)
                .Versioning("GetVersioning")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VersioningModel>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        retry();
                    }

                    @Override
                    public void onNext(List<VersioningModel> versioningModels) {
                        boolean update = false;

                        try {

                            VersioningModel vm = versioningModels.get(0);
                            int count = new Select().from(Versioning.class).query().getCount();
                            if (count == 0) {
                                Versioning vers = new Versioning();
                                vers.Version = vm.Version;
                                vers.CreateDate = vm.CreatedDate;
                                vers.save();
                                update = true ;

                            }
                            else {
                                Versioning vers = new Select().from(Versioning.class).querySingle();

                                if (vm.Version != vers.Version) {
                                    update = true;
                                    vers.Version = vm.Version;
                                    vers.CreateDate = vm.CreatedDate;
                                    vers.save();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        finally {
                            if (update)
                                initializing();
                            else
                                autoLogin();
                        }

                    }
                });
    }

    private boolean verifyVersion(List<Version> versions) {
        try {
            Version v = versions.get(0);

            LocalDateTime dateTime = UtilDate.toDateTime(v.DateTime, UtilDate.DATE_TIME_PATTERN_SERVER);
            boolean forceUpgrade = Boolean.parseBoolean(v.ForceUpgrade);
            int newVersion = Integer.parseInt(v.Version);

            int currentVersion = BuildConfig.VERSION_CODE;
            LocalDateTime now = UtilDate.getDateTime();

            if (currentVersion < newVersion) {
                if (now.isAfter(dateTime)) {
                    if (forceUpgrade) {
                        popupUpdate();
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void popupUpdate() {
        try {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.new_update));
            builder.setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String appPackageName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                    SplashActivity.this.finish();

                }
            });
            builder.setCancelable(false);
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> fillMapGetVersion() {
        HashMap<String, String> map = new HashMap<>();

        return map;
    }


    private void getSummaryPeriode() {
        WebServices ws = new WebServices(
                this,
                "GetAbsensiViewSummaryPeriod",
                new HashMap<String, String>(),
                getResources().getStringArray(R.array.GetAbsensiViewSummaryPeriod_post),
                getResources().getStringArray(R.array.GetAbsensiViewSummaryPeriod_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                new Delete().from(SummaryPeriode.class).queryClose();

                SummaryPeriode tableObject = null;
                for (HashMap<String, String> map : arrList
                        ) {
                    tableObject = new SummaryPeriode();

                    tableObject.Tahun = map.get("Tahun");
                    tableObject.Bulan = map.get("Bulan");

                    tableObject.save();
                }

                getSummaryPeriode = true;
            }

            @Override
            protected void onFailed(String message) {

            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }


    private void getMsCuti() {
        getMsCutiService = HrisService.createHrisService(null);

        _subscriptions.add(
                getMsCutiService.MsCuti("GetMsCuti")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<MsCutiModel>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Retrofit call 1 completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "woops we got an error while getting the list of contributors");
                            }


                            @Override
                            public void onNext(List<MsCutiModel> object) {
                                try {
                                    new Delete().from(MsCuti.class).queryClose();
                                    MsCuti f;

                                    for (MsCutiModel c : object) {

                                        f = new MsCuti();

                                        f.KodeCuti = c.KodeCuti;
                                        f.NamaCuti = c.NamaCuti;
                                        f.TipeCuti = c.TipeCuti;
                                        f.Gender = c.Gender;
                                        f.JumlahHari = c.JumlahHari;
                                        f.IsGaji = c.IsGaji;
                                        f.IsFix = c.IsFix;
                                        f.IsPotongCuti = c.IsPotongCuti;
                                        f.IsLihatCalendar = c.IsLihatCalendar;
                                        f.IsSetengahHari = c.IsSetengahHari;
                                        f.IsTelat = c.IsTelat;
                                        f.JamMasuk = c.JamMasuk;
                                        f.Keterangan = c.Keterangan;
                                        f.save();
                                    }

                                    getMsCuti = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }));


        WebServices ws = new WebServices(
                this,
                "GetMsCuti",
                new HashMap<String, String>(),
                getResources().getStringArray(R.array.GetMsCuti_post),
                getResources().getStringArray(R.array.GetMsCuti_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                new Delete().from(MsCuti.class).queryClose();

                MsCuti tableObject = null;
                for (HashMap<String, String> map : arrList
                        ) {
                    tableObject = new MsCuti();

                    tableObject.KodeCuti = map.get("KodeCuti");
                    tableObject.NamaCuti = map.get("NamaCuti");
                    tableObject.TipeCuti = map.get("TipeCuti");
                    tableObject.Gender = map.get("Gender");
                    tableObject.JumlahHari = map.get("JumlahHari");
                    tableObject.IsGaji = map.get("IsGaji");
                    tableObject.IsFix = map.get("IsFix");
                    tableObject.IsPotongCuti = map.get("IsPotongCuti");
                    tableObject.IsLihatCalendar = map.get("IsLihatCalendar");
                    tableObject.IsSetengahHari = map.get("IsSetengahHari");
                    tableObject.IsTelat = map.get("IsTelat");
                    tableObject.JamMasuk = map.get("JamMasuk");
                    tableObject.Keterangan = map.get("Keterangan");


                    tableObject.save();
                }

                getMsCuti = true;
            }

            @Override
            protected void onFailed(String message) {

            }

            @Override
            protected void onCancel() {

            }
        };
//        ws.execute();
    }

    private void getSetVar() {
        getSetVarService = HrisService.createHrisService(null);

        _subscriptions.add(
                getSetVarService.SetVar("GetSetVar")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<SetVarModel>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Retrofit call 1 completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "woops we got an error while getting the list of contributors");
                            }


                            @Override
                            public void onNext(List<SetVarModel> object) {
                                try {
                                    new Delete().from(SetVar.class).queryClose();
                                    SetVar f;

                                    for (SetVarModel c : object) {

                                        f = new SetVar();

                                        f.ParameterCode = c.ParameterCode;
                                        f.ParameterName = c.ParameterName;
                                        f.Value = c.Value;

                                        f.save();
                                    }

                                    getSetVar = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }));


        WebServices ws = new WebServices(
                this,
                "GetSetVar",
                new HashMap<String, String>(),
                getResources().getStringArray(R.array.GetSetVar_post),
                getResources().getStringArray(R.array.GetSetVar_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                new Delete().from(SetVar.class).queryClose();

                SetVar tableObject = null;
                for (HashMap<String, String> map : arrList
                        ) {
                    tableObject = new SetVar();

                    tableObject.ParameterCode = map.get("ParameterCode");
                    tableObject.ParameterName = map.get("ParameterName");
                    tableObject.Value = map.get("Value");

                    tableObject.save();
                }

                getSetVar = true;
            }

            @Override
            protected void onFailed(String message) {

            }

            @Override
            protected void onCancel() {

            }
        };
//        ws.execute();
    }


    private void getAbsensiFormTempHead() {
        getAbsensiFormTempHeadService = HrisService.createHrisService(null);

        _subscriptions.add(
                getAbsensiFormTempHeadService.FormHead("GetAbsensiFormTempHeadJson")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<FormHeadModel>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Retrofit call 1 completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "woops we got an error while getting the list of contributors");
                            }


                            @Override
                            public void onNext(List<FormHeadModel> formHead) {
                                try {
                                    new Delete().from(FormHead.class).queryClose();
                                    FormHead f;

                                    for (FormHeadModel c : formHead) {

                                        f = new FormHead();

                                        f.KodeForm = c.KodeForm;
                                        f.GrpFormAbsensi = c.GrpFormAbsensi;
                                        f.NamaForm = c.NamaForm;
                                        f.MaxHari = c.MaxHari;
                                        f.JamMin = c.JamMin;
                                        f.JamMax = c.JamMax;
                                        f.IsAttachMan = c.IsAttachMan;
                                        f.IsApprovalKhusus = c.IsApprovalKhusus;
                                        f.IsPassingToday = c.IsPassingToday;
                                        f.IsTelat = c.IsTelat;
                                        f.Keterangan = c.Keterangan;
                                        f.KetWidth = c.KetWidth;
                                        f.KetHeight = c.KetHeight;

                                        f.save();
                                    }

                                    getAbsensiFormTempHead = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }));

        WebServices ws = new WebServices(
                this,
                "GetAbsensiFormTempHead",
                new HashMap<String, String>(),
                getResources().getStringArray(R.array.GetAbsensiFormTempHead_post),
                getResources().getStringArray(R.array.GetAbsensiFormTempHead_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                new Delete().from(FormHead.class).queryClose();
                FormHead tableObject = null;

                for (HashMap<String, String> map : arrList
                        ) {
                    tableObject = new FormHead();
                    tableObject.KodeForm = map.get("KodeForm");
                    tableObject.GrpFormAbsensi = map.get("GrpFormAbsensi");
                    tableObject.NamaForm = map.get("NamaForm");
                    tableObject.MaxHari = map.get("MaxHari");
                    tableObject.JamMin = map.get("JamMin");
                    tableObject.JamMax = map.get("JamMax");
                    tableObject.IsAttachMan = map.get("IsAttachMan");
                    tableObject.IsApprovalKhusus = map.get("IsApprovalKhusus");
                    tableObject.IsPassingToday = map.get("IsPassingToday");
                    tableObject.IsTelat = map.get("IsTelat");
                    tableObject.Keterangan = map.get("Keterangan");
                    tableObject.KetWidth = map.get("KetWidth");
                    tableObject.KetHeight = map.get("KetHeight");

                    tableObject.save();
                }
                getAbsensiFormTempHead = true;
            }

            @Override
            protected void onFailed(String message) {

            }

            @Override
            protected void onCancel() {

            }
        };
//        ws.execute();
    }

    private void getAbsensiFormTempDet() {

        getAbsensiFormTempDetService = HrisService.createHrisService(null);

        _subscriptions.add(
                getAbsensiFormTempDetService.FormDetail("GetAbsensiFormTempDetJson")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<FormDetailModel>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Retrofit call 1 completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "woops we got an error while getting the list of contributors");
                            }


                            @Override
                            public void onNext(List<FormDetailModel> formDetail) {
                                try {
                                    new Delete().from(FormDetail.class).queryClose();
                                    FormDetail f;

                                    for (FormDetailModel c : formDetail) {

                                        f = new FormDetail();
                                        f.KodeKomponen = c.KodeKomponen;
                                        f.KodeForm = c.KodeForm;
                                        f.Caption = c.Caption;

                                        f.save();
                                    }

                                    getAbsensiFormTempDet = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }));
        WebServices ws = new WebServices(
                this,
                "GetAbsensiFormTempDet",
                new HashMap<String, String>(),
                getResources().getStringArray(R.array.GetAbsensiFormTempDet_post),
                getResources().getStringArray(R.array.GetAbsensiFormTempDet_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                new Delete().from(FormDetail.class).queryClose();

                FormDetail tableObject = null;
                for (HashMap<String, String> map : arrList
                        ) {
                    tableObject = new FormDetail();

                    tableObject.KodeKomponen = map.get("KodeKomponen");
                    tableObject.KodeForm = map.get("KodeForm");
                    tableObject.Caption = map.get("Caption");

                    tableObject.save();
                }

                getAbsensiFormTempDet = true;
            }

            @Override
            protected void onFailed(String message) {

            }

            @Override
            protected void onCancel() {

            }
        };
//        ws.execute();
    }

    private void getStandardFieldNameForm() {
        getStandardFieldNameFormService = HrisService.createHrisService(null);

        _subscriptions.add(
                getStandardFieldNameFormService.StandardField("GetStandardFieldNameForm")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<StandardFieldModel>>() {
                            @Override
                            public void onCompleted() {
                                Timber.d("Retrofit call 1 completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "woops we got an error while getting the list of contributors");
                            }


                            @Override
                            public void onNext(List<StandardFieldModel> object) {
                                try {
                                    new Delete().from(StandardField.class).queryClose();
                                    StandardField f;

                                    for (StandardFieldModel c : object) {

                                        f = new StandardField();

                                        f.FieldCode = c.FieldCode;
                                        f.FieldCodeDt = c.FieldCodeDt;
                                        f.FieldNameDt = c.FieldNameDt;
                                        f.Value = c.Value;
                                        f.Description = c.Description;

                                        f.save();
                                    }

                                    getStandardFieldNameForm = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }));


        WebServices ws = new WebServices(
                this,
                "GetStandardFieldNameForm",
                new HashMap<String, String>(),
                getResources().getStringArray(R.array.GetStandardFieldNameForm_post),
                getResources().getStringArray(R.array.GetStandardFieldNameForm_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                new Delete().from(StandardField.class).queryClose();
                StandardField tableObject = null;

                for (HashMap<String, String> map : arrList
                        ) {
                    tableObject = new StandardField();


                    tableObject.FieldCode = map.get("FieldCode");
                    tableObject.FieldCodeDt = map.get("FieldCodeDt");
                    tableObject.FieldNameDt = map.get("FieldNameDt");
                    tableObject.Value = map.get("Value");
                    tableObject.Description = map.get("Description");
                    tableObject.save();
                }
                getStandardFieldNameForm = true;
            }

            @Override
            protected void onFailed(String message) {

            }

            @Override
            protected void onCancel() {

            }
        };
//        ws.execute();
    }
}