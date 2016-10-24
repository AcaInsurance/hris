package aca.com.hris.Fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.List;

import aca.com.hris.Adapter.CutiAdapter;
import aca.com.hris.Database.CutiList;
import aca.com.hris.Database.User;
import aca.com.hris.PojoModel.CutiListModel;
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

public class CutiListFragment extends BaseFragment {

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

    private CutiAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private List<CutiListModel> cutiLists = null;

    private int page = 0;
    private boolean loadMore = false;
    private Subscription subscription;
    private HrisAPI cutiListAPI;

    public CutiListFragment() {
    }

    public static CutiListFragment newInstance() {
        CutiListFragment fragment = new CutiListFragment();
        return fragment;
    }

    @Override
    protected void onCreate() {
        loadDataRetrofit();
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }


    private String getMethod() {
        return User.getIdRole().equalsIgnoreCase(User.ID_ROLE_EMPLOYEE)
                ? "LoadAbsensiCutiListJson"
                : "LoadAbsensiCutiListHeadJson";
    }

    private void loadDataRetrofit() {
        final int page = 0;

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);

                cutiListAPI = HrisService.createHrisService(getBody(page));
                subscription = cutiListAPI.CutiList(getMethod())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(firstLoadObserver());
            }
        });

    }

    private void loadMoreRetrofit(int page) {
        cutiListAPI = HrisService.createHrisService(getBody(page));
        subscription = cutiListAPI.CutiList(getMethod())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MoreLoadObserver());
    }


    private Observer<? super List<CutiListModel>> firstLoadObserver() {
        return new Observer<List<CutiListModel>>() {

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
            public void onNext(List<CutiListModel> listModel) {
                setAdapter(listModel);
                showEmptyContent(listModel == null || listModel.size() == 0 ? true : false);
            }
        };
    }

    public void showEmptyContent(boolean show) {
        viewEmptyContent.setVisibility(show ? View.VISIBLE : View.GONE);
    }



    private Observer<? super List<CutiListModel>> MoreLoadObserver() {
        return new Observer<List<CutiListModel>>() {
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
            public void onNext(List<CutiListModel> CutiListModels) {
                int cur = cutiLists.size();
                cutiLists.addAll(CutiListModels);
                mAdapter.notifyItemRangeInserted(cur, CutiListModels.size());
            }
        };
    }


    private RequestBody getBody(int page) {
        RequestBody requestBody;

        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_EMPLOYEE)) {
            requestBody = new FormBody.Builder()
                    .add("IdKaryawan", User.getEmpCode())
                    .add("Limit", Var.maxRow + "")
                    .add("Offset", (page * Var.maxRow) + "")
                    .build();
        } else {
            requestBody = new FormBody.Builder()
                    .add("KodeLevel", User.getKodeLevel())
                    .add("Limit", Var.maxRow + "")
                    .add("Offset", (page * Var.maxRow) + "")
                    .build();
        }

        return requestBody;
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
        return R.layout.fragment_cuti_list;
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
                CutiListFragment.this.onRefresh();
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


//    private void loadData() {
//        HashMap<String, String> map;
//        String methodName;
//        int post, get;
//
//        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_EMPLOYEE)) {
//            methodName = "LoadAbsensiCutiList";
//            post = R.array.LoadAbsensiCutiList_post;
//            get = R.array.LoadAbsensiCutiList_get;
//            map = fillMap(page);
//
//        } else {
//            methodName = "LoadAbsensiCutiListHead";
//            post = R.array.LoadAbsensiCutiListHead_post;
//            get = R.array.LoadAbsensiCutiListHead_get;
//            map = fillMapHead(page);
//
//        }
//
//
//        if(!loadMore)
//            showProgress(true);
//
//        WebServices ws = new WebServices(getActivity(),
//                methodName,
//                map,
//                getActivity().getResources().getStringArray(post),
//                getActivity().getResources().getStringArray(get)
//        ) {
//            @Override
//            protected void onSuccess(@NonNull List<HashMap<String, String>> arrList) {
//
//                try {
//                    if (arrList == null) {
////                        Snackbar.make(getView(), getString(R.string.prompt_no_more_data_load), Snackbar.LENGTH_SHORT).show();
////                        clearListView();
//                        showProgress(false);
//                    } else {
//                        if (!loadMore)
//                            onSuccessLoadData(arrList);
//                        else
//                            onSuccessLoadMore(arrList);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            private void onSuccessLoadData(List<HashMap<String, String>> arrList) {
//                try {
//                    insertDB(arrList);
//                    setAdapter(listModel);
//
//                    page++;
//                    swipeContainer.setRefreshing(false);
//                    showProgress(false);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//            private void onSuccessLoadMore(List<HashMap<String, String>> arrList) {
//
//                try {
//                    insertDB(arrList);
//                    List<CutiListModel> arlist = (List<CutiListModel>) new Select().from(CutiList.class).queryList();
//
//                    cutiLists.clear();
//                    cutiLists.addAll(arlist);
//                    mAdapter.notifyDataSetChanged();
//
//                    page++;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            protected void onFailed(String message) {
//                try {
//                    swipeContainer.setRefreshing(false);
//
//                    if (!isAdded())
//                        return;
//
//                    Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
//
//                    showRetry(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            protected void onCancel() {
//
//            }
//        };
//        ws.execute();
//    }


    private void clearListView() {
        try {
            Cursor c = new Select().from(CutiList.class).query();
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


    private void setAdapter(List<CutiListModel> listModel) {
        if (cutiLists != null)
            cutiLists.clear();
        cutiLists = listModel;
        mAdapter = new CutiAdapter(getActivity(), cutiLists);
        listViewForm.setAdapter(mAdapter);
    }


    private void deleteDB() {
        new Delete().from(CutiList.class).queryClose();
    }

    private void insertDB(List<HashMap<String, String>> arrList) {
        CutiList cutiList;
        for (HashMap<String, String> map : arrList
                ) {

            LocalDateTime localDateTime = UtilDate.parseUTC(map.get("TanggalTransaksi"));
            LocalDate date = UtilDate.getDate(localDateTime);

            cutiList = new CutiList();
            cutiList.NoTransaksi = map.get("NoTransaksi");
            cutiList.TanggalTransaksi = date.toString(UtilDate.DATE_PATTERN_DISPLAY_1);
            cutiList.NamaLengkap = map.get("NamaKaryawan");
            cutiList.NamaForm = map.get("NamaCuti");
            cutiList.IsAppHead = map.get("IsAppHead");
            cutiList.IsRejectHead = map.get("IsRejectHead");
            cutiList.IsCancel = map.get("IsCancel");
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


    private HashMap<String, String> fillMapHead(int page) {
        String kodeLevel = User.getKodeLevel();
        int limit = Var.maxRow;
        int offset = page * limit;

        HashMap<String, String> map = new HashMap<>();
        map.put("KodeLevel", kodeLevel);
        map.put("Limit", limit + "");
        map.put("Offset", offset + "");
        return map;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
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


    public interface OnFragmentInteractionListener {
        public void listener(String id);
    }
}
