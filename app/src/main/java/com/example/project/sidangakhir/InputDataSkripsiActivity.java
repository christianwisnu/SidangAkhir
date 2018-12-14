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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import list.ListDosenPembimbing;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;
import utilities.Link;
import utilities.PrefUtil;

public class InputDataSkripsiActivity extends AppCompatActivity {

    private BaseApiService mApiService;
    private ProgressDialog pDialog;
    private PrefUtil prefUtil;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private ImageView imgBack;
    private Button btnSimpan;
    private TextView txtJudulSkripsi, txtNbi;
    private TextInputLayout inputLayoutDosBing, inputLayoutDosUji1, inputLayoutDosUji2, inputLayoutTglSidang,
            inputLayoutNoUrut;
    private EditText eDosBing, eDosUji1, eDosUji2, eTglSidang, eNoUrut;
    private String idDosbing, idUji1, idUji2, idMhs, nmMhs, nmDosbing, judul, tglSidang, idUser;
    private Integer idSidang;
    private int RESULT_DOSUJI1 = 1;
    private int RESULT_DOSUJI2 = 2;
    private Calendar dateAndTime = Calendar.getInstance();
    private SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
    private PrefUtil pref;
    private SharedPreferences shared;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_data_skripsi);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                idDosbing = i.getString("idDosbing");
                nmDosbing = i.getString("nmDosbing");
                idMhs = i.getString("idMhs");
                nmMhs = i.getString("nmMhs");
                judul = i.getString("judul");
                idSidang = i.getInt("idSidang");
            } catch (Exception e) {}
        }
        pref = new PrefUtil(this);
        try{
            shared  = pref.getUserInfo();
            idUser = shared.getString(PrefUtil.ID, null);

        }catch (Exception e){e.getMessage();}
        mApiService         = Link.getAPIService();
        prefUtil            = new PrefUtil(this);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        imgBack = (ImageView) findViewById(R.id.imgInfoDataSkripsiBack);
        btnSimpan = (Button) findViewById(R.id.bSimpanDataSkripsi);
        txtJudulSkripsi = (TextView)findViewById(R.id.txtInfoDataSkripsiJudul);
        txtNbi = (TextView)findViewById(R.id.txtInfoDataSkripsiNBIdanNama);
        inputLayoutDosBing = (TextInputLayout)findViewById(R.id.input_layout_dataskripsi_dosbing);
        inputLayoutDosUji1 = (TextInputLayout)findViewById(R.id.input_layout_dataskripsi_dosuji1);
        inputLayoutDosUji2 = (TextInputLayout)findViewById(R.id.input_layout_dataskripsi_dosuji2);
        inputLayoutTglSidang = (TextInputLayout)findViewById(R.id.input_layout_dataskripsi_tglsidang);
        inputLayoutNoUrut = (TextInputLayout)findViewById(R.id.input_layout_dataskripsi_nourut);
        eDosBing = (EditText)findViewById(R.id.eDataSkripsiDosbing);
        eDosUji1 = (EditText)findViewById(R.id.eDataSkripsiDosUji1);
        eDosUji2 = (EditText)findViewById(R.id.eDataSkripsiDosUji2);
        eTglSidang = (EditText)findViewById(R.id.eDataSkripsiTglSidang);
        eNoUrut = (EditText)findViewById(R.id.eDataSkripsiNoUrut);

        eDosBing.setText(nmDosbing);
        txtJudulSkripsi.setText(judul);
        txtNbi.setText("("+idMhs+") "+nmMhs);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUji1() && validateUji2() && validateTgl() && validateNoUrut() && validatePenguji()
                        && validateBingUji1() && validateBingUji2()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(InputDataSkripsiActivity.this);
                    builder.setTitle("Konfirmasi");
                    builder.setMessage("Data akan diproses?")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    updateData(idSidang, idUser, tglSidang, Integer.valueOf(eNoUrut.getText().toString()),
                                            idUji1, idUji2);
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

        eDosUji1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eDosUji1.setEnabled(false);
                hideKeyboard(v);
                pilihDosUji1();
                eDosUji1.setEnabled(true);
            }
        });

        eDosUji1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    eDosUji1.setEnabled(false);
                    hideKeyboard(v);
                    pilihDosUji1();
                    eDosUji1.setEnabled(true);
                }
            }
        });

        eDosUji2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eDosUji2.setEnabled(false);
                hideKeyboard(v);
                pilihDosUji2();
                eDosUji2.setEnabled(true);
            }
        });

        eDosUji2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    eDosUji2.setEnabled(false);
                    hideKeyboard(v);
                    pilihDosUji2();
                    eDosUji2.setEnabled(true);
                }
            }
        });

        eTglSidang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTglSidang.setEnabled(false);
                hideKeyboard(v);
                settingTanggalFrom();
                eTglSidang.setEnabled(true);
            }
        });

        eTglSidang.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    eTglSidang.setEnabled(false);
                    hideKeyboard(v);
                    settingTanggalFrom();
                    eTglSidang.setEnabled(true);
                }
            }
        });
    }

    private void updateData(Integer id, String idAdmin, String tglSidang, Integer noUrut, String idUji1, String idUji2){
        pDialog.setMessage("Update Database ...\nHarap Tunggu");
        showDialog();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        mApiService.updateDataSkripsi(id, idAdmin, tglSidang, noUrut, idUji1, idUji2, tanggalNow)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    Toast.makeText(InputDataSkripsiActivity.this, jsonRESULTS.getString("message"), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    hideDialog();
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(InputDataSkripsiActivity.this, error_message, Toast.LENGTH_LONG).show();
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
                            Toast.makeText(InputDataSkripsiActivity.this, "GAGAL UPDATE DATA", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(InputDataSkripsiActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
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
        eTglSidang.setText(df2.format(dateAndTime.getTime()));
        tglSidang = df1.format(dateAndTime.getTime());
    }

    private void settingTanggalFrom() {
        new DatePickerDialog(InputDataSkripsiActivity.this, dFrom, dateAndTime.get(Calendar.YEAR),dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void pilihDosUji1(){
        Intent i = new Intent(InputDataSkripsiActivity.this, ListDosenPembimbing.class);
        i.putExtra("userid", "1");
        i.putExtra("status", "UJI1");
        startActivityForResult(i,RESULT_DOSUJI1);
    }

    private void pilihDosUji2(){
        Intent i = new Intent(InputDataSkripsiActivity.this, ListDosenPembimbing.class);
        i.putExtra("userid", "1");
        i.putExtra("status", "UJI2");
        startActivityForResult(i,RESULT_DOSUJI2);
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

    private boolean validateUji1() {
        boolean value;
        if (eDosUji1.getText().toString().isEmpty()){
            value=false;
            inputLayoutDosUji1.setError(getString(R.string.err_msg_uji1));
        } else {
            value=true;
            inputLayoutDosUji1.setError(null);
        }
        return value;
    }

    private boolean validateUji2() {
        boolean value;
        if (eDosUji2.getText().toString().isEmpty()){
            value=false;
            inputLayoutDosUji2.setError(getString(R.string.err_msg_uji2));
        } else {
            value=true;
            inputLayoutDosUji2.setError(null);
        }
        return value;
    }

    private boolean validateTgl() {
        boolean value;
        if (eTglSidang.getText().toString().isEmpty()){
            value=false;
            inputLayoutTglSidang.setError(getString(R.string.err_msg_tglsidang));
        } else {
            value=true;
            inputLayoutTglSidang.setError(null);
        }
        return value;
    }

    private boolean validateNoUrut() {
        boolean value;
        if (eNoUrut.getText().toString().isEmpty()){
            value=false;
            requestFocus(eNoUrut);
            inputLayoutNoUrut.setError(getString(R.string.err_msg_nourut));
        } else {
            value=true;
            inputLayoutNoUrut.setError(null);
        }
        return value;
    }

    private boolean validatePenguji() {
        boolean value;
        if (idUji1.equals(idUji2)){
            value=false;
            inputLayoutDosUji1.setError(getString(R.string.err_msg_same_uji));
            inputLayoutDosUji2.setError(getString(R.string.err_msg_same_uji));
        } else {
            value=true;
            inputLayoutDosUji1.setError(null);
            inputLayoutDosUji1.setError(null);
        }
        return value;
    }

    private boolean validateBingUji1() {
        boolean value;
        if (idDosbing.equals(idUji1)){
            value=false;
            inputLayoutDosUji1.setError(getString(R.string.err_msg_same_bing_uji));
        } else {
            value=true;
            inputLayoutDosUji1.setError(null);
        }
        return value;
    }

    private boolean validateBingUji2() {
        boolean value;
        if (idDosbing.equals(idUji2)){
            value=false;
            inputLayoutDosUji2.setError(getString(R.string.err_msg_same_bing_uji));
        } else {
            value=true;
            inputLayoutDosUji2.setError(null);
        }
        return value;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_DOSUJI1) {
            if(resultCode == RESULT_OK) {
                idUji1=data.getStringExtra("kode");
                eDosUji1.setText(data.getStringExtra("nama"));
            }
        }
        if(requestCode == RESULT_DOSUJI2) {
            if(resultCode == RESULT_OK) {
                idUji2=data.getStringExtra("kode");
                eDosUji2.setText(data.getStringExtra("nama"));
            }
        }
        inputLayoutDosUji1.setError(null);
        inputLayoutDosUji2.setError(null);
        super.onActivityResult(requestCode, resultCode, data);
    }

}
