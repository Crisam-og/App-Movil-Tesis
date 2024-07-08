package com.pyfinal.proyectotesis.Fragments.Home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.Manifest;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.util.IOUtils;
import com.google.gson.JsonObject;
import com.pyfinal.proyectotesis.Interface.ApiService;
import com.pyfinal.proyectotesis.Model.Clientes.RegisterResponse;
import com.pyfinal.proyectotesis.Model.Distrito.Distrito;
import com.pyfinal.proyectotesis.Model.Distrito.DistritoResponse;
import com.pyfinal.proyectotesis.Model.Reportes.Reporte;
import com.pyfinal.proyectotesis.Model.Reportes.ReporteResponse;
import com.pyfinal.proyectotesis.R;

//Librerias de GoogleMaps
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.pyfinal.proyectotesis.Retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {
    public static final int REQUEST_CODE = 1;
    private EditText editTextDescripcion, editTexDireccionReferencial, editTextLatitud, editTextLongitud;
    private Button buttonEnviar, btnAbrirCamera;
    private Spinner spDistrito;
    private ImageView imgCapture;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSION_REQUEST_LOCATION = 3;
    private SharedPreferences sharedPreferences;
    private ArrayList<String> getnombre_distrito = new ArrayList<String>();
    private List<Distrito> distritos;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    private Uri imageUri;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("user_prefs",Context.MODE_PRIVATE);


        editTextDescripcion = view.findViewById(R.id.txtDescripcion);
        editTexDireccionReferencial = view.findViewById(R.id.txtDireccionReferencial);
        editTextLatitud = view.findViewById(R.id.txtLatitud);
        editTextLongitud = view.findViewById(R.id.txtLongitud);
        imgCapture = view.findViewById(R.id.imgCaptured);
        spDistrito = (Spinner) view.findViewById(R.id.spinnerDistrito);
        buttonEnviar = view.findViewById(R.id.btnEnviar);
        btnAbrirCamera = view.findViewById(R.id.btnOpenCamera);


        getDistrito();


        btnAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReporte();
            }
        });

        return view;
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private void enviarReporte(){
        getDistrito();
        obtenerCoordenada();

        String latitud = editTextLatitud.getText().toString();
        String longitud = editTextLongitud.getText().toString();

        if (latitud.isEmpty() || longitud.isEmpty()) {
            Toast.makeText(requireContext(), "Coordenadas no disponibles. Por favor, intentelo de nuevo.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Obtener el ID del usuario logueado desde SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", "");

        // Obtener el ID del distrito seleccionado
        int selectedPosition = spDistrito.getSelectedItemPosition();
        if (selectedPosition == 0) { // Si es "----Select----"
            Toast.makeText(getContext(), "Por favor, seleccione un distrito", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el distrito seleccionado
        Distrito selectedDistrito = distritos.get(selectedPosition - 1); // -1 porque el primer item es "----Select----"
        int distritoId = selectedDistrito.getId();
        Log.d("Distrito", "Select Distrito id: " + distritoId);
        // Obtener la descripción
        String descripcion = editTextDescripcion.getText().toString();

        // Obtener latitud y longitud

        // Obtener dirección
        String direccion = editTexDireccionReferencial.getText().toString();



        // Crear el cuerpo de la solicitud multipart
        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            try {
                InputStream is = requireContext().getContentResolver().openInputStream(imageUri);
                byte[] bytes = IOUtils.toByteArray(is);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
                imagePart = MultipartBody.Part.createFormData("imagen", "image.jpg", requestFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Crear el cuerpo de la solicitud
        RequestBody clienteBody = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody distritoBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(distritoId));
        RequestBody descripcionBody = RequestBody.create(MediaType.parse("text/plain"), descripcion);
        RequestBody latBody = RequestBody.create(MediaType.parse("text/plain"), latitud);
        RequestBody longBody = RequestBody.create(MediaType.parse("text/plain"), longitud);
        RequestBody direccionBody = RequestBody.create(MediaType.parse("text/plain"), direccion);

        Log.d("Cliente", "Cliente Rb id: " + clienteBody);
        Log.d("Distrito", "Distrito Rb id: " + distritoBody);
        Log.d("Descripción", "Descrpcion Rb valor: " + descripcionBody);
        Log.d("Latitud", "Latitud Rb valor: " + latBody);
        Log.d("Longitud", "Longitud Rb valor: " + longBody);
        Log.d("Dirección", "Dirección Rb valor: " + direccionBody);

        Log.d("Cliente", "Cliente id: " + userId);
        Log.d("Distrito", "Distrito id: " + distritoId);
        Log.d("Descripción", "Descrpcion valor: " + descripcion);
        Log.d("Latitud", "Latitud valor: " + latitud);
        Log.d("Longitud", "Longitud valor: " + longitud);
        Log.d("Dirección", "Dirección valor: " + direccion);
        // Hacer la llamada a la API
        Call<ReporteResponse> call = RetrofitClient.getInstance().getApi().registerReporte(
                clienteBody, distritoBody, imagePart, descripcionBody, latBody, longBody, direccionBody);

        call.enqueue(new Callback<ReporteResponse>() {
            @Override
            public void onResponse(Call<ReporteResponse> call, Response<ReporteResponse> response) {
                if (response.isSuccessful()) {
                    ReporteResponse reporteResponse = response.body();
                    Toast.makeText(getContext(), reporteResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    // Aquí puedes agregar lógica adicional después de un envío exitoso
                    limpiarCasillas();
                } else {
                    Toast.makeText(getContext(), "Error al enviar el reporte", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReporteResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getDistrito() {
        RetrofitClient.getInstance().getApi().getDistritos().enqueue(new Callback<DistritoResponse>() {
            @Override
            public void onResponse(Call<DistritoResponse> call, Response<DistritoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    distritos = response.body().getData();
                    List<String> getnombre_distrito = new ArrayList<>();

                    // Añadir un item por defecto
                    getnombre_distrito.add("----Select----");

                    // Extraer los nombres de los distritos
                    for (Distrito distrito : distritos) {
                        getnombre_distrito.add(distrito.getNombre_distrito());
                    }

                    // Configurar el adaptador del Spinner
                    ArrayAdapter<String> spinDistritoAdpater = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, getnombre_distrito);
                    spinDistritoAdpater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spDistrito.setAdapter(spinDistritoAdpater);

                    spDistrito.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // Implementa la lógica para el evento de selección aquí
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Implementa la lógica para cuando no se selecciona nada aquí
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DistritoResponse> call, Throwable t) {
                // Manejo de error aquí
            }
        });
    }


    private void obtenerCoordenada() {
        // Verificar si los servicios de ubicación están habilitados
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // Mostrar diálogo para habilitar los servicios de ubicación
            new AlertDialog.Builder(requireContext())
                    .setTitle("Servicios de ubicación desactivados")
                    .setMessage("Por favor, activa los servicios de ubicación para continuar.")
                    .setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Redirigir a la configuración de ubicación
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            solicitarPermisos(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_CODE);
            Toast.makeText(requireContext(), "Obteniendo tu ubicación", Toast.LENGTH_SHORT).show();
            try {
                LocationRequest locationRequest = LocationRequest.create(); // Cambiado para usar create()
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(3000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationServices.getFusedLocationProviderClient(requireActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(requireActivity()).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitud = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            editTextLatitud.setText(String.valueOf(latitud));
                            editTextLongitud.setText(String.valueOf(longitude));
                        } else {
                            // Mostrar un Toast si no se pudo obtener la ubicación
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), "No se pudo obtener tu ubicación", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }, Looper.getMainLooper()); // Asegurarse de usar el MainLooper
            } catch (Exception ex) {
                System.out.println("Error es :" + ex);
            }
        }
    }
    private void solicitarPermisos(String permiso, int requestCode) {
        if (ContextCompat.checkSelfPermission(requireContext(), permiso) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permiso}, requestCode);
        }
    }
    private  void limpiarCasillas(){
        editTextDescripcion.setText("");
        editTexDireccionReferencial.setText("");
        editTextLatitud.setText("");
        editTextLongitud.setText("");
        //imgCapture.setImageURI(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            switch (requestCode) {
                case REQUEST_CODE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        enviarReporte();
                    } else {
                        Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        openFileChooser();
                    } else {
                        Toast.makeText(requireContext(), "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            // Opcionalmente, puedes mostrar la imagen seleccionada en un ImageView
            imgCapture.setImageURI(imageUri);
        }
    }
}