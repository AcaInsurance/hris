package aca.com.hris;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import aca.com.hris.Adapter.CancellationAbsensiAdapter;
import aca.com.hris.Database.User;
import aca.com.hris.Fragment.AbsensiCancellationFragment;
import aca.com.hris.Fragment.AbsensiFragment;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.Utility;

public class AbsensiCancellationActivity extends BaseActivity
        implements
        AbsensiFragment.onAbsensiFormListener,
        AbsensiCancellationFragment.onFormCancellationFragmentListener {


    private static final String TAG = "AbsensiCancellationActivity";
    private static final String HEADER = "HEADER";
    private static final String DETAIL = "DETAIL";
    private int action;
    private String noTransaksi;
    private String noTransaksiDetail;
    private int position;

    private boolean saveSuccess = false;

    public void setSaveSuccess(boolean saveSuccess) {
        this.saveSuccess = saveSuccess;
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_form_cancellation;
    }

    @Override
    protected String setTitle() {
        return "Form Cancellation";
    }

    @Override
    protected void getIntentData() {
        try {
            Bundle bundle = getIntent().getExtras();

            if (bundle == null)
                return;

            action = bundle.getInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);
            noTransaksi = bundle.getString(CancellationAbsensiAdapter.BUNDLE_NO_TRANSAKSI_ID, UtilBundle.BUNDLE_INVALID_FLAG + "");
            noTransaksiDetail = bundle.getString(CancellationAbsensiAdapter.BUNDLE_NO_TRANSAKSI, UtilBundle.BUNDLE_INVALID_FLAG + "");
            position = bundle.getInt(UtilBundle.POSITION, 0);


            setAction(action);
            invalidateOptionsMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initFragment() {
        setFragment(R.id.fragmentHeader, new AbsensiCancellationFragment().newInstance(noTransaksi, action), TRANSITION_FADE, HEADER);
        setFragment(R.id.fragmentDetail, new AbsensiFragment().newInstance(noTransaksiDetail, action, true), TRANSITION_FADE, DETAIL);
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
    public void setAction(int action) {
        this.action = action;
        invalidateOptionsMenu();
    }



    private void loadData() {
        if (noTransaksi.isEmpty())
            return;

        AbsensiFragment fragment = (AbsensiFragment) getFragmentManager().findFragmentByTag(DETAIL);
        fragment.onLoad(noTransaksi);

        AbsensiCancellationFragment fragmentHeader = (AbsensiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
        fragmentHeader.onLoad(noTransaksi);

    }

    public void onSave() {
        AbsensiFragment fragment = (AbsensiFragment) getFragmentManager().findFragmentByTag(DETAIL);

        AbsensiCancellationFragment fragmentDemo = (AbsensiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
        fragmentDemo.onSave(fragment.getList());
    }

    public void onEdit() {
        AbsensiCancellationFragment fragmentDemo = (AbsensiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
        fragmentDemo.onEdit();
    }

    public void onDelete() {
        try {
            final AlertDialog.Builder builder =
                    new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(getString(R.string.prompt_delete_dialog));
            builder.setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AbsensiCancellationFragment fragment = (AbsensiCancellationFragment) AbsensiCancellationActivity.this
                            .getFragmentManager().findFragmentByTag(HEADER);
                    fragment.onDelete();
                }
            });
            builder.setNegativeButton("TIDAK", null);
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onUpdate() {
        AbsensiFragment fragment = (AbsensiFragment) getFragmentManager().findFragmentByTag(DETAIL);

        AbsensiCancellationFragment fragmentDemo = (AbsensiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
        fragmentDemo.onUpdate(fragment.getList());
    }

    public void onDeleteCompleted() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(UtilBundle.POSITION, position);
        setResult(UtilBundle.RESULT_CODE_DELETE_FORM, returnIntent);
        finish();
    }


    public void onNewCompleted() {
        Intent returnIntent = new Intent();
        setResult(UtilBundle.RESULT_CODE_NEW_FORM, returnIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form_cancellation, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem save = menu.findItem(R.id.action_save);
        MenuItem edit = menu.findItem(R.id.action_edit);
        MenuItem delete = menu.findItem(R.id.action_delete);
        MenuItem update = menu.findItem(R.id.action_update);
        MenuItem mail = menu.findItem(R.id.action_mail);


        if (action == UtilBundle.ACTION_BUNDLE_NEW) {
            save.setVisible(true);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            mail.setVisible(false);
        } else if (action == UtilBundle.ACTION_BUNDLE_EDIT) {
            save.setVisible(false);
            edit.setVisible(true);
            delete.setVisible(true);
            update.setVisible(false);
            mail.setVisible(false);
        } else if (action == UtilBundle.ACTION_BUNDLE_UPDATE) {
            save.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(true);
            mail.setVisible(false);
        } else if (action == UtilBundle.ACTION_BUNDLE_NOT_EDIT) {
            save.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            mail.setVisible(false);
        } else if (action == UtilBundle.ACTION_BUNDLE_MAIL) {
            save.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            mail.setVisible(true);
        } else if (action == UtilBundle.ACTION_BUNDLE_FAIL_LOADING) {
            save.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            mail.setVisible(false);
        }

        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_HEAD_EMPLOYEE)) {
            save.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            mail.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onFinish();
                return true;

            case R.id.action_save:
                Utility.hideSoftKeyboard(this);
                onSave();
                break;
            case R.id.action_edit:
                onEdit();
                break;
            case R.id.action_delete:
                onDelete();
                break;
            case R.id.action_update:
                Utility.hideSoftKeyboard(this);
                onUpdate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        onFinish();
    }

    @Override
    public void onSetAction(int action) {

    }
    private void onFinish() {
        if (saveSuccess) {
            onNewCompleted();
        } else
            finish();
    }


}
