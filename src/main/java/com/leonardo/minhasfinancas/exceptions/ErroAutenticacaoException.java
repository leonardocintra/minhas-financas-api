package com.leonardo.minhasfinancas.exceptions;

public class ErroAutenticacaoException extends RuntimeException {
	
	private static final long serialVersionUID = 6630140256186925955L;

	public ErroAutenticacaoException(String mensagem) {
		super(mensagem);
	}
}
