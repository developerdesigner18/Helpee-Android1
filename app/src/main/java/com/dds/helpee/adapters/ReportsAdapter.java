package com.dds.helpee.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dds.helpee.R;
import com.dds.helpee.activities.HomeActivity;
import com.dds.helpee.api.ApiClient;
import com.dds.helpee.fragments.ReportFragment;
import com.dds.helpee.model.Emergency;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder>
{
    Context context;
//    int screenwidth, screenheight;
//    DisplayMetrics metrics;
    List<Emergency> listEmergency ;
    ReportFragment reportFrag ;

    public ReportsAdapter(Context context, List<Emergency> listEmergency1, ReportFragment obj)
    {
        this.context = context;
        this.listEmergency = listEmergency1;
        this.reportFrag = obj;

//        metrics = this.context.getResources().getDisplayMetrics();
//        screenwidth = metrics.widthPixels;
//        screenheight = metrics.heightPixels;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_report, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        holder.tv_report.setText(listEmergency.get(position).getTypes());

        Glide.with(context).load(ApiClient.ALERT_IMAGE_URL+listEmergency.get(position).getImage()).into(holder.img_report);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((HomeActivity)context).CreateAlert(listEmergency.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return listEmergency.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img_report;
        LinearLayout relative;
        TextView tv_report;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            relative = (LinearLayout) itemView.findViewById(R.id.relative);
            img_report = (ImageView) itemView.findViewById(R.id.img_report);
            tv_report = (TextView) itemView.findViewById(R.id.tv_report);

        }
    }
}
