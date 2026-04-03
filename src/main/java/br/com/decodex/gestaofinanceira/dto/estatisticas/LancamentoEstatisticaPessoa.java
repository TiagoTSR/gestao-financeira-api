package br.com.decodex.gestaofinanceira.dto.estatisticas;

import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.model.TipoLancamento;

public class LancamentoEstatisticaPessoa {

	private TipoLancamento tipo;

	private Pessoa pessoa;

	private Long total;

	public LancamentoEstatisticaPessoa(TipoLancamento tipo, Pessoa pessoa, Long total) {
		this.tipo = tipo;
		this.pessoa = pessoa;
		this.total = total;
	}

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}