package br.com.vendasoffline.vendasoffline.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.util.ArrayList;
import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.activities.ListPedido;
import br.com.vendasoffline.vendasoffline.helpers.InputValidation;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.model.Pedido;
import br.com.vendasoffline.vendasoffline.model.PedidoItem;
import br.com.vendasoffline.vendasoffline.model.Produto;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by lrgabriel on 26/05/17.
 */

public class FragmentCadastroPedido extends Fragment implements View.OnClickListener {
    private View view;
    private DatabaseHelper databaseHelper;
    private TextInputLayout   textInputLytNroPedido;
    private TextInputEditText textInputEdtxtNroPedido;
    private TextInputEditText textInputEdtxtQtdeProduto;
    private TextInputEditText textInputEdtxtPrecoProduto;
    private SearchableSpinner spnClientes;
    private SearchableSpinner spnProdutos;
    private ImageButton btnAdicionar;
    private Button btnSalvarPedido;
    private Button btnCancelar;

    private ArrayList<PedidoItem> itens;
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

        textInputEdtxtNroPedido = (TextInputEditText) view.findViewById(R.id.textInputEdtxtNroPedido);
        textInputEdtxtQtdeProduto = (TextInputEditText) view.findViewById(R.id.textInputEdtxtQtdeProduto);
        textInputEdtxtPrecoProduto = (TextInputEditText) view.findViewById(R.id.textInputEdtxtPrecoProduto);

        spnClientes = (SearchableSpinner) view.findViewById(R.id.spnClientes);
        spnProdutos = (SearchableSpinner) view.findViewById(R.id.spnProdutos);
        btnSalvarPedido = (Button) view.findViewById(R.id.btnSalvarPedido);
        btnAdicionar = (ImageButton) view.findViewById(R.id.btnAdicionar);
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
        itens = new ArrayList<>();
        inputValidation = new InputValidation(getContext());

        setSpinner();

    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSalvarPedido:
                inserePedido();
                break;
            case R.id.btnCancelar:
                FragmentManager fm = getFragmentManager();
                fm.popBackStackImmediate();
                break;
            case R.id.btnAdicionar:
                inserirPedidoItem(true);
                break;
        }
    }

    @Override
    public void onDestroy() {
        ((ListPedido) getActivity()).getPedidos();
        super.onDestroy();
    }

    private void inserePedido() {

        if (!inputValidation.isInputEditTextFilled(textInputEdtxtNroPedido, textInputLytNroPedido, getString(R.string.error_message_name))) {
            textInputEdtxtNroPedido.requestFocus();
            return;
        }

        Customer cliente = (Customer) spnClientes.getSelectedItem();

        if (cliente == null){
            return;
        }

        Pedido pedido = new Pedido();

        pedido.setIdCliente(cliente.getId());
        pedido.setNomeCliente(cliente.getNome());
        pedido.setPedido(Integer.parseInt(textInputEdtxtNroPedido.getText().toString()));

        if (itens.size() > 0){
            double vlrTotal = 0;

            for (PedidoItem itemPed : itens){
                vlrTotal += itemPed.getPreco() * itemPed.getQtde();
            }

            pedido.setValorTotal(vlrTotal);
        }

        long id = databaseHelper.addPedido(pedido);

        inserirPedidoItem(false);

        if (itens.size() > 0){
            for (PedidoItem itemPed : itens){
                itemPed.setIdPedido(id);
            }

            // Adiciona todos produtos da lista no banco de dados.
            databaseHelper.addPedidoItem(itens);
        }

        // Snack Bar to show success message that record saved successfully
        Snackbar.make(view, getString(R.string.success_message), Snackbar.LENGTH_LONG).show();
        emptyInputEditText(true);

    }

    private void emptyInputEditText(boolean todos) {

        spnProdutos.setSelection(0);
        textInputEdtxtPrecoProduto.setText(null);
        textInputEdtxtQtdeProduto.setText(null);

        if (todos){
            textInputEdtxtNroPedido.setText(null);
            spnClientes.setSelection(0);
            textInputEdtxtNroPedido.requestFocus();
        }else {
            textInputEdtxtQtdeProduto.requestFocus();
        }
    }

    private void setSpinner(){
        try {

            Cursor cur = databaseHelper.getClientes(null, null);

            ArrayList<Customer> contacts = new ArrayList<>();

            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()){
                Customer cliente = new Customer();

                cliente.setId(cur.getInt(cur.getColumnIndex("_id")));
                cliente.setNome(cur.getString(cur.getColumnIndex("CLA001_NOME")));
                cliente.setTipoPessoa(cur.getString(cur.getColumnIndex("CLA001_TIPOPESSOA")));
                cliente.setCnpj(cur.getString(cur.getColumnIndex("CLA001_CNPJ")));
                cliente.setPais(cur.getString(cur.getColumnIndex("CLA001_PAIS")));
                cliente.setUf(cur.getString(cur.getColumnIndex("CLA001_UF")));
                cliente.setCidade(cur.getString(cur.getColumnIndex("CLA001_CIDADE")));
                cliente.setCep(cur.getString(cur.getColumnIndex("CLA001_CEP")));
                cliente.setEndereco(cur.getString(cur.getColumnIndex("CLA001_ENDERECO")));
                cliente.setNro(cur.getInt(cur.getColumnIndex("CLA001_NRO")));
                cliente.setSinc(cur.getInt(cur.getColumnIndex("CLA001_SINCRONIZADO")));

                contacts.add(cliente);
            }

            ArrayAdapter<Customer> adapterCliente =
                    new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, contacts);
            adapterCliente.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            spnClientes.setAdapter(adapterCliente);

            spnClientes.setTitle(getResources().getString(R.string.hint_spinner));
            spnClientes.setPositiveButton("Fechar");

            cur = databaseHelper.getProdutos(null,null);

            ArrayList<Produto> produtos = new ArrayList<>();

            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()){
                Produto produto = new Produto();

                produto.setId(cur.getInt(cur.getColumnIndex("_id")));
                produto.setCodigo(cur.getString(cur.getColumnIndex("ESA001_CODIGO")));
                produto.setDescricao(cur.getString(cur.getColumnIndex("ESA001_DESCRICAO")));


                produtos.add(produto);
            }

            ArrayAdapter<Produto> adapterProduto =
                    new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, produtos);
            adapterProduto.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            spnProdutos.setAdapter(adapterProduto);
            spnProdutos.setTitle(getResources().getString(R.string.hint_spinner));
            spnProdutos.setPositiveButton("Fechar");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void inserirPedidoItem(boolean mostraMsg){

        // Só insere o item na lista caso o preço e a quantidade não sejam nulos.
        if (textInputEdtxtPrecoProduto.length() > 0 && textInputEdtxtQtdeProduto.length() > 0){
            Produto produto = (Produto) spnProdutos.getSelectedItem();

            if (produto == null){
                return;
            }

            PedidoItem pedItem = new PedidoItem();

            pedItem.setIdProduto(produto.getId());
            pedItem.setPreco(Double.parseDouble(textInputEdtxtPrecoProduto.getText().toString()));
            pedItem.setQtde(Double.parseDouble(textInputEdtxtQtdeProduto.getText().toString()));

            itens.add(pedItem);

            if (mostraMsg){
                Snackbar.make(view, getString(R.string.pedido_item_inserido), Snackbar.LENGTH_LONG).show();
                emptyInputEditText(false);
            }
        }
    }

}