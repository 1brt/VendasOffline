package br.com.vendasoffline.vendasoffline.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.activities.ListCliente;
import br.com.vendasoffline.vendasoffline.activities.ListPedido;
import br.com.vendasoffline.vendasoffline.classes.Permission;
import br.com.vendasoffline.vendasoffline.helpers.InputValidation;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by lrgabriel on 26/05/17.
 */

public class FragmentCadastroPedido extends Fragment implements View.OnClickListener {
    private final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private View view;
    private TextInputLayout textInputLytNome;
    private TextInputLayout textInputLytCnpj;
    /*private TextInputLayout textInputLytCidade;
    private TextInputLayout textInputLytCep;
    private TextInputLayout textInputLytNro;
    private TextInputLayout textInputLytEndereco;*/

    private TextInputEditText textInputEdtxtNome;
    private TextInputEditText textInputEdtxtCnpj;
    private TextInputEditText textInputEdtxtCidade;
    private TextInputEditText textInputEdtxtCep;
    private TextInputEditText textInputEdtxtNro;
    private TextInputEditText textInputEdtxtEndereco;
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
    private List<String> lstUfsAbrev;
    private SharedPreferences prefs;
    private InputValidation inputValidation;
    private ProgressDialog load;
    private Permission permis;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //---Inflate the layout for this fragment---
        view = inflater.inflate(R.layout.fragment_cadastro_pedido, container, false);

        initViews();
        initListeners();
        initObjects();

        return view;
    }

    private void initViews() {

        textInputLytNome = (TextInputLayout) view.findViewById(R.id.textInputLytNome);
        textInputLytCnpj = (TextInputLayout) view.findViewById(R.id.textInputLytCnpj);
        textInputEdtxtNome = (TextInputEditText) view.findViewById(R.id.textInputEdtxtNome);
        textInputEdtxtNome.requestFocus();
        textInputEdtxtCnpj = (TextInputEditText) view.findViewById(R.id.textInputEdtxtCnpj);
        textInputEdtxtCidade = (TextInputEditText) view.findViewById(R.id.textInputEdtxtCidade);
        textInputEdtxtCep = (TextInputEditText) view.findViewById(R.id.textInputEdtxtCep);
        textInputEdtxtNro = (TextInputEditText) view.findViewById(R.id.textInputEdtxtNro);
        textInputEdtxtEndereco = (TextInputEditText) view.findViewById(R.id.textInputEdtxtEndereco);
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
        lstPaises = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_paises)));

        lstUfs = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_uf)));

        lstUfsAbrev = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_uf_abrev)));

        prefs = getContext().getSharedPreferences("br.com.vendasoffline.vendasoffline", MODE_PRIVATE);

        locationListener = new MyLocationListener();

        inputValidation = new InputValidation(getContext());

        handler = new Handler();

        permis = new Permission(getActivity(),prefs);

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

    @Override
    public void onDestroy() {
        ((ListPedido) getActivity()).getPedidos();
        super.onDestroy();
    }

    private void insereCliente() {
        String tipo = "";
        if (!inputValidation.isInputEditTextFilled(textInputEdtxtNome, textInputLytNome, getString(R.string.error_message_name))) {
            textInputEdtxtNome.requestFocus();
            return;
        }else if (!inputValidation.isInputEditTextFilled(textInputEdtxtCnpj, textInputLytCnpj, getString(R.string.error_message_name))) {
            textInputEdtxtCnpj.requestFocus();
            return;
        }

        cliente.setNome(textInputEdtxtNome.getText().toString().trim());

        if (rdgTipoPessoa.getCheckedRadioButtonId() == R.id.rdbFisica) {
            tipo = "Física";
        } else if (rdgTipoPessoa.getCheckedRadioButtonId() == R.id.rdbJuridica) {
            tipo = "Jurídica";
        }

        // TODO: Testar se os campos não não nulos, para não dar pau no toString.
        cliente.setTipoPessoa(tipo);
        cliente.setCnpj(textInputEdtxtCnpj.getText().toString().trim());
        cliente.setPais((String) spnPais.getSelectedItem());
        cliente.setUf(lstUfsAbrev.get(spnUf.getSelectedItemPosition()));
        cliente.setCidade(textInputEdtxtCidade.getText().toString().trim());
        cliente.setCep(textInputEdtxtCep.getText().toString().trim());
        cliente.setNro(Integer.parseInt(textInputEdtxtNro.getText().toString().trim()));
        cliente.setEndereco(textInputEdtxtEndereco.getText().toString().trim());

        databaseHelper.addCustomer(cliente);

        // Snack Bar to show success message that record saved successfully
        Snackbar.make(view, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
        emptyInputEditText();

    }

    private void emptyInputEditText() {
        textInputEdtxtNome.setText(null);
        textInputEdtxtCnpj.setText(null);
        textInputEdtxtCidade.setText(null);
        textInputEdtxtCep.setText(null);
        textInputEdtxtNro.setText(null);
        textInputEdtxtEndereco.setText(null);
        rdgTipoPessoa.check(R.id.rdbFisica);
        spnPais.setSelection(0);
        spnUf.setSelection(0);
        textInputEdtxtNome.requestFocus();
    }

    private void getLocation() {

        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            permis.requestLocationPermissions();

            return;
        }

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            buildAlertMessageNoGps();

            return;
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                load = ProgressDialog.show(getContext(), "Por favor Aguarde ...", "Recuperando Informações do GPS...");
            }
        });

        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener,null);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

        //------- To get city name from coordinates --------
            String cityName="";
            String postalCode="";
            String pais="";
            String uf="";
            String endereco="";

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
                    endereco = addresses.get(0).getAddressLine(0).toString();
                    if (endereco.contains(",")){
                        endereco = endereco.substring(0,endereco.indexOf(","));
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            spnPais.setSelection(lstPaises.indexOf(pais));
            spnUf.setSelection(lstUfs.indexOf(uf));
            textInputEdtxtCidade.setText(cityName);
            textInputEdtxtEndereco.setText(endereco);
            textInputEdtxtCep.setText(postalCode);

            load.dismiss();
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("O GPS do dispositivo parece estar desabilitado, deseja abilita-lo?")
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void removeUpdates(){
        locationManager.removeUpdates(locationListener);
    }
}