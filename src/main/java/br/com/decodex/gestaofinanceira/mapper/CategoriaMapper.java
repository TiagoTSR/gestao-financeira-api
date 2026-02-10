package br.com.decodex.gestaofinanceira.mapper;

import org.springframework.stereotype.Component;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.model.Categoria;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        return categoria;
    }

    public void updateEntity(Categoria categoria, CategoriaRequestDTO dto) {
        categoria.setNome(dto.nome());
    }

    public CategoriaResponseDTO toDTO(Categoria entity) {
        return new CategoriaResponseDTO(
                entity.getId(),
                entity.getNome()
        );
    }
}
