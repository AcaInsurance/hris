package aca.com.hris.Retrofit;

import android.text.TextUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import aca.com.hris.Util.UtilDate;
import aca.com.hris.Util.UtilService;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

public class HrisService {

    private HrisService() { }

    public static HrisAPI createHrisService(final RequestBody body) {
        Gson gson= new GsonBuilder()
                .setLenient()
                .create();

        Retrofit.Builder builder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(UtilService.getWsURL() + "/");

        if (body != null) {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Request newReq = request.newBuilder()
//                            .addHeader("Authorization", format("token %s", githubToken))
                            .post(body)
                            .build();
                    return chain.proceed(newReq);
                }
            }).build();

            builder.client(client);
        }

        return builder.build().create(HrisAPI.class);
    }
}
