package com.akshathakadri.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by akshathakadri on 3/29/18.
 */

public class GovtAdapter extends RecyclerView.Adapter<GovtHolder>{
    private static final String TAG = "GovtAdapter";
    private MainActivity mainAct;
    private List<Official> officialList;

    GovtAdapter(List<Official> officialList, MainActivity mainAct) {
        this.mainAct = mainAct;
        this.officialList = officialList;
    }

    @Override
    public GovtHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.govt_list, parent, false);

        itemView.setOnClickListener(mainAct);

        return new GovtHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GovtHolder holder, int position) {
        Official official = officialList.get(position);
        holder.name.setText(official.getName()+" ("+official.getParty()+")");
        holder.position.setText(official.getPosition());
    }

    @Override
    public int getItemCount() {
        return officialList.size();
    }

}

