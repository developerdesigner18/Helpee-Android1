package com.dds.helpee.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dds.helpee.model.Const;
import  com.dds.helpee.model.Number;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dds.helpee.R;

import java.util.List;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.MyViewHolder>
{
    Context context;
    List<Number> listCountries;
    SharedPreferences pre;
    CallBack mCallBack;

    public CountriesAdapter(Context context1, List<Number> listCountries1, CallBack obj)
    {
        this.context = context1;
        this.listCountries = listCountries1;
        mCallBack = obj;

        pre = context.getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE);
    }
    public void filterList(List<Number> filteredList)
    {
        listCountries = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_country, parent, false);
        MyViewHolder viewholder = new MyViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        if(pre.getString(Const.LANGUAGE, null).equals("fr"))
        {
            holder.tv_country.setText(listCountries.get(position).getFrenchName());
        }
        else
        {
            holder.tv_country.setText(listCountries.get(position).getEnglishName());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mCallBack.getCountryName(holder.tv_country.getText().toString().trim(), listCountries.get(position));
//                ((MainActivity) context).setCountry(holder.tv_country.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return listCountries.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_country;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            tv_country = (TextView) itemView.findViewById(R.id.tv_country);

        }
    }
    public interface CallBack
    {
        public void getCountryName(String countryName, Number countryPos);
    }
}

