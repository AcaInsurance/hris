package aca.com.hris;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import aca.com.hris.Adapter.CancellationCutiAdapter;
import aca.com.hris.Database.User;
import aca.com.hris.Fragment.CutiCancellationFragment;
import aca.com.hris.Fragment.CutiFragment;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.Utility;
import butterknife.Bind;

public class CutiCancellationActivity extends BaseActivity
        implements
        CutiFragment.onCutiFormListener,
        CutiCancellationFragment.onCutiCancellationFragmentListener {


    @Bind(R.id.rootView)
    CoordinatorLayout rootView;
    private int action;
    private String noTransaksi;
    private static final String TAG = "CutiCancellationActivity";
    private static final String HEADER = "HEADER";
    private static final String DETAIL = "DETAIL";
    private String noTransaksiCuti;
    private int position;

    private boolean saveSuccess = false;

    public void setSaveSuccess(boolean saveSuccess) {
        this.saveSuccess = saveSuccess;
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_cuti_cancellation_form;
    }

    @Override
    protected String setTitle() {
        return "Cuti Cancellation";
    }

    @Override
    protected void getIntentData() {
        try {
            Bundle bundle = getIntent().getExtras();

            if (bundle == null)
                return;

            action = bundle.getInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);
            noTransaksi = bundle.getString(CancellationCutiAdapter.BUNDLE_NO_TRANSAKSI_ID, UtilBundle.BUNDLE_INVALID_FLAG + "");
            noTransaksiCuti = bundle.getString(CancellationCutiAdapter.BUNDLE_NO_TRANSAKSI, UtilBundle.BUNDLE_INVALID_FLAG + "");
            position = bundle.getInt(UtilBundle.POSITION, 0);

            setAction(action);
            invalidateOptionsMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initFragment() {
        setFragment(R.id.fragmentHeader, new CutiCancellationFragment().newInstance(noTransaksi, action), TRANSITION_FADE, HEADER);
        setFragment(R.id.fragmentDetail, new CutiFragment().newInstance(noTransaksiCuti, action, true), TRANSITION_FADE, DETAIL);
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

        CutiFragment fragment = (CutiFragment) getFragmentManager().findFragmentByTag(DETAIL);
        fragment.onLoad(noTransaksi);

        CutiCancellationFragment fragmentHeader = (CutiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
        fragmentHeader.onLoad(noTransaksi);

    }

    public void onSave() {
        CutiFragment fragment = (CutiFragment) getFragmentManager().findFragmentByTag(DETAIL);

        CutiCancellationFragment fragmentDemo = (CutiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
        fragmentDemo.onSave(fragment.getList());
    }

    public void onEdit() {
        CutiCancellationFragment fragmentDemo = (CutiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
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
                    CutiCancellationFragment fragment = (CutiCancellationFragment) CutiCancellationActivity.this
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
        CutiFragment fragment = (CutiFragment) getFragmentManager().findFragmentByTag(DETAIL);

        CutiCancellationFragment fragmentDemo = (CutiCancellationFragment) getFragmentManager().findFragmentByTag(HEADER);
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
        getMenuInflater().inflate(R.menu.menu_cuti_cancellation, menu);
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
                Utility.hideSoftKeyboard(this);
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

    private void onFinish() {
        if (saveSuccess) {
            onNewCompleted();
        } else
            finish();
    }
}
