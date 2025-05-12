package com.example.javaauth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.material.imageview.ShapeableImageView;

public class SignIn {

    private final Activity activity;
    public final FirebaseAuth auth;
    private final GoogleSignInClient googleSignInClient;
    private final ShapeableImageView img_profile;
    private final TextView txv_name, txv_mail, txv_userid;
    private final SignInButton btn_sign;

    private final ActivityResultLauncher<Intent> activityResultLauncher;

    public SignIn(Activity activity) {
        this.activity = activity;

        // Inicializar Firebase
        FirebaseApp.initializeApp(activity);
        auth = FirebaseAuth.getInstance();

        // Configurar Google Sign In
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, options);

        // Vincular vistas
        img_profile = activity.findViewById(R.id.img_profile);
        txv_mail = activity.findViewById(R.id.txv_email);
        txv_name = activity.findViewById(R.id.txv_name);
        txv_userid = activity.findViewById(R.id.txv_userid);
        btn_sign = activity.findViewById(R.id.btn_sign);

        // Configurar launcher de resultado
        activityResultLauncher = ((MainActivity) activity).registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleSignInResult
        );

        setupListeners();
    }

    private void setupListeners() {
        btn_sign.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);
        });
    }

    private void handleSignInResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            try {
                GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                auth.signInWithCredential(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {



                        Intent intent = new Intent(activity, UserDashboard.class );
                        activity.startActivity(intent);
                        activity.finish();

                    } else {
                        Toast.makeText(activity, "No se pudo iniciar sesión " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (ApiException e) {
                Toast.makeText(activity, "Error en Sign In: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public FirebaseAuth getAuth() {
        return this.auth;
    }
}
