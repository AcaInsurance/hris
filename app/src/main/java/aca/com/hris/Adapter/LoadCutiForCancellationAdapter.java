package aca.com.hris.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.HashMap;

import aca.com.hris.CutiCancellationActivity;
import aca.com.hris.PojoModel.CutiListForCancellationModel;
import aca.com.hris.R;
import aca.com.hris.Util.UtilBundle;
import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.Var;
import butterknife.Bind;
import butterknife.ButterKnife;


public class LoadCutiForCancellationAdapter extends RecyclerView.Adapter<LoadCutiForCancellationAdapter.ViewHolder> {
    public static String BUNDLE_NO_TRANSAKSI = "BUNDLE_NO_TRANSAKSI";

    public List<CutiListForCancellationModel> arrList;
    public Context context;
    public Activity activity;


    public LoadCutiForCancellationAdapter(Activity activity, List<CutiListForCancellationModel> arrList) {
        this.arrList = arrList;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cuti_for_cancellation_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        CutiListForCancellationModel map = arrList.get(position);

        String tanggal = map.TanggalTransaksi;
        LocalDate date = UtilDate.toDate(tanggal, UtilDate.DATE_TIME_PATTERN_SERVER);
        tanggal = date.toString(UtilDate.DATE_PATTERN_DISPLAY_2);

        holder.imgTanggal.setImageDrawable(getDrawable(tanggal));
        holder.txtJumlahPotong.setText(map.JmlhPtgCuti);
        holder.txtNoTransaksi.setText(map.NoTransaksi);
        holder.txtTipeForm.setText(map.TipeCuti);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_NO_TRANSAKSI, holder.txtNoTransaksi.getText().toString());
                bundle.putInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);

                Intent intent = new Intent(activity, CutiCancellationActivity.class);
                intent.putExtras(bundle);
                activity.startActivityForResult(intent, UtilBundle.REQUEST_CODE_NEW_FORM);

            }
        });

    }



    private Drawable getDrawable(String tanggal) {
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize((int) activity.getResources().getDimension(R.dimen.font_text_drawable))
                .endConfig()
                .buildRound(tanggal, activity.getResources().getColor(R.color.colorPrimary));
        return drawable;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.txtNoTransaksi)
        TextView txtNoTransaksi;
        @Bind(R.id.txtTipeForm)
        TextView txtTipeForm;
        @Bind(R.id.txtJumlahPotong)
        TextView txtJumlahPotong;
        @Bind(R.id.viewContainer)
        RelativeLayout viewContainer;
        @Bind(R.id.imgTanggal)
        ImageView imgTanggal;
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
