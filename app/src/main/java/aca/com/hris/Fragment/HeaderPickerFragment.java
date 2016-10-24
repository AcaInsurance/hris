package aca.com.hris.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;

import aca.com.hris.Adapter.CoverAdapter;
import aca.com.hris.Database.GeneralVar;
import aca.com.hris.Database.ImageSource;
import aca.com.hris.FirstActivity;
import aca.com.hris.R;
import aca.com.hris.Util.UtilBundle;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class HeaderPickerFragment extends BaseFragment {

    @Bind(R.id.gallery1)
    Gallery gallery;
    @Bind(R.id.image1)
    ImageView image1;
    @Bind(R.id.btnSelect)
    Button btnSelect;
    private int selected = 0;

    @Override
    protected void onCreate() {

    }

    @Override
    protected void getArgsData() {

    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_header_picker;
    }

    @Override
    protected void initInstance(View view) {

        gallery.setAdapter(new CoverAdapter(getActivity()));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                image1.setImageResource(CoverAdapter.imageIDs[position]);
                selected = position;
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralVar generalVar = new GeneralVar();
                generalVar.key = GeneralVar.KEY_COVER_SOURCE;
                generalVar.value = GeneralVar.VALUE_APP_SOURCE;
                generalVar.save();

                ImageSource imageSource = new ImageSource();
                imageSource.key = ImageSource.BACKGROUND_HEADER;
                imageSource.imageName = CoverAdapter.imageIDs[selected] + "";
                imageSource.save();

                if (getPrev().equalsIgnoreCase(UtilBundle.LOGIN_ROLE_ACTIVITY)) {
                    Intent intent = new Intent(getActivity(), FirstActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                getActivity().finish();

            }
        });
    }

    private String getPrev() {
        String prevActivity = "";
        try {
            if (getActivity().getIntent().getStringExtra(UtilBundle.PREV_ACTIVITY) != null) {
                prevActivity = getActivity().getIntent().getStringExtra(UtilBundle.PREV_ACTIVITY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prevActivity;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
