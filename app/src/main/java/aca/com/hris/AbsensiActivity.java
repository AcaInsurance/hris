package aca.com.hris;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import aca.com.hris.Adapter.AbsensiAdapter;
import aca.com.hris.Database.User;
import aca.com.hris.Fragment.AbsensiFragment;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.Utility;
import aca.com.hris.Util.Var;
import butterknife.Bind;

public class AbsensiActivity extends BaseActivity implements AbsensiFragment.onAbsensiFormListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.viewCoordinatorLayout)
    CoordinatorLayout viewCoordinatorLayout;
    private int action;
    private String paramNoTransaksi;
    private boolean isLoad;
    private String MAIN = "MAIN";
    private int position;

    private boolean saveSuccess = false;

    public void setSaveSuccess(boolean saveSuccess) {
        this.saveSuccess = saveSuccess;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_absensi_form;
    }

    @Override
    protected String setTitle() {
        return "Absensi Form";
    }

    @Override
    protected void getIntentData() {
        try {
            Bundle bundle = getIntent().getExtras();

            if (bundle == null)
                return;

            action = bundle.getInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);
            paramNoTransaksi = bundle.getString(AbsensiAdapter.BUNDLE_NO_TRANSAKSI, UtilBundle.BUNDLE_INVALID_FLAG + "");
            isLoad = bundle.getBoolean(UtilBundle.BUNDLE_ON_LOAD, false);
            position = bundle.getInt(UtilBundle.POSITION, 0);

            onSetAction(action);
            invalidateOptionsMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initFragment() {
        setFragment(R.id.layContainer, new AbsensiFragment().newInstance(paramNoTransaksi, action, isLoad), TRANSITION_FADE, MAIN);

    }

    @Override
    protected void initInstances() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onCreate() {
//        loadData();
    }

    private void loadData() {
        if (paramNoTransaksi.isEmpty())
            return;

        AbsensiFragment absensiFormFragment = (AbsensiFragment) getFragmentManager().findFragmentById(R.id.layContainer);
        absensiFormFragment.onLoad(paramNoTransaksi);
    }

    @Override
    protected void initCoachBase() {
    }

    @Override
    protected void registerListener() {

    }

    public void onSave() {
        AbsensiFragment fragmentDemo = (AbsensiFragment) getFragmentManager().findFragmentById(R.id.layContainer);
        fragmentDemo.onSave();
    }

    public void onEdit() {
        AbsensiFragment fragmentDemo = (AbsensiFragment) getFragmentManager().findFragmentById(R.id.layContainer);
        fragmentDemo.onEdit();
    }

    public void onDelete() {
        try {

            new AlertDialog.Builder(this, Var.dialogTheme)
                .setMessage(getString(R.string.prompt_delete_dialog))
                .setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AbsensiFragment fragmentDemo = (AbsensiFragment) AbsensiActivity.this.getFragmentManager().findFragmentById(R.id.layContainer);
                        fragmentDemo.onDelete();
                    }
                })
                .setNegativeButton("TIDAK", null)
                .show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onUpdate() {
        AbsensiFragment fragmentDemo = (AbsensiFragment) getFragmentManager().findFragmentById(R.id.layContainer);
        fragmentDemo.onUpdate();
    }

    @Override
    public void onSetAction(int action) {
        this.action = action;
        invalidateOptionsMenu();
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
        getMenuInflater().inflate(R.menu.menu_absensi_form, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem save = menu.findItem(R.id.action_save);
        MenuItem edit = menu.findItem(R.id.action_edit);
        MenuItem delete = menu.findItem(R.id.action_delete);
        MenuItem update = menu.findItem(R.id.action_update);
        MenuItem mail = menu.findItem(R.id.action_mail);
        MenuItem image = menu.findItem(R.id.action_image);

        image.setVisible(false);

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
        } else if (action == UtilBundle.ACTION_BUNDLE_IMAGE) {
            save.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            mail.setVisible(false);
            image.setVisible(true);
        } else if (action == UtilBundle.ACTION_BUNDLE_FAIL_LOADING) {
            save.setVisible(false);
            edit.setVisible(false);
            delete.setVisible(false);
            update.setVisible(false);
            mail.setVisible(false);
            image.setVisible(false);
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
            case R.id.action_image:
                onReSendImage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onReSendImage() {
        AbsensiFragment fragmentDemo = (AbsensiFragment) getFragmentManager().findFragmentById(R.id.layContainer);
        fragmentDemo.syncAttachment();
    }


    @Override
    public void onBackPressed() {
        onFinish();
    }

    private void onFinish() {
        if (saveSuccess) {
            onNewCompleted();
        } else
            finish();
    }

}
