package aca.com.hris.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import aca.com.hris.CutiCancellationActivity;
import aca.com.hris.PojoModel.CutiCancellationListModel;
import aca.com.hris.R;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.UtilDate;
import butterknife.Bind;
import butterknife.ButterKnife;


public class CancellationCutiAdapter extends RecyclerView.Adapter<CancellationCutiAdapter.ViewHolder> {
    public static String BUNDLE_NO_TRANSAKSI = "BUNDLE_NO_TRANSAKSI";
    public static String BUNDLE_NO_TRANSAKSI_ID = "BUNDLE_NO_TRANSAKSI_ID";

    public List<CutiCancellationListModel> arrList;
    public Context context;
    public Activity activity;

    public CancellationCutiAdapter(Activity activity, List<CutiCancellationListModel> arrList) {
        this.arrList = arrList;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_form_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CutiCancellationListModel formList = arrList.get(position);
        holder.txtTanggalTranskasi.setText(UtilDate.format(formList.Tanggal,
                UtilDate.DATE_TIME_PATTERN_SERVER,
                UtilDate.DATE_PATTERN_DISPLAY_2));
        holder.txtNamaKaryawan.setText(formList.NamaKaryawan);
        holder.txtNoTransaksi.setText(formList.NoTransaksi);
        holder.txtTipeForm.setText(formList.NamaForm);
        setStatus(holder, formList);
        holder.txtRejectRemark.setText(formList.AlasanReject);
        bindWatermark(holder, formList);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action;

                if (isReviewed(formList))
                    action = UtilBundle.ACTION_BUNDLE_NOT_EDIT;
                else
                    action = UtilBundle.ACTION_BUNDLE_EDIT;

                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_NO_TRANSAKSI_ID, holder.txtNoTransaksi.getText().toString());
                bundle.putString(BUNDLE_NO_TRANSAKSI, formList.NoTransaksiCuti);
                bundle.putInt(UtilBundle.ACTION_BUNDLE, action);
                bundle.putInt(UtilBundle.POSITION, position);

                Intent intent = new Intent(activity, CutiCancellationActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, UtilBundle.REQUEST_CODE_EDIT_FORM);


            }
        });

    }

    private void bindWatermark(ViewHolder holder, CutiCancellationListModel formList) {
        if (formList == null || holder == null)
            return;

        boolean hrdApproved = Boolean.parseBoolean(formList.IsAppHRD);
        boolean hrdRejected = Boolean.parseBoolean(formList.IsRejectHRD);


        if (!hrdApproved && !hrdRejected) {
            holder.imgRejectedHRD.setVisibility(View.GONE);
            holder.imgApprovedHRD.setVisibility(View.GONE);
        } else if (hrdApproved) {
            holder.imgApprovedHRD.setVisibility(View.VISIBLE);
        } else if (hrdRejected) {
            holder.imgRejectedHRD.setVisibility(View.VISIBLE);
        }
    }


    private boolean isReviewed(CutiCancellationListModel list) {
        boolean approved = Boolean.parseBoolean(list.IsAppHead);
        boolean rejected = Boolean.parseBoolean(list.IsRejectHead);

        if (approved || rejected)
            return true;
        else
            return false;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setStatus(ViewHolder holder, CutiCancellationListModel list) {
        if (Boolean.parseBoolean(list.IsAppHead)) {
            holder.imgApproved.setVisibility(View.VISIBLE);
            holder.imgReject.setVisibility(View.GONE);
            holder.imgWaiting.setVisibility(View.GONE);
            holder.imgCancelled.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        } else if (Boolean.parseBoolean(list.IsRejectHead)) {
            holder.imgApproved.setVisibility(View.GONE);
            holder.imgReject.setVisibility(View.VISIBLE);
            holder.imgWaiting.setVisibility(View.GONE);
            holder.imgCancelled.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.VISIBLE);
        } else {
            holder.imgApproved.setVisibility(View.GONE);
            holder.imgReject.setVisibility(View.GONE);
            holder.imgWaiting.setVisibility(View.VISIBLE);
            holder.imgCancelled.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        }

//        if (Boolean.parseBoolean(list.IsCancel)) {
//            holder.imgApproved.setVisibility(View.GONE);
//            holder.imgReject.setVisibility(View.GONE);
//            holder.imgWaiting.setVisibility(View.GONE);
//            holder.imgCancelled.setVisibility(View.VISIBLE);
//            holder.txtRejectRemark.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.txtNoTransaksi)
        TextView txtNoTransaksi;
        @Bind(R.id.txtNamaKaryawan)
        TextView txtNamaKaryawan;
        @Bind(R.id.txtTanggalTranskasi)
        TextView txtTanggalTranskasi;
        @Bind(R.id.txtTipeForm)
        TextView txtTipeForm;
        @Bind(R.id.txtRejectRemark)
        TextView txtRejectRemark;
        @Bind(R.id.viewContainer)
        RelativeLayout viewContainer;
        @Bind(R.id.imgApproved)
        ImageView imgApproved;
        @Bind(R.id.imgReject)
        ImageView imgReject;
        @Bind(R.id.imgCancelled)
        ImageView imgCancelled;
        @Bind(R.id.imgWaiting)
        ImageView imgWaiting;
        @Bind(R.id.viewIcon)
        LinearLayout viewIcon;
        @Bind(R.id.imgApprovedHRD)
        ImageView imgApprovedHRD;
        @Bind(R.id.imgRejectedHRD)
        ImageView imgRejectedHRD;

        View view;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }


}
