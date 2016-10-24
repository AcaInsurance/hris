package aca.com.hris.PojoModel;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

/**
 * Created by Marsel on 22/2/2016.
 */
public class FormListModel {
    public String NoTransaksi,
            TanggalTransaksi,
            NamaKaryawan,
            NamaForm,
            IsApproved,
            IsRejected,
            IsCancelled,
            IsHRDApproved,
            IsHRDRejected,
            AlasanReject,
            IsActive,
            RowNum;
}
