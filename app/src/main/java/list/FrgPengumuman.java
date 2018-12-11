package list;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.example.project.sidangakhir.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import adapter.AdpPengumuman;
import model.master.ColPengumuman;
import utilities.AppController;
import utilities.Link;
import utilities.Utils;

public class FrgPengumuman extends Fragment {

    private View vupload;
    private ImageView ImgAdd;
    private AdpPengumuman adapter;
    private ListView lsvupload;
    private ArrayList<ColPengumuman> columnlist= new ArrayList<ColPengumuman>();
    private TextView tvstatus;
    private ProgressBar prbstatus;
    private String getUpload	="getListPengumuman.php?tglNow='";
    private String status;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date tglNowLast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle	 = this.getArguments();
        if (bundle!=null){
            status	= bundle.getString("status");
        }
        vupload     = inflater.inflate(R.layout.list_pengumuman,container,false);
        ImgAdd		= (ImageView)vupload.findViewById(R.id.imgListPengumumanAdd);
        lsvupload	= (ListView)vupload.findViewById(R.id.listPengumuman);
        tvstatus	= (TextView)vupload.findViewById(R.id.txtListPengumumanStatus);
        prbstatus	= (ProgressBar)vupload.findViewById(R.id.prbListPengumumanStatus);

        Calendar cal = Calendar.getInstance();
        tglNowLast = Utils.getLastTimeOfDay(cal.getTime());
        adapter		= new AdpPengumuman(getActivity(), R.layout.col_pengumuman, columnlist, status);
        lsvupload.setAdapter(adapter);
        getDataUpload(Link.BASE_URL_API+getUpload+df.format(tglNowLast)+"'");

        if(status.equals("A")){
            ImgAdd.setVisibility(View.VISIBLE);
        }else{
            ImgAdd.setVisibility(View.INVISIBLE);
        }

        ImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("A")){
                    /*Intent i  = new Intent(getActivity(), AddHomeKos.class);
                    i.putExtra("Status", 1);
                    i.putExtra("idCust", idCust);
                    startActivity(i);*/
                }
            }
        });

        lsvupload.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> Parent, View view, int position,
                                    long id) {

            }
        });
        return vupload;
    }

    private void getDataUpload(String Url){
        tvstatus.setVisibility(View.GONE);
        prbstatus.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonget = new JsonObjectRequest(Request.Method.GET, Url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int sucses= response.getInt("success");
                            Log.i("Status", String.valueOf(sucses));
                            if (sucses==1){
                                tvstatus.setVisibility(View.GONE);
                                prbstatus.setVisibility(View.GONE);
                                adapter.clear();
                                JSONArray JsonArray = response.getJSONArray("uploade");
                                for (int i = 0; i < JsonArray.length(); i++) {
                                    JSONObject object = JsonArray.getJSONObject(i);
                                    ColPengumuman colums 	= new ColPengumuman();
                                    colums.setId(object.getInt("i_idpengumuman"));
                                    colums.setJudul(object.getString("t_judul"));
                                    colums.setIsi(object.getString("t_isi"));
                                    colums.setTglAwal(object.getString("dt_start"));
                                    colums.setTglFinish(object.getString("dt_finish"));
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
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
        adapter		= new AdpPengumuman(getActivity(), R.layout.col_pengumuman, columnlist, status);
        lsvupload.setAdapter(adapter);
        getDataUpload(Link.BASE_URL_API+getUpload+df.format(tglNowLast)+"'");
    }
}