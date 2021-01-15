package com.power.doc.utils;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 
 */
public class ClientUploadUtils {

    public static ResponseBody upload(String url, String filePath, String fileName, String docName) throws Exception {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                .addFormDataPart("docName", docName)
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + UUID.randomUUID())
                .url(url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return response.body();
    }


    public static void main(String[] args) throws IOException {
        try {
            String fileName = "index.html";
            String filePath = "D:/md3/index.html";
            String url = "http://localhost:8888/anobody/manage/doc/upload";
            System.out.println(upload(url, filePath, fileName, "").string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}