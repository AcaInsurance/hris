package aca.com.hris.PojoModel;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import aca.com.hris.Database.DBMaster;

/**
 * Created by Marsel on 17/11/2015.
 */
public class MsCutiModel {
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
