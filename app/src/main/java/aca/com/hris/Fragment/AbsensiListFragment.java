package aca.com.hris.Fragment;

import android.app.Activity;
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

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Adapter.AbsensiAdapter;
import aca.com.hris.Database.FormList;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.PojoModel.FormListModel;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class AbsensiListFragment extends BaseFragment {

    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnRetry)
    Button btnRetry;

    @Bind(R.id.listViewForm)
    RecyclerView listViewForm;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @Bind(R.id.viewEmptyContent)
    TextView viewEmptyContent;

    private LinearLayoutManager linearLayoutManager;
    private AbsensiAdapter mAdapter;
    private OnFragmentInteractionListener mListener;
    private List<FormListModel> formLists = null;

    private int page = 0;
    private boolean loadMore = false;
    private HrisAPI formlistApi;
    Subscription subscription;

    public AbsensiListFragment() {
    }

    public static AbsensiListFragment newInstance() {
        AbsensiListFragment fragment = new AbsensiListFragment();
        return fragment;
    }

    @Override
    protected void onCreate() {
//        deleteDB();
//        loadData();
        loadDataRetrofit();
    }

    private void loadDataRetrofit() {
        final int page = 0;
//        showProgress(true);
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
                formlistApi = HrisService.createHrisService(formListBody(page));
                subscription = formlistApi.FormList(getMethod())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(firstLoadObserver());
            }
        });


    }

    private void loadMoreRetrofit(int page) {
        formlistApi = HrisService.createHrisService(formListBody(page));
        subscription = formlistApi.FormList(getMethod())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MoreLoadObserver());
    }

    private Observer<? super List<FormListModel>> firstLoadObserver() {
        return new Observer<List<FormListModel>>() {

            @Override
            public void onCompleted() {
//                showProgress(false);
                swipeContainer.setRefreshing(false);
//                page++;
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e.getMessage(), e);
                swipeContainer.setRefreshing(false);
//                showRetry(true);
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
            public void onNext(List<FormListModel> formListModels) {
//               insertData(formListModels);
                setAdapter(formListModels);
                showEmptyContent(formListModels == null || formListModels.size() == 0 ? true : false);

            }
        };
    }


    private Observer<? super List<FormListModel>> MoreLoadObserver() {
        return new Observer<List<FormListModel>>() {
            @Override
            public void onCompleted() {
//                page++;
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
            public void onNext(List<FormListModel> formListModels) {
//                insertData(formListModels);
//                ArrayList<FormList> arlist = (ArrayList<FormList>) new Select().from(FormList.class).queryList();
//                formLists.clear();
//                formLists.addAll(arlist);
                int cur = formLists.size();
                formLists.addAll(formListModels);
                mAdapter.notifyItemRangeInserted(cur, formListModels.size());
            }
        };
    }

    private void insertData(List<FormListModel> formListModels) {
        FormList formList;

        for (FormListModel f : formListModels) {
            String tanggal = UtilDate.format(
                    f.TanggalTransaksi,
                    UtilDate.DATE_TIME_PATTERN_SERVER,
                    UtilDate.DATE_PATTERN_DISPLAY_1);

            formList = new FormList();
            formList.NoTransaksi = f.NoTransaksi;
            formList.TanggalTransaksi = tanggal;
            formList.NamaKaryawan = f.NamaKaryawan;
            formList.NamaForm = f.NamaForm;
            formList.IsApproved = f.IsApproved;
            formList.IsRejected = f.IsRejected;
            formList.IsCancelled = f.IsCancelled;
            formList.IsHRDApproved = f.IsHRDApproved;
            formList.IsHRDRejected = f.IsHRDRejected;
            formList.AlasanReject = f.AlasanReject;
            formList.IsActive = f.IsActive;
            formList.save();
        }
    }


    @Override
    protected void getArgsData() {

    }

    public void showEmptyContent(boolean show) {
        viewEmptyContent.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void onRefresh() {
//        page = 0;
//        loadMore = false;
//        deleteDB();
//        loadData();
        loadDataRetrofit();
        listViewForm.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
//                loadMore = true;
//                loadData();
                loadMoreRetrofit(page);
            }
        });
    }

/*
    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }*/

    @Override
    public int getViewLayout() {
        return R.layout.fragment_absensi_list;
    }

    @Override
    protected void initInstance(View view) {
    }


    private String getMethod() {
        return User.getIdRole().equalsIgnoreCase(User.ID_ROLE_EMPLOYEE) ? "LoadAbsensiFormListJson" : "LoadAbsensiFormListHeadJson";
    }


    private RequestBody formListBody(int page) {
        RequestBody requestBody;

        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_EMPLOYEE)) {
            requestBody = new FormBody.Builder()
                    .add("IdKary", User.getEmpCode())
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
                AbsensiListFragment.this.onRefresh();
            }
        });

        listViewForm.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreRetrofit(page);
//                loadMore = true;
//                loadData();
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

        if (User.getIdRole().equalsIgnoreCase(User.ID_ROLE_EMPLOYEE)) {
            methodName = "LoadAbsensiFormList";
            post = R.array.LoadAbsensiFormList_post;
            get = R.array.LoadAbsensiFormList_get;
            map = fillMap(page);

        } else {
            methodName = "LoadAbsensiFormListHead";
            post = R.array.LoadAbsensiFormListHead_post;
            get = R.array.LoadAbsensiFormListHead_get;
            map = fillMapHead(page);

        }

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

    private void onSuccessLoadData(ArrayList<HashMap<String, String>> arrList) {
//        try {
//            insertDB(arrList);
//            setAdapter(formListModels);
//
//            swipeContainer.setRefreshing(false);
//            page++;
//
//            showProgress(false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private void onSuccessLoadMore(ArrayList<HashMap<String, String>> arrList) {

//        try {
//            insertDB(arrList);
//            ArrayList<FormList> arlist = (ArrayList<FormList>) new Select().from(FormList.class).queryList();
//
//            formLists.clear();
//            formLists.addAll(arlist);
//            mAdapter.notifyDataSetChanged();
//
//            page++;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private void setAdapter(List<FormListModel> formListModels) {
//        formLists = (ArrayList<FormList>) new Select().from(FormList.class).queryList();
        if (formLists != null)
            formLists.clear();
        formLists = formListModels;
        mAdapter = new AbsensiAdapter(getActivity(), formLists);
        listViewForm.setAdapter(mAdapter);
    }


    private void deleteDB() {
        new Delete().from(FormList.class).queryClose();
    }

    private void insertDB(ArrayList<HashMap<String, String>> arrList) {
        FormList formList;
        for (HashMap<String, String> map : arrList
                ) {


            LocalDateTime localDateTime = UtilDate.parseUTC(map.get("TanggalTransaksi"));
            LocalDate date = UtilDate.getDate(localDateTime);

            formList = new FormList();
            formList.NoTransaksi = map.get("NoTransaksi");
            formList.TanggalTransaksi = date.toString(UtilDate.DATE_PATTERN_DISPLAY_1);
            formList.NamaKaryawan = map.get("NamaKaryawan");
            formList.NamaForm = map.get("NamaForm");
            formList.IsApproved = map.get("IsApproved");
            formList.IsRejected = map.get("IsRejected");
            formList.IsCancelled = map.get("IsCancelled");
            formList.IsHRDApproved = map.get("IsHRDApproved");
            formList.IsHRDRejected = map.get("IsHRDRejected");
            formList.AlasanReject = map.get("AlasanReject");
            formList.IsActive = map.get("IsActive");
            formList.save();
        }
    }

    private HashMap<String, String> fillMap(int page) {
        String idKary = User.getEmpCode();
        int limit = Var.maxRow;
        int offset = page * limit;

        HashMap<String, String> map = new HashMap<>();
        map.put("IdKary", idKary);
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
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
        super.onDestroy();
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
//            mAdapter.notifyItemInserted(0);
//            listViewForm.scrollToPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onDeleted(int position) {
        try {
            formLists.remove(position);
            mAdapter.notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        public void onAbsensiListFragmentInteraction(String id);
    }
}
