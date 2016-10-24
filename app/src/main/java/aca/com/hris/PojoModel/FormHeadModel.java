package aca.com.hris.PojoModel;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import aca.com.hris.Database.DBMaster;

/**
 * Created by Marsel on 17/11/2015.
 */
public class FormHeadModel {

    @Column
    @PrimaryKey
    public String KodeForm;

//    (autoincrement = true)
//    public int _id;
//
//    @Column

    @Column 
    public String   GrpFormAbsensi;
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
