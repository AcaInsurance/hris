package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 17/11/2015.
 */
@Table(databaseName = DBMaster.NAME)
public class CutiList extends BaseModel {
    @Column
    @PrimaryKey
    public String NoTransaksi;

    @Column
    public String  TanggalTransaksi,
                    NamaLengkap,
                    NamaForm,
                    IsAppHead,
                    IsRejectHead,
                    IsCancel,
                    IsAppHRD,
                    IsRejectHRD,
                    AlasanReject,
                    CutiFrom,
                    CutiTo,
                    Alasan;



}
