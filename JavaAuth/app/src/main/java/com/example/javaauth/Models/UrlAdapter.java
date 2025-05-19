package com.example.javaauth.Models;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaauth.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlViewHolder> {

    private final List<UrlModel> urls;

    public UrlAdapter(List<UrlModel> urls) {
        this.urls = urls;
    }

    @NonNull
    @Override
    public UrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_url, parent, false); //inflar vista
        return new UrlViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UrlViewHolder holder, int position) {
        UrlModel urlModel = urls.get(position);
        holder.txv_short.setText(urlModel.getShortUrl());
        holder.txv_urlOriginal.setText("Url: " + urlModel.getOriginalUrl());

        // Mostrar la fecha de vencimiento de la url
        long expiryTimestampMillis = urlModel.getExpire() * 1000L;
        Date expiryDate = new Date(expiryTimestampMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault()); // Or specify a timezone if needed
        String formattedExpiryDate = "Vencimiento: " + sdf.format(expiryDate);
        holder.txv_vencimiento.setText(formattedExpiryDate);

        // URL funcional
        holder.txv_short.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlModel.getShortUrl()));
            v.getContext().startActivity(browserIntent);
        });

    }



    @Override
    public int getItemCount() {
        return urls.size();
    }

    public static class UrlViewHolder extends RecyclerView.ViewHolder {
        public TextView txv_short, txv_vencimiento, txv_urlOriginal;

        public UrlViewHolder(@NonNull View itemView) {
            super(itemView);
            txv_short = itemView.findViewById(R.id.txv_short);
            txv_vencimiento = itemView.findViewById(R.id.txv_vencimiento);
            txv_urlOriginal = itemView.findViewById(R.id.txv_urlOriginal);
        }
    }

}
