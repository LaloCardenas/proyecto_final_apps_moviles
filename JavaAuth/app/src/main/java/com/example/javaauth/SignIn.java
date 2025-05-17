package com.example.javaauth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/** @noinspection ALL*/
public class SignIn {

    private final Activity activity;
    public final FirebaseAuth auth;
    private final GoogleSignInClient googleSignInClient;
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

}
