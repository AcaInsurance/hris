package aca.com.hris.Fragment;

import android.app.Activity;
import android.database.Cursor;
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
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Adapter.ApprovalAbsensiCancellationAdapter;
import aca.com.hris.Database.AbsensiCancellationList;
import aca.com.hris.Database.User;
import aca.com.hris.HelperClass.Email;
import aca.com.hris.HelperClass.WebServices;
import aca.com.hris.HelperClass.WebServicesNonQuery;
import aca.com.hris.HelperClass.WebServicesNonQueryNonAsync;
import aca.com.hris.PojoModel.ApprovalAbsensiCancellationModel;
import aca.com.hris.R;
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
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class AbsensiCancellationApprovalFragment extends BaseFragment implements
        ApprovalAbsensiCancellationAdapter.AbsensiCancellationApprovalAdapterListener {

    @Bind(R.id.listViewForm)
    RecyclerView listViewForm;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @Bind(R.id.pbLoading)
    ProgressBar pbLoading;
    @Bind(R.id.btnRetry)
    Button btnRetry;
    @Bind(R.id.viewEmptyContent)
    TextView viewEmptyContent;

    private LinearLayoutManager linearLayoutManager;
    private ApprovalAbsensiCancellationAdapter mAdapter;
    private List<ApprovalAbsensiCancellationModel> formLists = null;

    private int page = 0;
    private boolean loadMore = false;
    private OnAbsensiCancellationApprovalListener mListener;
    private Subscription subscription;
    private static final String TAG = "AbsensiCancellationApprovalFragment";

    public AbsensiCancellationApprovalFragment() {
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
        return "LoadListAbsensiCancellationForApprovalJson";
    }

    private void loadDataRetrofit() {
        final int page = 0;

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {

                swipeContainer.setRefreshing(true);
                subscription = HrisService.createHrisService(getBody(page))
                        .ApprovalAbsensiCancellation(getMethod())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(firstLoadObserver());
            }
        });

    }

    private void loadMoreRetrofit(int page) {
        subscription = HrisService.createHrisService(getBody(page))
                .ApprovalAbsensiCancellation(getMethod())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MoreLoadObserver());
    }


    private Observer<? super List<ApprovalAbsensiCancellationModel>> firstLoadObserver() {
        return new Observer<List<ApprovalAbsensiCancellationModel>>() {

            @Override
            public void onCompleted() {
                swipeContainer.setRefreshing(false);
                mListener.setAction(true);
            }

            @Override
            public void onError(Throwable e) {
                Timber.d(e.getMessage(), e.getStackTrace());

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
            public void onNext(List<ApprovalAbsensiCancellationModel> listModel) {
                setAdapter(listModel);
                showEmptyContent(listModel == null || listModel.size() == 0 ? true : false);

            }
        };
    }


    public void showEmptyContent(boolean show) {
        viewEmptyContent.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    private Observer<? super List<ApprovalAbsensiCancellationModel>> MoreLoadObserver() {
        return new Observer<List<ApprovalAbsensiCancellationModel>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Timber.d(e.getMessage(), e.getStackTrace());
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
            public void onNext(List<ApprovalAbsensiCancellationModel> ApprovalAbsensiCancellationModels) {
                int cur = formLists.size();
                formLists.addAll(ApprovalAbsensiCancellationModels);
                mAdapter.notifyItemRangeInserted(cur, ApprovalAbsensiCancellationModels.size());
            }
        };
    }


    private RequestBody getBody(int page) {
        RequestBody requestBody;

        requestBody = new FormBody.Builder()
                .add("KodeLevel", User.getKodeLevel())
                .add("Limit", Var.maxRow + "")
                .add("Offset", (page * Var.maxRow) + "")
                .build();
        return requestBody;
    }

    @Override
    protected void getArgsData() {

    }

    @Override
    public int getViewLayout() {
        return R.layout.fragment_absensi_cancellation_approval;
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
                AbsensiCancellationApprovalFragment.this.refreshList();
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
                refreshList();
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


    private HashMap<String, String> fillMap(int page) {
        int limit = Var.maxRow;
        int offset = page * limit;

        HashMap<String, String> map = new HashMap<>();
        map.put("KodeLevel", User.getKodeLevel());
        map.put("Limit", limit + "");
        map.put("Offset", offset + "");
        return map;
    }


    private void loadData() {
        HashMap<String, String> map = fillMap(page);
        String methodName;
        int post, get;

        methodName = "LoadListAbsensiCancellationForApproval";
        post = R.array.LoadListAbsensiCancellationForApproval_post;
        get = R.array.LoadListAbsensiCancellationForApproval_get;

        WebServices ws = new WebServices(getActivity(),
                methodName,
                map,
                getActivity().getResources().getStringArray(post),
                getActivity().getResources().getStringArray(get)
        ) {
            @Override
            protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {

                try {
                    showProgress(false);

                    if (arrList == null) {
//                        Snackbar.make(getView(), getString(R.string.prompt_no_more_data_load), Snackbar.LENGTH_SHORT).show();
                        clearListView();
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
                    insertDB(arrList);
//                    setAdapter(listModel);

                    swipeContainer.setRefreshing(false);
                    page++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            private void onSuccessLoadMore(ArrayList<HashMap<String, String>> arrList) {

                try {
                    insertDB(arrList);
                    ArrayList<AbsensiCancellationList> arlist = (ArrayList<AbsensiCancellationList>) new Select().from(AbsensiCancellationList.class).queryList();

                    formLists.clear();
//                    formLists.addAll(arlist);
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
            Cursor c = new Select().from(AbsensiCancellationList.class).query();
            if (c.getCount() == 0) {
                if (formLists == null)
                    return;

                formLists.clear();
                mAdapter.notifyDataSetChanged();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void setAdapter(List<ApprovalAbsensiCancellationModel> listModel) {
        try {
            formLists = listModel;
            mAdapter = new ApprovalAbsensiCancellationAdapter(getActivity(), formLists);
            listViewForm.setAdapter(mAdapter);
            mAdapter.mListener = this;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deleteDB() {
        new Delete().from(AbsensiCancellationList.class).queryClose();
    }


    private void insertDB(ArrayList<HashMap<String, String>> arrList) {
        AbsensiCancellationList formList;
        for (HashMap<String, String> map : arrList
                ) {

            LocalDate tanggalTrans = UtilDate.parseUTC(map.get("TglCancel")).toLocalDate();
            LocalDate dateFrom = UtilDate.parseUTC(map.get("DateFrom")).toLocalDate();
            LocalDate dateTo = UtilDate.parseUTC(map.get("DateTo")).toLocalDate();

            formList = new AbsensiCancellationList();
            formList.NoCancel = map.get("NoCancel");
            formList.TglCancel = tanggalTrans.toString(UtilDate.DATE_PATTERN_DISPLAY_1);
            formList.NamaKaryawan = map.get("NamaKaryawan");
            formList.NamaForm = map.get("NamaForm");
            formList.DateFrom = dateFrom.toString(UtilDate.DATE_PATTERN_DISPLAY_1);
            formList.DateTo = dateTo.toString(UtilDate.DATE_PATTERN_DISPLAY_1);
            formList.TimeFrom = map.get("TimeFrom");
            formList.TimeTo = map.get("TimeTo");
            formList.AlasanBatal = map.get("AlasanBatal");

            formList.save();
        }
    }


    public void callApprovalService() {
        final ArrayList<HashMap<String, String>> arrList = mAdapter.getPendingList();

        Observable.create(approvalObservable(arrList))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(approvalSubcsriber(arrList));
    }

    private Observable.OnSubscribe<ArrayList<HashMap<String, String>>> approvalObservable(final ArrayList<HashMap<String, String>> arrList) {
        return new Observable.OnSubscribe<ArrayList<HashMap<String, String>>>() {
            @Override
            public void call(Subscriber<? super ArrayList<HashMap<String, String>>> subscriber) {
                for (final HashMap<String, String> map : arrList) {
                    WebServicesNonQueryNonAsync ws = new WebServicesNonQueryNonAsync(
                            getActivity(),
                            "DoApproveFormCancellation",
                            map,
                            getResources().getStringArray(R.array.DoApproveFormCancellation_post),
                            getResources().getStringArray(R.array.DoApproveFormCancellation_get)
                    );

                    ws.onPreExecute();
                    subscriber.onNext(ws.doInBackground());
                }
                subscriber.onCompleted();
            }
        };
    }

    private Subscriber<ArrayList<HashMap<String, String>>> approvalSubcsriber(final ArrayList<HashMap<String, String>> arrList) {

        return new Subscriber<ArrayList<HashMap<String, String>>>() {
            final int[] counter = {0};

            @Override
            public void onCompleted() {
                Log.d(TAG, "oncomplete ");

                if (counter[0] == arrList.size()) {
                    Toast.makeText(getActivity(), R.string.success_approve_form, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), R.string.not_all_success_approve_form, Toast.LENGTH_SHORT).show();
                }

                mListener.setAction(true);
                refreshList();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError " + e.getMessage());
//                Toast.makeText(getActivity(), R.string.failed_approve_form, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ArrayList<HashMap<String, String>> hashMaps) {
                String result = hashMaps.get(0).get(getResources().getStringArray(R.array.DoApproveFormCancellation_get)[0]);
                sendEmail(result);
                counter[0]++;
            }
        };
    }


    public void callApprovalServiceBackup() {
        final ArrayList<HashMap<String, String>> arrList = mAdapter.getPendingList();
        final int[] i = {0};

        final int listSize = arrList.size();
        for (final HashMap<String, String> map : arrList) {
            final WebServicesNonQuery ws = new WebServicesNonQuery(
                    getActivity(),
                    "DoApproveFormCancellation",
                    map,
                    getResources().getStringArray(R.array.DoApproveFormCancellation_post),
                    getResources().getStringArray(R.array.DoApproveFormCancellation_get),
                    true
            ) {
                @Override
                protected void onSuccess(@NonNull ArrayList<HashMap<String, String>> arrList) {
                    String result = arrList.get(0).get(getResources().getStringArray(R.array.DoApproveFormCancellation_get)[0]);
                    sendEmail(result);
                    i[0]++;
                }

                @Override
                protected void onFailed(String message) {
                    i[0]++;
                }

                @Override
                protected void onCancel() {

                }
            };
            ws.execute();
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                int sleep = 0;

                while (i[0] != listSize) {
                    try {
                        sleep += 100;
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                /**Write your logic here **/

                getActivity().runOnUiThread(new Runnable() {

                    /** If needed to update the view**/
                    @Override
                    public void run() {

                        Toast.makeText(getActivity(), getString(R.string.success_approve_form), Toast.LENGTH_SHORT).show();
                        refreshList();
                    }
                });
            }
        }).start();

    }


    private void sendEmail(String noTransaksi) {
        new Email() {
            @Override
            public void resultSending(boolean success) {
//                if (!success) {
//                    mListener.setAction(UtilBundle.ACTION_BUNDLE_MAIL);
//                }
            }
        }.sendEmail(getActivity(),
                noTransaksi,
                "DoSendEmailApproveAbsensiCancellation",
                R.array.DoSendEmailApproveAbsensiCancellation_post,
                R.array.DoSendEmailApproveAbsensiCancellation_get,
                Email.IS_APPROVAL);

    }


    public void refreshList() {
        loadDataRetrofit();
        listViewForm.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreRetrofit(page);
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAbsensiCancellationApprovalListener) activity;
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
    public void onClicked(boolean hideSave) {
        mListener.setAction(hideSave);
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

    public interface OnAbsensiCancellationApprovalListener {
        void setAction(boolean hideSave);
    }

}
