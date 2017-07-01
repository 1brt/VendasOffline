package br.com.vendasoffline.vendasoffline.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

public class ListProduto extends AppCompatActivity {

    private ListView produtoListView;
    private EditText pesquisar;
    private CursorAdapter ProdutoAdapter; // Adaptador para a ListView
    private String whereClause="";
    private String[] whereArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produto);

        produtoListView = (ListView) findViewById(R.id.listViewPedidos);
        pesquisar = (EditText) findViewById(R.id.edtConsulta);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"ESA001_PRODUTO","ESA001_DESCRICAO"};
        int[] destino = new int[] { R.id.txtProd, R.id.txtProdDescr};
        int flags = 0;

        ProdutoAdapter = new SimpleCursorAdapter(ListProduto.this,R.layout.activity_view_pedido,null,origem,destino,flags);
        produtoListView.setAdapter(ProdutoAdapter);

        pesquisar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getProdutos();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    @Override
    protected void onStart(){
        //sempre que executar onResume, irá fazer uma busca no banco de dados
        super.onStart();
        getProdutos();
    }

    private class getProdutos extends AsyncTask<Object, Object, Cursor> {
        DatabaseHelper dbConnection = new DatabaseHelper(ListProduto.this);
        @Override
        protected Cursor doInBackground(Object... params){
            return dbConnection.getProdutos(whereClause,whereArgs); //retorna a Pontuação
        }
        // usa o cursor retornado pelo doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            try {
                ProdutoAdapter.changeCursor(result); //altera o cursor para um novo cursor
                dbConnection.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void getProdutos(){
        whereClause = "ESA001_PRODUTO LIKE ?";
        whereArgs = new String[] {"%"+pesquisar.getText().toString()+"%"};
        new ListProduto.getProdutos().execute();
    }
}
