package list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.example.project.sidangakhir.MainActivity;
import com.example.project.sidangakhir.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.AdpValidasiPembayaran;
import model.master.ColSidang;
import utilities.AppController;
import utilities.Link;
import utilities.Utils;

public class FrgValidasiPembayaran extends android.support.v4.app.Fragment  {

    private View vupload;
    private AdpValidasiPembayaran adapter;
    private ListView lsvupload;
    private ArrayList<ColSidang> columnlist= new ArrayList<ColSidang>();
    private TextView tvstatus;
    private ProgressBar prbstatus;
    private String getUpload	="getListBlmValidasiBayar.php";
    private String idAdmin, status;
    private AlertDialog alert;
    private View promptsViewInfo;
    private LayoutInflater liInfo;
    private ImageView imgGbr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle	 = this.getArguments();
        if (bundle!=null){
            status	= bundle.getString("status");
            idAdmin	= bundle.getString("userId");
        }
        vupload     = inflater.inflate(R.layout.list_validasi_pembayaran,container,false);
        lsvupload	= (ListView)vupload.findViewById(R.id.listValidasiPembayaran);
        tvstatus	= (TextView)vupload.findViewById(R.id.txtListValidasiBayarStatus);
        prbstatus	= (ProgressBar)vupload.findViewById(R.id.prbListValidasiBayarStatus);

        adapter		= new AdpValidasiPembayaran(getActivity(), R.layout.col_validasi_pembayaran, columnlist, idAdmin);
        lsvupload.setAdapter(adapter);
        getDataUpload(Link.BASE_URL_API+getUpload);

        lsvupload.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                liInfo = LayoutInflater.from(getContext());
                promptsViewInfo = liInfo.inflate(R.layout.view_gbr_pembayaran, null);
                imgGbr = (ImageView) promptsViewInfo.findViewById(R.id.imgViewPembayaran);
                setDataInfo(columnlist.get(position).getFileBayar());

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Gambar Pembayaran");
                dialog.setView(promptsViewInfo);
                dialog.setCancelable(false);
                dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog ad = dialog.create();
                ad.show();
            }
        });

        return vupload;
    }

    private void setDataInfo(String fileGbr){
        imgGbr.setBackgroundResource(0);
        Utils.getCycleImage(Link.BASE_URL_IMAGE_BAYAR+fileGbr, imgGbr, getContext());
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
                            if (sucses==1){
                                tvstatus.setVisibility(View.GONE);
                                prbstatus.setVisibility(View.GONE);
                                adapter.clear();
                                JSONArray JsonArray = response.getJSONArray("uploade");
                                for (int i = 0; i < JsonArray.length(); i++) {
                                    JSONObject object = JsonArray.getJSONObject(i);
                                    ColSidang colums 	= new ColSidang();
                                    colums.setId(object.getInt("i_id"));
                                    colums.setJudulSidang(object.getString("t_judulsidang"));
                                    colums.setNbiMhs(object.getString("c_nbi_mhs"));
                                    colums.setNamaMhs(object.getString("nm_mhs"));
                                    colums.setNikDosBing(object.getString("c_dos_bing"));
                                    colums.setNamaDosBing(object.getString("nm_dosbing"));
                                    colums.setFileBayar(object.getString("vc_file_bayar"));
                                    colums.setFileBimbingan(object.getString("vc_file_bimbingan"));
                                    colums.setCdstatus(object.getString("c_dstatus"));
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
        columnlist= new ArrayList<ColSidang>();
        adapter		= new AdpValidasiPembayaran(getActivity(), R.layout.col_validasi_pembayaran, columnlist, idAdmin);
        lsvupload.setAdapter(adapter);
        getDataUpload(Link.BASE_URL_API+getUpload);
    }
}
