package com.example.javaauth.Views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.javaauth.Handlers.ApiHandler;
import com.example.javaauth.Models.CreateUrlRequest;
import com.example.javaauth.Models.GetUrlsRequest;
import com.example.javaauth.Models.UrlModel;
import com.example.javaauth.R;
import com.example.javaauth.Models.UrlAdapter;
import com.example.javaauth.UsageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
* Clase que es llamada una vez el usuario inicia sesión
* llama a la pantalla principal de la aplicación Usuario Dashboard
*   - Crear nuevas URL´s
*   - Ver el historial de las últimas URL's 5-10?
*   - Información de él mismo (correo y foto?)
*   - Botón para ver ajustes? -> neuva pantalla?,
* */
public class UserDashboard extends AppCompatActivity {

    EditText txe_url;
    TextView txv_nombre, txv_tipo, txv_usos;
    Button btn_plus;
    ImageView img_profile;
    RecyclerView recyclerUrls;
    public FirebaseUser firebaseUser;
    UsageManager usageManager;
    Context context;
    private final String premium = "ERES PREMIUM";


    @RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.context = this;

        //retrive al usuario
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txv_nombre = findViewById(R.id.txv_name);
        txv_tipo = findViewById(R.id.txv_tipo);
        txv_usos = findViewById(R.id.txv_usos);

        txe_url = findViewById(R.id.txe_url);

        img_profile = findViewById(R.id.img_profile);

        btn_plus = findViewById(R.id.btn_plus);

        usageManager = UsageManager.getInstance(this);

        recyclerUrls = findViewById(R.id.recycler_urls);
        recyclerUrls.setLayoutManager(new LinearLayoutManager(this));

        this.loadUrls();
        UsageManager usageManager = UsageManager.getInstance(this);

        //Pantalla para hacerte premium
        img_profile.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
        });

        /*
        * btn_plus -> botón para generar una nueva URL
        *
        * Evalua si el usuario tiene "capacidad" para agregar otra URL
        * Evalua que el campo de la URL no este vacío
        * Manda el request
        *
        * Muestra error en caso excepcionales
        * */
        btn_plus.setOnClickListener(v -> {

            if (usageManager.incrementUrlCount()) {
                if (txe_url.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Error, URL vacía", Toast.LENGTH_LONG).show();
                } else {
                    ApiHandler apiHandler = new ApiHandler(this);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;

                    CreateUrlRequest requestBody = null;

                    requestBody = new CreateUrlRequest(
                            txe_url.getText().toString(),
                            user.getUid()
                    );

                    Call<Object> call = apiHandler.getService().sendData(requestBody);

                    // Retrofit enqueue asíncrono
                    call.enqueue(new retrofit2.Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                            if (response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(UserDashboard.this, "Nueva generada con éxito", Toast.LENGTH_SHORT).show());
                                loadUrls();
                            } else {
                                runOnUiThread(() -> Toast.makeText(UserDashboard.this, "Hubo un error enviando la URL.", Toast.LENGTH_SHORT).show());
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            t.printStackTrace();
                            runOnUiThread(() -> Toast.makeText(UserDashboard.this, "Fallo de conexión.", Toast.LENGTH_SHORT).show());
                        }
                    });
                }
            } else {
                runOnUiThread(() -> Toast.makeText(UserDashboard.this, "No te quedan usos disponibles", Toast.LENGTH_SHORT).show());
            }
            urlRestante();
        });

        assert firebaseUser != null;
        txv_nombre.setText(firebaseUser.getDisplayName());
        Toast.makeText(
                this,
                "¡Hola de nuevo " + firebaseUser.getDisplayName() + "!" ,
                Toast.LENGTH_LONG
        ).show();

        Uri photoUrl = firebaseUser.getPhotoUrl();

        if(usageManager.getIsPremiumUser()){
            txv_tipo.setText(this.premium);
            txv_usos.setVisibility(View.INVISIBLE);
        } else {
            txv_tipo.setText("NO " + this.premium);
            txv_usos.setText(
                    String.valueOf("Usos restantes: " + usageManager.getRemainingUses())
            );
        }


        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(img_profile);
        }
    }

    private void loadUrls() {
        ApiHandler apiHandler = new ApiHandler(this);
        GetUrlsRequest body = new GetUrlsRequest(firebaseUser.getUid());

        Call<List<UrlModel>> call = apiHandler.getService().getUrls(body);

        call.enqueue(new retrofit2.Callback<List<UrlModel>>() {
            @Override
            public void onResponse(Call<List<UrlModel>> call, Response<List<UrlModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UrlModel> urls = response.body();
                    UrlAdapter adapter = new UrlAdapter(urls, context);
                    recyclerUrls.setAdapter(adapter);
                } else {
                    Toast.makeText(UserDashboard.this, "Error cargando URLs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UrlModel>> call, Throwable t) {
                Toast.makeText(UserDashboard.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void urlRestante(){
        if(usageManager.getIsPremiumUser()){
            txv_usos.setVisibility(View.INVISIBLE);
        } else {
            txv_usos.setText(
                    String.valueOf("Usos restantes: " + usageManager.getRemainingUses())
            );
        }
    }

}
