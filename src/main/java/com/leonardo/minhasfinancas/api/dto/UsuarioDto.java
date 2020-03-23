package com.leonardo.minhasfinancas.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioDto {
	
	private String email;
	private String nome;
	private String senha;
}
