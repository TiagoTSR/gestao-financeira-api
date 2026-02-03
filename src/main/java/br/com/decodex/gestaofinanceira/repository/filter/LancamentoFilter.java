package br.com.decodex.gestaofinanceira.repository.filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class LancamentoFilter {

    private String descricao;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimentoDe;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataVencimentoAte;

    private BigDecimal valorMin;
    private BigDecimal valorMax;

    private Long pessoaId;
    private Long categoriaId;

    public String getDescricao() {
    	return descricao; 
    }
    
    public void setDescricao(String descricao) {
    	this.descricao = descricao; 
    }

    public LocalDate getDataVencimentoDe() {
    	return dataVencimentoDe; 
    }
    
    public void setDataVencimentoDe(LocalDate dataVencimentoDe) {
    	this.dataVencimentoDe = dataVencimentoDe; 
    }

    public LocalDate getDataVencimentoAte() {
    	return dataVencimentoAte; 
    }
    
    public void setDataVencimentoAte(LocalDate dataVencimentoAte) {
    	this.dataVencimentoAte = dataVencimentoAte; 
    }

    public BigDecimal getValorMin() {
    	return valorMin; 
    }
    
    public void setValorMin(BigDecimal valorMin) {
    	this.valorMin = valorMin; 
    }

    public BigDecimal getValorMax() {
    	return valorMax; 
    }
    
    public void setValorMax(BigDecimal valorMax) {
    	this.valorMax = valorMax; 
    }

    public Long getPessoaId() {
    	return pessoaId; 
    }
    
    public void setPessoaId(Long pessoaId) {
    	this.pessoaId = pessoaId; 
    }

    public Long getCategoriaId() {
    	return categoriaId;
    }
    
    public void setCategoriaId(Long categoriaId) {
    	this.categoriaId = categoriaId; 
    }
}