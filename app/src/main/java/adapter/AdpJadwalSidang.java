package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.project.sidangakhir.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.master.ColSidang;

public class AdpJadwalSidang extends ArrayAdapter<ColSidang> {

    private List<ColSidang> columnslist;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;
    private Context context;
    private String idMhs;
    private DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    public AdpJadwalSidang(Context context, int resource, List<ColSidang> objects, String userMhs) {
        super(context, resource,  objects);
        this.context = context;
        vi	=	(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource		= resource;
        columnslist		= objects;
        idMhs = userMhs;
    }

    @Override
    public View getView (final int position, View convertView, final ViewGroup parent){
        View v	=	convertView;
        if (v == null){
            holder	=	new ViewHolder();
            v= vi.inflate(Resource, null);
            holder.TvJudul	    =	 (TextView)v.findViewById(R.id.txtColJadwalSidangJudul);
            holder.TvNBIdanNama	    =	 (TextView)v.findViewById(R.id.txtColJadwalSidangNBIdanNama);
            holder.TvNIKdanNamaDosBing    =	 (TextView)v.findViewById(R.id.txtColJadwalSidangNamaDosBing);
            holder.TvNIKdanNamaDosUji1    =	 (TextView)v.findViewById(R.id.txtColJadwalSidangNamaDosUji1);
            holder.TvNIKdanNamaDosUji2    =	 (TextView)v.findViewById(R.id.txtColJadwalSidangNamaDosUji2);
            holder.TvTglSidang    =	 (TextView)v.findViewById(R.id.txtColJadwalSidangTglSidang);
            holder.TvNoUrut    =	 (TextView)v.findViewById(R.id.txtColJadwalSidangNoUrut);
            v.setTag(holder);
        }else{
            holder 	= (ViewHolder)v.getTag();
        }
        Date tgll=null;
        try{
            tgll = new SimpleDateFormat("yyyy-MM-dd").parse(columnslist.get(position).getTglSidang());
        }catch (Exception e){e.getMessage();}
        String tgl = df.format(tgll);
        holder.TvJudul.setText("Judul Sidang: "+columnslist.get(position).getJudulSidang());
        holder.TvNBIdanNama.setText("NBI/Nama Mhs: ("+columnslist.get(position).getNbiMhs()+") "+columnslist.get(position).getNamaMhs());
        holder.TvNIKdanNamaDosBing.setText("Dosen Pembimbing: "+columnslist.get(position).getNamaDosBing());
        holder.TvNIKdanNamaDosUji1.setText("Dosen Penguji 1: "+columnslist.get(position).getNmDosUji1());
        holder.TvNIKdanNamaDosUji2.setText("Dosen Penguji 2: "+columnslist.get(position).getNmDosUji2());
        holder.TvTglSidang.setText("Tanggal Sidang: "+tgl);
        holder.TvNoUrut.setText("No Urut: "+String.valueOf(columnslist.get(position).getNoUrut()));

        if(columnslist.get(position).getNbiMhs().equals(idMhs)){
            holder.TvJudul.setTextColor(Color.BLUE);
            holder.TvNBIdanNama.setTextColor(Color.BLUE);
            holder.TvNIKdanNamaDosBing.setTextColor(Color.BLUE);
            holder.TvNIKdanNamaDosUji1.setTextColor(Color.BLUE);
            holder.TvNIKdanNamaDosUji2.setTextColor(Color.BLUE);
            holder.TvTglSidang.setTextColor(Color.BLUE);
            holder.TvNoUrut.setTextColor(Color.BLUE);
        }else{
            holder.TvJudul.setTextColor(Color.BLACK);
            holder.TvNBIdanNama.setTextColor(Color.BLACK);
            holder.TvNIKdanNamaDosBing.setTextColor(Color.BLACK);
            holder.TvNIKdanNamaDosUji1.setTextColor(Color.BLACK);
            holder.TvNIKdanNamaDosUji2.setTextColor(Color.BLACK);
            holder.TvTglSidang.setTextColor(Color.BLACK);
            holder.TvNoUrut.setTextColor(Color.BLACK);
        }
        return v;
    }

    static class ViewHolder{ ;
        private TextView TvJudul;
        private TextView TvNBIdanNama;
        private TextView TvNIKdanNamaDosBing;
        private TextView TvNIKdanNamaDosUji1;
        private TextView TvNIKdanNamaDosUji2;
        private TextView TvTglSidang;
        private TextView TvNoUrut;
    }
}
