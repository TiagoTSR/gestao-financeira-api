package br.com.decodex.gestaofinanceira.repository.filter;

import br.com.decodex.gestaofinanceira.model.Endereco;

public class PessoaFilter {

    private String nome;

    private Endereco endereco;

    private Boolean ativo;
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}