package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Marsel on 17/11/2015.
 */
@Table(databaseName = DBMaster.NAME)
public class AbsensiCancellationList extends BaseModel {
    @Column
    @PrimaryKey
    public String NoCancel;

    @Column
    public String   TglCancel,
                    NoTransaksi,
                    NamaKaryawan,
                    NamaForm,
                    DateFrom,
                    DateTo,
                    TimeFrom,
                    TimeTo,
                    AlasanBatal,
                    IsApproved,
                    IsRejected,
                    IsHRDApproved,
                    IsHRDRejected,
                    AlasanReject,
                    IsActive;

}
