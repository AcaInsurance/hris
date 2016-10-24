package aca.com.hris;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Adapter.MyCursorAdapter;
import aca.com.hris.Database.GeneralVar;
import aca.com.hris.Database.GeneralVar$Table;
import aca.com.hris.Database.Jabatan;
import aca.com.hris.Database.Jabatan$Table;
import aca.com.hris.Database.Role;
import aca.com.hris.Database.Role$Table;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.Util.UtilBundle;
import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginRoleActivity extends AppCompatActivity {

    @Bind(R.id.btnChooseJabatan)
    Button btnChooseJabatan;
    @Bind(R.id.btnChooseRole)
    Button btnChooseRole;

    MyCursorAdapter jabatanAdapter, roleAdapter;
    @Bind(R.id.login_progress)
    ProgressBar loginProgress;
    int count = 0;
    @Bind(R.id.btnNext)
    Button btnNext;
    @Bind(R.id.rootView)
    CoordinatorLayout rootView;
    private AlertDialog.Builder jabatanBuilder, roleBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_role);
        ButterKnife.bind(this);

        refresh();
        registerListener();

    }

    public void setCount(int count) {
        this.count = count;
    }


    private void refresh() {
        setCount(0);
        showProgress(true);
        enableNext(false);
        enableJabatan(false);
        enableRole(false);
        loadJabatan();
        loadRole();
    }

    private void enableRole(boolean enable) {
        btnChooseRole.setEnabled(enable);
    }

    private void enableJabatan(boolean enable) {
        btnChooseJabatan.setEnabled(enable);
    }

    private void registerListener() {
        btnChooseJabatan.setOnClickListener(btnChooseJabatanListener());
        btnChooseRole.setOnClickListener(btnChooseRoleListener());
        btnNext.setOnClickListener(btnNextListener());
    }

    private View.OnClickListener btnChooseRoleListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roleBuilder.show();
            }
        };
    }

    private View.OnClickListener btnChooseJabatanListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jabatanBuilder.show();
            }
        };
    }

    private View.OnClickListener btnNextListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextActivity();
            }
        };
    }

    private boolean gotoPickHeader() {
        int count = new Select().from(GeneralVar.class)
                .where(Condition.column(GeneralVar$Table.KEY).eq(GeneralVar.KEY_COVER_SOURCE))
                .query().getCount();

        if (count == 0) {
            return true;
        }
        return false;

    }

    private void gotoNextActivity() {
        if (gotoPickHeader()) {
            startActivity(new Intent(LoginRoleActivity.this, HeaderPickerActivity.class)
                    .putExtra(UtilBundle.PREV_ACTIVITY, UtilBundle.LOGIN_ROLE_ACTIVITY));
        } else {
            startActivity(new Intent(LoginRoleActivity.this, FirstActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        LoginRoleActivity.this.finish();
    }

    private void enableNext(boolean enable) {
        btnNext.setEnabled(enable);
    }

    private void loadJabatan() {
        HashMap<String, String> map = new HashMap<>();
        map.put("IdKaryawan", User.getEmpCode());

        WebServices ws = new WebServices(this,
                "GetJabatan",
                map,
                getResources().getStringArray(R.array.GetJabatan_post),
                getResources().getStringArray(R.array.GetJabatan_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String idValue = "";
                showProgress(false);
                if (arrList != null) {
                    insertJabatan(arrList);
                    bindJabatan();
                    idValue = jabatanAdapter.setSelected(btnChooseJabatan, 0);
                    setJabatan(idValue);
                    increaseCount();
                    enableJabatan(true);
                }
                else
                    Snackbar.make(rootView, R.string.check_your_connection, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    refresh();
                                }
                            }).show();

            }


            @Override
            protected void onFailed(String message) {
                Snackbar.make(rootView, R.string.check_your_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refresh();
                            }
                        }).show();
                showProgress(false);
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }

    private void setJabatan(String idValue) {
        User user = new Select().from(User.class).querySingle();
        user.kodeLevel = idValue;
        user.save();
    }

    private void setRole(String idValue) {
        User user = new Select().from(User.class).querySingle();
        user.idRole = idValue;
        user.save();
    }

    private void increaseCount() {
        count++;
        if (count == 2) {
            enableNext(true);
            count = 0;
        }
    }


    private void loadRole() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userID", User.getUserID());

        WebServices ws = new WebServices(this,
                "GetUserRole",
                map,
                getResources().getStringArray(R.array.GetUserRole_post),
                getResources().getStringArray(R.array.GetUserRole_get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                String idValue = "";
                showProgress(false);
                if (arrList != null) {
                    insertRole(arrList);
                    bindRole();
                    idValue = roleAdapter.setSelected(btnChooseRole, 0);
                    setRole(idValue);
                    increaseCount();
                    enableRole(true);
                }
                else
                    Snackbar.make(rootView, R.string.check_your_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refresh();
                            }
                        }).show();
            }


            @Override
            protected void onFailed(String message) {
                Snackbar.make(rootView, R.string.check_your_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                refresh();
                            }
                        }).show();
                showProgress(false);
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }

    private void insertJabatan(ArrayList<HashMap<String, String>> arrList) {
        Jabatan jabatan;
        new Delete().from(Jabatan.class).queryClose();

        for (HashMap<String, String> map : arrList) {
            jabatan = new Jabatan();
            jabatan.KodeLevel = map.get("KodeLevel");
            jabatan.NamaJabatan = map.get("NamaJabatan");

            jabatan.save();
        }

    }

    private void insertRole(ArrayList<HashMap<String, String>> arrList) {
        Role role;

        new Delete().from(Role.class).queryClose();

        for (HashMap<String, String> map : arrList) {
            role = new Role();
            role.roleid = map.get("roleid");
            role.RoleDesc = map.get("RoleDesc");

            role.save();
        }

    }

    private void bindJabatan() {
        Cursor cur = new Select().from(Jabatan.class).query();

        jabatanBuilder = new AlertDialog.Builder(this);
        jabatanAdapter = new MyCursorAdapter(this, cur, false, Jabatan$Table.KODELEVEL, Jabatan$Table.NAMAJABATAN);
        jabatanBuilder.setAdapter(jabatanAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = ((Cursor) jabatanAdapter.getItem(which)).getString(jabatanAdapter.getCursor().getColumnIndex(Jabatan$Table.KODELEVEL));
                String value = ((Cursor) jabatanAdapter.getItem(which)).getString(jabatanAdapter.getCursor().getColumnIndex(Jabatan$Table.NAMAJABATAN));
                btnChooseJabatan.setText(value);
                btnChooseJabatan.setTag(id);

                User user = new Select().from(User.class).querySingle();
                user.kodeLevel = btnChooseJabatan.getTag().toString();
                user.save();
            }

        });
        jabatanBuilder.setTitle("Pilih Jabatan");
    }


    private void bindRole() {
        Cursor cur = new Select().from(Role.class).query();

        roleBuilder = new AlertDialog.Builder(this);
        roleAdapter = new MyCursorAdapter(this, cur, false, Role$Table.ROLEID, Role$Table.ROLEDESC);
        roleBuilder.setAdapter(roleAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String id = ((Cursor) roleAdapter.getItem(which)).getString(roleAdapter.getCursor().getColumnIndex(Role$Table.ROLEID));
                String value = ((Cursor) roleAdapter.getItem(which)).getString(roleAdapter.getCursor().getColumnIndex(Role$Table.ROLEDESC));
                btnChooseRole.setText(value);
                btnChooseRole.setTag(id);

                User user = new Select().from(User.class).querySingle();
                user.idRole = btnChooseRole.getTag().toString();
                user.save();
            }
        });
        roleBuilder.setTitle("Pilih Role");
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            loginProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            loginProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
