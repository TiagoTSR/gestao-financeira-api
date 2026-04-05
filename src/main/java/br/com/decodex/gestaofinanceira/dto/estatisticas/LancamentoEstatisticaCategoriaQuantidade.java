package br.com.decodex.gestaofinanceira.dto.estatisticas;

import br.com.decodex.gestaofinanceira.model.Categoria;

public class LancamentoEstatisticaCategoriaQuantidade {

    private Categoria categoria;
	
    private long quantidade;
	
	public LancamentoEstatisticaCategoriaQuantidade(Categoria categoria, Long quantidade) {
        this.categoria = categoria;
        this.quantidade = quantidade != null ? quantidade : 0L;
    }

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Long  getTotal() {
		return quantidade;
	}

	public void setTotal(Long  quantidade) {
		this.quantidade = quantidade;
	}
}