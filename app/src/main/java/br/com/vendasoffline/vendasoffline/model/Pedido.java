package br.com.vendasoffline.vendasoffline.model;

/**
 * Created by lrgabriel on 28/06/17.
 */

public class Pedido {

    private int id;
    private int idCliente;
    private int pedido;
    private double valorTotal;

    public void setId(int id){ this.id = id; }

    public void setIdCliente(int idCliente){ this.idCliente = idCliente; }

    public void setPedido(int pedido){ this.pedido = pedido; }

    public void setValorTotal(double valorTotal){ this.valorTotal= valorTotal; }

    public int getId(){ return id; }

    public int getIdCliente(){ return idCliente; }

    public int getPedido(){ return pedido; }

    public double getValorTotal(){ return valorTotal; }

}
