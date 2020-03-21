package com.leonardo.minhasfinancas.exceptions;

public class RegraNegocioException extends RuntimeException {

	private static final long serialVersionUID = 5426639728288229874L;

	public RegraNegocioException(String mensagem) {
        super(mensagem);
    }

}
