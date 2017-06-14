package br.com.vendasoffline.vendasoffline.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.activities.RegisterActivity;
import br.com.vendasoffline.vendasoffline.helpers.InputValidation;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by lrgabriel on 26/05/17.
 */

public class FragmentCadastroCliente extends Fragment implements View.OnClickListener {

    private View view;
    private EditText edtxtNome;
    private EditText edtxtCnpj;
    private EditText edtxtCidade;
    private EditText edtxtCep;
    private EditText edtxtNro;
    private RadioGroup rdgTipoPessoa;
    private Spinner spnPais;
    private Spinner spnUf;
    private Button btnCadastrar;
    private Button btnCancelar;
    private ImageButton btnGPS;
    private Customer cliente;
    private DatabaseHelper databaseHelper;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private List<String> lstPaises;
    private List<String> lstUfs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //---Inflate the layout for this fragment---
        view = inflater.inflate(R.layout.fragment_cadastro_cliente, container, false);

        initViews();
        initListeners();
        initObjects();

        return view;
    }

    private void initViews() {
        edtxtNome = (EditText) view.findViewById(R.id.edtxtNome);
        edtxtCnpj = (EditText) view.findViewById(R.id.edtxtCnpj);
        edtxtCidade = (EditText) view.findViewById(R.id.edtxtCidade);
        edtxtCep = (EditText) view.findViewById(R.id.edtxtCep);
        edtxtNro = (EditText) view.findViewById(R.id.edtxtNro);
        edtxtNome = (EditText) view.findViewById(R.id.edtxtNome);
        rdgTipoPessoa = (RadioGroup) view.findViewById(R.id.rdgTipoPessoa);
        spnPais = (Spinner) view.findViewById(R.id.spnPais);
        spnUf = (Spinner) view.findViewById(R.id.spnUF);
        btnCadastrar = (Button) view.findViewById(R.id.btnCadastrar);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelar);
        btnGPS = (ImageButton) view.findViewById(R.id.btnGPS);
    }

    private void initListeners() {
        btnCadastrar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        btnGPS.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(getActivity());
        cliente = new Customer();
        locationManager = (LocationManager)
                getContext().getSystemService(Context.LOCATION_SERVICE);

        // O getResources().getStringArray(R.array.array_paises) retorna um String[].
        lstPaises = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.array_paises)));

        lstUfs = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.array_uf)));
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCadastrar:
                insereCliente();
                break;
            case R.id.btnCancelar:
                FragmentManager fm = getFragmentManager();
                fm.popBackStackImmediate();
                break;
            case R.id.btnGPS:
                getLocation();
                break;
        }
    }

    private void insereCliente() {
        String tipo = "";

        // TODO Fazer verificação dos campos, para ver se não são nulos.

        cliente.setNome(edtxtNome.getText().toString().trim());

        if (rdgTipoPessoa.getCheckedRadioButtonId() == R.id.rdbFisica) {
            tipo = "F";
        } else if (rdgTipoPessoa.getCheckedRadioButtonId() == R.id.rdbJuridica) {
            tipo = "J";
        }

        cliente.setTipoPessoa(tipo);
        cliente.setCnpj(edtxtCnpj.getText().toString().trim());
        cliente.setPais(spnPais.getTransitionName());
        cliente.setUf(spnUf.getTransitionName());
        cliente.setCidade(edtxtCidade.getText().toString().trim());
        cliente.setCep(Integer.parseInt(edtxtCep.getText().toString().trim()));
        cliente.setNro(Integer.parseInt(edtxtNro.getText().toString().trim()));

        databaseHelper.addCustomer(cliente);

        // Snack Bar to show success message that record saved successfully
        Snackbar.make(view, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
        emptyInputEditText();

    }

    private void emptyInputEditText() {
        edtxtNome.setText(null);
        edtxtCnpj.setText(null);
        edtxtCidade.setText(null);
        edtxtCep.setText(null);
        edtxtNro.setText(null);
        rdgTipoPessoa.check(R.id.rdbFisica);
        spnPais.setSelection(0);
        spnUf.setSelection(0);
    }

    private void getLocation() {
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, /*10*/0, locationListener);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

        /*------- To get city name from coordinates -------- */
            String cityName="";
            String postalCode="";
            String pais="";
            String uf="";

            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    pais = addresses.get(0).getCountryName();
                    uf = addresses.get(0).getAdminArea();
                    cityName = addresses.get(0).getLocality();
                    postalCode = addresses.get(0).getPostalCode();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            spnPais.setSelection(lstPaises.indexOf(pais));
            spnUf.setSelection(lstUfs.indexOf(uf));
            edtxtCidade.setText(cityName);
            edtxtCep.setText(postalCode);

            locationManager.removeUpdates(locationListener);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}

