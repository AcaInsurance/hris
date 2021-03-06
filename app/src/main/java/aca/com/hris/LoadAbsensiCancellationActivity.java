package aca.com.hris;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import aca.com.hris.Fragment.LoadCutiCancellationFragment;
import aca.com.hris.Fragment.LoadFormCancellationFragment;
import aca.com.hris.Fragment.LoadFormCancellationFragment;
import aca.com.hris.Util.UtilBundle;

public class LoadAbsensiCancellationActivity extends BaseActivity {

    private boolean saveSuccess = false;

    public void setSaveSuccess(boolean saveSuccess) {
        this.saveSuccess = saveSuccess;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_load_form_cancellation;
    }

    @Override
    protected String setTitle() {
        return "List Absensi";
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
        getMenuInflater().inflate(R.menu.menu_load_form_cancellation, menu);
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
                onFinish();
                return true;

            case R.id.action_refresh:
                LoadFormCancellationFragment fragment = (LoadFormCancellationFragment) getFragmentManager().findFragmentById(R.id.fragment);
                fragment.onRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UtilBundle.REQUEST_CODE_NEW_FORM && resultCode == UtilBundle.RESULT_CODE_NEW_FORM) {
            LoadFormCancellationFragment fragment = (LoadFormCancellationFragment) getFragmentManager().findFragmentById(R.id.fragment);
            fragment.onRefresh();
            setSaveSuccess(true);
        }
    }



    public void onNewCompleted() {
        Intent returnIntent = new Intent();
        setResult(UtilBundle.RESULT_CODE_NEW_FORM, returnIntent);
        finish();
    }

    private void onFinish() {
        if (saveSuccess) {
            onNewCompleted();
        } else
            finish();
    }

    @Override
    public void onBackPressed() {
        onFinish();
    }


}
