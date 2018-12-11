package com.example.project.sidangakhir;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;
import utilities.Link;
import utilities.PrefUtil;
import utilities.Utils;

public class PengumumanActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextView judulToolbar;
    private EditText edJudul, edIsi, edTglwal, edTglFinish;
    private TextInputLayout ilJudul, ilIsi;
    private Button btnSave;
    private PrefUtil prefUtil;
    private BaseApiService mApiService;
    private ProgressDialog pDialog;
    private Calendar dateAndTime = Calendar.getInstance();
    private SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    private Date tglFrom, tglTo;
    private String hasilTglTo, hasilTglFrom, userId;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String status, idPengumuman, judul, isi, tglAwal, tglAkhir, hasiltglAwal, hasiltglAkhir;
    private SharedPreferences shared;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pengumuman_view);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                status = i.getString("Status");// ADD/EDIT
                idPengumuman = i.getString("idPengumuman");
                judul = i.getString("judul");
                isi = i.getString("isi");
                tglAwal = i.getString("tglAwal");
                tglAkhir = i.getString("tglAkhir");
                hasiltglAwal = i.getString("hasilTglAwal");
                hasiltglAkhir = i.getString("hsilTglAkhir");
            } catch (Exception e) {}
        }
        prefUtil            = new PrefUtil(this);
        mApiService         = Link.getAPIService();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        try{
            shared  = prefUtil.getUserInfo();
            userId = shared.getString(PrefUtil.ID, null);
        }catch (Exception e){e.getMessage();}

        imgBack = (ImageView)findViewById(R.id.imgPengumumanBack);
        judulToolbar = (TextView)findViewById(R.id.txtPengumumanJudulToolbar);
        edJudul = (EditText) findViewById(R.id.eAddPengumumanJudul);
        ilJudul = (TextInputLayout) findViewById(R.id.input_layout_pengumuman_judul);
        edIsi = (EditText) findViewById(R.id.eAddPengumumanIsi);
        ilIsi = (TextInputLayout) findViewById(R.id.input_layout_pengumuman_isi);
        edTglwal = (EditText) findViewById(R.id.eAddPengumumanTglBerlakuwal);
        edTglFinish = (EditText) findViewById(R.id.eAddPengumumanTglBerlakuFinish);
        btnSave = (Button) findViewById(R.id.btnPengumumanSave);

        if(status.equals("EDIT")){
            edJudul.setText(judul);
            edIsi.setText(isi);
            edTglwal.setText(tglAwal);
            edTglFinish.setText(tglAkhir);
            hasilTglTo=hasiltglAkhir;
            hasilTglFrom=hasiltglAwal;
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateJudul() && validateIsi() &&
                        validateTglAwal() && validateTglTo() ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PengumumanActivity.this);
                    builder.setTitle("Konfirmasi");
                    builder.setMessage("Data akan diproses?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(status.equals("ADD")){
                                        insertData(edJudul.getText().toString(), edIsi.getText().toString(), hasilTglFrom, hasilTglTo, userId);
                                    }else if(status.equals("EDIT")){
                                        updateData(idPengumuman, edJudul.getText().toString(), edIsi.getText().toString(), hasilTglFrom, hasilTglTo, userId);
                                    }
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

        edTglwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edTglwal.setEnabled(false);
                hideKeyboard(v);
                new DatePickerDialog(PengumumanActivity.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                edTglwal.setEnabled(true);
            }
        });

        edTglwal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edTglwal.setEnabled(false);
                    hideKeyboard(v);
                    new DatePickerDialog(PengumumanActivity.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                            dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                    edTglwal.setEnabled(true);
                }
            }
        });

        edTglFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edTglFinish.setEnabled(false);
                hideKeyboard(v);
                new DatePickerDialog(PengumumanActivity.this, dTo, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                edTglFinish.setEnabled(true);
            }
        });

        edTglFinish.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edTglFinish.setEnabled(false);
                    hideKeyboard(v);
                    new DatePickerDialog(PengumumanActivity.this, dTo, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                            dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
                    edTglFinish.setEnabled(true);
                }
            }
        });
    }

    private void insertData(String judul, String isi, String tglFrom, String tglTo, String userId){
        pDialog.setMessage("Simpan Data ...\nHarap Tunggu");
        showDialog();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        mApiService.insertPengumuman(judul, isi, tglFrom, tglTo, userId, tanggalNow)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    hideDialog();
                                    Toast.makeText(PengumumanActivity.this, "DATA BERHASIL DISIMPAN", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(PengumumanActivity.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
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
                            Toast.makeText(PengumumanActivity.this, "PROSES SIMPAN GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(PengumumanActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateData(String idPengumuman, String judul, String isi, String tglFrom, String tglTo, String userId){
        pDialog.setMessage("Update Data ...\nHarap Tunggu");
        showDialog();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        mApiService.updatePengumuman(idPengumuman, judul, isi, tglFrom, tglTo, userId, tanggalNow)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    hideDialog();
                                    Toast.makeText(PengumumanActivity.this, "DATA BERHASIL DIPERBARUI", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(PengumumanActivity.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
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
                            Toast.makeText(PengumumanActivity.this, "PROSES UPDATE GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(PengumumanActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateJudul() {
        boolean value;
        if (edJudul.getText().toString().isEmpty()){
            value=false;
            requestFocus(edJudul);
            ilJudul.setError(getString(R.string.err_msg_judul_pengumuman));
        } else {
            value=true;
            ilJudul.setError(null);
        }
        return value;
    }

    private boolean validateIsi() {
        boolean value;
        if (edIsi.getText().toString().isEmpty()){
            value=false;
            requestFocus(edIsi);
            ilIsi.setError(getString(R.string.err_msg_judul_isi));
        } else {
            value=true;
            ilIsi.setError(null);
        }
        return value;
    }

    private boolean validateTglAwal() {
        boolean value;
        if (edTglwal.getText().toString().isEmpty()){
            value=false;
            Toast.makeText(PengumumanActivity.this, "Tanggal berlaku awal harap diisi", Toast.LENGTH_LONG).show();
        } else {
            value=true;
        }
        return value;
    }

    private boolean validateTglTo() {
        boolean value;
        if (edTglFinish.getText().toString().isEmpty()){
            value=false;
            Toast.makeText(PengumumanActivity.this, "Tanggal berlaku akhir harap diisi", Toast.LENGTH_LONG).show();
        } else {
            value=true;
        }
        return value;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private DatePickerDialog.OnDateSetListener dFrom =new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);
            updatelabelFrom();
        }
    };

    private void updatelabelFrom(){
        edTglwal.setText(sdf1.format(dateAndTime.getTime()));
        tglFrom=dateAndTime.getTime();
        hasilTglFrom=sdf2.format(dateAndTime.getTime());
    }

    private DatePickerDialog.OnDateSetListener dTo =new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, month);
            dateAndTime.set(Calendar.DAY_OF_MONTH, day);
            updatelabelTo();
        }
    };

    private void updatelabelTo(){
        edTglFinish.setText(sdf1.format(dateAndTime.getTime()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateAndTime.getTime());
        tglTo=Utils.getLastTimeOfDay(cal.getTime());
        hasilTglTo=df.format(tglTo);
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
