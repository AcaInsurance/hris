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
public class User extends BaseModel {

    public static String ID_ROLE_EMPLOYEE = "10";
    public static String ID_ROLE_HEAD_EMPLOYEE = "15";
    public static String LOGOUT = "0";
    public static String LOGIN = "1";

    @Column
    @PrimaryKey
    public String UserId;

    @Column
    public String EmpCode,
                  kodeLevel,
                  idRole,
                  UserPass,
                  LoginStatus  ;

    public static String getUserID() {
        User user = new Select().from(User.class).querySingle();
        return user.UserId;
    }
    public static String getEmpCode() {
        User user = new Select().from(User.class).querySingle();
        if (user == null)
            return "";

        return user.EmpCode;
    }

    public static String getKodeLevel() {
        User user = new Select().from(User.class).querySingle();
        return user.kodeLevel;
    }
    public static String getIdRole() {
        User user = new Select().from(User.class).querySingle();
        return user.idRole;
    }
    public static String getUserPass() {
        User user = new Select().from(User.class).querySingle();
        return user.UserPass;
    }

    public static String getLoginStatus() {
        User user = new Select().from(User.class).querySingle();
        return user.LoginStatus;
    }

}
