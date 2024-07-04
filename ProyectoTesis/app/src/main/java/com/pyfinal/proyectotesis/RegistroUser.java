package com.pyfinal.proyectotesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.pyfinal.proyectotesis.Model.Clientes.Cliente;
import com.pyfinal.proyectotesis.Model.Clientes.RegisterResponse;
import com.pyfinal.proyectotesis.Retrofit.RetrofitClient;

public class RegistroUser extends Activity {
    private EditText editTextName, editTextApellidos, editTextUserName, editTextPassword, editTextPhone, editTextEmail, editTexDireccion;
    private Button buttonRegister;
    private TextView registerView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        editTextName = findViewById(R.id.username);
        editTextApellidos = findViewById(R.id.apellidos);
        editTextUserName = findViewById(R.id.username2);
        editTextPhone = findViewById(R.id.phone);
        editTextEmail = findViewById(R.id.email);
        editTexDireccion = findViewById(R.id.address);
        editTextPassword = findViewById(R.id.password);
        buttonRegister = findViewById(R.id.registrarseButton);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClient();
            }
        });
    }

    private void registerClient() {
        String firstName = editTextName.getText().toString().trim();
        String lastName = editTextApellidos.getText().toString().trim();
        String username = editTextUserName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTexDireccion.getText().toString().trim();

        Cliente registerRequest = new Cliente(firstName, lastName, username, email,
                password, phone, address);

        Call<RegisterResponse> call = RetrofitClient
                .getInstance()
                .getApi()
                .registerClient(registerRequest);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful()) {
                    RegisterResponse registerResponse = response.body();
                    Toast.makeText(RegistroUser.this, registerResponse.getMessage(), Toast.LENGTH_LONG).show();
                    // Aquí puedes navegar a la actividad de login o a donde sea apropiado
                    startLogin();
                } else {
                    Toast.makeText(RegistroUser.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegistroUser.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void startLogin(){
        Intent intent = new Intent(RegistroUser.this, LoginActivity.class);
        startActivity(intent);
    }
}

