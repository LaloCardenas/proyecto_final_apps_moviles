package com.example.javaauth;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
* Clase que es llamada una vez el usuario inicia sesión
* llama a la pantalla principal de la aplicación Usuario Dashboard
*   - Crear nuevas URL´s
*   - Ver el historial de las últimas URL's 5-10?
*   - Información de él mismo (correo y foto?)
*   - Botón para ver ajustes? -> neuva pantalla?,
* */
public class UserDashboard extends AppCompatActivity {

    TextView txv_nombre;
    ImageView img_profile, img_plus;

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

        Toast.makeText(this, "¡Hola de nuevo " + firebaseUser.getDisplayName() + "!" , Toast.LENGTH_LONG).show();
        txv_nombre = findViewById(R.id.txv_name);
        img_profile = findViewById(R.id.img_profile);
        img_plus = findViewById(R.id.btn_plus);

        //Cuando el usuario le de click a su foto de perfil abrir el menú de settings
        img_profile.setOnClickListener(v -> {

            //llamar a otra vista, de ajustes

        });

        //Cuando el usuario le de click a la imagen de "+" debemos evaluar si el campo tiene valores o si esta vacio
        img_plus.setOnClickListener(v ->{

        });



        txv_nombre.setText(firebaseUser.getDisplayName());

        Uri photoUrl = firebaseUser.getPhotoUrl();
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .into(img_profile);
        }
    }
}
