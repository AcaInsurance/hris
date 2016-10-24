package aca.com.hris.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Adapter.AbsensiViewAdapter;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.PojoModel.AbsensiViewListModel;
import aca.com.hris.R;
import aca.com.hris.Retrofit.HrisAPI;
import aca.com.hris.Retrofit.HrisService;
import aca.com.hris.Util.Var;
import aca.com.hris.Widget.DividerItemDecoration;
import aca.com.hris.Widget.EndlessRecyclerViewScrollListener;
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

public class AbsensiViewFragment extends BaseFragment {

    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnRetry)
    Button btnRetry;
    @Bind(R.id.lvAbsensiView)
    RecyclerView lvAbsensiView;
    AbsensiViewAdapter mAdapter;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.viewEmptyContent)
    TextView viewEmptyContent;

    private LinearLayoutManager linearLayoutManager;
    private List<AbsensiViewListModel> viewList = null;
    private onAbsensiViewFragmentListener mListener;

    private int page = 0;
    private boolean loadMore = false;
    private HrisAPI loadListAPI;
    private Subscription subscription;

    public static AbsensiViewFragment newInstance() {
        Bundle args = new Bundle();

        AbsensiViewFragment fragment = new AbsensiViewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void onCreate() {
        loadDataRetrofit();
    }


    private String getMethod() {
        return "LoadListAbsensiViewJson";
    }

    private void loadDataRetrofit() {
        final int page = 0;
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                loadListAPI = HrisService.createHrisService(getBody(page));
                subscription = loadListAPI.AbsensiViewList(getMethod())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(firstLoadObserver());
            }
        });


    }

    private void loadMoreRetrofit(int page) {
        loadListAPI = HrisService.createHrisService(getBody(page));
        subscription = loadListAPI.AbsensiViewList(getMethod())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MoreLoadObserver());
    }


    private Observer<? super List<AbsensiViewListModel>> firstLoadObserver() {
        return new Observer<List<AbsensiViewListModel>>() {

            @Override
            public void onCompleted() {
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e.getMessage(), e.getStackTrace());
                Log.d("tag", "onError " + e.getMessage());

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
            public void onNext(List<AbsensiViewListModel> listModel) {
                setAdapter(listModel);
                showEmptyContent(listModel == null || listModel.size() == 0 ? true : false);

            }
        };
    }

    public void showEmptyContent(boolean show) {
        viewEmptyContent.setVisibility(show ? View.VISIBLE : View.GONE);
    }



    private Observer<? super List<AbsensiViewListModel>> MoreLoadObserver() {
        return new Observer<List<AbsensiViewListModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e.getMessage(), e);
                Log.d("tag", "onError " + e.getMessage());
                Snackbar.make(getView(), R.string.check_your_connection, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadMoreRetrofit(page);
                            }
                        })
                        .show();
            }

            @Override
            public void onNext(List<AbsensiViewListModel> AbsensiCancellationListModels) {
                int cur = viewList.size();
                viewList.addAll(AbsensiCancellationListModels);
                mAdapter.notifyItemRangeInserted(cur, AbsensiCancellationListModels.size());
            }
        };
    }


    private RequestBody getBody(int page) {
        RequestBody requestBody;

        requestBody = new FormBody.Builder()
                .add("IdKaryawan", User.getEmpCode())
                .add("Limit", Var.maxRow + "")
                .add("Offset", (page * Var.maxRow) + "")
                .build();

        return requestBody;
    }

    @Override
    public void onDestroyView() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void getArgsData() {

    }

    @Override
    protected int getViewLayout() {
        return R.layout.fragment_absensi_view;
    }

    @Override
    protected void initInstance(View view) {

    }

    @Override
    protected void init(View view) {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        lvAbsensiView.setLayoutManager(linearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        lvAbsensiView.addItemDecoration(itemDecoration);
        lvAbsensiView.setItemAnimator(new SlideInUpAnimator());

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
                AbsensiViewFragment.this.refreshList();
            }
        });
        lvAbsensiView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreRetrofit(page);
            }
        });
        btnRetry.setOnClickListener(btnRetryListener());
    }

    private View.OnClickListener btnRetryListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshList();
            }
        };
    }

    public void refreshList() {
        loadDataRetrofit();
        lvAbsensiView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreRetrofit(page);
            }
        });
    }

    private void emptyList() {
        if (viewList == null)
            return;

        viewList.clear();
    }

    private HashMap<String, String> fillMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("IdKaryawan", User.getEmpCode());
        map.put("Limit", Var.maxRow + "");
        map.put("Offset", (Var.maxRow * page) + "");

        return map;
    }

    private void loadData() {
        HashMap<String, String> map;
        String methodName;
        int post, get;

        methodName = "GetListAbsensiView";
        post = R.array.GetListAbsensiView_post;
        get = R.array.GetListAbsensiView_get;
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
                try {
                    if (arrList == null) {
//                        Snackbar.make(getView(), getString(R.string.prompt_no_more_data_load), Snackbar.LENGTH_SHORT).show();
                        clearListView();
                        showProgress(false);
                    } else {
                        if (!loadMore)
                            onSuccessLoadData(arrList);
                        else
                            onSuccessLoadMore(arrList);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            private void onSuccessLoadData(ArrayList<HashMap<String, String>> arrList) {
                try {
//                    setAdapter(arrList);
                    page++;
                    showProgress(false);
                    swipeContainer.setRefreshing(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void onSuccessLoadMore(ArrayList<HashMap<String, String>> arrList) {

                try {
//                    viewList.addAll(arrList);
                    mAdapter.notifyDataSetChanged();

                    page++;
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
//        ws.execute();
    }


    private void setAdapter(List<AbsensiViewListModel> listModel) {
        try {
            viewList = listModel;
            mAdapter = new AbsensiViewAdapter(getActivity(), viewList);
            lvAbsensiView.setAdapter(mAdapter);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void clearListView() {
        try {
            if (viewList.size() == 0) {
                if (viewList == null)
                    return;

                viewList.clear();
                mAdapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected View getRootPanel() {
        return lvAbsensiView;
    }

    @Override
    protected Button getButtonRetry() {
        return btnRetry;
    }

    @Override
    protected ProgressBar getProgressLoading() {
        return pbLoading;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onAbsensiViewFragmentListener) {
            mListener = (onAbsensiViewFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onAbsensiViewFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public interface onAbsensiViewFragmentListener {
        public void setAction(int action);
    }
}
