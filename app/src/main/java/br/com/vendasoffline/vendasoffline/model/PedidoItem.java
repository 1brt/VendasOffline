package br.com.vendasoffline.vendasoffline.model;

/**
 * Created by lrgabriel on 28/06/17.
 */

public class PedidoItem {

    private int id;
    private int idPedido;
    private int idProduto;
    private double qtde;
    private double preco;

    public void setId(int id){ this.id = id; }

    public void setIdPedido(int idProduto){ this.idProduto = idProduto; }

    public void setIdProduto(int idProduto){ this.idProduto = idProduto; }

    public void setQtde(double qtde){ this.qtde = qtde; }

    public void setPreco(double preco){ this.preco = preco; }

    public int getId(){ return id; }

    public int getIdPedido(){ return idPedido; }

    public int getIdProduto(){ return idProduto; }

    public double getQtde(){ return qtde; }

    public double getPreco(){ return preco; }

}
