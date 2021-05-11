package com.example.smart_control.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_control.Myapp;
import com.example.smart_control.R;
import com.example.smart_control.handler.DatabaseHandler;
import com.example.smart_control.model.AlarmModel;
import com.example.smart_control.network.ApiInterface;
import com.example.smart_control.network.ApiLocalClient;
import com.example.smart_control.repository.AlarmRepository;
import com.example.smart_control.ui.user.activity.DetailDevicesActivity;
import com.example.smart_control.ui.user.feeder.HomeFeederActivity;
import com.example.smart_control.utils.SharedPrefManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterListAlarm extends RecyclerView.Adapter<AdapterListAlarm.AlarmViewHolder> {
    private ArrayList<AlarmModel> alarms;
    private Context context;
    private SharedPrefManager sharedPrefManager;
    private ApiInterface apiInterface;
    private DatabaseHandler db;
    private AlarmRepository alarmRepository;

    private AlertDialog.Builder b;

    public AdapterListAlarm(Context context) {
        this.context = context;
        sharedPrefManager = new SharedPrefManager(Myapp.getContext());
        apiInterface = ApiLocalClient.getClient().create(ApiInterface.class);
        db = new DatabaseHandler(context);
        b =  new AlertDialog.Builder(context);
        alarmRepository = new AlarmRepository();
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

        notifyDataSetChanged();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Integer id;
        String time, count, old_time;
        ImageView img_edit_alarm, img_delete_alarm;
        TextView rvTime, rvCount, rvOldTime;

        public AlarmViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            rvTime = itemView.findViewById(R.id.txt_time);

            img_edit_alarm = itemView.findViewById(R.id.img_edit_alarm);
            img_delete_alarm = itemView.findViewById(R.id.delete_edit_alarm);

            img_edit_alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, DetailDevicesActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("IT_ID", String.valueOf(id))
                            .putExtra("IT_TIME", time)
                            .putExtra("IT_COUNT", count)
                    );
                }
            });

            img_delete_alarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mView.getRootView().getContext())
                            .setMessage("Anda yakin mau menghapus Timer pakan ?")
                            .setCancelable(false)
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // do something...
                                    ProgressDialog progressDialog = new ProgressDialog(mView.getRootView().getContext());
                                    progressDialog.show();
                                    progressDialog.setMessage("Sedang menghapus timer");

                                    Call<String> deleteAlarm = apiInterface.deleteAlarm(
                                            time,
                                            sharedPrefManager.getSpSecretKey()
                                    );

                                    deleteAlarm.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            Log.d("deleteeee", response.body().toString());
                                            progressDialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                                    db.deleteById(id);
                                    alarmRepository.getAlarmLocal(mView.getRootView().getContext());

                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Timer berhasil di hapus", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.show();

//                    b.setTitle("Apakah anda yakin menghapus timer pakan ?");
//                    b.setPositiveButton("OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    // do something...
//                                    progressDialog.show();
//                                    progressDialog.setMessage("Sedang menghapus timer");
//
//                                    Call<String> deleteAlarm = apiInterface.deleteAlarm(
//                                            time,
//                                            sharedPrefManager.getSpSecretKey()
//                                    );
//
//                                    deleteAlarm.enqueue(new Callback<String>() {
//                                        @Override
//                                        public void onResponse(Call<String> call, Response<String> response) {
//                                            progressDialog.dismiss();
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<String> call, Throwable t) {
//
//                                        }
//                                    });
//                                }
//                            }
//                    );
//                    b.setNegativeButton("Cancel",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    dialog.dismiss();
//                                }
//                            }
//                    );
//                    b.show();
                }
            });

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    context.startActivity(new Intent(context, DetailDevicesActivity.class)
//                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            .putExtra("IT_ID", String.valueOf(id))
//                            .putExtra("IT_TIME", time)
//                            .putExtra("IT_COUNT", count)
//                    );
//
//                }
//            });
        }

        public void setId(Integer ids) {
            this.id = ids;
            Log.d("idneeee", String.valueOf(id));
        }

        public void setTime(String time) {
            this.time = time;
            rvTime = mView.findViewById(R.id.txt_time);
            rvTime.setText(time.toUpperCase());
            Log.d("setTime", String.valueOf(rvTime));
        }

        public void setCount(String count) {
            this.count = count;
            rvCount = mView.findViewById(R.id.txt_count);
            rvCount.setText(count + " Kali");
//            Log.d("setCount", String.valueOf(rvCount));
        }

        public void setOldTime(String old_time) {
            this.old_time = old_time;
        }

    }

}