package br.com.vendasoffline.vendasoffline.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.vendasoffline.vendasoffline.model.Customer;
import br.com.vendasoffline.vendasoffline.model.Pedido;
import br.com.vendasoffline.vendasoffline.model.PedidoItem;
import br.com.vendasoffline.vendasoffline.model.Produto;
import br.com.vendasoffline.vendasoffline.model.User;

/**
 * Created by lrgabriel on 24/05/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "VendasOffline.db";

    // User table name
    private static final String TABLE_USER = "CQTBA013";
    // Customer table name
    private static final String TABLE_CUSTOMER = "CLTBA001";
    // Product table name
    private static final String TABLE_PRODUCT = "ESTBA001";

    // Pedido table name
    private static final String TABLE_PEDIDO = "PETBA001";

    // Pedido Itens table name
    private static final String TABLE_PEDIDO_ITEM = "PETBB001";

    // User Table Columns names
    private static final String COLUMN_USER_ID = "CQA013_ID";
    private static final String COLUMN_USER_NAME = "CQA013_NOME";
    private static final String COLUMN_USER = "CQA013_USUARIO";
    private static final String COLUMN_USER_EMAIL = "CQA013_EMAIL";
    private static final String COLUMN_USER_PASSWORD = "CQA013_SENHA";

    // Customer Table Columns names
    private static final String COLUMN_CUSTOMER_ID = "CLA001_ID";
    private static final String COLUMN_CUSTOMER_NOME = "CLA001_NOME";
    private static final String COLUMN_CUSTOMER_TIPOPESSOA = "CLA001_TIPOPESSOA";
    private static final String COLUMN_CUSTOMER_CNPJ = "CLA001_CNPJ";
    private static final String COLUMN_CUSTOMER_PAIS = "CLA001_PAIS";
    private static final String COLUMN_CUSTOMER_UF = "CLA001_UF";
    private static final String COLUMN_CUSTOMER_CIDADE = "CLA001_CIDADE";
    private static final String COLUMN_CUSTOMER_CEP = "CLA001_CEP";
    private static final String COLUMN_CUSTOMER_NRO = "CLA001_NRO";
    private static final String COLUMN_CUSTOMER_ENDERECO = "CLA001_ENDERECO";
    private static final String COLUMN_CUSTOMER_SINC = "CLA001_SINCRONIZADO";

    // Product Table Columns names
    private static final String COLUMN_PRODUCT_ID = "ESA001_ID";
    private static final String COLUMN_PRODUCT_CODIGO = "ESA001_CODIGO";
    private static final String COLUMN_PRODUCT_DESCRICAO = "ESA001_DESCRICAO";

    // Pedido Table Columns names
    private static final String COLUMN_PEDIDO_ID = "PEA001_ID";
    private static final String COLUMN_PEDIDO_IDCLIENTE = "PEA001_CLA001_ID";
    private static final String COLUMN_PEDIDO_NOMECLIENTE = "PEA001_CLIENTE";
    private static final String COLUMN_PEDIDO_PEDIDO = "PEA001_PEDIDO";
    private static final String COLUMN_PEDIDO_VALORTOTAL = "PEA001_VALORTOTAL";

    // Pedido Itens Table Columns names
    private static final String COLUMN_PEDIDO_ITEM_ID = "PEB001_ID";
    private static final String COLUMN_PEDIDO_ITEM_PEDIDO = "PEB001_PEA001_ID";
    private static final String COLUMN_PEDIDO_ITEM_PRODUTO = "PEB001_ESA001_ID";
    private static final String COLUMN_PEDIDO_ITEM_QTDE = "PEB001_QTDESOL";
    private static final String COLUMN_PEDIDO_ITEM_PRECO = "PEB001_PRECO";

    // create User table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER+ " TEXT UNIQUE, " + COLUMN_USER_EMAIL + " TEXT UNIQUE, " + COLUMN_USER_PASSWORD + " TEXT" + ")";

    // create Customer table sql query
    private String CREATE_CUSTOMER_TABLE = "CREATE TABLE "+TABLE_CUSTOMER+" (" +
            COLUMN_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CUSTOMER_NOME + " TEXT NOT NULL," +
            COLUMN_CUSTOMER_TIPOPESSOA + " TEXT NOT NULL," +
            COLUMN_CUSTOMER_CNPJ + " TEXT NOT NULL UNIQUE," +
            COLUMN_CUSTOMER_PAIS + " TEXT," +
            COLUMN_CUSTOMER_UF + " TEXT," +
            COLUMN_CUSTOMER_CIDADE + " TEXT," +
            COLUMN_CUSTOMER_CEP + " TEXT," +
            COLUMN_CUSTOMER_NRO + " INTEGER," +
            COLUMN_CUSTOMER_ENDERECO + " TEXT," +
            COLUMN_CUSTOMER_SINC + " INTEGER DEFAULT 0)";

    private String CREATE_PRODUCT_TABLE = "CREATE TABLE "+TABLE_PRODUCT+" (" +
            COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PRODUCT_CODIGO + " TEXT NOT NULL," +
            COLUMN_PRODUCT_DESCRICAO + " TEXT NOT NULL)";

    private String CREATE_PEDIDO_TABLE = "CREATE TABLE "+TABLE_PEDIDO+" (" +
            COLUMN_PEDIDO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PEDIDO_IDCLIENTE + " INTEGER NOT NULL," +
            COLUMN_PEDIDO_NOMECLIENTE + " TEXT NOT NULL," +
            COLUMN_PEDIDO_PEDIDO + " INTEGER NOT NULL," +
            COLUMN_PEDIDO_VALORTOTAL + " INTEGER DEFAULT 0, FOREIGN KEY (" +
            COLUMN_PEDIDO_IDCLIENTE +") REFERENCES " +
            TABLE_CUSTOMER +" (" + COLUMN_CUSTOMER_ID + "))";

    private String CREATE_PEDIDO_ITEM_TABLE = "CREATE TABLE "+TABLE_PEDIDO_ITEM+" (" +
            COLUMN_PEDIDO_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PEDIDO_ITEM_PEDIDO + " INTEGER NOT NULL," +
            COLUMN_PEDIDO_ITEM_PRODUTO + " INTEGER NOT NULL," +
            COLUMN_PEDIDO_ITEM_QTDE + " INTEGER,"+
            COLUMN_PEDIDO_ITEM_PRECO + " INTEGER, FOREIGN KEY (" +
            COLUMN_PEDIDO_ITEM_PEDIDO +") REFERENCES " +
            TABLE_PEDIDO +" (" + COLUMN_PEDIDO_ID + "), FOREIGN KEY (" +
            COLUMN_PEDIDO_ITEM_PRODUTO +") REFERENCES " +
            TABLE_PRODUCT +" (" + COLUMN_PRODUCT_ID + "))";
    
    // drop User table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    // drop Customer table sql query
    private String DROP_CUSTOMER_TABLE = "DROP TABLE IF EXISTS " + TABLE_CUSTOMER;

    // drop Product table sql query
    private String DROP_PRODUCT_TABLE = "DROP TABLE IF EXISTS " + TABLE_PRODUCT;

    // drop Product table sql query
    private String DROP_PEDIDO_TABLE = "DROP TABLE IF EXISTS " + TABLE_PEDIDO;

    // drop Product Item table sql query
    private String DROP_PEDIDO_ITEM_TABLE = "DROP TABLE IF EXISTS " + TABLE_PEDIDO_ITEM;
    
    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {//força integridade referencial
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CUSTOMER_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_PEDIDO_TABLE);
        db.execSQL(CREATE_PEDIDO_ITEM_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_CUSTOMER_TABLE);
        db.execSQL(DROP_PRODUCT_TABLE);
        db.execSQL(DROP_PEDIDO_TABLE);
        db.execSQL(DROP_PEDIDO_ITEM_TABLE);

        // Create tables again
        onCreate(db);

    }

    /**
     * This method is to create user record
     *
     * @param user
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getNome());
        values.put(COLUMN_USER,user.getUsuario());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getSenha());

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return User
     */
    public User getUser(String usuario){
        User user = new User();

        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER,
                COLUMN_USER_EMAIL,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD
        };
        String whereClause = COLUMN_USER+" = ?";
        String[] whereArgs = {usuario};

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                whereClause,        //columns for the WHERE clause
                whereArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                null); //The sort order

        if (cursor.moveToFirst()) {
            user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
            user.setNome(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
            user.setUsuario(cursor.getString(cursor.getColumnIndex(COLUMN_USER)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
            user.setSenha(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
        }
        cursor.close();
        db.close();

        return user;

    }

    /**
     * This method to check user exist or not
     *
     * @param usuario
     * @return true/false
     */
    public boolean checkUser(String usuario) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        String selection;

        if (usuario.contains("@")){
            // selection criteria
            selection = COLUMN_USER_EMAIL + " = ?";
        }else{
            selection = COLUMN_USER + " = ?";
        }

        // selection argument
        String[] selectionArgs = {usuario};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param usuario
     * @param password
     * @return true/false
     */
    public boolean checkUser(String usuario, String password) {

        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        String selection;

        if (usuario.contains("@")){
            selection = COLUMN_USER_EMAIL + " = ?" ;
        }else {
            selection = COLUMN_USER + " = ?";
        }
        // selection criteria
        selection = selection + " AND " + COLUMN_USER_PASSWORD + " = ?";

        // selection arguments
        String[] selectionArgs = {usuario, password};

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        int cursorCount = cursor.getCount();

        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    public Cursor getClientes(String whereClause,String[] whereArgs) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_CUSTOMER_ID+" as _id",
                COLUMN_CUSTOMER_NOME,
                COLUMN_CUSTOMER_TIPOPESSOA,
                COLUMN_CUSTOMER_CNPJ,
                COLUMN_CUSTOMER_CIDADE,
                COLUMN_CUSTOMER_PAIS,
                COLUMN_CUSTOMER_UF,
                COLUMN_CUSTOMER_CEP,
                COLUMN_CUSTOMER_ENDERECO,
                COLUMN_CUSTOMER_NRO,
                COLUMN_CUSTOMER_SINC,
        };

        // sorting orders
        String sortOrder =
                COLUMN_CUSTOMER_NOME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */

        Cursor cursor = db.query(TABLE_CUSTOMER, //Table to query
                columns,     //columns to return
                whereClause, //columns for the WHERE clause
                whereArgs,   //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        return cursor;
    }

    public void addCustomer(Customer cliente){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NOME, cliente.getNome());
        values.put(COLUMN_CUSTOMER_TIPOPESSOA,cliente.getTipoPessoa());
        values.put(COLUMN_CUSTOMER_CNPJ, cliente.getCnpj());
        values.put(COLUMN_CUSTOMER_PAIS, cliente.getPais());
        values.put(COLUMN_CUSTOMER_UF, cliente.getUf());
        values.put(COLUMN_CUSTOMER_CIDADE, cliente.getCidade());
        values.put(COLUMN_CUSTOMER_CEP, cliente.getCep());
        values.put(COLUMN_CUSTOMER_NRO, cliente.getNro());
        values.put(COLUMN_CUSTOMER_ENDERECO, cliente.getEndereco());

        // Inserting Row
        db.insert(TABLE_CUSTOMER, null, values);
        db.close();
    }

    public void addCustomer(ArrayList<Customer> clientes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (Customer cli : clientes){

            values.put(COLUMN_CUSTOMER_NOME, cli.getNome());
            values.put(COLUMN_CUSTOMER_TIPOPESSOA,cli.getTipoPessoa());
            values.put(COLUMN_CUSTOMER_CNPJ, cli.getCnpj());
            values.put(COLUMN_CUSTOMER_PAIS, cli.getPais());
            values.put(COLUMN_CUSTOMER_UF, cli.getUf());
            values.put(COLUMN_CUSTOMER_CIDADE, cli.getCidade());
            values.put(COLUMN_CUSTOMER_CEP, cli.getCep());
            values.put(COLUMN_CUSTOMER_NRO, cli.getNro());
            values.put(COLUMN_CUSTOMER_ENDERECO, cli.getEndereco());

            // Inserting Row
            db.insert(TABLE_CUSTOMER, null, values);

            values.clear();
        }

        db.close();
    }

    public void addProduct(Produto produto){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_CODIGO, produto.getCodigo());
        values.put(COLUMN_PRODUCT_DESCRICAO,produto.getDescricao());

        // Inserting Row
        db.insert(TABLE_PRODUCT, null, values);
        db.close();
    }

    public void addProduct(ArrayList<Produto> produtos){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        for (Produto prod : produtos){

            values.put(COLUMN_PRODUCT_CODIGO, prod.getCodigo());
            values.put(COLUMN_PRODUCT_DESCRICAO,prod.getDescricao());

            // Inserting Row
            db.insert(TABLE_PRODUCT, null, values);

            values.clear();
        }

        db.close();
    }

    public Cursor getProdutos(String whereClause,String[] whereArgs) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PRODUCT_ID+" as _id",
                COLUMN_PRODUCT_CODIGO,
                COLUMN_PRODUCT_DESCRICAO
        };

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         **/

         Cursor cursor = db.query(TABLE_PRODUCT, //Table to query
         columns,     //columns to return
         whereClause, //columns for the WHERE clause
         whereArgs,   //The values for the WHERE clause
         null,       //group the rows
         null,       //filter by row groups
         null); //The sort order

         return cursor;
    }
    
    public long addPedido(Pedido pedido){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PEDIDO_PEDIDO, pedido.getPedido());
        values.put(COLUMN_PEDIDO_IDCLIENTE,pedido.getIdCliente());
        values.put(COLUMN_PEDIDO_NOMECLIENTE,pedido.getNomeCliente());
        values.put(COLUMN_PEDIDO_VALORTOTAL,pedido.getValorTotal());

        // Inserting Row
        long id = db.insert(TABLE_PEDIDO, null, values);

        db.close();

        return id;
    }

    public Cursor getPedidos(String whereClause,String[] whereArgs) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PEDIDO_ID+" as _id",
                COLUMN_PEDIDO_PEDIDO,
                COLUMN_PEDIDO_IDCLIENTE,
                COLUMN_PEDIDO_NOMECLIENTE,
                COLUMN_PEDIDO_VALORTOTAL,
        };

        // sorting orders
        String sortOrder =
                COLUMN_PEDIDO_PEDIDO + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */

        Cursor cursor = db.query(TABLE_PEDIDO, //Table to query
                columns,     //columns to return
                whereClause, //columns for the WHERE clause
                whereArgs,   //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order

        return cursor;
    }

    public void addPedidoItem(ArrayList<PedidoItem> pedidoItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        for (PedidoItem ped : pedidoItem){
            values.put(COLUMN_PEDIDO_ITEM_PEDIDO, ped.getIdPedido());
            values.put(COLUMN_PEDIDO_ITEM_PRODUTO,ped.getIdProduto());
            values.put(COLUMN_PEDIDO_ITEM_QTDE,ped.getQtde());
            values.put(COLUMN_PEDIDO_ITEM_PRECO,ped.getPreco());

            // Inserting Row
            db.insert(TABLE_PEDIDO_ITEM, null, values);
        }

        db.close();
    }

    public void addPedidoItem(PedidoItem pedidoItem){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_PEDIDO_ITEM_PEDIDO, pedidoItem.getIdPedido());
        values.put(COLUMN_PEDIDO_ITEM_PRODUTO,pedidoItem.getIdProduto());
        values.put(COLUMN_PEDIDO_ITEM_QTDE,pedidoItem.getQtde());
        values.put(COLUMN_PEDIDO_ITEM_PRECO,pedidoItem.getPreco());

        // Inserting Row
        db.insert(TABLE_PEDIDO_ITEM, null, values);

        db.close();
    }

}
