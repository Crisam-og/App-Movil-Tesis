package com.pyfinal.proyectotesis.Fragments.Home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.pyfinal.proyectotesis.Interface.ApiService;
import com.pyfinal.proyectotesis.Model.Distrito.Distrito;
import com.pyfinal.proyectotesis.Model.Distrito.DistritoResponse;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class HomeFragment extends Fragment {
    public static final int REQUEST_CODE = 1;
    private EditText editTextDescripcion, editTexDireccionReferencial, editTextLatitud, editTextLongitud;
    private Button buttonEnviar;
    private Spinner spDistrito;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSION_REQUEST_LOCATION = 3;
    private ArrayList<String> getnombre_distrito = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        editTextDescripcion = view.findViewById(R.id.txtDescripcion);
        editTexDireccionReferencial = view.findViewById(R.id.txtDireccionReferencial);
        editTextLatitud = view.findViewById(R.id.txtLatitud);
        editTextLongitud = view.findViewById(R.id.txtLongitud);
        spDistrito = (Spinner) view.findViewById(R.id.spinnerDistrito);
        buttonEnviar = view.findViewById(R.id.btnEnviar);


        getDistrito();

        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReporte();
            }
        });

        return view;
    }
    private void getDistrito() {
        RetrofitClient.getInstance().getApi().getDistritos().enqueue(new Callback<DistritoResponse>() {
            @Override
            public void onResponse(Call<DistritoResponse> call, Response<DistritoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Distrito> distritos = response.body().getData();
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


    private void enviarReporte() {
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
                            editTexDireccionReferencial.setText(String.valueOf("https://www.google.com/maps?q="+latitud+","+longitude));
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enviarReporte();
            } else {
                Toast.makeText(requireContext(), "Permiso Denegado ..", Toast.LENGTH_SHORT).show();
            }
        }
    }
}