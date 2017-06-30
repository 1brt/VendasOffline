package br.com.vendasoffline.vendasoffline.model;

/**
 * Created by lrgabriel on 28/06/17.
 */

public class Produto {

    private long id;
    private String codigo;
    private String descricao;

    public void setId(int id){ this.id = id; }

    public void setCodigo(String codigo){ this.codigo = codigo; }

    public void setDescricao(String descricao){ this.descricao = descricao; }

    public long getId(){ return id; }

    public String getCodigo(){ return codigo; }

    public String getDescricao(){ return descricao; }

    @Override
    public String toString(){
        return codigo+"-"+descricao;
    }
}
