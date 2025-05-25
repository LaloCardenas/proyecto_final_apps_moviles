package com.example.javaauth.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextWatcher;

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
    Button btn_back;

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
        btn_back = findViewById(R.id.btn_atras);

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

        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, UserDashboard.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        txe_card.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private final String divider = "-";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;

                isUpdating = true;
                StringBuilder formatted = new StringBuilder(s.toString().replace(divider, ""));
                int length = formatted.length();

                if (length > 0) {
                    for (int i = 4; i < length; i += 5) {
                        formatted.insert(i, divider);
                        length++;
                    }
                }

                txe_card.setText(formatted.toString());
                txe_card.setSelection(txe_card.getText().length());
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Formato automático para fecha de vencimiento (MM/AA)
        txe_expiration.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) return;

                isUpdating = true;

                String input = s.toString().replace("/", "");
                if (input.length() > 4) input = input.substring(0, 4); // Limitar a MMYY

                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < input.length(); i++) {
                    if (i == 2) formatted.append("/");
                    formatted.append(input.charAt(i));
                }

                txe_expiration.setText(formatted.toString());
                txe_expiration.setSelection(formatted.length());
                isUpdating = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
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