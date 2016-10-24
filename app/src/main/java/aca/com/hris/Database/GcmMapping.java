package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;


/**
 * Created by Marsel on 6/4/2016.
 */
@Table(databaseName = DBMaster.NAME)
public class GcmMapping extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public int id;

    @Column
    public String
            IdKaryawan,
            RegisteredToken,
            IsActive,
            IsLogin;

    @Column
    public Date
            CreateDate,
            ModifyDate;

    public GcmMapping() {
        IsLogin = String.valueOf(false);
    }
}
