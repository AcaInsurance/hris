package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 17/11/2015.
 */
@Table(databaseName = DBMaster.NAME)
public class FormHead extends BaseModel {

    @Column
    @PrimaryKey (autoincrement = true)
    public int _id;

    @Column
    public String KodeForm;

    @Column 
    public String GrpFormAbsensi;
    @Column
    public String NamaForm;
    @Column
    public String MaxHari;
    @Column
    public String JamMin;
    @Column
    public String JamMax;
    @Column
    public String IsAttachMan;
    @Column
    public String IsApprovalKhusus;
    @Column
    public String IsPassingToday;
    @Column
    public String IsTelat;
    @Column
    public String Keterangan;
    @Column
    public String KetWidth;
    @Column
    public String KetHeight;

}
