package aca.com.hris;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import aca.com.hris.Database.User;
import aca.com.hris.Fragment.AbsensiCancellationListFragment;
import aca.com.hris.Fragment.CutiCancellationFragment;
import aca.com.hris.Fragment.CutiCancellationListFragment;
import aca.com.hris.Fragment.CutiListFragment;
import aca.com.hris.Util.UtilBundle;
import butterknife.Bind;
import butterknife.OnClick;

public class CutiCancellationListActivity extends BaseActivity implements
        CutiCancellationListFragment.onCutiCancellationListFragmentListener{

    @Bind(R.id.fabMakeForm)
    FloatingActionButton fabMakeForm;
    @Override
    protected int getContentView() {
        return R.layout.activity_cuti_cancellation_list;
    }

    @Override
    protected String setTitle() {
        return "History Cuti Cancellation";
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
        hideFb();
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
    private void hideFb() {
        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_HEAD_EMPLOYEE)) {
            fabMakeForm.setVisibility(View.GONE);
        }
    }



    @OnClick(R.id.fabMakeForm)
    public void makeAForm () {
        Intent intent = new Intent(this, LoadCutiCancellationActivity.class);
        startActivityForResult(intent, UtilBundle.REQUEST_CODE_NEW_FORM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UtilBundle.REQUEST_CODE_EDIT_FORM&& resultCode == UtilBundle.RESULT_CODE_DELETE_FORM) {
            int position = data.getIntExtra(UtilBundle.POSITION, 0);

            CutiCancellationListFragment fragment = (CutiCancellationListFragment) getFragmentManager().findFragmentById(R.id.fragment);
            fragment.onDeleted(position);
        }
        else if (requestCode == UtilBundle.REQUEST_CODE_NEW_FORM && resultCode == UtilBundle.RESULT_CODE_NEW_FORM) {
            CutiCancellationListFragment fragment = (CutiCancellationListFragment) getFragmentManager().findFragmentById(R.id.fragment);
            fragment.onNewForm();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cuti_cancellation_list, menu);
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
                finish();
                return true;

            case R.id.action_refresh:
                CutiCancellationListFragment fragment = (CutiCancellationListFragment) getFragmentManager().findFragmentById(R.id.fragment);
                fragment.onRefresh();
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