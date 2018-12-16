package list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.example.project.sidangakhir.JadwalDosenActivity;
import com.example.project.sidangakhir.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.AdpJadwalDosen;
import model.master.ColJadwalDosen;
import utilities.AppController;
import utilities.Link;

public class FrgJadwalDosen extends Fragment {

    private View vupload;
    private ImageView ImgAdd;
    private AdpJadwalDosen adapter;
    private ListView lsvupload;
    private ArrayList<ColJadwalDosen> columnlist= new ArrayList<ColJadwalDosen>();
    private TextView tvstatus;
    private ProgressBar prbstatus;
    private String getUpload	="getJadwalDosen.php";
    private String getUploadAll	="getJadwalDosenAll.php";
    private String status, userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle	 = this.getArguments();
        if (bundle!=null){
            status	= bundle.getString("status");
            userId = bundle.getString("userId");
        }
        vupload     = inflater.inflate(R.layout.list_jadwal_dosen,container,false);
        ImgAdd		= (ImageView)vupload.findViewById(R.id.imgListJadwalDosennAdd);
        lsvupload	= (ListView)vupload.findViewById(R.id.listJadwalDosen);
        tvstatus	= (TextView)vupload.findViewById(R.id.txtListJadwalDosenStatus);
        prbstatus	= (ProgressBar)vupload.findViewById(R.id.prbListJadwalDosenStatus);

        adapter		= new AdpJadwalDosen(getActivity(), R.layout.col_jadwal_dosen, columnlist, status, userId);
        lsvupload.setAdapter(adapter);

        if(status.equals("D")){
            getDataUpload(Link.BASE_URL_API+getUpload, userId);
            ImgAdd.setVisibility(View.VISIBLE);
        }else{
            getDataUpload(Link.BASE_URL_API+getUploadAll, userId);
            ImgAdd.setVisibility(View.INVISIBLE);
        }

        ImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("D")){
                    Intent i  = new Intent(getActivity(), JadwalDosenActivity.class);
                    i.putExtra("Status", "ADD");
                    i.putExtra("userId", userId);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        return vupload;
    }

    private void getDataUpload(String Url, final String userId){
        tvstatus.setVisibility(View.GONE);
        prbstatus.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            if (sucses==1){
                                tvstatus.setVisibility(View.GONE);
                                prbstatus.setVisibility(View.GONE);
                                adapter.clear();
                                JSONArray JsonArray = response.getJSONArray("uploade");
                                for (int i = 0; i < JsonArray.length(); i++) {
                                    JSONObject object = JsonArray.getJSONObject(i);
                                    ColJadwalDosen colums 	= new ColJadwalDosen();
                                    colums.setNikDosen(object.getString("c_nik_dosen"));
                                    colums.setNamaDosen(object.getString("vc_username"));
                                    colums.setLine(object.getInt("i_line"));
                                    colums.setHari(object.getString("vc_hari"));
                                    colums.setJamAwal(object.getString("dt_mulai"));
                                    colums.setJamAkhir(object.getString("dt_akhir"));
                                    columnlist.add(colums);
                                }
                            }else{
                                tvstatus.setVisibility(View.VISIBLE);
                                tvstatus.setText("Tidak Ada Data");
                                prbstatus.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                        //lsvupload.invalidate();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check Koneksi Internet Anda");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof AuthFailureError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("AuthFailureError");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof ServerError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check ServerError");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof NetworkError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check NetworkError");
                    prbstatus.setVisibility(View.GONE);
                } else if (error instanceof ParseError) {
                    tvstatus.setVisibility(View.VISIBLE);
                    tvstatus.setText("Check ParseError");
                    prbstatus.setVisibility(View.GONE);
                }
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("userId", userId);
                return params;
            }
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                java.util.Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().invalidate(Url, true);
        AppController.getInstance().addToRequestQueue(jsonget);
    }

    @Override
    public void onResume() {
        super.onResume();
        columnlist= new ArrayList<ColJadwalDosen>();
        adapter		= new AdpJadwalDosen(getActivity(), R.layout.col_jadwal_dosen, columnlist, status, userId);
        lsvupload.setAdapter(adapter);
        if(status.equals("D")){
            getDataUpload(Link.BASE_URL_API+getUpload, userId);
        }else{
            getDataUpload(Link.BASE_URL_API+getUploadAll, userId);
        }
    }
}