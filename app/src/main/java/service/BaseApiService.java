package service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by christian on 14/02/18.
 */

public interface BaseApiService {

    @FormUrlEncoded
    @POST("login.php")
    Call<ResponseBody> loginRequest(@Field("userid") String id,
                                    @Field("password") String pasw,
                                    @Field("status") String status);

    @FormUrlEncoded
    @POST("register.php")
    Call<ResponseBody> registerRequest(@Field("userid") String id,
                                       @Field("password") String pasw,
                                       @Field("username") String nama,
                                       @Field("tglNow") String tanggalNow,
                                       @Field("telp") String telp,
                                       @Field("alamat") String alamat,
                                       @Field("email") String email,
                                       @Field("status") String status);

    /*@FormUrlEncoded
    @POST("saveTrans.php")
    Call<ResponseBody> savePemeriksaan(@Field("pasienId") String pasienId,
                                       @Field("noTrans") String noTrans,
                                       @Field("keluhan") String keluhan,
                                       @Field("tlKanan") String tlKanan,
                                       @Field("tlKiri") String tlKiri,
                                       @Field("hdKanan") String hdKanan,
                                       @Field("hdKiri") String hdKiri,
                                       @Field("tgr") String tgr,
                                       @Field("lokasiVideo") String lokasiVideo,
                                       @Field("gbr1") String gbr1,
                                       @Field("gbr2") String gbr2,
                                       @Field("gbr3") String gbr3,
                                       @Field("kesimpulan") String kesimpulan,
                                       @Field("userBy") String userBy,
                                       @Field("tglNow") String tglNow);

    @FormUrlEncoded
    @POST("updatePasien.php")
    Call<ResponseBody> updatePasien(@Field("kode") String id,
                                    @Field("nama") String nama,
                                    @Field("alamat") String alamat,
                                    @Field("telp") String telp,
                                    @Field("gender") String gender,
                                    @Field("tgl") String birthday,
                                    @Field("userBy") String userBy,
                                    @Field("tglNow") String tglNow);

    @Multipart
    @POST("uploadGambar.php?apicall=upload")
    Call<ResponseBody> uploadImage(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file,
                                   @Part("desc") RequestBody desc);

    @Multipart
    @POST("uploadVideo.php?apicall=upload")
    Call<ResponseBody> uploadVideo(@Part("video\"; filename=\"myfile.mp4\" ") RequestBody file,
                                   @Part("desc") RequestBody desc);

    @FormUrlEncoded
    @POST("listPasien.php")
    Call<JSONResponse> getPasien(@Field("userId") String idUser);*/
}