package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NetworkError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.ParseError;
import com.android.volley.error.ServerError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.example.project.sidangakhir.PengumumanActivity;
import com.example.project.sidangakhir.R;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.master.ColPengumuman;
import utilities.AppController;
import utilities.Link;

public class AdpPengumuman extends ArrayAdapter<ColPengumuman> {

    private List<ColPengumuman> columnslist;
    private LayoutInflater vi;
    private int Resource;
    private int lastPosition = -1;
    private ViewHolder holder;
    private Activity parent;
    private Context context;
    private ListView lsvchoose;
    private static final int SEND_UPLOAD = 201;
    private AlertDialog alert;
    private String FileDeteleted = "deletePengumuman.php";
    private DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat dfSave = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String statusku, userIdku;

    public AdpPengumuman(Context context, int resource, List<ColPengumuman> objects, String status,
                         String userId) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
        statusku = status;
        userIdku = userId;
    }

    @Override
    public View getView (final int position, View convertView, final ViewGroup parent){
        View v	=	convertView;
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.ImgDelete	=	 (ImageView)v.findViewById(R.id.imgColPengumumanDelete);
            holder.ImgEdit		=	 (ImageView)v.findViewById(R.id.imgColPengumumanEdit);
            holder.TvJudul	    =	 (TextView)v.findViewById(R.id.txtColPengumumanJudul);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }

        if(statusku.equals("A")){
            holder.ImgDelete.setVisibility(View.VISIBLE);
            holder.ImgEdit.setVisibility(View.VISIBLE);
        }else{
            holder.ImgDelete.setVisibility(View.GONE);
            holder.ImgEdit.setVisibility(View.GONE);
        }
        holder.ImgDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder msMaintance = new AlertDialog.Builder(getContext());
                msMaintance.setCancelable(false);
                msMaintance.setMessage("Yakin akan dihapus? ");
                msMaintance.setNegativeButton("Ya", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String sidupload = String.valueOf(columnslist.get(position).getId());
                        deletedData(Link.BASE_URL_API + FileDeteleted, position, sidupload, userIdku);
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

        holder.ImgEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PengumumanActivity.class);
                Date tglAwal=null, tglAkhir=null;
                try{
                    tglAwal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(columnslist.get(position).getTglAwal());
                    tglAkhir = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(columnslist.get(position).getTglFinish());
                }catch (Exception e){e.getMessage();}

                i.putExtra("Status", "EDIT");
                i.putExtra("idPengumuman", String.valueOf(columnslist.get(position).getId()));
                i.putExtra("judul", columnslist.get(position).getJudul());
                i.putExtra("isi", columnslist.get(position).getIsi());
                i.putExtra("tglAwal", df.format(tglAwal));
                i.putExtra("tglAkhir", df.format(tglAkhir));
                i.putExtra("hasilTglAwal", df2.format(tglAwal));
                i.putExtra("hsilTglAkhir", df2.format(tglAkhir));
                getContext().startActivity(i);
            }
        });

        holder.TvJudul.setText(columnslist.get(position).getJudul());
        return v;
    }

    private void deletedData(String save, final int position, final String idPengumuman, final String userId){
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
                                Toast.makeText(getContext(), "DATA BERHASIL DI HAPUS", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getContext(), "HAPUS DATA GAGAL", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "HAPUS DATA GAGAL", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "HAPUS DATA GAGAL.\nCheck Koneksi Internet Anda", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getContext(), "HAPUS DATA GAGAL.\nAuthFailureError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(getContext(), "HAPUS DATA GAGAL.\nCheck ServerError", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getContext(), "HAPUS DATA GAGAL.\nCheck NetworkError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(getContext(), "HAPUS DATA GAGAL.\nCheck ParseError", Toast.LENGTH_LONG).show();
                }
            }
        }){
            @Override
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("id", idPengumuman);
                params.put("userId", userId);
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

    static class ViewHolder{
        private ImageView ImgDelete;
        private ImageView ImgEdit;
        private TextView TvJudul;
    }
}
