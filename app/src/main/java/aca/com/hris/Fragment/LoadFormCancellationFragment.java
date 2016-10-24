package aca.com.hris.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Adapter.LoadAbsensiForCancellationAdapter;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.PojoModel.FormListForCancellationModel;
import aca.com.hris.R;
import aca.com.hris.Retrofit.HrisAPI;
import aca.com.hris.Retrofit.HrisService;
import aca.com.hris.Widget.DividerItemDecoration;
import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoadFormCancellationFragment extends BaseFragment {

    @Bind(R.id.listViewForm)
    RecyclerView listViewForm;
    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnRetry)
    Button btnRetry;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.viewEmptyContent)
    TextView viewEmptyContent;

    private LinearLayoutManager linearLayoutManager;


    private LoadAbsensiForCancellationAdapter mAdapter;
    private List<FormListForCancellationModel> absensiList = null;

    private int page = 0;
    private boolean loadMore = false;
    private Subscription subscription;
    private HrisAPI loadListAPI;


    @Override
    protected void onCreate() {
        loadDataRetrofit();
    }


    private String getMethod() {
        return "LoadListAbsensiFormForCancellationJson";
    }

    private void loadDataRetrofit() {

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                loadListAPI = HrisService.createHrisService(getBody());
                subscription = loadListAPI.FormListForCancellation(getMethod())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(firstLoadObserver());
            }
        });

    }


    private Observer<? super List<FormListForCancellationModel>> firstLoadObserver() {
        return new Observer<List<FormListForCancellationModel>>() {

            @Override
            public void onCompleted() {
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e.getMessage(), e.getStackTrace());

                swipeContainer.setRefreshing(false);
                Snackbar.make(getView(), R.string.check_your_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadDataRetrofit();
                            }
                        })
                        .show();
            }

            @Override
            public void onNext(List<FormListForCancellationModel> listModel) {
                setAdapter(listModel);
                showEmptyContent(listModel == null || listModel.size() == 0 ? true : false);

            }
        };
    }

    public void showEmptyContent(boolean show) {
        viewEmptyContent.setVisibility(show ? View.VISIBLE : View.GONE);
    }



    private void setAdapter(List<FormListForCancellationModel> listModel) {
        absensiList = listModel;
        mAdapter = new LoadAbsensiForCancellationAdapter(getActivity(), absensiList);
        listViewForm.setAdapter(mAdapter);
    }


    private RequestBody getBody() {
        RequestBody requestBody;

        requestBody = new FormBody.Builder()
                .add("IdKaryawan", User.getEmpCode())
                .build();

        return requestBody;
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    protected void getArgsData() {

    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_load_form_cancellation;
    }

    @Override
    protected void initInstance(View view) {

    }

    @Override
    protected void init(View view) {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        listViewForm.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        listViewForm.addItemDecoration(itemDecoration);
        listViewForm.setItemAnimator(new SlideInUpAnimator());

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void registerListener(View view) {
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadFormCancellationFragment.this.onRefresh();
            }
        });
        btnRetry.setOnClickListener(btnRetryListener());
    }

    private View.OnClickListener btnRetryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        };
    }

    @Override
    protected View getRootPanel() {
        return listViewForm;
    }

    @Override
    protected Button getButtonRetry() {
        return btnRetry;
    }

    @Override
    protected ProgressBar getProgressLoading() {
        return pbLoading;
    }


    private void emptyList() {
        if (absensiList == null)
            return;

        absensiList.clear();
    }


    public void onRefresh() {
        loadDataRetrofit();
    }

    private void loadData() {
        HashMap<String, String> map;
        String methodName;
        int post, get;

        methodName = "LoadListAbsensiFormForCancellation";
        post = R.array.LoadListAbsensiFormForCancellation_post;
        get = R.array.LoadListAbsensiFormForCancellation_get;
        map = fillMap();

        if (!loadMore)
            showProgress(true);


        WebServices ws = new WebServices(getActivity(),
                methodName,
                map,
                getActivity().getResources().getStringArray(post),
                getActivity().getResources().getStringArray(get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {

                if (arrList == null) {
//                    Snackbar.make(getView(), getString(R.string.prompt_no_more_data_load), Snackbar.LENGTH_SHORT).show();
                    clearListView();
                    showProgress(false);

                } else {
                    if (!loadMore)
                        onSuccessLoadData(arrList);
                    else
                        onSuccessLoadMore(arrList);
                }
            }


            private void onSuccessLoadData(ArrayList<HashMap<String, String>> arrList) {
                try {
                    setAdapter(arrList);
                    page++;
                    showProgress(false);
                    swipeContainer.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            private void onSuccessLoadMore(ArrayList<HashMap<String, String>> arrList) {
                try {
//                    absensiList.addAll(arrList);
                    mAdapter.notifyDataSetChanged();

                    page++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            private void setAdapter(ArrayList<HashMap<String, String>> arrList) {
                try {
//                    absensiList = arrList;

                    mAdapter = new LoadAbsensiForCancellationAdapter(getActivity(), absensiList);
                    listViewForm.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailed(String message) {
                try {
                    swipeContainer.setRefreshing(false);
                    if (!isAdded())
                        return;

                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
                    showRetry(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onCancel() {

            }
        };
        ws.execute();
    }


    public void onDeleted(int position) {
        try {
            absensiList.remove(position);
            mAdapter.notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearListView() {
        try {
            if (absensiList.size() == 0) {
                if (absensiList == null)
                    return;

                absensiList.clear();
                mAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private HashMap<String, String> fillMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("IdKaryawan", User.getEmpCode());

        return map;
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
