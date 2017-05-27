package br.com.vendasoffline.vendasoffline.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.activities.RegisterActivity;
import br.com.vendasoffline.vendasoffline.helpers.InputValidation;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by lrgabriel on 26/05/17.
 */

public class FragmentCadastroCliente extends Fragment implements View.OnClickListener{

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
    private Customer cliente;
    private DatabaseHelper databaseHelper;

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
    }

    private void initListeners() {
        btnCadastrar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        databaseHelper = new DatabaseHelper(getActivity());
        cliente = new Customer();
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

    private void insereCliente(){
        String tipo="";

        cliente.setNome(edtxtNome.getText().toString().trim());

        if (rdgTipoPessoa.getCheckedRadioButtonId() == R.id.rdbFisica){
            tipo = "F";
        }else if (rdgTipoPessoa.getCheckedRadioButtonId() == R.id.rdbJuridica){
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
}

