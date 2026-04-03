package br.com.decodex.gestaofinanceira.dto.estatisticas;

import br.com.decodex.gestaofinanceira.model.Categoria;

public class LancamentoEstatisticaCategoria {
	
	private Categoria categoria;
	
	private Long total;

	public LancamentoEstatisticaCategoria(Categoria categoria, Long total) {
		this.categoria = categoria;
		this.total = total;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}