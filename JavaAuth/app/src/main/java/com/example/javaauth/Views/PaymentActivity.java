package com.example.javaauth.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.javaauth.R;
import com.example.javaauth.UsageManager;

import java.util.Calendar; // Importa Calendar
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentActivity extends AppCompatActivity {

    EditText txe_card, txe_expiration, txe_cvv;
    Button btn_pay;
    Button btn_atras;

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
        btn_atras = findViewById(R.id.btn_atras);

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
                Toast.makeText(this, "Ingresa una fecha de vencimiento válida y futura (MM/AA)", Toast.LENGTH_LONG).show();
            } else if (!checkCVV(txe_cvv.getText().toString())) {
                Toast.makeText(this, "Ingresa un CVV válido", Toast.LENGTH_LONG).show();
            } else {
                usageManager.setPremiumUser(true);
                Toast.makeText(this, "Ahora eres premium", Toast.LENGTH_LONG).show();

                //Congela un segundo la pantalla para que el usuario vea
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(PaymentActivity.this, UserDashboard.class);
                        startActivity(intent);
                        finish(); // Cierra PaymentActivity
                    }
                }, 900); // 1 segundo
            }
        });

        btn_atras.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, UserDashboard.class);
            startActivity(intent);
            finish();
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
        // Patrón para MM/AA
        String regex = "^(0[1-9]|1[0-2])/(\\d{2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            return false; // No cumple con el formato MM/AA
        }

        try {
            int month = Integer.parseInt(matcher.group(1)); // Mes (MM)
            int yearShort = Integer.parseInt(matcher.group(2)); // Año corto (AA)

            Calendar currentCalendar = Calendar.getInstance();
            int currentYearFull = currentCalendar.get(Calendar.YEAR);
            int currentMonth = currentCalendar.get(Calendar.MONTH) + 1; // Meses en Calendar son 0-11


            int fullYear = (currentYearFull / 100) * 100 + yearShort;

            // Comparar la fecha de vencimiento con la fecha actual
            if (fullYear < currentYearFull) {
                return false; // El año de vencimiento ya pasó
            } else if (fullYear == currentYearFull) {
                if (month < currentMonth) {
                    return false; // El mes de vencimiento ya pasó en el año actual
                }
            }
            return true; // La fecha de vencimiento es válida y futura
        } catch (NumberFormatException e) {
            Log.e("PaymentActivity", "Error al parsear la fecha de expiración: " + e.getMessage());
            return false;
        }
    }

    public static boolean checkCVV(String input) {
        return input.matches("\\d{3}");
    }
}