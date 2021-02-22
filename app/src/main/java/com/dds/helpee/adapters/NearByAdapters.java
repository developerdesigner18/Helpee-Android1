package com.dds.helpee.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dds.helpee.R;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.fragments.ReportFragment;
import com.dds.helpee.model.Alerts;
import com.dds.helpee.model.Emergency;

import java.util.List;

public class NearByAdapters extends RecyclerView.Adapter<NearByAdapters.MyViewHolder>
{
    Context context;
    int screenwidth, screenheight;
    DisplayMetrics metrics;
    List<Alerts> listAlerts ;

    public NearByAdapters(Context context1, List<Alerts> listEmergency1)
    {
        this.context = context1;
        this.listAlerts = listEmergency1;

        if(this.context != null)
        {
            metrics = this.context.getResources().getDisplayMetrics();
            screenwidth = metrics.widthPixels;
            screenheight = metrics.heightPixels;
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_nearby, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tv_date_time.setText(listAlerts.get(position).getCreatedAt());
        holder.tv_location.setText(listAlerts.get(position).getLocation());

        holder.tv_report.setText(""+listAlerts.get(position).getAlertTypeId());

        for(int  i = 0 ; i < ReportFragment.listEmergency.size() ; i++)
        {
            if(ReportFragment.listEmergency.get(i).getId() == listAlerts.get(position).getAlertTypeId() )
            {
                holder.tv_report.setText(ReportFragment.listEmergency.get(i).getTypes());
                Glide.with(context).load(ApiClient.ALERT_IMAGE_URL+ReportFragment.listEmergency.get(i).getImage()).into(holder.img_report);
            }
        }

        if(ReportFragment.listEmergency.contains(listAlerts.get(position).getAlertTypeId()))
        {
            int index = ReportFragment.listEmergency.indexOf(listAlerts.get(position).getAlertTypeId());
            Log.e("index",""+index);
        }

//        holder.tv_report.setText(listAlerts.get(position).get());
//        holder.img_report.setImageURI(Uri.parse(listAlerts.get(position).getImage()));

//        Glide.with(context).load(ApiClient.ALERT_IMAGE_URL+listAlerts.get(position).getImage()).into(holder.img_report);

    }

    @Override
    public int getItemCount()
    {
        return listAlerts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img_report;
        LinearLayout relative;
        TextView tv_report, tv_date_time,tv_location;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            relative = (LinearLayout) itemView.findViewById(R.id.relative);
            img_report = (ImageView) itemView.findViewById(R.id.img_report);
            tv_report = (TextView) itemView.findViewById(R.id.tv_report);
            tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
            tv_location = (TextView) itemView.findViewById(R.id.tv_location);

        }
    }
}
