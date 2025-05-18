package com.example.javaauth;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import retrofit2.Call;

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
    TextView txv_nombre;
    Button btn_plus;
    ImageView img_profile;

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
        //retrive al usuario
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txv_nombre = findViewById(R.id.txv_name);
        txe_url = findViewById(R.id.txe_url);

        img_profile = findViewById(R.id.img_profile);
        btn_plus = findViewById(R.id.btn_plus);

        //Cuando el usuario le de click a su foto de perfil abrir el menú de settings
        img_profile.setOnClickListener(v -> {

            //llamar a otra vista, de ajustes
            //Vista de ajustes para comprar premium

        });

        //Cuando el usuario le de click al botón de "+" evaluar si el campo tiene valores o si esta vacio
        btn_plus.setOnClickListener(v -> {
            if (txe_url.getText().toString().isEmpty()) {
                Toast.makeText(this, "Error, URL vacía", Toast.LENGTH_LONG).show();
            } else {
                ApiHandler apiHandler = new ApiHandler(this);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;

                RequestBodyModel requestBody = new RequestBodyModel(
                        txe_url.getText().toString(),
                        user.getUid()
                );

                Call<Object> call = apiHandler.getService().sendData(requestBody);

                // Retrofit enqueue asíncrono
                call.enqueue(new retrofit2.Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> Toast.makeText(UserDashboard.this, "¡URL enviada con éxito!", Toast.LENGTH_SHORT).show());
                            System.out.println("Respuesta: " + response.body());
                        } else {
                            try {
                                System.out.println("Error: " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
        });

        assert firebaseUser != null;
        txv_nombre.setText(firebaseUser.getDisplayName());
        Toast.makeText(
                this,
                "¡Hola de nuevo " + firebaseUser.getDisplayName() + "!" ,
                Toast.LENGTH_LONG
        ).show();

        Uri photoUrl = firebaseUser.getPhotoUrl();

        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(img_profile);
        }
    }
}
