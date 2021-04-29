package com.example.smart_control.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smart_control.R;
import com.github.druk.rx2dnssd.BonjourService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {

        private final int mSelectedBackground;
        private final int mBackground;
        private final ArrayList<BonjourService> services = new ArrayList<>();

        private long mSelectedItemId = -1;

        protected ServicesAdapter(Context context) {
                TypedValue mTypedValue = new TypedValue();
                context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
                mBackground = mTypedValue.resourceId;

                context.getTheme().resolveAttribute(R.attr.selectableItemBackgroundBorderless, mTypedValue, true);
                mSelectedBackground = mTypedValue.resourceId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.two_text_item, viewGroup, false));
        }

        @Override
        public int getItemCount() {
                return services.size();
        }

        @Override
        public long getItemId(int position) {
                return services.get(position).hashCode();
        }

        public BonjourService getItem(int position) {
                return services.get(position);
        }

        public void clear() {
                this.services.clear();
        }

        public long getSelectedItemId() {
                return mSelectedItemId;
        }

        public void setSelectedItemId(long selectedPosition) {
                mSelectedItemId = selectedPosition;
        }

        protected int getBackground(int position){
                return (getItemId(position) == mSelectedItemId) ? mSelectedBackground : mBackground;
        }

        public void add(BonjourService service) {
                this.services.remove(service);
                this.services.add(service);
                sortServices(services);
        }

        public void swap(List<BonjourService> service) {
                this.services.clear();
                this.services.addAll(service);
                sortServices(services);
                notifyDataSetChanged();
        }

        public void remove(BonjourService bonjourService) {
                if (this.services.remove(bonjourService)) {
                        sortServices(services);
                }
        }

        public void sortServices() {
                sortServices(services);
        }

        public void sortServices(ArrayList<BonjourService> services) {
                Collections.sort(services, (lhs, rhs) -> lhs.getServiceName().compareTo(rhs.getServiceName()));
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
                public TextView text1;
                public TextView text2;

                ViewHolder(View itemView) {
                        super(itemView);
                        text1 = itemView.findViewById(R.id.text1);
                        text2 = itemView.findViewById(R.id.text2);
                }
        }
}
