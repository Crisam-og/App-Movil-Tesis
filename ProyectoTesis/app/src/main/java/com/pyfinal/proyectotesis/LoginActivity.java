package com.pyfinal.proyectotesis;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import com.pyfinal.proyectotesis.Model.Login.LoginRequest;
import com.pyfinal.proyectotesis.Model.Login.LoginResponse;
import com.pyfinal.proyectotesis.Retrofit.RetrofitClient;

public class LoginActivity extends Activity {
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.useremail);
        passwordEditText = findViewById(R.id.userpassword);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.signupText);

        // Inicializa SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                login(email, password);
            }
        });
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para abrir la pantalla de registro
                Intent intent = new Intent(LoginActivity.this, RegistroUser.class);
                startActivity(intent);
            }
        });
    }

    private void login(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    // Guarda los tokens en SharedPreferences o en un lugar seguro
                    saveTokens(loginResponse);

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    // Navega a la siguiente actividad
                    startMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

        String refreshToken = sharedPreferences.getString("refresh_token", null);
        String accessToken = sharedPreferences.getString("access_token", null);
        Log.d("LoginActivity", "Stored Refresh Token: " + refreshToken);
        Log.d("LoginActivity", "Stored Access Token: " + accessToken);
    }
    private void saveTokens(LoginResponse loginResponse) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("refresh_token", loginResponse.getRefresh());
        editor.putString("access_token", loginResponse.getAccess());
        editor.putString("nombre", loginResponse.getNombre());
        editor.putString("apellidos", loginResponse.getApellidos());
        editor.putString("email", loginResponse.getEmail());
        editor.putString("phone_number", loginResponse.getPhone_number());
        editor.putString("address", loginResponse.getAddress());
        editor.apply();  // Aplica los cambios de forma asincrónica

        // Log para ver los tokens
        Log.d("LoginActivity", "Refresh Token: " + loginResponse.getRefresh());
        Log.d("LoginActivity", "Access Token: " + loginResponse.getAccess());Log.d("LoginActivity", "Nombre: " + loginResponse.getNombre());
        Log.d("LoginActivity", "Apellidos: " + loginResponse.getApellidos());
        Log.d("LoginActivity", "Email: " + loginResponse.getEmail());
        Log.d("LoginActivity", "Phone Number: " + loginResponse.getPhone_number());
        Log.d("LoginActivity", "Address: " + loginResponse.getAddress());

    }
}
