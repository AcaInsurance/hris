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
public class ImageSource extends BaseModel {

    public static final String SURAT_DOKTER = "SURAT_DOKTER";
    public static final String PROFILE_PICTURE = "PROFILE_PICTURE";
    public static final String BACKGROUND_HEADER = "BACKGROUND_HEADER";


    @Column
    @PrimaryKey
    public String key;

    @Column
    public String imageName,
                  imagePath;

}
