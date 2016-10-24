package aca.com.hris;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.Utility;
import butterknife.ButterKnife;


public abstract class BaseActivity extends AppCompatActivity {
    static final int TRANSITION_SLIDE = 0;
    static final int TRANSITION_FADE = 1;
    static final int TRANSITION_MASK = 2;
    public static final int INVALID_FLAG = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        UtilDate.setAutoDateTime(this);
        Utility.setLocale(this);
        ButterKnife.bind(this);

        setTitle(setTitle());
        getIntentData();
//        initToolbar();
        initFragment();
        initInstances();
        onCreate();
        initCoachBase();
        registerListener();
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    protected abstract int getContentView();
    protected abstract String setTitle();

    protected abstract void getIntentData();
    protected abstract void initFragment();
    protected abstract void initInstances();
    protected abstract void onCreate();
    protected abstract void initCoachBase();
    protected abstract void registerListener();


    @Override
    protected void onResume() {
        super.onResume();
        UtilDate.setAutoDateTime(this);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void setFragment (int container, Fragment fragment, int transition, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (transition) {
            case TRANSITION_SLIDE :
                ft.setCustomAnimations(R.anim.slide_left_enter,
                        R.anim.slide_left_exit,
                        R.anim.slide_right_enter,
                        R.anim.slide_right_exit);
                break;

            case TRANSITION_FADE :
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                break;

            case TRANSITION_MASK :
                ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                break;
        }

        ft.replace(container, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }


    public void setFragment (int container, Fragment fragment, int transition) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch (transition) {
            case TRANSITION_SLIDE :
                ft.setCustomAnimations(R.anim.slide_left_enter,
                        R.anim.slide_left_exit,
                        R.anim.slide_right_enter,
                        R.anim.slide_right_exit);
                break;

            case TRANSITION_FADE :
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                break;

            case TRANSITION_MASK :
                ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                break;
        }

        ft.replace(container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /* OPTION MENU */



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_absensi_form, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem save = menu.findItem(R.id.action_save);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

        }

        return super.onOptionsItemSelected(item);
    }


    /******SHOW PROGRESS*******/

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View formView, final View progressView) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                formView.setVisibility(show ? View.GONE : View.VISIBLE);
                formView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        formView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                progressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
            } else {
                progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                formView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

}
