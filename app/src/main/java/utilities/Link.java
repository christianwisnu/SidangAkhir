package utilities;

import service.BaseApiService;

public class Link {

    public static final String BASE_URL_API = "http://softchrist.com/sidang_ta/php/";
    public static final String BASE_URL_IMAGE = "http://softchrist.com/sidang_ta/image/";
    public static final String BASE_URL_VIDEO = "http://softchrist.com/sidang_ta/video/";

    // Mendeklarasikan Interface BaseApiService
    public static BaseApiService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }

    public static BaseApiService getImageService(){
        return RetrofitClient.getClient(BASE_URL_IMAGE).create(BaseApiService.class);
    }

    public static BaseApiService getVideoService(){
        return RetrofitClient.getClient(BASE_URL_VIDEO).create(BaseApiService.class);
    }
}
