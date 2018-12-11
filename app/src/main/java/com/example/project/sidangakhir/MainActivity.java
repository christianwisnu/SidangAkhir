package com.example.project.sidangakhir;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import list.FrgPengumuman;
import list.FrgValidasiJudul;
import list.ListDosenPembimbing;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import service.BaseApiService;
import utilities.Link;
import utilities.PrefUtil;

import static android.view.Gravity.START;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private PrefUtil pref;
    private TextView txtId, txtNama, txtDosBing;
    private TextView txtInfoJudul, txtInfoStatus, txtInfoDosBing, txtInfoDosUji1, txtInfoDosUji2, txtInfoTglSidang, txtInfoNoUrut;
    private ImageView imgInfo;
    private SharedPreferences shared;
    private String userId, username, judul, telp, alamat, email, status;
    private TextInputLayout ilNamaJudul;
    private EditText edNamaJudul, edNamaDosen;
    private AlertDialog alert;
    private BaseApiService mApiService;
    private ProgressDialog pDialog;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int RESULT_DOSBING = 2;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = new PrefUtil(this);
        mApiService         = Link.getAPIService();
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        try{
            shared  = pref.getUserInfo();
            userId = shared.getString(PrefUtil.ID, null);
            username = shared.getString(PrefUtil.NAME, null);
            telp = shared.getString(PrefUtil.TELP, null);
            alamat = shared.getString(PrefUtil.ALAMAT, null);
            email = shared.getString(PrefUtil.EMAIL, null);
            status = shared.getString(PrefUtil.STATUS, null);
            judul= shared.getString(PrefUtil.JUDUL_SIDANG, null);

        }catch (Exception e){e.getMessage();}

        if(status.equals("M")){
            menuMhs();
        }else if(status.equals("D")){
            menuDosen();
        }else{
            menuAdmin();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        txtId = (TextView)header.findViewById(R.id.txtNavHeaderNIK);
        txtNama = (TextView)header.findViewById(R.id.txtNavHeaderNama);
        imgInfo = (ImageView) header.findViewById(R.id.imgNavHeaderInfo);
        String a = status.equals("M")?"NBI: ":"NIK: ";
        txtId.setText(a+userId);
        txtNama.setText(username);

        LayoutInflater liInfo = LayoutInflater.from(MainActivity.this);
        View promptsViewInfo = liInfo.inflate(R.layout.info_sidang_main, null);
        txtInfoJudul = (TextView) promptsViewInfo.findViewById(R.id.txtInfoSidangMainJudul);
        txtInfoStatus = (TextView) promptsViewInfo.findViewById(R.id.txtInfoSidangMainStatus);
        txtInfoDosBing = (TextView) promptsViewInfo.findViewById(R.id.txtInfoSidangMainDosBing);
        txtInfoDosUji1 = (TextView) promptsViewInfo.findViewById(R.id.txtInfoSidangMainDosUji1);
        txtInfoDosUji2 = (TextView) promptsViewInfo.findViewById(R.id.txtInfoSidangMainDosUji2);
        txtInfoTglSidang = (TextView) promptsViewInfo.findViewById(R.id.txtInfoSidangMainDosTglSidang);
        txtInfoNoUrut = (TextView) promptsViewInfo.findViewById(R.id.txtInfoSidangMainDosNoUrut);

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.inputjudul, null);
        edNamaJudul = (EditText) promptsView.findViewById(R.id.eInputJudul);
        ilNamaJudul = (TextInputLayout) promptsView.findViewById(R.id.input_layout_namajudul);
        edNamaDosen = (EditText) promptsView.findViewById(R.id.eInputDosBing);
        txtDosBing = (TextView) promptsView.findViewById(R.id.txtIdDosBing);

        edNamaDosen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edNamaDosen.setEnabled(false);
                hideKeyboard(v);
                pilihDosBing();
                edNamaDosen.setEnabled(true);
            }
        });

        edNamaDosen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edNamaDosen.setEnabled(false);
                    hideKeyboard(v);
                    pilihDosBing();
                    edNamaDosen.setEnabled(true);
                }
            }
        });

        if(status.equals("M")){
            imgInfo.setVisibility(View.VISIBLE);
            if(judul==null || judul.equals("")){
                AlertDialog.Builder dialogJudul = new AlertDialog.Builder(MainActivity.this);
                dialogJudul.setView(promptsView);
                dialogJudul.setCancelable(false);
                dialogJudul.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(validateJudul() && validateDosen()){
                            simpanJudul(userId, edNamaJudul.getText().toString(), txtDosBing.getText().toString());
                        }
                    }
                });
                alert = dialogJudul.create();
                alert.show();
            }
        }else{
            imgInfo.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void pilihDosBing(){
        Intent i = new Intent(MainActivity.this, ListDosenPembimbing.class);
        i.putExtra("userid", '1');
        startActivityForResult(i,RESULT_DOSBING);
    }

    private boolean validateJudul() {
        boolean value;
        if (edNamaJudul.getText().toString().isEmpty()){
            value=false;
            requestFocus(edNamaJudul);
            ilNamaJudul.setError(getString(R.string.err_msg_judul));
        } else {
            value=true;
            ilNamaJudul.setError(null);
        }
        return value;
    }

    private boolean validateDosen() {
        boolean value;
        if (edNamaDosen.getText().toString().isEmpty()){
            value=false;
            requestFocus(edNamaDosen);
            Toast.makeText(MainActivity.this, "Dosen pembimbing harus diisi!", Toast.LENGTH_LONG).show();
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

    private void menuMhs(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.jadwaldosen).setVisible(true);
        nav_Menu.findItem(R.id.jadwalsidang).setVisible(true);
        nav_Menu.findItem(R.id.pengumuman).setVisible(true);
        nav_Menu.findItem(R.id.uploadBukti).setVisible(true);
        nav_Menu.findItem(R.id.logout).setVisible(true);

        nav_Menu.findItem(R.id.validasijudulsidang).setVisible(false);
        nav_Menu.findItem(R.id.inputdosensidang).setVisible(false);
        nav_Menu.findItem(R.id.validasibimbingan).setVisible(false);
        nav_Menu.findItem(R.id.validasipembayaran).setVisible(false);
    }

    private void menuDosen(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.jadwaldosen).setVisible(false);
        nav_Menu.findItem(R.id.jadwalsidang).setVisible(true);
        nav_Menu.findItem(R.id.pengumuman).setVisible(true);
        nav_Menu.findItem(R.id.uploadBukti).setVisible(false);
        nav_Menu.findItem(R.id.logout).setVisible(true);

        nav_Menu.findItem(R.id.validasijudulsidang).setVisible(true);
        nav_Menu.findItem(R.id.inputdosensidang).setVisible(false);
        nav_Menu.findItem(R.id.validasibimbingan).setVisible(false);
        nav_Menu.findItem(R.id.validasipembayaran).setVisible(false);
    }

    private void menuAdmin(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.jadwaldosen).setVisible(false);
        nav_Menu.findItem(R.id.jadwalsidang).setVisible(false);
        nav_Menu.findItem(R.id.pengumuman).setVisible(true);
        nav_Menu.findItem(R.id.uploadBukti).setVisible(false);
        nav_Menu.findItem(R.id.logout).setVisible(true);

        nav_Menu.findItem(R.id.validasijudulsidang).setVisible(false);
        nav_Menu.findItem(R.id.inputdosensidang).setVisible(true);
        nav_Menu.findItem(R.id.validasibimbingan).setVisible(true);
        nav_Menu.findItem(R.id.validasipembayaran).setVisible(true);
    }

    private void simpanJudul(final String userId, final String namaJudul, final String nikDosen){
        pDialog.setMessage("Proses Simpan Judul ...\nHarap Tunggu");
        showDialog();
        Date today = Calendar.getInstance().getTime();
        String tanggalNow =df.format(today);
        mApiService.saveJudul(userId, namaJudul, tanggalNow, nikDosen)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("value").equals("false")){
                                    pref.saveUserInfo(userId, username, telp, alamat, email, status, namaJudul);
                                    hideDialog();
                                    Toast.makeText(MainActivity.this, "BERHASIL SIMPAN DATA", Toast.LENGTH_LONG).show();
                                    alert.dismiss();
                                } else {
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(MainActivity.this, error_message, Toast.LENGTH_LONG).show();
                                    hideDialog();
                                }
                            } catch (JSONException e) {
                                hideDialog();
                                Toast.makeText(MainActivity.this, "PROSES SIMPAN GAGAL", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            } catch (IOException e) {
                                hideDialog();
                                Toast.makeText(MainActivity.this, "PROSES SIMPAN GAGAL", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else {
                            hideDialog();
                            Toast.makeText(MainActivity.this, "PROSES SIMPAN GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        hideDialog();
                        Toast.makeText(MainActivity.this, "Koneksi Internet Bermasalah", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.jadwalsidang) {

        } else if (id == R.id.jadwaldosen) {

        } else if (id == R.id.pengumuman) {
            changeFragmentListUploadKriteria(new FrgPengumuman(), status, userId);
        } else if (id == R.id.uploadBukti) {

        } else if (id == R.id.logout) {
            pref.clear();
            startActivity(new Intent(MainActivity.this, Login.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else if (id == R.id.validasijudulsidang) {
            changeFragmentListUploadKriteria(new FrgValidasiJudul(), status, userId);
        } else if (id == R.id.inputdosensidang) {

        } else if (id == R.id.validasibimbingan) {

        } else if (id == R.id.validasipembayaran) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragmentListUploadKriteria(Fragment targetFragment, String status, String userId){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FrmMainMenu, targetFragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.blink, R.anim.fade_in)
                .commit();
        Bundle extras = new Bundle();
        extras.putString("status", status);
        extras.putString("userId", userId);
        targetFragment.setArguments(extras);
        drawer.closeDrawer(START);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_DOSBING) {
            if(resultCode == RESULT_OK) {
                txtDosBing.setText(data.getStringExtra("kode"));
                edNamaDosen.setText(data.getStringExtra("nama"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}