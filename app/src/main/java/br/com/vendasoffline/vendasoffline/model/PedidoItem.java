package br.com.vendasoffline.vendasoffline.model;

/**
 * Created by lrgabriel on 28/06/17.
 */

public class PedidoItem {

    private long id;
    private long idPedido;
    private long idProduto;
    private double qtde;
    private double preco;

    public void setId(long id){ this.id = id; }

    public void setIdPedido(long idPedido){ this.idPedido = idPedido; }

    public void setIdProduto(long idProduto){ this.idProduto = idProduto; }

    public void setQtde(double qtde){ this.qtde = qtde; }

    public void setPreco(double preco){ this.preco = preco; }

    public long getId(){ return id; }

    public long getIdPedido(){ return idPedido; }

    public long getIdProduto(){ return idProduto; }

    public double getQtde(){ return qtde; }

    public double getPreco(){ return preco; }

}
