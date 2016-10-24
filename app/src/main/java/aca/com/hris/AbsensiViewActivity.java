package aca.com.hris;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import aca.com.hris.Fragment.AbsensiListFragment;
import aca.com.hris.Fragment.AbsensiViewFragment;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class AbsensiViewActivity extends BaseActivity implements AbsensiViewFragment.onAbsensiViewFragmentListener{

    @Override
    protected int getContentView() {
        return R.layout.activity_absensi_view;
    }

    @Override
    protected String setTitle() {
        return "Absensi View";
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void initCoachBase() {

    }

    @Override
    protected void registerListener() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_absensi_view, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_refresh:
                AbsensiViewFragment fragment = (AbsensiViewFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
                fragment.refreshList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void setAction(int action) {

    }
}
