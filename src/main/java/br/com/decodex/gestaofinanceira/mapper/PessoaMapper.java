package br.com.decodex.gestaofinanceira.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.decodex.gestaofinanceira.dto.contato.ContatoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.contato.ContatoResponseDTO;
import br.com.decodex.gestaofinanceira.dto.endereco.EnderecoDTO;
import br.com.decodex.gestaofinanceira.dto.pessoa.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.pessoa.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.model.Contato;
import br.com.decodex.gestaofinanceira.model.Endereco;
import br.com.decodex.gestaofinanceira.model.Pessoa;

@Component
public class PessoaMapper {

    public Pessoa toEntity(PessoaRequestDTO dto) {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setAtivo(dto.ativo());
        pessoa.setEndereco(toEnderecoEntity(dto.endereco()));
        
        if (dto.contatos() != null) {
            List<Contato> contatos = dto.contatos().stream()
                    .map(c -> toContatoEntity(c, pessoa))
                    .collect(Collectors.toList());
            pessoa.setContatos(contatos);
        }
        
        return pessoa;
    }

    public void updateEntity(Pessoa pessoa, PessoaRequestDTO dto) {
        pessoa.setNome(dto.nome());
        pessoa.setAtivo(dto.ativo());
        pessoa.setEndereco(toEnderecoEntity(dto.endereco()));

        List<Contato> atuais = pessoa.getContatos();
        List<Contato> novos = dto.contatos().stream()
            .map(c -> toContatoEntity(c, pessoa))
            .toList();

        atuais.removeIf(contatoAtual ->
            novos.stream().noneMatch(n -> n.getId() != null && n.getId().equals(contatoAtual.getId()))
        );

        for (Contato novo : novos) {
            if (novo.getId() == null) {
                atuais.add(novo);
            } else {
                Contato existente = atuais.stream()
                    .filter(c -> c.getId().equals(novo.getId()))
                    .findFirst()
                    .orElse(null);

                if (existente != null) {
                    existente.setNome(novo.getNome());
                    existente.setEmail(novo.getEmail());
                    existente.setTelefone(novo.getTelefone());
                }
            }
        }
    }

    public PessoaResponseDTO toDTO(Pessoa entity) {
        List<ContatoResponseDTO> contatosDTO = new ArrayList<>();
        
        if (entity.getContatos() != null) {
            contatosDTO = entity.getContatos().stream()
                    .map(this::toContatoResponseDTO)
                    .toList();
        }

        return new PessoaResponseDTO(
                entity.getId(),
                entity.getNome(),
                toEnderecoDTO(entity.getEndereco()),
                entity.getAtivo(),
                contatosDTO
        );
    }

    // --- MÉTODOS AUXILIARES DE ENDEREÇO ---

    private Endereco toEnderecoEntity(EnderecoDTO dto) {
        if (dto == null) return null;
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
        if (entity == null) return null;
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

    // --- MÉTODOS AUXILIARES DE CONTATO ---

    private Contato toContatoEntity(ContatoRequestDTO dto, Pessoa pessoa) {
        if (dto == null) return null;
        Contato contato = new Contato();
        contato.setId(dto.id());
        contato.setNome(dto.nome());
        contato.setEmail(dto.email());
        contato.setTelefone(dto.telefone());
        contato.setPessoa(pessoa); // IMPORTANTE: Vincula o contato à pessoa
        return contato;
    }

    private ContatoResponseDTO toContatoResponseDTO(Contato entity) {
        if (entity == null) return null;
        return new ContatoResponseDTO(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getTelefone()
        );
    }
}