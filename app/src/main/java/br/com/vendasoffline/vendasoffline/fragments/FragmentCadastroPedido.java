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
    private View view;
    private DatabaseHelper databaseHelper;
    private TextInputLayout   textInputLytNroPedido;
    private TextInputLayout   textInputLytQtdeProduto;
    private TextInputLayout   textInputLytPrecoProduto;
    private TextInputEditText textInputEdtxtNroPedido;
    private TextInputEditText textInputEdtxtQtdeProduto;
    private TextInputEditText textInputEdtxtPrecoProduto;
    private Spinner spnClientes;
    private Spinner spnProdutos;
    private Button btnAdicionar;
    private Button btnSalvarPedido;
    private Button btnCancelar;

    private Customer cliente;
    private List<String> lstClientes;
    private List<String> lstProdutos;
    private InputValidation inputValidation;

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

        textInputLytNroPedido = (TextInputLayout) view.findViewById(R.id.textInputLytNroPedido);
        textInputLytQtdeProduto = (TextInputLayout) view.findViewById(R.id.textInputLytQtdeProduto);
        textInputLytPrecoProduto = (TextInputLayout) view.findViewById(R.id.textInputLytPrecoProduto);

        textInputEdtxtNroPedido = (TextInputEditText) view.findViewById(R.id.textInputEdtxtNroPedido);
        textInputEdtxtQtdeProduto = (TextInputEditText) view.findViewById(R.id.textInputEdtxtQtdeProduto);
        textInputEdtxtPrecoProduto = (TextInputEditText) view.findViewById(R.id.textInputEdtxtPrecoProduto);

        spnClientes = (Spinner) view.findViewById(R.id.spnClientes);
        spnProdutos = (Spinner) view.findViewById(R.id.spnProdutos);
        btnSalvarPedido = (Button) view.findViewById(R.id.btnSalvarPedido);
        btnAdicionar = (Button) view.findViewById(R.id.btnAdicionar);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelar);

    }

    private void initListeners() {
        btnAdicionar.setOnClickListener(this);
        btnSalvarPedido.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(getActivity());
        cliente = new Customer();

        // O getResources().getStringArray(R.array.array_paises) retorna um String[].
        /*
        lstPaises = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_paises)));

        lstUfs = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_uf)));

        lstUfsAbrev = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_uf_abrev)));
        */
        inputValidation = new InputValidation(getContext());

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
        }
    }

    @Override
    public void onDestroy() {
        ((ListPedido) getActivity()).getPedidos();
        super.onDestroy();
    }

    private void insereCliente() {
        /*
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
        */
    }

    private void emptyInputEditText() {
        textInputEdtxtNroPedido.setText(null);
        textInputEdtxtPrecoProduto.setText(null);
        textInputEdtxtQtdeProduto.setText(null);
        /*
        spnPais.setSelection(0);
        spnUf.setSelection(0);
        textInputEdtxtNome.requestFocus();
        */
    }

}