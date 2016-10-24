package aca.com.hris;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.HashMap;

import aca.com.hris.Database.GcmMapping;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.GcmDal;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.Util.Utility;
import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.lblEmail)
    TextInputLayout lblEmail;
    @Bind(R.id.lblPassword)
    TextInputLayout lblPassword;

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private Button mEmailSignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin(mEmailView.getText().toString(), mPasswordView.getText().toString());
            }
        });

        mProgressView = findViewById(R.id.login_progress);
    }

    private void enableLogin (boolean enable) {
        mEmailSignInButton.setEnabled(enable);
    }


    private void attemptLogin(String email, String password) {
        if (!validateLogin(email, password)) {
            return;
        } else {
            Utility.hideSoftKeyboard(this);
            showProgress(true);
            enableLogin(false);

            password = Utility.md5(password);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("userID", email);
            map.put("userPass", password);

            WebServices ws = new WebServices(LoginActivity.this,
                    "ValidateLoginUser",
                    map,
                    getResources().getStringArray(R.array.ValidateLoginUser_post),
                    getResources().getStringArray(R.array.ValidateLoginUser_get)
            ) {
                @Override
                protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                    enableLogin(true);
                    showProgress(false);

                    if (arrList != null) {
                        insertDB(arrList);
                        startActivity(new Intent(LoginActivity.this, LoginRoleActivity.class));
                        LoginActivity.this.finish();
                    } else {
                        lblPassword.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    }
                }

                private void insertDB(ArrayList<HashMap<String, String>> arrList) {
                    new Delete().from(User.class).queryClose();
                    User user = new User();
                    user.UserId = mEmailView.getText().toString();
                    user.EmpCode = arrList.get(0).get(getResources().getStringArray(R.array.ValidateLoginUser_get)[0]);
                    user.UserPass = Utility.md5(mPasswordView.getText().toString());
                    user.LoginStatus = User.LOGIN;
                    user.save();

                    GcmMapping gcmMapping = new Select().from(GcmMapping.class).querySingle();
                    if (gcmMapping == null)
                        gcmMapping = new GcmMapping();

                    gcmMapping.IdKaryawan = User.getEmpCode();
                    gcmMapping.IsLogin = String.valueOf(true);
                    gcmMapping.save();
                    GcmDal.sendRegistrationToken(LoginActivity.this);
                }

                @Override
                protected void onFailed(String message) {
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    showProgress(false);
                    enableLogin(true);
                }

                @Override
                protected void onCancel() {
                    showProgress(false);
                    enableLogin(true);
                }
            };
            ws.execute();
        }
    }

    private boolean validateLogin(String email, String password) {
        boolean valid = true;

        lblEmail.setErrorEnabled(false);
        lblPassword.setErrorEnabled(false);

        if (TextUtils.isEmpty(email)) {
            lblEmail.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            lblPassword.setError(getString(R.string.error_field_required));
            mPasswordView.requestFocus();
            valid = false;
        }

        return valid;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        }
    }

}

