package aca.com.hris.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.Database.SetVar;
import aca.com.hris.Database.SetVar$Table;
import aca.com.hris.Database.SummaryPeriode;
import aca.com.hris.Database.SummaryPeriode$Table;
import aca.com.hris.Database.User;
import aca.com.hris.PojoModel.ApprovalAbsensiModel;
import aca.com.hris.PojoModel.Clicked;
import aca.com.hris.R;
import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.Var;
import butterknife.Bind;
import butterknife.ButterKnife;


public class ApprovalAbsensiAdapter extends RecyclerView.Adapter<ApprovalAbsensiAdapter.ViewHolder> {
    public static String BUNDLE_NO_TRANSAKSI = "BUNDLE_NO_TRANSAKSI";
    public List<ApprovalAbsensiModel> arrList;
    public Activity activity;
    public absensiApprovalAdapterListener mListener;
    private ArrayList<HashMap<String, String>> pendingList = new ArrayList<>();
    private ArrayList<Clicked> arrClicked = new ArrayList<>();


    public ApprovalAbsensiAdapter(Activity activity, List<ApprovalAbsensiModel> arrList) {
        this.arrList = arrList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_absensi_approval_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ApprovalAbsensiModel ApprovalAbsensiModel = arrList.get(position);
        if (ApprovalAbsensiModel == null || holder == null)
            return;


        holder.txtTanggalTranskasi.setText(UtilDate.format(ApprovalAbsensiModel.TanggalTransaksi,
                UtilDate.DATE_TIME_PATTERN_SERVER,
                UtilDate.DATE_PATTERN_DISPLAY_2));
        holder.txtNamaKaryawan.setText(ApprovalAbsensiModel.NamaLengkap);
        holder.txtNoTransaksi.setText(ApprovalAbsensiModel.NoTransaksi);
        holder.txtTipeFormulir.setText(ApprovalAbsensiModel.NamaForm);
        holder.txtDateFrom.setText(UtilDate.format(ApprovalAbsensiModel.DateFrom
                , UtilDate.DATE_TIME_PATTERN_SERVER
                , UtilDate.DATE_PATTERN_DISPLAY_1));
        holder.txtDateTo.setText(UtilDate.format(ApprovalAbsensiModel.DateTo,
                UtilDate.DATE_TIME_PATTERN_SERVER,
                UtilDate.DATE_PATTERN_DISPLAY_1));

        if (!TextUtils.isEmpty(ApprovalAbsensiModel.TimeFrom) && !TextUtils.isEmpty(ApprovalAbsensiModel.TimeTo))
            holder.txtTime.setText(ApprovalAbsensiModel.TimeFrom + " - " + ApprovalAbsensiModel.TimeTo);
        else if (!TextUtils.isEmpty(ApprovalAbsensiModel.TimeFrom))
            holder.txtTime.setText(ApprovalAbsensiModel.TimeFrom);
        else if (!TextUtils.isEmpty(ApprovalAbsensiModel.TimeTo))
            holder.txtTime.setText(ApprovalAbsensiModel.TimeTo);
        else
            holder.txtTime.setText("");

        holder.txtAlasan.setText(ApprovalAbsensiModel.Alasan);

        initSaveState(position);
        bindButton(holder, position);

        holder.imgApproved.setOnClickListener(imgApprovedListener(holder, position));
        holder.imgReject.setOnClickListener(imgRejectedListener(holder, position));
        holder.imgApprovedReviewed.setOnClickListener(imgApprovedReviewedListener(holder, position));
        holder.imgRejectReviewed.setOnClickListener(imgRejectedReviewdListener(holder, position));

    }

    private View.OnClickListener imgApprovedListener(final ViewHolder holder, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateSummaryPeriode(holder.txtDateFrom.getText().toString())) {

                    if (holder.imgApproved.getVisibility() == View.VISIBLE && holder.imgReject.getVisibility() == View.VISIBLE) {
                        pendingList.add(fillMap(holder, "1"));
                    } else if (holder.imgRejectReviewed.getVisibility() == View.VISIBLE) {
                        removeFromList(holder.txtNoTransaksi.getText().toString());
                        pendingList.add(fillMap(holder, "1"));
                    }

                    saveStateClicked(Var.APPROVED_CLICK, position);

                    holder.imgApproved.setVisibility(View.GONE);
                    holder.imgApprovedReviewed.setVisibility(View.VISIBLE);
                    holder.imgReject.setVisibility(View.VISIBLE);
                    holder.imgRejectReviewed.setVisibility(View.GONE);
                    holder.txtRejectRemark.setVisibility(View.GONE);

                    mListener.onClicked(pendingList.size() == 0 ? true : false);

                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.prompt_error_invalid_periode_approval), Toast.LENGTH_SHORT).show();
                }

            }
        };
    }



    private void bindButton(ViewHolder holder, int position) {
        Clicked c = arrClicked.get(position);
        holder.txtRejectRemark.setText(c.remark);

        if (c.approvedClick) {
            holder.imgApproved.setVisibility(View.GONE);
            holder.imgApprovedReviewed.setVisibility(View.VISIBLE);
            holder.imgReject.setVisibility(View.VISIBLE);
            holder.imgRejectReviewed.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        }
        else if (c.rejectedClick) {
            holder.imgApproved.setVisibility(View.VISIBLE);
            holder.imgApprovedReviewed.setVisibility(View.GONE);
            holder.imgReject.setVisibility(View.GONE);
            holder.imgRejectReviewed.setVisibility(View.VISIBLE);
            holder.txtRejectRemark.setVisibility(View.VISIBLE);
        }
        else if (c.approvedReviewedClick) {
            holder.imgApproved.setVisibility(View.VISIBLE);
            holder.imgApprovedReviewed.setVisibility(View.GONE);
            holder.imgReject.setVisibility(View.VISIBLE);
            holder.imgRejectReviewed.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        }
        else if (c.rejectedReviewedClick) {
            holder.imgApproved.setVisibility(View.VISIBLE);
            holder.imgApprovedReviewed.setVisibility(View.GONE);
            holder.imgReject.setVisibility(View.VISIBLE);
            holder.imgRejectReviewed.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        }
        else {
            holder.imgApproved.setVisibility(View.VISIBLE);
            holder.imgApprovedReviewed.setVisibility(View.GONE);
            holder.imgReject.setVisibility(View.VISIBLE);
            holder.imgRejectReviewed.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        }
    }


    private void initSaveState(int position) {
        if (arrClicked.size() > position)
            if (arrClicked.get(position) != null)
                return;

        Clicked pojo = new Clicked();
        arrClicked.add(pojo);
    }

    private void saveStateClicked(int choice, int position) {
        saveStateClicked(choice, position, "");
    }
    private void saveStateClicked(int choice, int position, String remark) {
        Clicked pojo = new Clicked();

        switch (choice) {
            case Var.APPROVED_CLICK:
                pojo.approvedClick = true;
                pojo.approvedReviewedClick = false;
                pojo.rejectedClick = false;
                pojo.rejectedReviewedClick = false;
                break;

            case Var.REJECT_CLICK:
                pojo.approvedClick = false;
                pojo.approvedReviewedClick = false;
                pojo.rejectedClick = true;
                pojo.rejectedReviewedClick = false;
                break;

            case Var.APPROVED_REV_CLICK:
                pojo.approvedClick = false;
                pojo.approvedReviewedClick = true;
                pojo.rejectedClick = false;
                pojo.rejectedReviewedClick = false;
                break;

            case Var.REJECT_REV_CLICK:
                pojo.approvedClick = false;
                pojo.approvedReviewedClick = false;
                pojo.rejectedClick = false;
                pojo.rejectedReviewedClick = true;
                break;

            default:
                pojo.approvedClick = false;
                pojo.approvedReviewedClick = false;
                pojo.rejectedClick = false;
                pojo.rejectedReviewedClick = false;
        }

        pojo.remark = remark;

        addToListClicked(pojo, position);
    }


    private void addToListClicked(Clicked pojo, int position) {
        if (arrClicked.get(position) != null) {
            arrClicked.set(position, pojo);
        } else {
            arrClicked.add(pojo);
        }
    }

    private View.OnClickListener imgRejectedListener(final ViewHolder holder, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateSummaryPeriode(holder.txtDateFrom.getText().toString())) {
                    showDialogRemark(holder.txtRejectRemark);
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.prompt_error_invalid_periode_approval), Toast.LENGTH_SHORT).show();
                }
            }

            private void showDialogRemark(final EditText txtRejectRemark) {
                try {
                    View view = activity.getLayoutInflater().inflate(R.layout.layout_dialog_input, null);
                    final EditText input = (EditText) view.findViewById(R.id.txtInput);

                    final AlertDialog.Builder builder =
                            new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);
                    builder.setView(view);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String remark = input.getText().toString();

                            if (remark.isEmpty()) {
                                input.setError(activity.getString(R.string.prompt_error_empty_field));
                            } else {

                                saveStateClicked(Var.REJECT_CLICK, position, remark);

                                txtRejectRemark.setText(remark);
                                txtRejectRemark.setVisibility(View.VISIBLE);

                                if (holder.imgReject.getVisibility() == View.VISIBLE && holder.imgApproved.getVisibility() == View.VISIBLE) {
                                    pendingList.add(fillMap(holder, "0"));

                                } else if (holder.imgReject.getVisibility() == View.VISIBLE || holder.imgApproved.getVisibility() == View.VISIBLE) {
                                    removeFromList(holder.txtNoTransaksi.getText().toString());
                                    pendingList.add(fillMap(holder, "0"));
                                }
                                holder.imgReject.setVisibility(View.GONE);
                                holder.imgRejectReviewed.setVisibility(View.VISIBLE);

                                holder.imgApproved.setVisibility(View.VISIBLE);
                                holder.imgApprovedReviewed.setVisibility(View.GONE);

                                mListener.onClicked(pendingList.size() == 0 ? true : false);

                                dialog.dismiss();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private View.OnClickListener imgApprovedReviewedListener(final ViewHolder holder, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStateClicked(Var.APPROVED_REV_CLICK, position);

                holder.imgApproved.setVisibility(View.VISIBLE);
                holder.imgApprovedReviewed.setVisibility(View.GONE);
                holder.imgReject.setVisibility(View.VISIBLE);
                holder.imgRejectReviewed.setVisibility(View.GONE);

                holder.txtRejectRemark.setVisibility(View.GONE);
                removeFromList(holder.txtNoTransaksi.getText().toString());
                mListener.onClicked(pendingList.size() == 0 ? true : false);
            }
        };
    }

    private View.OnClickListener imgRejectedReviewdListener(final ViewHolder holder, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveStateClicked(Var.REJECT_REV_CLICK, position);

                holder.imgApproved.setVisibility(View.VISIBLE);
                holder.imgApprovedReviewed.setVisibility(View.GONE);
                holder.imgReject.setVisibility(View.VISIBLE);
                holder.imgRejectReviewed.setVisibility(View.GONE);

                holder.txtRejectRemark.setVisibility(View.GONE);
                removeFromList(holder.txtNoTransaksi.getText().toString());
                mListener.onClicked(pendingList.size() == 0 ? true : false);

            }
        };
    }

    private HashMap<String, String> fillMap(ViewHolder holder, String isApprove) {
        HashMap<String, String> map = new HashMap<>();
        map.put("NoTransaksi", holder.txtNoTransaksi.getText().toString());
        map.put("IdKaryawan", User.getEmpCode());
        map.put("KodeLevel", User.getKodeLevel());
        map.put("UserId", User.getUserID());
        map.put("IsApprove", isApprove);
        map.put("AlasanReject", holder.txtRejectRemark.getText().toString());

        return map;
    }

    public void removeFromList(String noTransaksi) {
        int i = 0;
        try {
            for (HashMap<String, String> map : pendingList) {
                if (map.get("NoTransaksi").equalsIgnoreCase(noTransaksi)) {
                    pendingList.remove(i);
                    break;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<HashMap<String, String>> getPendingList() {
        try {
            return pendingList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean validateSummaryPeriode(String tanggalMulai) {

        try {
            SetVar setVar = new Select().from(SetVar.class)
                    .where(Condition.column(SetVar$Table.PARAMETERCODE).eq("BtsAppHead"))
                    .querySingle();

            int tanggalBatas = Integer.parseInt(setVar.Value);

            LocalDate dateFrom = UtilDate.toDate(tanggalMulai);
            LocalDate today = UtilDate.getDate();
            LocalDate dateBatas = today.withDayOfMonth(tanggalBatas);

            long count;

            count = cekDiSummaryPeriode(dateFrom);

            if (count != 0) {
                return false;
            }

            int diffMonth = UtilDate.monthDiff(today, dateFrom);
            if (diffMonth >= 0) {
                return true;
            } else if (diffMonth == -1) {
                if (today.compareTo(dateBatas) <= 0)
                    return true;
                else
                    return false;
            } else
                return false;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    private long cekDiSummaryPeriode(LocalDate myDate) {
        long count = 0;
        try {
            count = new Select()
                    .from(SummaryPeriode.class)
                    .where(Condition.column(SummaryPeriode$Table.BULAN).eq(myDate.getMonthOfYear()),
                            Condition.column(SummaryPeriode$Table.TAHUN).eq((myDate.getYear())))
                    .query().getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public interface absensiApprovalAdapterListener {
        public void onClicked(boolean hideSave);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txtNoTransaksi)
        TextView txtNoTransaksi;
        @Bind(R.id.txtNamaKaryawan)
        TextView txtNamaKaryawan;
        @Bind(R.id.txtTipeFormulir)
        TextView txtTipeFormulir;
        @Bind(R.id.txtDateFrom)
        TextView txtDateFrom;
        @Bind(R.id.txtDateTo)
        TextView txtDateTo;
        @Bind(R.id.viewDate)
        LinearLayout viewDate;
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.txtAlasan)
        TextView txtAlasan;
        @Bind(R.id.txtTanggalTranskasi)
        TextView txtTanggalTranskasi;
        @Bind(R.id.viewContainer)
        RelativeLayout viewContainer;
        @Bind(R.id.imgReject)
        ImageView imgReject;
        @Bind(R.id.imgApproved)
        ImageView imgApproved;
        @Bind(R.id.imgRejectReviewed)
        ImageView imgRejectReviewed;
        @Bind(R.id.imgApprovedReviewed)
        ImageView imgApprovedReviewed;
        @Bind(R.id.txtRejectRemark)
        EditText txtRejectRemark;
        @Bind(R.id.viewIcon)
        RelativeLayout viewIcon;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}
