package com.pyfinal.proyectotesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
        registerView = findViewById(R.id.signupTextRegistro);



        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerClient();
            }
        });
        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
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

        if (firstName.isEmpty()) {
            editTextName.setError("Nombre requerido");
            editTextName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            editTextApellidos.setError("Apellidos requeridos");
            editTextApellidos.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Correo electrónico inválido");
            editTextEmail.requestFocus();
            return;
        }

        if (!esContrasenaSegura(password)) {
            editTextPassword.setError("La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una minúscula y un número");
            editTextPassword.requestFocus();
            return;
        }

        if (phone.length() != 9) {
            editTextPhone.setError("Teléfono debe contener 9 dígitos");
            editTextPhone.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            editTexDireccion.setError("Dirección requerida");
            editTexDireccion.requestFocus();
            return;
        }

        Cliente registerRequest = new Cliente(firstName, lastName, email,
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
    private boolean esContrasenaSegura(String password) {
        // Establecer los criterios de seguridad aquí
        // Por ejemplo, longitud mínima de 8 caracteres y al menos una letra mayúscula, una minúscula y un número
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return password.matches(regex);
    }
}

