package com.example.project.sidangakhir;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import utilities.Utils;

public class ViewPengumumanActivity extends AppCompatActivity {

    private TextView txtJudul, txtIsi;
    private ImageView imgBack;
    private String judul, isi, id;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_pengumuman);
        Bundle i = getIntent().getExtras();
        if (i != null){
            try {
                id = i.getString("idPengumuman");
                judul = i.getString("judul");
                isi = i.getString("isi");
            } catch (Exception e) {}
        }
        imgBack = (ImageView)findViewById(R.id.imgInfoPengumumanBack);
        txtJudul = (TextView)findViewById(R.id.txtInfoPengumumanJudul);
        txtIsi = (TextView)findViewById(R.id.txtInfoPengumumanIsi);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtJudul.setText(judul);
        txtIsi.setText(isi);
    }

    @Override
    protected void onDestroy() {
        Utils.freeMemory();
        super.onDestroy();
        Utils.trimCache(this);
    }
}
