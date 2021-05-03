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
import com.example.smart_control.ui.user.activity.DetailDevicesActivity;
import com.example.smart_control.utils.SharedPrefManager;

import java.util.ArrayList;

public class AdapterListAlarm extends RecyclerView.Adapter<AdapterListAlarm.AlarmViewHolder> {
    private ArrayList<AlarmModel> alarms;
    private Context context;
    private SharedPrefManager sharedPrefManager;

    public AdapterListAlarm(Context context) {
        this.context = context;
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_list_time_timer, parent, false);

        return new AlarmViewHolder(view);
//        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListAlarm.AlarmViewHolder holder, int position) {
        holder.setId(alarms.get(position).getId());
        holder.setTime(alarms.get(position).getTime());
        holder.setCount(alarms.get(position).getCount());
        holder.setOldTime(alarms.get(position).getOld_time());

    }

    @Override
    public int getItemCount() {
        return (alarms != null) ? alarms.size() : 0;
    }

    public void setArtikels(ArrayList<AlarmModel> alarms) {
        Log.d("alarams", alarms.toString());
        this.alarms = alarms;

//        setModel Artikel ke aritkel baru
//        artikelsArrayartikels = new ArrayList<Artikel>();
        notifyDataSetChanged();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        View mView;
        int id;
        String time, count, old_time;

        TextView rvTime, rvCount, rvOldTime;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, DetailDevicesActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("IT_ID", id)
                            .putExtra("IT_TIME", time)
                            .putExtra("IT_COUNT", count)
//                            .putExtra("IT_TGL_UPLOAD", tgl_upload)
//                            .putExtra("IT_URL_GAMBAR", url_gambar)
                    );

                }
            });
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setTime(String time) {
            this.time = time;
            rvTime = mView.findViewById(R.id.txt_time);
            rvTime.setText(time.toUpperCase());
            Log.d("setTime", String.valueOf(rvTime));
        }

        public void setCount(String count) {
            this.count = count;
//            rvCount = mView.findViewById(R.id.txt_count);
//            rvCount.setText(count.toUpperCase());
//            Log.d("setCount", String.valueOf(rvCount));
        }

        public void setOldTime(String old_time) {
            this.old_time = old_time;

//            rvOldTime = mView.findViewById(R.id.txt_count);
//            rvOldTime.setText(count.toUpperCase());
//            Log.d("setCount", String.valueOf(rvOldTime));
        }

    }

}