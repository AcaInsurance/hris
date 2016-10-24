package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 2/2/2016.
 */

@Table(databaseName = DBMaster.NAME)
public class GeneralVar extends BaseModel {
    public static final String VALUE_APP_SOURCE = "0";
    public static final String VALUE_MEDIA_SOURCE = "1";

    public static final String KEY_COVER_SOURCE = "COVER_SOURCE";

    @Column
    @PrimaryKey
    public String key;

    @Column
    public String value;
}
