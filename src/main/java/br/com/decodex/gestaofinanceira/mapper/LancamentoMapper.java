package br.com.decodex.gestaofinanceira.mapper;

import org.springframework.stereotype.Component;
import br.com.decodex.gestaofinanceira.dto.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.model.Pessoa;

@Component
public class LancamentoMapper {

    public Lancamento toEntity(LancamentoRequestDTO dto, Pessoa pessoa, Categoria categoria) {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao(dto.descricao());
        lancamento.setDataVencimento(dto.dataVencimento());
        lancamento.setDataPagamento(dto.dataPagamento());
        lancamento.setValor(dto.valor());
        lancamento.setObservacao(dto.observacao());
        lancamento.setTipo(dto.tipo());
        lancamento.setPessoa(pessoa);
        lancamento.setCategoria(categoria);
        return lancamento;
    }

    public void updateEntity(Lancamento lancamento, LancamentoRequestDTO dto, Pessoa pessoa, Categoria categoria) {
        lancamento.setDescricao(dto.descricao());
        lancamento.setDataVencimento(dto.dataVencimento());
        lancamento.setDataPagamento(dto.dataPagamento());
        lancamento.setValor(dto.valor());
        lancamento.setObservacao(dto.observacao());
        lancamento.setTipo(dto.tipo());
        lancamento.setPessoa(pessoa);
        lancamento.setCategoria(categoria);
    }

    public LancamentoResponseDTO toDTO(Lancamento entity) {
        return new LancamentoResponseDTO(
                entity.getId(),
                entity.getDescricao(),
                entity.getDataVencimento(),
                entity.getDataPagamento(),
                entity.getValor(),
                entity.getObservacao(),
                entity.getTipo(),
                entity.getCategoria().getId(),
                entity.getCategoria().getNome(),
                entity.getPessoa().getId(),
                entity.getPessoa().getNome()
        );
    }
}