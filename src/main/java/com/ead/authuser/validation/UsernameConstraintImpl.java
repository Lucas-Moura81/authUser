package com.ead.authuser.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
// essa classe implementa a classe ConstraintValidator, passa a notação criada e qual tipo que ela vai validar. 
// precisa implementar alguns métodos que a propria ide ajuda 
public class UsernameConstraintImpl implements ConstraintValidator<UsernameConstraint, String> {


	// criando restrição para user name, se não é null, vazio ou tem espaço em branco entre os caracter. 
	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		// trim remover espaços em branco. 
		// isEmpty se é vazio 
		// contains () 
		if(username == null || username.trim().isEmpty() || username.contains(" ")) {
			return false; 
		}
		return true;
	}
	
}
