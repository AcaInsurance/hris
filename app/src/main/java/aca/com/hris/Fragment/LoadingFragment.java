package aca.com.hris.Fragment;


import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import aca.com.hris.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingFragment extends BaseFragment {
//    @Bind(R.id.progressLoading)
//    ProgressBar progressLoading;
//    @Bind(R.id.buttonRetry)
//    Button buttonRetry;

    public static LoadingFragment newInstance() {
        Bundle args = new Bundle();

        LoadingFragment fragment = new LoadingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    protected void onCreate() {

    }

    @Override
    protected void getArgsData() {

    }

    @Override
    protected int getViewLayout() {
        return R.layout.layout_loading_circle;
    }

    @Override
    protected void initInstance(View view) {

    }

    @Override
    protected void init(View view) {

    }

    @Override
    protected void registerListener(View view) {
    }

    @Override
    protected View getRootPanel() {
        return null;
    }

    @Override
    protected Button getButtonRetry() {
        return null;
    }

    @Override
    protected ProgressBar getProgressLoading() {
        return null;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        try {
            progressLoading.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            rootPanel.setVisibility(show ? View.GONE : View.VISIBLE);
            buttonRetry.setVisibility(View.GONE);

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void showRetry(final boolean show) {
        try {
            if (show) {
                buttonRetry.setVisibility(View.VISIBLE);
                rootPanel.setVisibility(View.GONE);
                progressLoading.setVisibility(View.INVISIBLE);
            }
            else {
                buttonRetry.setVisibility(View.GONE);
                rootPanel.setVisibility(View.VISIBLE);
                progressLoading.setVisibility(View.VISIBLE);
            }

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
}
