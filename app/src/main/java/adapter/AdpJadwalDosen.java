package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.project.sidangakhir.JadwalDosenActivity;
import com.example.project.sidangakhir.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.master.ColJadwalDosen;
import utilities.AppController;
import utilities.Link;

public class AdpJadwalDosen extends ArrayAdapter<ColJadwalDosen> {

    private List<ColJadwalDosen> columnslist;
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
    /*private DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    private DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat dfSave = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");*/
    private String statusku, userIdku;

    public AdpJadwalDosen(Context context, int resource, List<ColJadwalDosen> objects, String status, String userId) {
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
            holder.ImgDelete	=	 (ImageView)v.findViewById(R.id.imgColJadwalDosenDelete);
            holder.ImgEdit		=	 (ImageView)v.findViewById(R.id.imgColJadwalDosenEdit);
            holder.TvNama       =    (TextView)v.findViewById(R.id.txtColJadwalDosenNama);
            holder.TvHari	    =	 (TextView)v.findViewById(R.id.txtColJadwalDosenHari);
            holder.TvJamAwal	    =	 (TextView)v.findViewById(R.id.txtColJadwalDosenJamAwal);
            holder.TvJamAkhir	    =	 (TextView)v.findViewById(R.id.txtColJadwalDosenJamAkhir);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }

        if(statusku.equals("D")){
            holder.ImgDelete.setVisibility(View.VISIBLE);
            holder.ImgEdit.setVisibility(View.VISIBLE);
            holder.TvNama.setVisibility(View.GONE);
        }else{
            holder.ImgDelete.setVisibility(View.GONE);
            holder.ImgEdit.setVisibility(View.GONE);
            holder.TvNama.setVisibility(View.VISIBLE);
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
                        String nikDosen = columnslist.get(position).getNikDosen();
                        Integer line = columnslist.get(position).getLine();
                        deletedData(Link.BASE_URL_API + FileDeteleted, position, nikDosen, line);
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
                Intent i = new Intent(getContext(), JadwalDosenActivity.class);
                i.putExtra("Status", "EDIT");
                i.putExtra("userId", columnslist.get(position).getNikDosen());
                i.putExtra("line", columnslist.get(position).getLine());
                i.putExtra("hari", columnslist.get(position).getHari());
                i.putExtra("jamAwal", columnslist.get(position).getJamAwal().substring(0, columnslist.get(position).getJamAwal().length()-3));
                i.putExtra("jamAkhir", columnslist.get(position).getJamAkhir().substring(0, columnslist.get(position).getJamAwal().length()-3));
                getContext().startActivity(i);
            }
        });

        holder.TvNama.setText(columnslist.get(position).getNamaDosen());
        holder.TvHari.setText("Hari: "+columnslist.get(position).getHari());
        holder.TvJamAwal.setText("Mulai Jam: "+columnslist.get(position).getJamAwal().substring(0, columnslist.get(position).getJamAwal().length()-3));
        holder.TvJamAkhir.setText("Sampai Jam: "+columnslist.get(position).getJamAkhir().substring(0, columnslist.get(position).getJamAwal().length()-3));

        return v;
    }

    private void deletedData(String save, final int position, final String userId, final Integer line){
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
                params.put("line", String.valueOf(line));
                params.put("userId", userId);
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
        private TextView TvNama;
        private TextView TvHari;
        private TextView TvJamAwal;
        private TextView TvJamAkhir;
    }
}
