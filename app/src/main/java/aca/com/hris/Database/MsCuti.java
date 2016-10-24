package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 17/11/2015.
 */
@Table(databaseName = DBMaster.NAME)
public class MsCuti extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    public int _id;

    @Column
    public String KodeCuti,
                    NamaCuti,
                    TipeCuti,
                    Gender,
                    JumlahHari,
                    IsGaji,
                    IsFix,
                    IsPotongCuti,
                    IsLihatCalendar,
                    IsSetengahHari,
                    IsTelat,
                    JamMasuk,
                    Keterangan;

}
