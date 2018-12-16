package com.example.project.sidangakhir;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class JadwalDosenActivity extends AppCompatActivity {

    private String status, userId, hari, jamAwal, jamAkhir;
    private Button btnSave;
    private PrefUtil prefUtil;
    private BaseApiService mApiService;
    private ProgressDialog pDialog;
    private ImageView imgBack, imgJamAwal, imgJamAkhir;
    private TextView judulToolbar;
    private EditText edJamAwal, edJamAkhir;
    private Spinner sphari;
    private SharedPreferences shared;
    private Integer line;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jadwal_dosen_activity);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                status = i.getString("Status");// ADD/EDIT
                userId = i.getString("userId");
                hari = i.getString("hari");
                line = i.getInt("line");
                jamAwal = i.getString("jamAwal");
                jamAkhir = i.getString("jamAkhir");
            } catch (Exception e) {}
        }
        prefUtil            = new PrefUtil(this);
        mApiService         = Link.getAPIService();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        imgBack = (ImageView)findViewById(R.id.imgJadwalDosenBack);
        sphari = (Spinner)findViewById(R.id.spJadwalDosenHari);
        edJamAwal = (EditText)findViewById(R.id.eAddJadwalDosenAwal);
        imgJamAwal = (ImageView)findViewById(R.id.imgJadwalDosenJamAwal);
        edJamAkhir = (EditText)findViewById(R.id.eAddJadwalDosenAkhir);
        imgJamAkhir = (ImageView)findViewById(R.id.imgJadwalDosenJamAkhir);
        btnSave = (Button)findViewById(R.id.btnJadwalDosenSave);

        if(status.equals("EDIT")){
            edJamAwal.setText(jamAwal);
            edJamAkhir.setText(jamAkhir);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.country_arrays_hari, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sphari.setAdapter(adapter);
            if (hari != null) {
                int spinnerPosition = adapter.getPosition(hari);
                sphari.setSelection(spinnerPosition);
            }
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgJamAwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihJam(edJamAwal);
            }
        });

        imgJamAkhir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihJam(edJamAkhir);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateJamAwal() && validateJamAkhir() ){
                    AlertDialog.Builder builder = new AlertDialog.Builder(JadwalDosenActivity.this);
                    builder.setTitle("Konfirmasi");
                    builder.setMessage("Data akan diproses?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if(status.equals("ADD")){
                                        insertData(userId, String.valueOf(sphari.getSelectedItem()), edJamAwal.getText().toString(),
                                                edJamAkhir.getText().toString());
                                    }else if(status.equals("EDIT")){
                                        updateData(userId, String.valueOf(sphari.getSelectedItem()), edJamAwal.getText().toString(),
                                                edJamAkhir.getText().toString(), line);
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
    }

    private void insertData(String nikDosen, String hari, String jamFrom, String jamTo){
        pDialog.setMessage("Simpan Data ...\nHarap Tunggu");
        showDialog();
        mApiService.insertJadwalDosen(nikDosen, jamFrom, jamTo, hari)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    hideDialog();
                                    Toast.makeText(JadwalDosenActivity.this, "DATA BERHASIL DISIMPAN", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(JadwalDosenActivity.this, error_message, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(JadwalDosenActivity.this, "PROSES SIMPAN GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(JadwalDosenActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateData(String nikDosen, String hari, String jamFrom, String jamTo, Integer line){
        pDialog.setMessage("Update Data ...\nHarap Tunggu");
        showDialog();
        mApiService.updateJadwalDosen(nikDosen, line, jamFrom, jamTo, hari)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    hideDialog();
                                    Toast.makeText(JadwalDosenActivity.this, "DATA BERHASIL DIPERBARUI", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(JadwalDosenActivity.this, error_message, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(JadwalDosenActivity.this, "PROSES UPDATE GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(JadwalDosenActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private boolean validateJamAwal() {
        boolean value;
        if (edJamAwal.getText().toString().isEmpty()){
            value=false;
            Toast.makeText(JadwalDosenActivity.this, "Jam Mulai harap diisi", Toast.LENGTH_LONG).show();
        } else {
            value=true;
        }
        return value;
    }

    private boolean validateJamAkhir() {
        boolean value;
        if (edJamAkhir.getText().toString().isEmpty()){
            value=false;
            Toast.makeText(JadwalDosenActivity.this, "Jam Akhir harap diisi", Toast.LENGTH_LONG).show();
        } else {
            value=true;
        }
        return value;
    }

    private void pilihJam(final EditText edittext) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(JadwalDosenActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String sHrs, sMins;
                if (selectedMinute < 10) {
                    sMins = "0" + selectedMinute;
                } else {
                    sMins = String.valueOf(selectedMinute);
                }
                if (selectedHour < 10) {
                    sHrs = "0" + selectedHour;
                } else {
                    sHrs = String.valueOf(selectedHour);
                }
                setSelectedTime(sHrs, sMins, edittext);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Pilih Jam");
        mTimePicker.show();
    }

    public void setSelectedTime(String hourOfDay, String minute, EditText edText) {
        edText.setText(hourOfDay + ":" + minute);
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}