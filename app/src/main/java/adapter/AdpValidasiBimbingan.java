package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.example.project.sidangakhir.R;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.master.ColSidang;
import utilities.AppController;
import utilities.Link;

public class AdpValidasiBimbingan extends ArrayAdapter<ColSidang> {

    private List<ColSidang> columnslist;
    private ProgressDialog pDialog;
    private LayoutInflater vi;
    private int Resource;
    private int lastPosition = -1;
    private ViewHolder holder;
    private Activity parent;
    private Context context;
    private ListView lsvchoose;
    private static final int SEND_UPLOAD = 201;
    private AlertDialog alert;
    private String validasiPhp = "validasiBimbingan.php";
    private String admin;
    private DateFormat dfSave = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AdpValidasiBimbingan(Context context, int resource, List<ColSidang> objects, String userAdmin) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
        admin = userAdmin;
    }

    @Override
    public View getView (final int position, View convertView, final ViewGroup parent){
        View v	=	convertView;
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.ImgValidasi	=	 (ImageView)v.findViewById(R.id.imgColValidasiBimbingan);
            holder.TvJudul	    =	 (TextView)v.findViewById(R.id.txtColValidasiBimbinganJudul);
            holder.TvNBIdanNama	    =	 (TextView)v.findViewById(R.id.txtColValidasiBimbinganNBIdanNama);
            holder.TvNIKdanNama    =	 (TextView)v.findViewById(R.id.txtColValidasiBimbinganNikdanNama);
            holder.TvID	        =	 (TextView)v.findViewById(R.id.txtColValidasiBimbinganId);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }

        holder.ImgValidasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder msMaintance = new AlertDialog.Builder(getContext());
                msMaintance.setCancelable(false);
                msMaintance.setMessage("Validasi lembar bimbingan ini? ");
                msMaintance.setNegativeButton("Ya", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sidupload = String.valueOf(columnslist.get(position).getId());
                        validasiData(Link.BASE_URL_API + validasiPhp, position, sidupload, admin);
                    }
                });

                msMaintance.setPositiveButton("Tidak", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                    }
                });
                alert	=msMaintance.create();
                alert.show();
            }
        });
        holder.TvJudul.setText("Judul Sidang: "+columnslist.get(position).getJudulSidang());
        holder.TvNBIdanNama.setText("NBI/Nama Mhs: ("+columnslist.get(position).getNbiMhs()+") "+columnslist.get(position).getNamaMhs());
        holder.TvNIKdanNama.setText("NIK/Nama Dosen Pembimbing: ("+columnslist.get(position).getNikDosBing()+") "+columnslist.get(position).getNamaDosBing());
        return v;
    }

    private void validasiData(String save, final int position, final String idSidang, final String userAdmin){
        pDialog.setMessage("Proses Validasi ...\nHarap Tunggu");
        showDialog();
        Date today = Calendar.getInstance().getTime();
        final String tanggalNow =dfSave.format(today);
        StringRequest register = new StringRequest(Request.Method.POST, save,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonrespon = new JSONObject(response);
                            int Sucsess = jsonrespon.getInt("success");
                            if (Sucsess ==1 ){
                                columnslist.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(getContext(), "DATA BERHASIL DI VALIDASI", Toast.LENGTH_LONG).show();
                                hideDialog();
                            }else{
                                Toast.makeText(getContext(), "VALIDASI GAGAL", Toast.LENGTH_LONG).show();
                                hideDialog();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "VALIDASI GAGAL.", Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "VALIDASI GAGAL.\nCheck Koneksi Internet Anda", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getContext(), "VALIDASI GAGAL.\nAuthFailureError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getContext(), "VALIDASI GAGAL.\nCheck ServerError", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getContext(), "VALIDASI GAGAL.\nCheck NetworkError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getContext(), "VALIDASI GAGAL.\nCheck ParseError", Toast.LENGTH_LONG).show();
                }
                hideDialog();
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("id", idSidang);
                params.put("userId", userAdmin);//NIK ADMIN
                params.put("tglNow", tanggalNow);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(register);
    }

    static class ViewHolder{ ;
        private ImageView ImgValidasi;
        private TextView TvJudul;
        private TextView TvNBIdanNama;
        private TextView TvNIKdanNama;
        private TextView TvID;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
