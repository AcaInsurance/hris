package aca.com.hris.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;

import aca.com.hris.R;

/**
 * Created by Marsel on 2/2/2016.
 */
public class CoverAdapter extends BaseAdapter {
    private Context mContext;

    public static Integer[] imageIDs = {
            R.drawable.view3,
            R.drawable.view4,
            R.drawable.view5,
            R.drawable.view6,
            R.drawable.view7,
            R.drawable.view8,
            R.drawable.view9,
            R.drawable.view10,
            R.drawable.view11,
            R.drawable.view12
    };

    public CoverAdapter(Context c) {
        mContext = c;

    }

    public int getCount() {
        return imageIDs.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setImageResource(imageIDs[position]);
            imageView.setLayoutParams(new Gallery.LayoutParams(400, 400));
            imageView.setBackgroundColor(Color.BLACK);
            return imageView;

        } else {
            imageView = (ImageView) convertView;
        }

        return imageView;
    }
}
