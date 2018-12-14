package com.example.project.sidangakhir;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;

public class UploadPembayaran extends AppCompatActivity {

    private ImageView imgBack, imgFoto;
    private Button btnProses;
    private String path, namaImage, hasilImage, hasilFoto="N", userId, encodedString, cdstatus;
    private String[] items = {"Camera", "Gallery"};
    private static final int REQUEST_CODE_CAMERA = 0012;
    private static final int REQUEST_CODE_GALLERY = 0013;
    private PrefUtil pref;
    private SharedPreferences shared;
    private Uri selectedImage;
    private ProgressDialog pDialog;
    private BaseApiService mApiService, mUploadService;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView txtMessage;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_pembayaran);
        pref = new PrefUtil(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        mApiService = Link.getAPIService();
        mUploadService = Link.getImageServiceBayar();
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                namaImage = i.getString("namaImage");
                cdstatus = i.getString("cdstatus");
            } catch (Exception e) {}
        }
        try{
            shared  = pref.getUserInfo();
            userId = shared.getString(PrefUtil.ID, null);

        }catch (Exception e){e.getMessage();}
        imgBack = (ImageView)findViewById(R.id.imgUploadBayarBack);
        imgFoto = (ImageView)findViewById(R.id.imgBuktiBayar);
        txtMessage = (TextView) findViewById(R.id.txtMessageBayar);
        btnProses = (Button) findViewById(R.id.btnUploadBayar);

        if(!namaImage.equals("-")){
            imgFoto.setBackgroundResource(0);
            Utils.getCycleImage(Link.BASE_URL_IMAGE_BAYAR+namaImage, imgFoto, this);
            if(!cdstatus.substring(1,2).equals("*")){
                btnProses.setVisibility(View.GONE);
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setText("Bukti Pembayaran sudah divalidasi");
            }else{
                btnProses.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.GONE);
                txtMessage.setText("");
            }
        }else{
            btnProses.setVisibility(View.VISIBLE);
            txtMessage.setVisibility(View.GONE);
            txtMessage.setText("");
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        btnProses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!hasilFoto.equals("N")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(UploadPembayaran.this);
                    builder.setTitle("Konfirmasi");
                    builder.setMessage("Data akan diproses?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    uploadImage();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    private void uploadImage() {
        pDialog.setMessage("Uploading Image Pembayaran ke Server...");
        showDialog();
        File file = new File(getRealPathFromURI(selectedImage));
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(selectedImage)), file);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), hasilImage);
        mUploadService.uploadImageBayar(requestFile, descBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonRESULTS = new JSONObject(response.body().string());
                        hideDialog();
                        if (jsonRESULTS.getString("value").equals("false")){
                            updateData(userId, hasilImage);
                        }else{
                            Toast.makeText(UploadPembayaran.this, jsonRESULTS.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException e) {
                        hideDialog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        hideDialog();
                        e.printStackTrace();
                    }
                }else{
                    hideDialog();
                    Toast.makeText(UploadPembayaran.this, "Some error occurred...", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
                Toast.makeText(UploadPembayaran.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateData(String idMhs, String fileNameGbr){
        pDialog.setMessage("Update Database ...\nHarap Tunggu");
        showDialog();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        mApiService.updateUploadBayar(idMhs, fileNameGbr, tanggalNow)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    Toast.makeText(UploadPembayaran.this, jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    hideDialog();
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(UploadPembayaran.this, error_message, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                hideDialog();
                                e.printStackTrace();
                            } catch (IOException e) {
                                hideDialog();
                                e.printStackTrace();
                            }
                        } else {
                            hideDialog();
                            Toast.makeText(UploadPembayaran.this, "GAGAL UPDATE DATA", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(UploadPembayaran.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_CODE_GALLERY);

        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    //dialog.dismiss();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);
                } else if (items[i].equals("Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, REQUEST_CODE_GALLERY);
                }
            }
        });
        builder.show();*/
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                hasilFoto = "Y";
                hasilImage = userId + "_PEMBAYARAN.jpg";
                selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Utils.getCycleImage("file:///"+picturePath, imgFoto, this);
                String fileNameSegments[] = picturePath.split("/");
                Bitmap myImg = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Must compress the Image to reduce image size to make upload easy
                myImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedString = Base64.encodeToString(byte_arr, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
