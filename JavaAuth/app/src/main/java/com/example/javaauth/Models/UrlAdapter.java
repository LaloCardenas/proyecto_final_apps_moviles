package com.example.javaauth.Models;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javaauth.R;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UrlAdapter extends RecyclerView.Adapter<UrlAdapter.UrlViewHolder> {

    private final List<UrlModel> urls;
    Context context;

    public UrlAdapter(List<UrlModel> urls, Context context) {
        this.urls = urls;
        this.context = context;
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
        ZoneId localZone = ZoneId.systemDefault();
        long expiryTimestampMillis = urlModel.getExpire() * 1000L;
        Date expiryDate = new Date(expiryTimestampMillis);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm z", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(localZone));

        String formattedExpiryDate = "Vencimiento: " + sdf.format(expiryDate);
        holder.txv_vencimiento.setText(formattedExpiryDate);


        // URL funcional
        holder.txv_short.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlModel.getShortUrl()));
            v.getContext().startActivity(browserIntent);
        });

        // permite al usuario copiar la URL
        holder.imb_copy.setOnClickListener(v -> {

            //crea una nueva clase que permite copiar los elementos
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Short URL", urlModel.getShortUrl());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "URL corta copiada!", Toast.LENGTH_SHORT).show();

        });

    }



    @Override
    public int getItemCount() {
        return urls.size();
    }

    public static class UrlViewHolder extends RecyclerView.ViewHolder {
        public TextView txv_short, txv_vencimiento, txv_urlOriginal;
        public ImageButton imb_copy;

        public UrlViewHolder(@NonNull View itemView) {
            super(itemView);
            txv_short = itemView.findViewById(R.id.txv_short);
            txv_vencimiento = itemView.findViewById(R.id.txv_vencimiento);
            txv_urlOriginal = itemView.findViewById(R.id.txv_urlOriginal);
            imb_copy = itemView.findViewById(R.id.btn_copy);
        }
    }

}
