package com.seladanghijau.uitme_reportstaf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Zaim Fared on 12/1/2018.
 */

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.MyViewHolder>{

    private List<Laporan> laporanList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        ImageView imgLaporan;
        TextView txtTempat, txtTarikh, txtMasa, txtStatus;

        ItemClickListener itemClickListener;

        public MyViewHolder(View view){
            super(view);
            imgLaporan = view.findViewById(R.id.imgLaporan);
            txtTempat = view.findViewById(R.id.laporan_tempat);
            txtTarikh = view.findViewById(R.id.laporan_tarikh);
            txtMasa = view.findViewById(R.id.laporan_masa);
            txtStatus = view.findViewById(R.id.laporan_status);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return true;
        }
    }

    public LaporanAdapter(List<Laporan> laporanList, Context context){
        this.laporanList = laporanList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.laporan_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Laporan laporan = laporanList.get(position);
        holder.imgLaporan.setImageBitmap(decodeBase64(laporan.getLaporan_imej()));
        holder.txtTempat.setText(laporan.getLaporan_tempat());
        holder.txtTarikh.setText(laporan.getLaporan_tarikh());
        holder.txtMasa.setText(laporan.getLaporan_masa());
        holder.txtStatus.setText(laporan.getLaporan_status());

        switch(laporan.getLaporan_status().toUpperCase()) {
            case "DILAPORKAN":
                holder.txtStatus.setBackground(context.getDrawable(R.drawable.status_laporan_dilaporkan));
                break;
            case "DIJADUALKAN":
                holder.txtStatus.setBackground(context.getDrawable(R.drawable.status_laporan_dijadualkan));
                break;
            case "DIKUATKUASAKAN":
                holder.txtStatus.setBackground(context.getDrawable(R.drawable.status_laporan_dikuatkuasakan));
                break;
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //Pass id kat sini
                Intent i = new Intent(context, Info_Laporan.class);
                i.putExtra("id", laporan.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    private static Bitmap decodeBase64(String input){
        byte[] decodeBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodeBytes, 0, decodeBytes.length);
    }
}
