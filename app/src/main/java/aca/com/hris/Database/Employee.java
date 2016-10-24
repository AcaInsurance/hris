package aca.com.hris.Database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.joda.time.DateTime;

/**
 * Created by Marsel on 17/11/2015.
 */
@Table(databaseName = DBMaster.NAME)
public class Employee extends BaseModel {

    @Column
    @PrimaryKey
    public long IdKary;

    @Column
    public String NIK
            ,StatusKaryawan
            ,PeriodeAwal
            ,PeriodeAkhir
            ,NamaCabang
            ,NamaJabatan
            ,NamaRangking
            ,JobDesk
            ,NamaKaryawan
            ,NamaPanggil
            ,JenisKelamin
            ,KdGender
            ,TglLahir
            ,TempatLahir
            ,Kewarganegaraan
            ,Telp1
            ,Telp2
            ,KTP
            ,Agama
            ,BpjsKetenagakerjaan
            ,BpjsKesehatan
            ,NoDPLP
            ,NoNPWP
            ,StatusPernikahan
            ,KodeLevel
            ,KodeCabang
            ,KdTpKerja
            ,KdRanking;

    @Column
    public Double SisaCuti;

    @Column
    public boolean
            IsAllowCutiMin,
            IsAllowBatasForm,
            IsApprovalKhusus;



}
