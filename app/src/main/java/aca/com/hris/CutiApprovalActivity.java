package aca.com.hris;

import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import aca.com.hris.Fragment.AbsensiApprovalFragment;
import aca.com.hris.Fragment.CutiApprovalFragment;

public class CutiApprovalActivity extends BaseActivity implements CutiApprovalFragment.OnCutiApprovalListener{

    private boolean hideSave;
    @Override
    protected int getContentView() {
        return R.layout.activity_absensi_cuti_approval;
    }

    @Override
    protected String setTitle() {
        return "Cuti Approval";
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
        getMenuInflater().inflate(R.menu.menu_absensi_cuti_approval, menu);
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
                CutiApprovalFragment fragment = (CutiApprovalFragment) getFragmentManager().findFragmentById(R.id.fragment);
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
                    CutiApprovalFragment fragment = (CutiApprovalFragment) CutiApprovalActivity.this.getFragmentManager().findFragmentById(R.id.fragment);
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
        CutiApprovalFragment fragment = (CutiApprovalFragment) getFragmentManager().findFragmentById(R.id.fragment);
        fragment.callApprovalService();
    }

    @Override
    public void setAction(boolean hideSave) {
        this.hideSave = hideSave;
        invalidateOptionsMenu();
    }

}
