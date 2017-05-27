package br.com.vendasoffline.vendasoffline.model;

/**
 * Created by lrgabriel on 27/05/17.
 */

public class Customer {
    private int id;
    private String nome;
    private String tipoPessoa;
    private String cnpj;
    private String pais;
    private String uf;
    private String cidade;
    private int cep;
    private int nro;

    public int getId(){return id;}

    public void setId(int id){this.id = id;}

    public String getNome(){return nome;}

    public void setNome(String nome){this.nome = nome;}

    public String getTipoPessoa(){return tipoPessoa;}

    public void setTipoPessoa(String tipoPessoa){this.tipoPessoa = tipoPessoa;}

    public String getCnpj(){return cnpj;}

    public void setCnpj(String cnpj){this.cnpj = cnpj;}

    public String getPais(){return pais;}

    public void setPais(String pais){this.pais = pais;}

    public String getUf(){return uf;}

    public void setUf(String uf){this.uf = uf;}

    public String getCidade(){return cidade;}

    public void setCidade(String cidade){this.cidade = cidade;}

    public int getCep(){return cep;}

    public void setCep(int cep){this.cep = cep;}

    public int getNro(){return nro;}

    public void setNro(int nro){this.nro = nro;}

}
