package aca.com.hris.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.esotericsoftware.kryo.util.Util;

import org.joda.time.LocalDate;

import java.util.List;
import java.util.HashMap;
import java.util.List;

import aca.com.hris.PojoModel.AbsensiViewListModel;
import aca.com.hris.R;
import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.Var;
import butterknife.Bind;
import butterknife.ButterKnife;


public class AbsensiViewAdapter extends RecyclerView.Adapter<AbsensiViewAdapter.ViewHolder> {
    public List<AbsensiViewListModel> arrList;
    public Context context;
    public Activity activity;
    private static final String TAG = "AbsensiViewAdapter";
    private List<Integer> arrYear;


    public AbsensiViewAdapter(Activity activity, List<AbsensiViewListModel> arrList) {
        this.arrList = arrList;
        this.activity = activity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_absensi_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AbsensiViewListModel map = arrList.get(position);

        LocalDate tanggal = getTanggal(map);
        String year = tanggal.getYear() + "";
        String tanggalDisplay = tanggal.toString(UtilDate.DATE_PATTERN_DISPLAY_2);

        holder.txtYear.setText(year);
        holder.imgTanggal.setImageDrawable(getDrawable(tanggalDisplay));
        holder.txtJenisAbsen.setText(getJenisAbsen(map));
        holder.txtTime.setVisibility(getVisibility(map) ? View.VISIBLE : View.GONE);
        holder.txtTime.setText(getTime(map));

//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Bundle bundle = new Bundle();
////                bundle.putString(BUNDLE_NO_TRANSAKSI, holder.txtNoTransaksi.getText().toString());
////                bundle.putInt(UtilBundle.ACTION_BUNDLE, UtilBundle.ACTION_BUNDLE_NEW);
////
////                Intent intent = new Intent(activity, CutiCancellationActivity.class);
////                intent.putExtras(bundle);
////                activity.startActivity(intent);
//
//            }
//        });

    }

    private LocalDate getTanggal(AbsensiViewListModel map) {
        String tanggal = map.TglAbsensi;
        LocalDate date = UtilDate.toDate(tanggal, UtilDate.DATE_TIME_PATTERN_SERVER);

        return date;
    }


    private String setYear(TextView txtYear, String tanggal, int position) {
        LocalDate date = UtilDate.toDate(tanggal);
        int year = date.getYear();

        if(arrYear.contains(year))
            return "0";
        arrYear.add(year);

        return year + "";
    }

    private Drawable getDrawable(String tanggal) {
        Log.d(TAG, "getDrawable font size " + (int) activity.getResources().getDimension(R.dimen.font_text_drawable));
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .fontSize((int) activity.getResources().getDimension(R.dimen.font_text_drawable))
                .endConfig()
                .buildRound(tanggal, activity.getResources().getColor(R.color.colorPrimary));
        return drawable;
    }

    private String getTime(AbsensiViewListModel map) {
        String masuk = map.JamMsk;
        return masuk;
    }

    private boolean getVisibility(AbsensiViewListModel map) {
        boolean telat = Boolean.parseBoolean(map.IsTelat);

        return telat;
    }

    private String getJenisAbsen(AbsensiViewListModel map) {
        boolean telat = Boolean.parseBoolean(map.IsTelat);
        boolean mangkir = Boolean.parseBoolean(map.IsMangkir);
        String konten = "";

        if (telat && mangkir)
            konten = "Telat dan Mangkir";
        else if (telat)
            konten = "Telat";
        else if (mangkir)
            konten = "Mangkir";

        return konten;
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

        View view;

        @Bind(R.id.txtYear)
        TextView txtYear;
        @Bind(R.id.viewYear)
        RelativeLayout viewYear;
        @Bind(R.id.txtJenisAbsen)
        TextView txtJenisAbsen;
        @Bind(R.id.txtTime)
        TextView txtTime;
        @Bind(R.id.viewContainer)
        RelativeLayout viewContainer;
        @Bind(R.id.imgTanggal)
        ImageView imgTanggal;
        @Bind(R.id.viewIcon)
        LinearLayout viewIcon;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }

}
