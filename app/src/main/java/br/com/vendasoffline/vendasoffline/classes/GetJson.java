package br.com.vendasoffline.vendasoffline.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Locale;

import br.com.vendasoffline.vendasoffline.activities.MainActivity;
import br.com.vendasoffline.vendasoffline.helpers.NetworkChangeReceiver;
import br.com.vendasoffline.vendasoffline.helpers.Utils;
import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.model.Produto;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by home on 26/06/17.
 */

public class GetJson extends AsyncTask<Void, Void, String> {

    private Activity activity;
    private Context context;
    private ProgressDialog load;

    public GetJson(Activity activity) {
        this.activity = activity;
    }

    public GetJson(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        if (activity != null){
            load = ProgressDialog.show(activity, "Por favor Aguarde ...", "Recuperando Informações do Servidor...");
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        ArrayList<Customer> customerList = null;
        ArrayList<Produto> productList = null;

        // Feito isso pois quando é executado pelo broadcast receiver, vem o contexto e não a atividade.
        // E quando é executado direto pelo programa, vem a atividade, para que a animação do load, apareça na tela.
        if (context == null){
            context = activity;
        }

        if (new NetworkChangeReceiver().isOnline(context)){

            //c = util.getInformacao("https://randomuser.me/api/");
            customerList = new Utils("Cliente").getInformacao("http://web.effectiveerp.com.br:88/teste/ECommerce/ErpRestService.svc/AndroidListarClientes/2");
            (new DatabaseHelper(context)).addCustomer(customerList);

            productList = new Utils("Produto").getInformacao("http://web.effectiveerp.com.br:88/teste/ECommerce/ErpRestService.svc/AndroidListarProdutos/2");
            (new DatabaseHelper(context)).addProduct(productList);

            setJson();
        }

        return "Sincronização Concluída!";
    }

    @Override
    protected void onPostExecute(String s){
        if (activity != null){
            load.dismiss();
        }
    }

    private void setJson(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String whereClause = "CLA001_SINCRONIZADO = ?";
        String [] whereArgs = new String[] {String.format(Locale.getDefault(),"%d",0)};

        Cursor cur = databaseHelper.getClientes(whereClause,whereArgs);

        ArrayList<Customer> clientes = new ArrayList<>();

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

            clientes.add(cliente);
        }

        JSONArray jsArray = new JSONArray(clientes);
        JSONObject jsObject = new JSONObject();

        // populate the array
        try {
            jsObject.put("Clientes",jsArray);
            String xml = XML.toString(jsObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
