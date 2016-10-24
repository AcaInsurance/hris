package aca.com.hris;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.iid.FirebaseInstanceId;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.builder.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import aca.com.hris.Database.GeneralVar;
import aca.com.hris.Database.GeneralVar$Table;
import aca.com.hris.Database.ImageSource;
import aca.com.hris.Database.ImageSource$Table;
import aca.com.hris.Database.Jabatan;
import aca.com.hris.Database.Role;
import aca.com.hris.Database.User;
import aca.com.hris.Fragment.MyProfilFragment;
import aca.com.hris.HelperClass.GcmDal;
import aca.com.hris.Util.UtilBundle;
import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.EasyImage;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FirstActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        MyProfilFragment.MyProfilFragmentListener {

    private static final int EASY_IMAGE_FLAG_PROFILE = 0;
    private static final int EASY_IMAGE_FLAG_COVER = 1;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.layContainer)
    FrameLayout layContainer;
    @Bind(R.id.fabCuti)
    FloatingActionButton fabCuti;
    @Bind(R.id.fabAbsensi)
    FloatingActionButton fabAbsensi;
    @Bind(R.id.fabCutiCancellation)
    FloatingActionButton fabCutiCancellation;
    @Bind(R.id.fabAbsensiCancellation)
    FloatingActionButton fabAbsensiCancellation;
    @Bind(R.id.famFirst)
    FloatingActionsMenu famFirst;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.rootView)
    CoordinatorLayout rootView;
    @Bind(R.id.imgCover)
    ImageView imgCover;
    private TextView txtJabatan;
    private TextView txtRole;
    private TextView txtUserID;
    private CircleImageView imgProfile;
    private DrawerLayout drawer;
    private int easyImageFlag;
    private AlertDialog.Builder dialogImageChooser;

    @Override
    protected int getContentView() {
        return R.layout.activity_first;
    }

    @Override
    protected String setTitle() {
        return "Profil";
    }

    @Override
    protected void getIntentData() {

    }

    @Override
    protected void initFragment() {

    }

    @Override
    protected void initInstances() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                famFirst.collapseImmediately();
            }
        });
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().performIdentifierAction(R.id.nav_profile, 0);
        navigationView.getMenu().getItem(0).setChecked(true);

        View headerLayout = navigationView.getHeaderView(0);
        txtJabatan = (TextView) headerLayout.findViewById(R.id.txtJabatan);
        txtRole = (TextView) headerLayout.findViewById(R.id.txtRole);
        txtUserID = (TextView) headerLayout.findViewById(R.id.txtUserID);
        imgProfile = (CircleImageView) headerLayout.findViewById(R.id.imgProfile);

    }

    @Override
    protected void onCreate() {
        initMainDrawer();
        initFloatButton();
        bindAdapter();
        fillJabatan();
        fillRole();
        fillUserID();
        filProfilePicture();
        fillCover();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navView.getMenu().performIdentifierAction(R.id.nav_profile, 0);
        navView.getMenu().getItem(0).setChecked(true);

        filProfilePicture();
        fillCover();

    }

    private void bindAdapter() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.add("Storage");
        arrayAdapter.add("Gallery");

        dialogImageChooser = new AlertDialog.Builder(this);
        dialogImageChooser.setTitle("Select source");
        dialogImageChooser.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {
                    Intent i = new Intent(FirstActivity.this, HeaderPickerActivity.class);
                    i.putExtra(UtilBundle.PREV_ACTIVITY, UtilBundle.FIRST_ACTIVITY);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(i);
                } else {
                    showCoverPicker();
                }
            }
        });
    }


    private void initFloatButton() {
        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_HEAD_EMPLOYEE)) {
            famFirst.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initCoachBase() {

    }

    @Override
    protected void registerListener() {
        famFirst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (famFirst.isExpanded())
                        famFirst.collapse();
            }
        });
        fabAbsensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                famFirst.collapseImmediately();
                Bundle bundle = new Bundle();
                bundle.putInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);

                Intent intent = new Intent(FirstActivity.this, AbsensiActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        fabCuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                famFirst.collapseImmediately();
                Bundle bundle = new Bundle();
                bundle.putInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);

                Intent intent = new Intent(FirstActivity.this, CutiActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        fabAbsensiCancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                famFirst.collapseImmediately();
                Bundle bundle = new Bundle();
                bundle.putInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);

                Intent intent = new Intent(FirstActivity.this, LoadAbsensiCancellationActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        fabCutiCancellation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                famFirst.collapseImmediately();
                Bundle bundle = new Bundle();
                bundle.putInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);

                Intent intent = new Intent(FirstActivity.this, LoadCutiCancellationActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        imgProfile.setOnClickListener(imgProfileListener());
        imgCover.setOnClickListener(imgCoverListener());


    }

    private View.OnClickListener imgCoverListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImageChooser.show();
            }
        };
    }


    private View.OnClickListener imgProfileListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFotoPicker();
            }
        };
    }


    private void showCoverPicker() {
        easyImageFlag = EASY_IMAGE_FLAG_COVER;
        EasyImage.openChooser(FirstActivity.this, "Choose an apps", true);
    }


    private void showFotoPicker() {
        easyImageFlag = EASY_IMAGE_FLAG_PROFILE;
        EasyImage.openChooser(FirstActivity.this, "Choose an apps", true);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, FirstActivity.this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
                Snackbar.make(rootView, getResources().getString(R.string.prompt_error_photo), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource imageSource) {
                onPhotoReturned(file);

            }

            @Override
            public void onCanceled(EasyImage.ImageSource imageSource) {
                if (imageSource == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(FirstActivity.this);
                    if (photoFile != null) photoFile.delete();
                }
            }


        });
    }

    private void onPhotoReturned(final File photoFile) {
        try {
            if (easyImageFlag == EASY_IMAGE_FLAG_PROFILE) {
                Picasso.with(this)
                        .load(photoFile)
                        .resize(100, 100)
                        .centerCrop()
                        .into(imgProfile);


                //            imgProfile.setImageBitmap(myBitmap);

                new Delete().from(ImageSource.class)
                        .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.PROFILE_PICTURE))
                        .queryClose();

                ImageSource cImage = new ImageSource();
                cImage.key = ImageSource.PROFILE_PICTURE;
                cImage.imageName = photoFile.getName();
                cImage.imagePath = photoFile.getPath();
                cImage.save();
            } else if (easyImageFlag == EASY_IMAGE_FLAG_COVER) {
                Picasso.with(this)
                        .load(photoFile)
                        .fit()
                        .centerCrop()
                        .into(imgCover);
//                Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
//
//                imgCover.setImageBitmap(myBitmap);

                new Delete().from(ImageSource.class)
                        .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.BACKGROUND_HEADER))
                        .queryClose();

                ImageSource cImage = new ImageSource();
                cImage.key = ImageSource.BACKGROUND_HEADER;
                cImage.imageName = photoFile.getName();
                cImage.imagePath = photoFile.getPath();
                cImage.save();

                GeneralVar generalVar = new GeneralVar();
                generalVar.key = GeneralVar.KEY_COVER_SOURCE;
                generalVar.value = GeneralVar.VALUE_MEDIA_SOURCE;
                generalVar.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void fillCover() {
        try {
            GeneralVar gv = new Select().from(GeneralVar.class)
                    .where(Condition.column(GeneralVar$Table.KEY).eq(GeneralVar.KEY_COVER_SOURCE))
                    .querySingle();

            ImageSource img = new Select().from(ImageSource.class)
                    .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.BACKGROUND_HEADER))
                    .querySingle();

            if (img == null)
                return;

            if (gv.value.equalsIgnoreCase(GeneralVar.VALUE_APP_SOURCE)) {
                int imageSource = Integer.parseInt(img.imageName);
                Picasso.with(this)
                        .load(imageSource)
                        .fit()
                        .centerCrop()
                        .into(imgCover);

//                imgCover.setImageResource(imageSource);
            } else {
                File imgFile = new File(img.imagePath);
                if (imgFile.exists()) {
                    Picasso.with(this)
                            .load(imgFile)
                            .fit()
                            .centerCrop()
                            .into(imgCover);
//
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    imgCover.setImageBitmap(myBitmap);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void filProfilePicture() {
        try {
            ImageSource img = new Select().from(ImageSource.class)
                    .where(Condition.column(ImageSource$Table.KEY).eq(ImageSource.PROFILE_PICTURE))
                    .querySingle();

            if (img == null)
                return;

            File imgFile = new File(img.imagePath);

            if (imgFile.exists()) {
                Picasso.with(this)
                        .load(imgFile)
                        .fit()
                        .centerCrop()
                        .into(imgProfile);

//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                imgProfile.setImageBitmap(myBitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fillUserID() {
        txtUserID.setText((new Select().from(User.class).querySingle()).UserId);
    }

    public void fillRole() {
        User cUser = new Select().from(User.class).querySingle();
        txtRole.setText((new Select().from(Role.class).where(new SQLCondition[]{Condition.column("roleid").eq(cUser.idRole)}).querySingle()).RoleDesc);
    }

    public void fillJabatan() {
        User cUser = new Select().from(User.class).querySingle();
        txtJabatan.setText((new Select().from(Jabatan.class).where(new SQLCondition[]{Condition.column("KodeLevel").eq(cUser.kodeLevel)}).querySingle()).NamaJabatan);
    }

    public void initMainDrawer() {
        MenuItem approval = navView.getMenu().findItem(R.id.nav_approval);
        MenuItem formApproval = navView.getMenu().findItem(R.id.nav_approval_form);
        MenuItem cutiApproval = navView.getMenu().findItem(R.id.nav_approval_cuti);
        MenuItem cutiCancellationApproval = navView.getMenu().findItem(R.id.nav_approval_cuti_cancel);
        MenuItem absensiCancellationApproval = navView.getMenu().findItem(R.id.nav_approval_absensi_cancel);

        MenuItem absensiCancel = navView.getMenu().findItem(R.id.nav_absensi_cancellation);
        MenuItem cutiCancel = navView.getMenu().findItem(R.id.nav_cuti_cancellation);
        MenuItem absensiView = navView.getMenu().findItem(R.id.nav_view);

        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_EMPLOYEE)) {
            approval.setVisible(false);
            formApproval.setVisible(false);
            cutiApproval.setVisible(false);
            cutiCancellationApproval.setVisible(false);
            absensiCancellationApproval.setVisible(false);
            return;
        }


        approval.setVisible(true);
        formApproval.setVisible(true);
        cutiApproval.setVisible(true);
        cutiCancellationApproval.setVisible(true);
        absensiCancellationApproval.setVisible(true);

        absensiView.setVisible(false);
        absensiCancel.setVisible(false);
        cutiCancel.setVisible(false);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        boolean flag = false;

        if (famFirst.isExpanded()) {
            famFirst.collapse();
            flag = true;
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            flag = true;
        }

        if (!flag) {
            super.onBackPressed();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                ((MyProfilFragment) this.getFragmentManager().findFragmentById(R.id.layContainer)).onRefresh();
                break;
            case R.id.action_change_cover:
                dialogImageChooser.show();
                break;

            case R.id.action_change_profile:
                showFotoPicker();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                switch (item.getItemId()) {
                    case R.id.nav_profile:
                        MyProfilFragment myProfilFragment = new MyProfilFragment().newInstance();
                        FirstActivity.this.setFragment(R.id.layContainer, myProfilFragment, TRANSITION_FADE);
                        break;
                    case R.id.nav_absensi:

                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, AbsensiListActivity.class));
                        break;
                    case R.id.nav_cuti:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, CutiListActivity.class));

                        break;
                    case R.id.nav_cuti_cancellation:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, CutiCancellationListActivity.class));

                        break;
                    case R.id.nav_absensi_cancellation:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, AbsensiCancellationListActivity.class));
                        break;
                    case R.id.nav_view:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, AbsensiViewActivity.class));
                        break;
                    case R.id.nav_approval_form:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, AbsensiApprovalActivity.class));

                        break;
                    case R.id.nav_approval_cuti:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, CutiApprovalActivity.class));

                        break;
                    case R.id.nav_approval_cuti_cancel:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, CutiCancellationApprovalActivity.class));

                        break;
                    case R.id.nav_approval_absensi_cancel:
                        FirstActivity.this.startActivity(new Intent(FirstActivity.this, AbsensiCancellationApprovalActivity.class));

                        break;
                    case R.id.nav_change_role:
                        Intent intent;
                        intent = new Intent(FirstActivity.this, LoginRoleActivity.class);
                        FirstActivity.this.startActivity(intent);
                        break;
                    case R.id.nav_sign_out:
                        performLogout();
                        intent = new Intent(FirstActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        FirstActivity.this.startActivity(intent);
                        FirstActivity.this.finish();
                        break;
                }
                famFirst.collapseImmediately();
            }
        }, 250);
        return true;
    }

    private void performLogout() {
        setLogout();
        syncLogout();

    }


    private void syncLogout() {
        GcmDal.deleteToken();
    }

    private void setLogout() {
        User user = new Select().from(User.class).querySingle();
        user.LoginStatus = User.LOGOUT;
        user.save();

        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {

                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    FirebaseInstanceId.getInstance().getToken();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }


    public void onFragmentInteractionBlack(String uri) {
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
