package aca.com.hris.Database.Migration;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import aca.com.hris.Database.DBMaster;
import aca.com.hris.Database.Employee;

/**
 * Created by Marsel on 26/10/2015.
 */
@Migration(version = DBMaster.VERSION, databaseName = DBMaster.NAME)
public class Migration1 extends AlterTableMigration<Employee> {

    public Migration1() {
        super(Employee.class);
    }

    @Override
    public void onPreMigrate() {
        super.onPreMigrate();

        addColumn(boolean.class, "IsAllowCutiMin");
        addColumn(boolean.class, "IsAllowBatasForm");
        addColumn(boolean.class, "IsApprovalKhusus");
    }


}
