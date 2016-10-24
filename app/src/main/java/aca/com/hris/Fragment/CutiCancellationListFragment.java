package aca.com.hris.Fragment;

import android.app.Activity;
import android.database.Cursor;
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

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Adapter.CancellationCutiAdapter;
import aca.com.hris.Database.CutiCancellationList;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.PojoModel.CutiCancellationListModel;
import aca.com.hris.R;
import aca.com.hris.Retrofit.HrisAPI;
import aca.com.hris.Retrofit.HrisService;
import aca.com.hris.Util.UtilDate;
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

public class CutiCancellationListFragment extends BaseFragment {

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
    private CancellationCutiAdapter mAdapter;
    private onCutiCancellationListFragmentListener mListener;
    private List<CutiCancellationListModel> cutiLists = null;

    private int page = 0;
    private boolean loadMore = false;
    private HrisAPI loadListAPI;
    private Subscription subscription;

    public CutiCancellationListFragment() {
    }


    public static CutiCancellationListFragment newInstance() {

        Bundle args = new Bundle();

        CutiCancellationListFragment fragment = new CutiCancellationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onCreate() {
        loadDataRetrofit();
    }


    private String getMethod() {
        return "LoadAbsensiCutiCancellationListJson";
    }

    private void loadDataRetrofit() {
        final int page = 0;

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                loadListAPI = HrisService.createHrisService(getBody(page));
                subscription = loadListAPI.CutiCancellationList(getMethod())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(firstLoadObserver());
            }
        });


    }

    private void loadMoreRetrofit(int page) {
        loadListAPI = HrisService.createHrisService(getBody(page));
        subscription = loadListAPI.CutiCancellationList(getMethod())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MoreLoadObserver());
    }


    private Observer<? super List<CutiCancellationListModel>> firstLoadObserver() {
        return new Observer<List<CutiCancellationListModel>>() {

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
            public void onNext(List<CutiCancellationListModel> listModel) {
                setAdapter(listModel);
                showEmptyContent(listModel == null || listModel.size() == 0 ? true : false);

            }
        };
    }

    public void showEmptyContent(boolean show) {
        viewEmptyContent.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    private Observer<? super List<CutiCancellationListModel>> MoreLoadObserver() {
        return new Observer<List<CutiCancellationListModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e.getMessage(), e);
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
            public void onNext(List<CutiCancellationListModel> CutiCancellationListModels) {
                int cur = cutiLists.size();
                cutiLists.addAll(CutiCancellationListModels);
                mAdapter.notifyItemRangeInserted(cur, CutiCancellationListModels.size());
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
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    protected void getArgsData() {

    }

    public void onRefresh() {
        loadDataRetrofit();
        listViewForm.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreRetrofit(page);
            }
        });
    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_cuti_cancellation_list;
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
        btnRetry.setOnClickListener(btnRetryListener());
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CutiCancellationListFragment.this.onRefresh();
            }
        });
        listViewForm.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreRetrofit(page);
            }
        });
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


    private void loadData() {
        HashMap<String, String> map;
        String methodName;
        int post, get;


        methodName = "LoadAbsensiCutiCancellationList";
        post = R.array.LoadAbsensiCutiCancellationList_post;
        get = R.array.LoadAbsensiCutiCancellationList_get;
        map = fillMap(page);

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
                    insertDB(arrList);
//                    setAdapter(listModel);

                    swipeContainer.setRefreshing(false);
                    page++;
                    showProgress(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            private void onSuccessLoadMore(ArrayList<HashMap<String, String>> arrList) {

                try {
                    insertDB(arrList);
                    List<CutiCancellationList> arlist = (List<CutiCancellationList>) new Select().from(CutiCancellationList.class).queryList();

                    cutiLists.clear();
//                    cutiLists.addAll(arlist);
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
        ws.execute();
    }


    private void clearListView() {
        try {
            Cursor c = new Select().from(CutiCancellationList.class).query();
            if (c.getCount() == 0) {
                if (cutiLists == null)
                    return;

                cutiLists.clear();
                mAdapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onNewForm() {
        try {
            onRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onDeleted(int position) {
        try {
            cutiLists.remove(position);
            mAdapter.notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setAdapter(List<CutiCancellationListModel> listModel) {
        cutiLists = listModel;
        mAdapter = new CancellationCutiAdapter(getActivity(), cutiLists);
        listViewForm.setAdapter(mAdapter);
    }


    private void deleteDB() {
        new Delete().from(CutiCancellationList.class).queryClose();
    }

    private void insertDB(List<HashMap<String, String>> arrList) {
        CutiCancellationList cutiList;
        for (HashMap<String, String> map : arrList
                ) {

            LocalDateTime localDateTime = UtilDate.parseUTC(map.get("TanggalTransaksi"));
            LocalDate date = UtilDate.getDate(localDateTime);

            cutiList = new CutiCancellationList();
            cutiList.NoTransaksi = map.get("NoTransaksi");
            cutiList.Tanggal = date.toString(UtilDate.DATE_PATTERN_DISPLAY_1);
            cutiList.NoTransaksiCuti = map.get("NoTransaksiCuti");
            cutiList.NamaKaryawan = map.get("NamaKaryawan");
            cutiList.NamaCuti = map.get("NamaForm");
            cutiList.AlasanBatal = map.get("AlasanBatal");
            cutiList.IsAppHead = map.get("IsAppHead");
            cutiList.IsRejectHead = map.get("IsRejectHead");
            cutiList.IsAppHRD = map.get("IsAppHRD");
            cutiList.IsRejectHRD = map.get("IsRejectHRD");
            cutiList.AlasanReject = map.get("AlasanReject");
            cutiList.save();
        }
    }

    private HashMap<String, String> fillMap(int page) {
        String idKary = User.getEmpCode();
        int limit = Var.maxRow;
        int offset = page * limit;

        HashMap<String, String> map = new HashMap<>();
        map.put("IdKaryawan", idKary);
        map.put("Limit", limit + "");
        map.put("Offset", offset + "");
        return map;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onCutiCancellationListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public interface onCutiCancellationListFragmentListener {
        public void setAction(int action);
    }
}
