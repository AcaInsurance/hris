package aca.com.hris.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import aca.com.hris.R;
import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.Utility;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.EasyImage;


public abstract class BaseFragment extends Fragment {

    LayoutInflater layoutInflater;
    ViewGroup container;

    ProgressBar progressLoading;
    Button buttonRetry;
    View rootPanel;
    Toast toast;

    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getViewLayout(), container, false);
        ButterKnife.bind(this, view);
        getArgsData();
        initEasyImage();
        initInstance(view);
        init(view);

        setProgressLoading(getProgressLoading());
        setButtonRetry(getButtonRetry());
        setRootPanel(getRootPanel());

        registerListener(view);
        onCreate();

        return view;
    }



    private void initEasyImage() {
        EasyImage.configuration(getActivity())
                .setImagesFolderName(getString(R.string.app_name))
                .saveInAppExternalFilesDir()
                .setCopyExistingPicturesToPublicLocation(true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    protected abstract void onCreate();
    protected abstract void getArgsData();
    protected abstract int getViewLayout();
    protected abstract void initInstance(View view);
    protected abstract void init(View view);
    protected abstract void registerListener(View view);

    protected abstract View getRootPanel();
    protected abstract Button getButtonRetry();
    protected abstract ProgressBar getProgressLoading();


    /* Fragment Listener Callback **/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            context = activity;
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        UtilDate.setAutoDateTime(context);
    }

    public void setProgressLoading(ProgressBar progressLoading) {
        this.progressLoading = progressLoading;
    }

    public void setButtonRetry(Button buttonRetry) {
        this.buttonRetry = buttonRetry;
    }
    public void setRootPanel(View root) {
        this.rootPanel = root;
    }
    /******SHOW PROGRESS*******/

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//                rootPanel.setVisibility(show ? View.GONE : View.VISIBLE);
//                rootPanel.animate().setDuration(shortAnimTime).alpha(
//                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        rootPanel.setVisibility(show ? View.GONE : View.VISIBLE);
//                    }
//                });
//
//                progressLoading.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//                progressLoading.animate().setDuration(shortAnimTime).alpha(
//                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        progressLoading.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//                    }
//                });
//
//            } else {
//                progressLoading.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//                rootPanel.setVisibility(show ? View.GONE : View.VISIBLE);
//            }
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

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
