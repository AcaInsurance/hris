package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 17/11/2015.
 */
@Table(databaseName = DBMaster.NAME)
public class Head extends BaseModel {

    @Column
    @PrimaryKey
    public  String HeadKodeLevel;

    @Column 
    public String   IdKary,
                    FullName;


    public static String getIdApprover() {
        Head head = new Select().from(Head.class).querySingle();
        return head.IdKary;
    }

}
