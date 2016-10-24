package aca.com.hris.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.List;

import aca.com.hris.Database.Employee;
import aca.com.hris.Database.Head;
import aca.com.hris.Database.ImageSource;
import aca.com.hris.Database.ImageSource$Table;
import aca.com.hris.Database.User;
import aca.com.hris.FirstActivity;
import aca.com.hris.PojoModel.HeadKodeLevelModel;
import aca.com.hris.PojoModel.MangkirModel;
import aca.com.hris.PojoModel.UserModel;
import aca.com.hris.R;
import aca.com.hris.Retrofit.HrisAPI;
import aca.com.hris.Retrofit.HrisService;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.EasyImage;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

public class MyProfilFragment extends BaseFragment {

    private static final String TAG = "myprofil fragment";
    private static final int EASY_IMAGE_FLAG_PROFILE = 0;
    private static final int EASY_IMAGE_FLAG_BACKGROUND = 1;
    ProgressBar pbLoading;
    Button btnRetry;
    NestedScrollView viewScrollProfile;
    CoordinatorLayout rootView;
    TextView txtNama;
    TextView txtJabatan;
    EditText txtCabang;
    EditText txtRangking;
    EditText txtSisaCuti;
    TextView txtBulanBerjalanWhole;
    TextView txtTelatHariWhole;
    TextView txtTelatMenitWhole;
    TextView txtMangkirWhole;
    TextView txtBulanBerjalan;
    TextView txtTelatHari;
    TextView txtTelatMenit;
    TextView txtMangkir;
    CircleImageView imgProfile;
    SwipeRefreshLayout swipeContainer;
    Subscription subscription;
    private MyProfilFragmentListener mListener;
    private int easyImageFlag;
    private HrisAPI userDataService,
            mangkirService,
            headKodeLevelService,
            mangkirTaunanService;

    public static MyProfilFragment newInstance() {
        MyProfilFragment fragment = new MyProfilFragment();
        return fragment;
    }

    @Override
    public void onDestroy() {
        if (subscription != null)
            subscription.unsubscribe();
        super.onDestroy();
    }


    @Override
    protected void onCreate() {
        loadData();
    }

    @Override
    protected void getArgsData() {

    }

    @Override
    protected View getRootPanel() {
        return viewScrollProfile;
    }

    @Override
    protected Button getButtonRetry() {
        return btnRetry;
    }

    @Override
    protected ProgressBar getProgressLoading() {
        return pbLoading;
    }

    public void onRefresh() {
        loadData();
    }

    private void loadData() {
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                subscription = Observable.zip(
                        userDataService.User("GetUserDataJsonTemporary").subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
                        mangkirService.Mangkir("GetTelatMangkirUserJson").subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
                        mangkirTaunanService.Mangkir("GetTelatMangkirUserJson").subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
                        headKodeLevelService.HeadKodeLevel("GetHeadKodeLevelJson").subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()),
                        new Func4<List<UserModel>, List<MangkirModel>, List<MangkirModel>, List<HeadKodeLevelModel>, Object>() {
                            @Override
                            public Object call(List<UserModel> userModels, List<MangkirModel> mangkirModels, List<MangkirModel> mangkirModels2, List<HeadKodeLevelModel> headKodeLevelModels) {

                                try {
                                    insertUserProfile(userModels);
                                    insertMangkir(mangkirModels, false);
                                    insertMangkir(mangkirModels2, true);
                                    insertHeadKode(headKodeLevelModels);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }

                ).subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Object>() {
                            @Override
                            public void onCompleted() {
                                swipeContainer.setRefreshing(false);
                            }


                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError " + e.getMessage());
                                swipeContainer.setRefreshing(false);
                                Snackbar.make(mListener.getRootView(), R.string.check_your_connection, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.retry, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                onRefresh();
                                            }
                                        })
                                        .show();
                            }


                            @Override
                            public void onNext(Object o) {
                            }
                        });

            }
        });


    }

    private void getProfilePict() {
        try {
            ImageSource img = new Select().from(ImageSource.class)
                    .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.PROFILE_PICTURE))
                    .querySingle();

            if (img == null)
                return;

            File imgFile = new File(img.imagePath);

            if (imgFile.exists()) {
//                Picasso.with(getActivity())
//                .load(imgFile)
//                .fit()
//                .centerCrop()
//                .into(imgProfile, new Callback() {
//                    @Override
//                    public void onSuccess() {
//
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imgProfile.setImageBitmap(myBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_my_profil;
    }

    @Override
    protected void initInstance(View view) {
        rootView    = (CoordinatorLayout)view.findViewById(R.id.rootView);
        viewScrollProfile = (NestedScrollView) view.findViewById(R.id.viewScrollProfile);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
        btnRetry = (Button) view.findViewById(R.id.btnRetry);
        txtNama = (TextView) view.findViewById(R.id.txtNama);
        txtJabatan = (TextView) view.findViewById(R.id.txtJabatan);
        txtCabang = (EditText) view.findViewById(R.id.txtCabang);
        txtRangking = (EditText) view.findViewById(R.id.txtRangking);
        txtSisaCuti = (EditText) view.findViewById(R.id.txtSisaCuti);
        txtBulanBerjalanWhole = (TextView) view.findViewById(R.id.txtBulanBerjalanWhole);
        txtTelatHariWhole = (TextView) view.findViewById(R.id.txtTelatHariWhole);
        txtTelatMenitWhole = (TextView) view.findViewById(R.id.txtTelatMenitWhole);
        txtMangkirWhole = (TextView) view.findViewById(R.id.txtMangkirWhole);
        txtBulanBerjalan = (TextView) view.findViewById(R.id.txtBulanBerjalan);
        txtTelatHari = (TextView) view.findViewById(R.id.txtTelatHari);
        txtTelatMenit = (TextView) view.findViewById(R.id.txtTelatMenit);
        txtMangkir = (TextView) view.findViewById(R.id.txtMangkir);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

    }


    @Override
    protected void init(View view) {
        userDataService = HrisService.createHrisService(getUserDataBody());
        mangkirService = HrisService.createHrisService(getMangkirBody("0"));
        mangkirTaunanService = HrisService.createHrisService(getMangkirBody("1"));
        headKodeLevelService = HrisService.createHrisService(getHeadKodeBody());

    }

    private RequestBody getHeadKodeBody() {
        RequestBody formBody = new FormBody.Builder()
                .add("IDKaryawan", User.getEmpCode())
                .add("KodeLevel", User.getKodeLevel())
                .build();
        return formBody;
    }


    private RequestBody getMangkirBody(String isTahunan) {
        RequestBody formBody = new FormBody.Builder()
                .add("IDKaryawan", User.getEmpCode())
                .add("IsTahunan", isTahunan)
                .build();
        return formBody;
    }

    private RequestBody getUserDataBody() {
        RequestBody formBody = new FormBody.Builder()
                .add("IDKaryawan", User.getEmpCode())
                .add("KodeLevel", User.getKodeLevel())
                .build();
        return formBody;
    }

    @Override
    protected void registerListener(View view) {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MyProfilFragment.this.onRefresh();
            }
        });


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

        Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        if (easyImageFlag == EASY_IMAGE_FLAG_PROFILE) {
            imgProfile.setImageBitmap(myBitmap);

            new Delete().from(ImageSource.class)
                    .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.PROFILE_PICTURE))
                    .queryClose();

            ImageSource cImage = new ImageSource();
            cImage.key = ImageSource.PROFILE_PICTURE;
            cImage.imageName = photoFile.getName();
            cImage.imagePath = photoFile.getPath();
            cImage.save();
        }
//        else if (easyImageFlag == EASY_IMAGE_FLAG_BACKGROUND){
//            toolbarLayout.setBackgroundColor(Color.TRANSPARENT);
////            toolbarLayout.setBackgroundResource(0);
//            toolbarLayout.setImageBitmap(myBitmap);
//
//
//
//            new Delete().from(ImageSource.class)
//                    .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.BACKGROUND_HEADER))
//                    .queryClose();
//
//            ImageSource cImage = new ImageSource();
//            cImage.key = ImageSource.BACKGROUND_HEADER;
//            cImage.imageName = photoFile.getName();
//            cImage.imagePath = photoFile.getPath();
//            cImage.save();
//        }

    }

    private void insertUserProfile(List<UserModel> arrList) {
        if (arrList != null) {
            UserModel u = arrList.get(0);

            insertDB(u);
            fillProfile();
        }
    }

    private void insertMangkir(List<MangkirModel> arrList, boolean istahunan) {
        MangkirModel m = arrList.get(0);

        if (istahunan) {
            txtBulanBerjalanWhole.setText("Bulan Berjalan January - December");
            txtTelatHariWhole.setText(m.telatHari + " hari");
            txtTelatMenitWhole.setText(m.telatMenit + " menit");
            txtMangkirWhole.setText(m.mangkirHari + " hari");
        } else {
            txtBulanBerjalan.setText("Bulan Berjalan " + m.bulan);
            txtTelatHari.setText(m.telatHari + " hari");
            txtTelatMenit.setText(m.telatMenit + " menit");
            txtMangkir.setText(m.mangkirHari + " hari");
        }

    }

    private void insertHeadKode(List<HeadKodeLevelModel> arrList) {
        new Delete().from(Head.class).queryClose();
        Head headKodeLevel = null;

        for (HeadKodeLevelModel headKodeLevelModel : arrList
                ) {
            headKodeLevel = new Head();
            headKodeLevel.HeadKodeLevel = headKodeLevelModel.HeadKodeLevel;
            headKodeLevel.IdKary = headKodeLevelModel.IdKary;
            headKodeLevel.FullName = headKodeLevelModel.FullName;
            headKodeLevel.save();
        }

    }


    private void insertDB(UserModel userModel) {
        new Delete().from(Employee.class).queryClose();

        Employee employee = new Employee();
        employee.IdKary = Long.parseLong(userModel.IdKary);
        employee.NIK = userModel.NIK;
        employee.StatusKaryawan = userModel.StatusKaryawan;
        employee.PeriodeAwal = userModel.PeriodeAwal;
        employee.PeriodeAkhir = userModel.PeriodeAkhir;
        employee.NamaCabang = userModel.NamaCabang;
        employee.NamaJabatan = userModel.NamaJabatan;
        employee.NamaRangking = userModel.NamaRangking;
        employee.JobDesk = userModel.JobDesk;
        employee.NamaKaryawan = userModel.NamaKaryawan;
        employee.NamaPanggil = userModel.NamaPanggil;
        employee.JenisKelamin = userModel.JenisKelamin;
        employee.KdGender = userModel.KdGender;
        employee.TglLahir = userModel.TglLahir;
        employee.TempatLahir = userModel.TempatLahir;
        employee.Kewarganegaraan = userModel.Kewarganegaraan;
        employee.Telp1 = userModel.Telp1;
        employee.Telp2 = userModel.Telp2;
        employee.KTP = userModel.KTP;
        employee.Agama = userModel.Agama;
        employee.BpjsKetenagakerjaan = userModel.BpjsKetenagakerjaan;
        employee.BpjsKesehatan = userModel.BpjsKesehatan;
        employee.NoDPLP = userModel.NoDPLP;
        employee.NoNPWP = userModel.NoNPWP;
        employee.StatusPernikahan = userModel.StatusPernikahan;
        employee.KodeLevel = userModel.KodeLevel;
        employee.KodeCabang = userModel.KodeCabang;
        employee.KdTpKerja = userModel.KdTpKerja;
        employee.KdRanking = userModel.KdRanking;
        employee.SisaCuti = Double.parseDouble(userModel.SisaCuti);
        employee.save();

    }

    private void fillProfile() {
        Employee emp = new Select().from(Employee.class).querySingle();
        txtNama.setText(emp.NamaKaryawan);
        txtJabatan.setText(emp.NamaJabatan);
        txtRangking.setText(emp.NamaRangking);
        txtCabang.setText(emp.NamaCabang);
        txtSisaCuti.setText(emp.SisaCuti.toString());
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MyProfilFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface MyProfilFragmentListener {
        public View getRootView();
    }

}
