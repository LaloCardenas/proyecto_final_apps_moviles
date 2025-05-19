package com.example.javaauth;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentActivity extends AppCompatActivity {

    EditText txe_card, txe_expiration, txe_cvv;
    Button btn_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        UsageManager usageManager = UsageManager.getInstance(this);

        txe_card = findViewById(R.id.txe_card);
        txe_expiration = findViewById(R.id.txe_expiration);
        txe_cvv = findViewById(R.id.txe_cvv);
        btn_pay = findViewById(R.id.btn_pay);

        btn_pay.setOnClickListener(v -> {
            Log.i("Tarjeta", txe_card.getText().toString());
            Log.i("Expiracion", txe_expiration.getText().toString());
            Log.i("CVV", txe_cvv.getText().toString());

            if (txe_card.getText().toString().isEmpty()
                    || txe_expiration.getText().toString().isEmpty()
                    || txe_cvv.getText().toString().isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_LONG).show();
            } else if (!checkCard(txe_card.getText().toString())) {
                Toast.makeText(this, "Ingresa un número de tarjeta válido", Toast.LENGTH_LONG).show();
            } else if (!checkExpiration(txe_expiration.getText().toString())) {
                Toast.makeText(this, "Ingresa una fecha de vencimiento válida", Toast.LENGTH_LONG).show();
            } else if (!checkCVV(txe_cvv.getText().toString())) {
                Toast.makeText(this, "Ingresa un CVV válido", Toast.LENGTH_LONG).show();
            } else {
                usageManager.setPremiumUser(true);
                Toast.makeText(this, "Ahora eres premium", Toast.LENGTH_LONG).show();
            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static boolean checkCard(String input) {
        String regex = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }

    public static boolean checkExpiration(String input) {
        String regex = "^\\d{2}/\\d{2}$";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }

    public static boolean checkCVV(String input) {
        return input.matches("\\d{3}");
    }
}