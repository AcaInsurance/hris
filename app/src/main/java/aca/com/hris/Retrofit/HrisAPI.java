package aca.com.hris.Retrofit;

import java.util.List;

import aca.com.hris.Database.Employee;
import aca.com.hris.PojoModel.AbsensiCancellationListModel;
import aca.com.hris.PojoModel.AbsensiViewListModel;
import aca.com.hris.PojoModel.ApprovalAbsensiCancellationModel;
import aca.com.hris.PojoModel.ApprovalAbsensiModel;
import aca.com.hris.PojoModel.ApprovalCutiCancellationModel;
import aca.com.hris.PojoModel.ApprovalCutiModel;
import aca.com.hris.PojoModel.CutiCancellationListModel;
import aca.com.hris.PojoModel.CutiListForCancellationModel;
import aca.com.hris.PojoModel.CutiListModel;
import aca.com.hris.PojoModel.EmployeeModel;
import aca.com.hris.PojoModel.FormDetailModel;
import aca.com.hris.PojoModel.FormHeadModel;
import aca.com.hris.PojoModel.FormListForCancellationModel;
import aca.com.hris.PojoModel.FormListModel;
import aca.com.hris.PojoModel.HeadKodeLevelModel;
import aca.com.hris.PojoModel.MangkirModel;
import aca.com.hris.PojoModel.MsCutiModel;
import aca.com.hris.PojoModel.SetVarModel;
import aca.com.hris.PojoModel.StandardFieldModel;
import aca.com.hris.PojoModel.SummaryPeriodeModel;
import aca.com.hris.PojoModel.UserModel;
import aca.com.hris.PojoModel.Version;
import aca.com.hris.PojoModel.VersioningModel;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface HrisAPI {

    @GET("{method}")    Observable<List<FormDetailModel>> FormDetail(@Path("method") String method);

    @GET("{method}")    Observable<List<FormHeadModel>> FormHead(@Path("method") String method);

    @GET("{method}")    Observable<List<StandardFieldModel>> StandardField(@Path("method") String method);

    @GET("{method}")    Observable<List<SetVarModel>> SetVar(@Path("method") String method);

    @GET("{method}")  Observable<List<MsCutiModel>> MsCuti(@Path("method") String method);

    @GET("{method}")    Observable<List<SummaryPeriodeModel>> SummaryPeriode(@Path("method") String method);

    @GET("{method}")    Observable<List<Version>> Version(@Path("method") String method);

    @GET("{method}")    Observable<List<UserModel>> User(@Path("method") String method);

    @GET("{method}")    Observable<List<MangkirModel>> Mangkir(@Path("method") String method);

    @GET("{method}")    Observable<List<HeadKodeLevelModel>> HeadKodeLevel(@Path("method") String method);

    @GET("{method}")    Observable<List<FormListModel>> FormList(@Path("method") String method);

    @GET("{method}")    Observable<List<CutiListModel>> CutiList(@Path("method") String method);

    @GET("{method}")    Observable<List<AbsensiCancellationListModel>> AbsensiCancellationList(@Path("method") String method);

    @GET("{method}")    Observable<List<CutiCancellationListModel>> CutiCancellationList(@Path("method") String method);

    @GET("{method}")    Observable<List<AbsensiViewListModel>> AbsensiViewList(@Path("method") String method);

    @GET("{method}")    Observable<List<FormListForCancellationModel>> FormListForCancellation(@Path("method") String method);

    @GET("{method}")    Observable<List<CutiListForCancellationModel>> CutiListForCancellation(@Path("method") String method);

    @GET("{method}")    Observable<List<ApprovalAbsensiModel>> ApprovalAbsensi(@Path("method") String method);

    @GET("{method}")    Observable<List<ApprovalCutiModel>> ApprovalCuti(@Path("method") String method);

    @GET("{method}")    Observable<List<ApprovalAbsensiCancellationModel>> ApprovalAbsensiCancellation(@Path("method") String method);

    @GET("{method}")    Observable<List<ApprovalCutiCancellationModel>> ApprovalCutiCancenllation(@Path("method") String method);

    @GET("{method}")    Observable<List<VersioningModel>> Versioning(@Path("method") String method);

    @GET("{method}")    Observable<Void> DeleteToken(@Path("method") String method);

    @GET("{method}")    Observable<List<EmployeeModel>> GetKaryawanStatusAllowed(@Path("method") String method);


}