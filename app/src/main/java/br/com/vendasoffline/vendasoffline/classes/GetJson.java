package br.com.vendasoffline.vendasoffline.classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;
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

            setJsonCliente();
            setJsonPedido();
            setJsonPedidoItem();
        }

        return "Sincronização Concluída!";
    }

    @Override
    protected void onPostExecute(String s){
        if (activity != null){
            load.dismiss();
        }
    }

    private void setJsonCliente(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String whereClause = "CLA001_SINCRONIZADO = ?";
        String [] whereArgs = new String[] {String.format(Locale.getDefault(),"%d",0)};

        Cursor cur = databaseHelper.getCustomer(whereClause,whereArgs);

        JSONArray jsArray = new JSONArray();
        JSONObject jsResult = new JSONObject();

        try {
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()){
                JSONObject jsGroup = new JSONObject();
                long id = cur.getInt(cur.getColumnIndex("_id"));

                jsGroup.put("ID", id);
                jsGroup.put("Nome", cur.getString(cur.getColumnIndex("CLA001_NOME")));
                jsGroup.put("TipoPessoa", cur.getString(cur.getColumnIndex("CLA001_TIPOPESSOA")));
                jsGroup.put("CNPJ", cur.getString(cur.getColumnIndex("CLA001_CNPJ")));
                jsGroup.put("Pais", cur.getString(cur.getColumnIndex("CLA001_PAIS")));
                jsGroup.put("UF", cur.getString(cur.getColumnIndex("CLA001_UF")));
                jsGroup.put("Cidade", cur.getString(cur.getColumnIndex("CLA001_CIDADE")));
                jsGroup.put("CEP", cur.getString(cur.getColumnIndex("CLA001_CEP")));
                jsGroup.put("Endereco", cur.getString(cur.getColumnIndex("CLA001_ENDERECO")));
                jsGroup.put("Numero", cur.getInt(cur.getColumnIndex("CLA001_NRO")));

                JSONObject jsOuter = new JSONObject();

                jsOuter.put("Cliente", jsGroup);

                jsArray.put(jsOuter);

                databaseHelper.altCustomer(id);
            }

            jsResult.put("Clientes", jsArray);

            // Aqui seria chamado a classe NetworkUtils, onde seria mandando o arquivo JSON atravéz do método POST, para o webservice, para que os clientes
            // fossem inseridos, porém a empresa não disponibiliza nenhum webservice para que possamos fazer isso.
            // Porém foi feito essa simulação de como seria caso tivesse um webservice para mandar os dados.

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setJsonPedido(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        Cursor cur = databaseHelper.getPedidos(null,null);

        JSONArray jsArray = new JSONArray();
        JSONObject jsResult = new JSONObject();

        try {
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()){
                JSONObject jsGroup = new JSONObject();
                long id = cur.getInt(cur.getColumnIndex("_id"));

                jsGroup.put("ID", id);
                jsGroup.put("TipoDoc", "PEDWEB");
                jsGroup.put("NroPedido", cur.getString(cur.getColumnIndex("PEA001_PEDIDO")));
                jsGroup.put("Cliente", cur.getString(cur.getColumnIndex("PEA001_CLIENTE")));
                jsGroup.put("ValorTotal", cur.getString(cur.getColumnIndex("PEA001_VALORTOTAL")));

                JSONObject jsOuter = new JSONObject();

                jsOuter.put("Pedido", jsGroup);

                jsArray.put(jsOuter);

            }

            jsResult.put("Pedidos", jsArray);

            // Aqui seria chamado a classe NetworkUtils, onde seria mandando o arquivo JSON atravéz do método POST, para o webservice, para que os pedidos
            // fossem inseridos, porém a empresa não disponibiliza nenhum webservice para que possamos fazer isso.
            // Porém foi feito essa simulação de como seria caso tivesse um webservice para mandar os dados.

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setJsonPedidoItem(){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        String whereClause = "PEB001_ESA001_ID = ESA001_ID";

        Cursor cur = databaseHelper.getPedidoItens(whereClause,null);

        JSONArray jsArray = new JSONArray();
        JSONObject jsResult = new JSONObject();

        try {
            for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()){
                JSONObject jsGroup = new JSONObject();

                jsGroup.put("ID", cur.getInt(cur.getColumnIndex("_id")));
                jsGroup.put("IDPedido", cur.getInt(cur.getColumnIndex("PEB001_PEA001_ID")));
                jsGroup.put("Produto", cur.getString(cur.getColumnIndex("ESA001_CODIGO")));
                jsGroup.put("Descricao", cur.getString(cur.getColumnIndex("ESA001_DESCRICAO")));
                jsGroup.put("Quantidade", cur.getString(cur.getColumnIndex("PEB001_QTDESOL")));
                jsGroup.put("Preco", cur.getString(cur.getColumnIndex("PEB001_PRECO")));

                JSONObject jsOuter = new JSONObject();

                jsOuter.put("Item", jsGroup);

                jsArray.put(jsOuter);
            }

            jsResult.put("Itens", jsArray);

            // Aqui seria chamado a classe NetworkUtils, onde seria mandando o arquivo JSON atravéz do método POST, para o webservice, para que os itens dos pedidos
            // fossem inseridos, porém a empresa não disponibiliza nenhum webservice para que possamos fazer isso.
            // Porém foi feito essa simulação de como seria caso tivesse um webservice para mandar os dados.

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
