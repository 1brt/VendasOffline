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
    private String endereco;
    private String cep;
    private int nro;
    private int sinc;

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

    public String getCep(){return cep;}

    public void setCep(String cep){this.cep = cep;}

    public int getNro(){return nro;}

    public void setNro(int nro){this.nro = nro;}

    public String getEndereco(){return endereco;}

    public void setEndereco(String endereco){this.endereco = endereco;}

    public int getSinc(){return sinc;}

    public void setSinc(int sinc){this.sinc = sinc;}

    @Override
    public String toString() {
        return nome;
    }
}
