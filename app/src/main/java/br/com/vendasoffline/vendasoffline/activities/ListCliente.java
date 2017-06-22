package br.com.vendasoffline.vendasoffline.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

/**
 * Created by lrgabriel on 21/06/17.
 */

public class ListCliente extends AppCompatActivity {

    private ListView clienteListView;
    private CursorAdapter clienteAdapter; // Adaptador para a ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        getSupportActionBar().hide();

        clienteListView = (ListView) findViewById(R.id.listView);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"CLA001_NOME","CLA001_TIPOPESSOA","CLA001_CNPJ","CLA001_CIDADE","CLA001_UF"};
        int[] destino = new int[] { R.id.txtNome, R.id.txtTipoPessoa, R.id.txtCPF,R.id.txtCidade,R.id.txtUf};
        int flags = 0;

        clienteAdapter = new SimpleCursorAdapter(ListCliente.this,R.layout.activity_view_cliente,null,origem,destino,flags);
        clienteListView.setAdapter(clienteAdapter);

    }

    @Override
    protected void onResume(){
        //sempre que executar onResume, irá fazer uma busca no banco de dados
        //e vai atualizar a tela de exibição da pontuação
        super.onResume();
        new getClientes().execute();
    }

    private class getClientes extends AsyncTask<Object, Object, Cursor> {
        DatabaseHelper dbConnection = new DatabaseHelper(ListCliente.this);
        @Override
        protected Cursor doInBackground(Object... params){
            return dbConnection.getClientes(); //retorna a Pontuação
        }
        // usa o cursor retornado pelo doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            try {
                clienteAdapter.changeCursor(result); //altera o cursor para um novo cursor
                dbConnection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
