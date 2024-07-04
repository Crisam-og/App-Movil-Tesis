package com.pyfinal.proyectotesis.Interface;

import com.pyfinal.proyectotesis.Model.Clientes.Cliente;
import com.pyfinal.proyectotesis.Model.Clientes.RegisterResponse;
import com.pyfinal.proyectotesis.Model.Login.LoginRequest;
import com.pyfinal.proyectotesis.Model.Login.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface ApiService {
    @POST("login/client/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register/client/")
    Call<RegisterResponse> registerClient(@Body Cliente registerRequest);
}
