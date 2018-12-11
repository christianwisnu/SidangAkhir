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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.example.project.sidangakhir.R;

import org.json.JSONObject;

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
    private String FileDeteleted 		= "delete_kos.php?id=";
    private String statusku;

    public AdpPengumuman(Context context, int resource, List<ColPengumuman> objects, String status) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
        statusku = status;
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

        if(!statusku.equals("1")){
            holder.ImgDelete.setVisibility(View.INVISIBLE);
            holder.ImgEdit.setVisibility(View.INVISIBLE);
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
                        deletedData(Link.BASE_URL_API + FileDeteleted + sidupload, position);
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
                /*Intent i = new Intent(getContext(), AddHomeKos.class);
                i.putExtra("Status", 2);
                i.putExtra("id_kos", columnslist.get(position).getId_kos());
                i.putExtra("i_idcust", columnslist.get(position).getId_cust());
                i.putExtra("i_idsewastatus", String.valueOf(columnslist.get(position).getId_sewaStatus()));
                i.putExtra("vc_namakost", columnslist.get(position).getNamaKos());
                i.putExtra("vc_alamat", columnslist.get(position).getAlamat());
                i.putExtra("vc_gambar", columnslist.get(position).getGambar());
                i.putExtra("vc_gambar2", columnslist.get(position).getGambar2());
                i.putExtra("vc_gambar3", columnslist.get(position).getGambar3());
                i.putExtra("vc_gambar4", columnslist.get(position).getGambar4());
                i.putExtra("vc_gambar5", columnslist.get(position).getGambar5());
                i.putExtra("i_lebar", columnslist.get(position).getLebar());
                i.putExtra("i_panjang", columnslist.get(position).getPanjang());
                i.putExtra("c_statuslistrik", columnslist.get(position).getStatusListrik());
                i.putExtra("i_jmlkamar", columnslist.get(position).getJmlhKamar());
                i.putExtra("t_fasilitas", columnslist.get(position).getFasilitas());
                i.putExtra("d_latitude", columnslist.get(position).getLatitude());
                i.putExtra("d_longtitude", columnslist.get(position).getLongtitude());
                i.putExtra("n_harga", columnslist.get(position).getHarga());
                i.putExtra("c_kodekota", columnslist.get(position).getKodeKota());
                i.putExtra("vc_namakota", columnslist.get(position).getNamaKota());
                getContext().startActivity(i);*/
            }
        });

        holder.TvJudul.setText(columnslist.get(position).getJudul());
        return v;
    }

    private void deletedData(String save, final int position){
        StringRequest register = new StringRequest(Request.Method.POST, save,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        VolleyLog.d("Respone", response.toString());
                        try {
                            JSONObject jsonrespon = new JSONObject(response);
                            int Sucsess = jsonrespon.getInt("success");
                            Log.i("success", String.valueOf(Sucsess));
                            if (Sucsess ==1 ){
                                columnslist.remove(position);
                                notifyDataSetChanged();
                            }else{
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
            }
        }){
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
