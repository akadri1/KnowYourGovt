package com.akshathakadri.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by akshathakadri on 3/29/18.
 */

public class GovtHolder extends RecyclerView.ViewHolder{
    public TextView position;
    public TextView name;

    public GovtHolder(View view) {
        super(view);
        position = view.findViewById(R.id.position);
        name = view.findViewById(R.id.name);
    }
}
