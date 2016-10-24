package aca.com.hris.PojoModel;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import aca.com.hris.Database.DBMaster;

/**
 * Created by Marsel on 17/11/2015.
 */
public class AbsensiCancellationListModel     {

    public String NoCancel,
            TglCancel,
            NoTransaksi,
            NamaKaryawan,
            NamaForm,
            IsApproved,
            IsRejected,
            IsHRDApproved,
            IsHRDRejected,
            AlasanReject,
            IsActive,
            RowNum;

}
