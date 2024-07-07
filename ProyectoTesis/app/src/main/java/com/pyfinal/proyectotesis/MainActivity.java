package com.pyfinal.proyectotesis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.pyfinal.proyectotesis.Fragments.Help.HelpFragment;
import com.pyfinal.proyectotesis.Fragments.Home.HomeFragment;
import com.pyfinal.proyectotesis.Fragments.Reporte.ListReportFragment;

import java.util.ArrayList;
import android.Manifest;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ImageView imageView;
    private TextView nombreUser, correoUser;
    private SharedPreferences sharedPreferences;
    private static final int PERMISSION_REQUEST_ALL = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        solicitarPermisos();
        Toolbar toolbar = findViewById(R.id.toolbar); //Ignore red line errors
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);



        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        imageView = headerView.findViewById(R.id.imagenP);
        nombreUser = headerView.findViewById(R.id.username);
        correoUser = headerView.findViewById(R.id.correouser);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        String userNombre = sharedPreferences.getString("nombre",null);
        String userApellidos = sharedPreferences.getString("apellidos",null);
        String userCorreo = sharedPreferences.getString("email",null);

        if(userNombre !=null && userCorreo !=null){
            nombreUser.setText(userNombre);
            correoUser.setText(userCorreo);
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (itemId == R.id.nav_report) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListReportFragment()).commit();
        } else if (itemId == R.id.nav_help) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
        }else if (itemId == R.id.nav_logout) {
            cerrarSesion();
            Toast.makeText(this, "Sesion Cerrada!", Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void cerrarSesion(){
        sharedPreferences.edit().clear().apply();
        startLoginActivity();

    }
    private void solicitarPermisos() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_ALL);
        }
    }
}