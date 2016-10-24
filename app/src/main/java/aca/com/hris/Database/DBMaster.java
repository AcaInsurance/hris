package aca.com.hris.Database;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

/**
 * Created by Marsel on 3/11/2015.
 */
@Database(name = DBMaster.NAME, version = DBMaster.VERSION)
 public class DBMaster {

    public static final String NAME = "DBMaster";
    public static final int VERSION = 4;


}