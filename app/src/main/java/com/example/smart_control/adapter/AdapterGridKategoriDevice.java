package com.example.smart_control.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.smart_control.R;
import com.example.smart_control.model.Artikel;
import com.example.smart_control.ui.user.activity.DetailDevicesActivity;
import com.example.smart_control.ui.user.activity.HomeActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterGridKategoriDevice extends ArrayAdapter<Artikel> {
    List<Artikel> artikels;
    Context context;
    int resource;

    public AdapterGridKategoriDevice(@NonNull Context context, int resource, @NonNull List<Artikel> artikels){
        super(context, resource, artikels);

        this.context = context;
        this.resource = resource;
        this.artikels = artikels;
    }

    @NonNull
    @Override
    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if (convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView=layoutInflater.inflate(R.layout.grid_list_device,null,true);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(getContext(), DetailDevicesActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        Artikel artikel = getItem(position);
        ImageView img_art = convertView.findViewById(R.id.img_devices);
        TextView txt_nama_kategori = convertView.findViewById(R.id.txt_nama_kategori);

        txt_nama_kategori.setText(artikel.getJudul());

        Picasso.with(context)
                .load(artikel.getGambar())
                .error(R.drawable.lampu_on)
                .into(img_art);

        return  convertView;
    }
}
