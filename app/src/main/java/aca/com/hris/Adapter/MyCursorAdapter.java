package aca.com.hris.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.Select;

import aca.com.hris.Database.Jabatan;
import aca.com.hris.Database.Jabatan$Table;
import aca.com.hris.Database.User;
import aca.com.hris.R;

/**
 * Created by Marsel on 18/12/2015.
 */
public class MyCursorAdapter extends CursorAdapter {
    private Activity activity;


    private String colId;
    private String colValue;

    public MyCursorAdapter(Activity activity, Cursor c, boolean autoRecover, String colId, String colValue) {
        super(activity, c, autoRecover);
        this.activity = activity;
        this.colId = colId;
        this.colValue = colValue;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return  activity.getLayoutInflater().from(context).inflate(R.layout.spinner_dropdown_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView txtID = (TextView) view.findViewById(R.id.txtIDItem);
        TextView txtValue = (TextView) view.findViewById(R.id.txtValueItem);

        String id = cursor.getString(cursor.getColumnIndexOrThrow(colId));
        String value = cursor.getString(cursor.getColumnIndexOrThrow(colValue));

        txtID.setText(id);
        txtValue.setText(String.valueOf(value));
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    public String getValueById (String id) {
        int colIndex ;
        String idValue;
        String descValue = null;

        for (int i = 0 ; i < getCursor().getCount() ; i++) {
            colIndex = ((Cursor)this.getItem(i)).getColumnIndex(colId);
            idValue = ((Cursor)this.getItem(i)).getString(colIndex);

            if (idValue.equalsIgnoreCase(id))
            {
                colIndex = ((Cursor)this.getItem(i)).getColumnIndex(colValue);
                descValue = ((Cursor)this.getItem(i)).getString(colIndex);

                break;
            }
        }

        return descValue;
    }

    public String setSelected(Button button, int post) {
        int colIndex ;
        String idValue, descValue;

        colIndex = ((Cursor)this.getItem(post)).getColumnIndex(colId);
        idValue = ((Cursor)this.getItem(post)).getString(colIndex);
        button.setTag(idValue);

        colIndex = ((Cursor)this.getItem(post)).getColumnIndex(colValue);
        descValue = ((Cursor)this.getItem(post)).getString(colIndex);
        button.setText(descValue);

        return idValue;
    }

    public String getColId() {
        return colId;
    }

    public String getColValue() {
        return colValue;
    }


}

