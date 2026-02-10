package br.com.decodex.gestaofinanceira.mapper;

import org.springframework.stereotype.Component;

import br.com.decodex.gestaofinanceira.dto.EnderecoDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.model.Endereco;
import br.com.decodex.gestaofinanceira.model.Pessoa;

@Component
public class PessoaMapper {

    public Pessoa toEntity(PessoaRequestDTO dto) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setAtivo(dto.ativo());
        pessoa.setEndereco(toEnderecoEntity(dto.endereco()));
        return pessoa;
    }

    public void updateEntity(Pessoa pessoa, PessoaRequestDTO dto) {
        pessoa.setNome(dto.nome());
        pessoa.setAtivo(dto.ativo());
        pessoa.setEndereco(toEnderecoEntity(dto.endereco()));
    }

    public PessoaResponseDTO toDTO(Pessoa entity) {
        return new PessoaResponseDTO(
                entity.getId(),
                entity.getNome(),
                toEnderecoDTO(entity.getEndereco()),
                entity.getAtivo()
        );
    }

    private Endereco toEnderecoEntity(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }

        Endereco endereco = new Endereco();
        endereco.setLogradouro(dto.logradouro());
        endereco.setNumero(dto.numero());
        endereco.setComplemento(dto.complemento());
        endereco.setBairro(dto.bairro());
        endereco.setCep(dto.cep());
        endereco.setCidade(dto.cidade());
        endereco.setEstado(dto.estado());

        return endereco;
    }

    private EnderecoDTO toEnderecoDTO(Endereco entity) {
        if (entity == null) {
            return null;
        }

        return new EnderecoDTO(
                entity.getLogradouro(),
                entity.getNumero(),
                entity.getComplemento(),
                entity.getBairro(),
                entity.getCep(),
                entity.getCidade(),
                entity.getEstado()
        );
    }
}
