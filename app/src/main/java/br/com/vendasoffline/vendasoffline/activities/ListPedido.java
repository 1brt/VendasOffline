package br.com.vendasoffline.vendasoffline.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import br.com.vendasoffline.vendasoffline.R;
import br.com.vendasoffline.vendasoffline.fragments.FragmentCadastroPedido;
import br.com.vendasoffline.vendasoffline.sql.DatabaseHelper;

public class ListPedido extends AppCompatActivity {

    private ListView pedidoListView;
    private EditText pesquisar;
    private CursorAdapter PedidoAdapter; // Adaptador para a ListView
    private String whereClause="";
    private String[] whereArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        getSupportActionBar().hide();

        pedidoListView = (ListView) findViewById(R.id.listViewPedidos);
        pedidoListView.setOnItemClickListener(ListPedidosListener);
        pesquisar = (EditText) findViewById(R.id.edtConsulta);

        // mapeia cada coluna da tabela com um componente da tela
        String[] origem = new String[]{"PEA001_PEDIDO","PEA001_CLIENTE","PEA001_VALORTOTAL"};
        int[] destino = new int[] { R.id.txtPedido, R.id.txtCliente, R.id.txtValorTotal};
        int flags = 0;

        PedidoAdapter = new SimpleCursorAdapter(ListPedido.this,R.layout.activity_view_pedido,null,origem,destino,flags);
        pedidoListView.setAdapter(PedidoAdapter);

        registerForContextMenu(pedidoListView);

        FloatingActionButton fabPedido = (FloatingActionButton) findViewById(R.id.fabPedido);
        fabPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentCadastroPedido();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.drawer_layout_pedido, fragment, fragment.getClass().getSimpleName()).addToBackStack("cadastroPedido").commit();
            }
        });

        pesquisar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                getPedidos();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override
    protected void onStart(){
        //sempre que executar onResume, irá fazer uma busca no banco de dados
        super.onStart();
        getPedidos();
    }

    AdapterView.OnItemClickListener ListPedidosListener = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent, View view, int posicao,long id){
            Intent viewProdutos = new Intent(getApplicationContext(), ListProduto.class);
            viewProdutos.putExtra("idPedido", id);
            startActivity(viewProdutos);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listViewPedidos) {
            // Utilizado para vibrar o celular.
            Vibrator vibe = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE) ;
            vibe.vibrate(50);

            String[] menuItems = new String[] {"Excluir"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();

        if (menuItemIndex == 0){  // Excluir
            new DatabaseHelper(getBaseContext()).delPedido(info.id);
            new getPedidos().execute();
        }

        return true;
    }

    private class getPedidos extends AsyncTask<Object, Object, Cursor> {
        DatabaseHelper dbConnection = new DatabaseHelper(ListPedido.this);
        @Override
        protected Cursor doInBackground(Object... params){
            return dbConnection.getPedidos(whereClause,whereArgs); //retorna a Pontuação
        }
        // usa o cursor retornado pelo doInBackground
        @Override
        protected void onPostExecute(Cursor result){
            PedidoAdapter.changeCursor(result); //altera o cursor para um novo cursor
            dbConnection.close();
        }
    }

    public void getPedidos(){
        whereClause = "PEA001_PEDIDO LIKE ?";
        whereArgs = new String[] {"%"+pesquisar.getText().toString()+"%"};
        new getPedidos().execute();
    }
}
