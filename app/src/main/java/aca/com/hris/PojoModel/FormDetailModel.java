package aca.com.hris.PojoModel;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import aca.com.hris.Database.DBMaster;
import aca.com.hris.Database.FormDetail;

/**
 * Created by Marsel on 17/11/2015.
 */
public class FormDetailModel  {
    public String KodeKomponen,
                  KodeForm;

    public String  Caption;

}
