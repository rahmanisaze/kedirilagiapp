package com.example.kedirilagi;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class RecKulinerAdapter extends RecyclerView.Adapter<RecKulinerAdapter.MyViewHolder> implements Filterable {

    Context mContext;
    List<Kuliners> mData;
    List<Kuliners> mDataAll;

    public RecKulinerAdapter(Context mContext, List<Kuliners> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.mDataAll = new ArrayList<>(mData);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.recommendation_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvNama.setText(mData.get(position).getNama());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.image);
        //Hitung jarak
        GetLocation getLocation = new GetLocation(mContext);
        Location location = getLocation.getLocation();
        double latUser = location.getLatitude();
        double longUser = location.getLongitude();
        double latDestination = Double.valueOf(mData.get(position).getLatitude());
        double longDestination = Double.valueOf(mData.get(position).getLongitude());
        double distance = getDistance(latDestination, longDestination,latUser, longUser);
        holder.tvJarak.setText(String.format("%.2f KM",distance));
        holder.tvJarak.setVisibility(View.VISIBLE);
        holder.view2.setVisibility(View.VISIBLE);


    }

    private double getDistance(double latDestination, double longDestination, double latUser, double longUser) {

        Double lat1 = latDestination;
        Double lon1 = longDestination;
        Double lat2 = latUser;
        Double lon2 = longUser;
        final int R = 6371;

        Double latRad1 = lat1 * (Math.PI / 180);
        Double latRad2 = lat2 * (Math.PI / 180);
        Double deltaLatRad = (lat2 - lat1) * (Math.PI / 180);
        Double deltaLonRad = (lon2 - lon1) * (Math.PI / 180);
        //rumus haversine//
        Double a = Math.sin(deltaLatRad / 2)
                * Math.sin(deltaLatRad / 2)
                + Math.cos(latRad1)
                * Math.cos(latRad2)
                * Math.sin(deltaLonRad / 2)
                * Math.sin(deltaLonRad / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        Double s = R * c;
        return s;

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Kuliners> filteredlist = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredlist.addAll(mDataAll);
            } else {
                String pttrn = constraint.toString().toLowerCase().trim();
                for (Kuliners kuliners: mDataAll) {
                    if (kuliners.getNama().toLowerCase().contains(pttrn)) {
                        filteredlist.add(kuliners);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredlist;
            return filterResults;
        }
        //run on ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mData.clear();
            mData.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder{

        //Button btDetail;
        TextView tvNama;
        TextView tvOpen;
        ImageView image;
        TextView tvJarak;
        View view2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNama = itemView.findViewById(R.id.tvNama);
            tvJarak = itemView.findViewById(R.id.tvJarak);
            view2 = itemView.findViewById(R.id.view2);
            image = itemView.findViewById(R.id.img);
            //btDetail = itemView.findViewById(R.id.btDetail);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent kulinerDetailActivity = new Intent(mContext, KulinerDetailActivity.class);
                    int position = getAdapterPosition();

                    kulinerDetailActivity.putExtra("nama", mData.get(position).getNama());
                    kulinerDetailActivity.putExtra("kulinerImage", mData.get(position).getPicture());
                    kulinerDetailActivity.putExtra("description", mData.get(position).getDescription());
                    kulinerDetailActivity.putExtra("alamat", mData.get(position).getAlamat());
                    kulinerDetailActivity.putExtra("harga", mData.get(position).getHargaMenu());
                    kulinerDetailActivity.putExtra("open", mData.get(position).getOpen() );
                    kulinerDetailActivity.putExtra("kulinerKey", mData.get(position).getKulinerKey());
                    mContext.startActivity(kulinerDetailActivity);
                }
            });

        }
    }
}

