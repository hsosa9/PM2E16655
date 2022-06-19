package com.example.pm2e16655;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class AdaptadorSpinner extends BaseAdapter {

    private Context context;
    private String [] array;
    private int posicionSeleccionada;

    public AdaptadorSpinner(Context context, String[] array){
        this.context = context;
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Object getItem(int i) {
        return array[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_spinner, null);
        TextView tvContenido = v.findViewById(R.id.tvContenido);
        tvContenido.setText((String)getItem(i));
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                posicionSeleccionada = i;
                return false;
            }
        });
        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        View v =  super.getDropDownView(position, convertView, parent);
        if (posicionSeleccionada == position){
            v.setBackgroundResource(R.color.purple_700);
            TextView tv = v.findViewById(R.id.tvContenido);
            tv.setTextColor(Color.WHITE);
        }
        return v;
    }
}