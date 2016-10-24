package aca.com.hris;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import aca.com.hris.Database.User;
import aca.com.hris.Fragment.AbsensiListFragment;
import aca.com.hris.Util.UtilBundle;
import butterknife.Bind;
import butterknife.OnClick;

public class AbsensiListActivity extends BaseActivity implements AbsensiListFragment.OnFragmentInteractionListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fabMakeForm)
    FloatingActionButton fabMakeForm;

    @Override
    protected int getContentView() {
        return R.layout.activity_absensi_list;
    }

    @Override
    protected String setTitle() {
        return "History Absensi";
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

    private void hideFb() {
        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_HEAD_EMPLOYEE)) {
            fabMakeForm.setVisibility(View.GONE);
        }
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
        getMenuInflater().inflate(R.menu.menu_absensi_list, menu);
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
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_refresh:
                AbsensiListFragment fragment = (AbsensiListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
                fragment.onRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAbsensiListFragmentInteraction(String id) {

    }


    @OnClick(R.id.fabMakeForm)
    public void makeAForm() {
        Intent intent = new Intent(this, AbsensiActivity.class);
        startActivityForResult(intent, UtilBundle.REQUEST_CODE_NEW_FORM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UtilBundle.REQUEST_CODE_EDIT_FORM && resultCode == UtilBundle.RESULT_CODE_DELETE_FORM) {
            int position = data.getIntExtra(UtilBundle.POSITION, 0);

            AbsensiListFragment fragment = (AbsensiListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
            fragment.onDeleted(position);
        } else if (requestCode == UtilBundle.REQUEST_CODE_NEW_FORM && resultCode == UtilBundle.RESULT_CODE_NEW_FORM) {
            AbsensiListFragment fragment = (AbsensiListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
            fragment.onNewForm();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
