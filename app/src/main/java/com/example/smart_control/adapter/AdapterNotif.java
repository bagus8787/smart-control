package com.example.smart_control.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.model.Notification;
import com.example.smart_control.ui.user.activity.DetailDevicesActivity;
import com.example.smart_control.utils.SharedPrefManager;

import java.util.ArrayList;

public class AdapterNotif extends RecyclerView.Adapter<AdapterNotif.AlarmViewHolder> {
    private ArrayList<Notification> notifs;
    private Context context;
    private SharedPrefManager sharedPrefManager;

    public AdapterNotif(Context context) {
        this.context = context;
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
    }

    @NonNull
    @Override
    public AdapterNotif.AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list_notif, parent, false);

        return new AdapterNotif.AlarmViewHolder(view);
//        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotif.AlarmViewHolder holder, int position) {
        holder.setId(notifs.get(position).getId());
        holder.setTitle(notifs.get(position).getTitle());
        holder.setBody(notifs.get(position).getBody());
//        holder.setOldTime(notifs.get(position).getOld_time());

    }

    @Override
    public int getItemCount() {
        return (notifs != null) ? notifs.size() : 0;
    }

    public void setArtikels(ArrayList<Notification> notifs) {
        Log.d("alarams", notifs.toString());
        this.notifs = notifs;

//        setModel Artikel ke aritkel baru
//        artikelsArrayartikels = new ArrayList<Artikel>();
        notifyDataSetChanged();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        View mView;
        int id;
        String time, title, body;

        TextView rvTime, rvTitle, rvBody;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    context.startActivity(new Intent(context, DetailDevicesActivity.class)
//                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                    .putExtra("IT_ID", id)
//                                    .putExtra("IT_TIME", time)
//                                    .putExtra("IT_COUNT", count)
////                            .putExtra("IT_TGL_UPLOAD", tgl_upload)
////                            .putExtra("IT_URL_GAMBAR", url_gambar)
//                    );
                }
            });
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
            rvTime = mView.findViewById(R.id.txt_v_title);
            rvTime.setText(title.toUpperCase());
            Log.d("setTime", String.valueOf(rvTime));
        }

        public void setBody(String body) {
            this.body = body;
            rvBody = mView.findViewById(R.id.txt_v_body);
            rvBody.setText(body);
            Log.d("setCount", String.valueOf(rvBody));
        }

    }

}
