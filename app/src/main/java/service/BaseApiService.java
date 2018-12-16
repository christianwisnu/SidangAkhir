package service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import utilities.JSONResponse;

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

    @FormUrlEncoded
    @POST("saveJudulSidang.php")
    Call<ResponseBody> saveJudul(@Field("userid") String id,
                                        @Field("judul") String judul,
                                        @Field("tglNow") String tanggalNow,
                                        @Field("nikDosen") String nikDosen);

    @FormUrlEncoded
    @POST("listDosBing.php")
    Call<JSONResponse> getDosBing(@Field("userId") String idUser);

    @FormUrlEncoded
    @POST("insertPengumuman.php")
    Call<ResponseBody> insertPengumuman(@Field("judul") String judul,
                                       @Field("isi") String isi,
                                       @Field("tglFrom") String tglFrom,
                                       @Field("tglTo") String tglTo,
                                       @Field("userId") String userId,
                                       @Field("tglNow") String tglNow);

    @FormUrlEncoded
    @POST("updatePengumuman.php")
    Call<ResponseBody> updatePengumuman(@Field("id") String idPengumuman,
                                        @Field("judul") String judul,
                                        @Field("isi") String isi,
                                        @Field("tglFrom") String tglFrom,
                                        @Field("tglTo") String tglTo,
                                        @Field("userId") String userId,
                                        @Field("tglNow") String tglNow);

    @FormUrlEncoded
    @POST("insertJadwalDosen.php")
    Call<ResponseBody> insertJadwalDosen(@Field("idDosen") String userId,
                                        @Field("jamMulai") String jamMulai,
                                        @Field("jamAkhir") String jamAkhir,
                                        @Field("hari") String hari);

    @FormUrlEncoded
    @POST("updateJadwalDosen.php")
    Call<ResponseBody> updateJadwalDosen(@Field("idDosen") String userId,
                                         @Field("line") Integer line,
                                        @Field("jamMulai") String jamMulai,
                                        @Field("jamAkhir") String jamAkhir,
                                        @Field("hari") String hari);

    @Multipart
    @POST("uploadPembayaran.php?apicall=upload")
    Call<ResponseBody> uploadImageBayar(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file,
                                       @Part("desc") RequestBody desc);

    @Multipart
    @POST("uploadBimbingan.php?apicall=upload")
    Call<ResponseBody> uploadImageBimbingan(@Part("image\"; filename=\"myfile.jpg\" ") RequestBody file,
                                        @Part("desc") RequestBody desc);

    @FormUrlEncoded
    @POST("updateGbrPembayaran.php")
    Call<ResponseBody> updateUploadBayar(@Field("userId") String idMhs,
                                    @Field("fileName") String nama,
                                    @Field("tglNow") String tglNow);

    @FormUrlEncoded
    @POST("updateGbrBimbingan.php")
    Call<ResponseBody> updateUploadBimbingan(@Field("userId") String idMhs,
                                         @Field("fileName") String nama,
                                         @Field("tglNow") String tglNow);

    @FormUrlEncoded
    @POST("updateDataSkripsi.php")
    Call<ResponseBody> updateDataSkripsi(@Field("id") Integer id,
                                         @Field("idAdmin") String idAdmin,
                                         @Field("tglSidang") String tglSidang,
                                         @Field("noUrut") Integer noUrut,
                                         @Field("idUji1") String idUji1,
                                         @Field("idUji2") String idUji2,
                                         @Field("tglNow") String tglNow);
}