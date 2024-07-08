package com.pyfinal.proyectotesis.Interface;

import com.pyfinal.proyectotesis.Model.Clientes.Cliente;
import com.pyfinal.proyectotesis.Model.Clientes.RegisterResponse;
import com.pyfinal.proyectotesis.Model.Distrito.Distrito;
import com.pyfinal.proyectotesis.Model.Distrito.DistritoResponse;
import com.pyfinal.proyectotesis.Model.Login.LoginRequest;
import com.pyfinal.proyectotesis.Model.Login.LoginResponse;
import com.pyfinal.proyectotesis.Model.Reportes.ReporteResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiService {
    @POST("login/client/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("register/client/")
    Call<RegisterResponse> registerClient(@Body Cliente registerRequest);

    @GET("get/distrito/")
    Call<DistritoResponse> getDistritos();

    @Multipart
    @POST("register/reporte/")
    Call<ReporteResponse> registerReporte(
            @Part("cliente") RequestBody cliente,
            @Part("distrito") RequestBody distrito,
            @Part MultipartBody.Part imagen,
            @Part("descripcion") RequestBody descripcion,
            @Part("lat") RequestBody lat,
            @Part("long") RequestBody lon,
            @Part("direccion") RequestBody direccion
    );
}
