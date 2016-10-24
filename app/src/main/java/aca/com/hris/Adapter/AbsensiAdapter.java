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

import java.util.ArrayList;
import java.util.List;

import aca.com.hris.AbsensiActivity;
import aca.com.hris.Database.FormList;
import aca.com.hris.PojoModel.FormListModel;
import aca.com.hris.R;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.UtilDate;
import butterknife.Bind;
import butterknife.ButterKnife;


public class AbsensiAdapter extends RecyclerView.Adapter<AbsensiAdapter.ViewHolder> {
    public static String BUNDLE_NO_TRANSAKSI = "BUNDLE_NO_TRANSAKSI";

    public List<FormListModel> arrList;
    public Context context;
    public Activity activity;


    public AbsensiAdapter(Activity activity, List<FormListModel> arrList) {
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
        final FormListModel formList = arrList.get(position);
        String formatTanggal = UtilDate.format(formList.TanggalTransaksi, UtilDate.DATE_TIME_PATTERN_SERVER, UtilDate.DATE_PATTERN_DISPLAY_2);
        holder.txtTanggalTranskasi.setText(formatTanggal);
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
                bundle.putString(BUNDLE_NO_TRANSAKSI, holder.txtNoTransaksi.getText().toString());
                bundle.putInt(UtilBundle.ACTION_BUNDLE, action);
                bundle.putBoolean(UtilBundle.BUNDLE_ON_LOAD, true);
                bundle.putInt(UtilBundle.POSITION, position);


                Intent intent = new Intent(activity, AbsensiActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, UtilBundle.REQUEST_CODE_EDIT_FORM);

            }
        });

    }

    private void bindWatermark(ViewHolder holder, FormListModel formList) {
        if (formList == null || holder == null)
            return;

        boolean hrdApproved = Boolean.parseBoolean(formList.IsHRDApproved);
        boolean hrdRejected = Boolean.parseBoolean(formList.IsHRDRejected);


        if(!hrdApproved && !hrdRejected) {
            holder.imgRejectedHRD.setVisibility(View.GONE);
            holder.imgApprovedHRD.setVisibility(View.GONE);
        }
        else if (hrdApproved) {
            holder.imgApprovedHRD.setVisibility(View.VISIBLE);
        }
        else if(hrdRejected)  {
            holder.imgRejectedHRD.setVisibility(View.VISIBLE);
        }
    }

    private boolean isReviewed(FormListModel list) {
        boolean approved = Boolean.parseBoolean(list.IsApproved);
        boolean rejected = Boolean.parseBoolean(list.IsRejected);

        if (approved || rejected)
            return true;
        else
            return false;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setStatus(ViewHolder holder, FormListModel formList) {
        if (Boolean.parseBoolean(formList.IsApproved)) {
            holder.imgApproved.setVisibility(View.VISIBLE);
            holder.imgReject.setVisibility(View.GONE);
            holder.imgWaiting.setVisibility(View.GONE);
            holder.imgCancelled.setVisibility(View.GONE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        } else if (Boolean.parseBoolean(formList.IsRejected)) {
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

        if (Boolean.parseBoolean(formList.IsCancelled)) {
            holder.imgApproved.setVisibility(View.GONE);
            holder.imgReject.setVisibility(View.GONE);
            holder.imgWaiting.setVisibility(View.GONE);
            holder.imgCancelled.setVisibility(View.VISIBLE);
            holder.txtRejectRemark.setVisibility(View.GONE);
        }

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
        @Bind(R.id.txtTipeForm)
        TextView txtTipeForm;
        @Bind(R.id.txtTanggalTranskasi)
        TextView txtTanggalTranskasi;
        @Bind(R.id.txtRejectRemark)
        TextView txtRejectRemark;
        @Bind(R.id.imgApprovedHRD)
        ImageView imgApprovedHRD;
        @Bind(R.id.imgRejectedHRD)
        ImageView imgRejectedHRD;
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

        View view;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }


}
