package aca.com.hris;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import aca.com.hris.Fragment.AbsensiApprovalFragment;
import aca.com.hris.Fragment.CutiApprovalFragment;
import aca.com.hris.Fragment.CutiCancellationApprovalFragment;

public class CutiCancellationApprovalActivity extends BaseActivity implements CutiCancellationApprovalFragment.OnCutiCancellationApprovalListener {

    private boolean hideSave;
    @Override
    protected int getContentView() {
        return R.layout.activity_cuti_cancellation_approval;
    }

    @Override
    protected String setTitle() {
        return "Cuti Cancellation Approval";
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
        hideSave = true;
        invalidateOptionsMenu();
    }

    @Override
    protected void initCoachBase() {

    }

    @Override
    protected void registerListener() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cuti_cancellation_approval, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem save = menu.findItem(R.id.action_save);


        if (hideSave)
            save.setVisible(false);
        else
            save.setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBack();
                return true;

            case R.id.action_save:
                onSave();
                break;

            case R.id.action_refresh:
                CutiCancellationApprovalFragment fragment = (CutiCancellationApprovalFragment) getFragmentManager().findFragmentById(R.id.fragment);
                fragment.refreshList();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        onBack();
    }


    public void onBack(){
        if (!hideSave) {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.prompt_save_dialog));
            builder.setPositiveButton("SIMPAN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CutiCancellationApprovalFragment fragment = (CutiCancellationApprovalFragment) CutiCancellationApprovalActivity.this.getFragmentManager().findFragmentById(R.id.fragment);
                    fragment.callApprovalService();
                }
            });
            builder.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
        else
            finish();
    }


    public void onSave() {
        CutiCancellationApprovalFragment fragment = (CutiCancellationApprovalFragment) getFragmentManager().findFragmentById(R.id.fragment);
        fragment.callApprovalService();
    }

    @Override
    public void setAction(boolean hideSave) {
        this.hideSave = hideSave;
        invalidateOptionsMenu();
    }
}
