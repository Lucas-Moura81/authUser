package com.ead.authuser.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;
// validação para o atributo username para não conter espaço e não ser null.
@Documented // 
@Constraint(validatedBy = UsernameConstraintImpl.class)// classe que implementa essa interface e cria a validação em si.
@Target({ElementType.METHOD, ElementType.FIELD}) // aonde pode utilizar essa notação no método ou no atribudo. aqui está nos 2
@Retention(RetentionPolicy.RUNTIME) // quando essa validação vai ocorrer ? no caso está em tempo de execução
// depois de criado a interface contendo a notação. é criado uma classe de impl UsernameConstraintImpl, que ira implementar ConstraintValidator
// e recebe como <> UsernameConstraint

public @interface UsernameConstraint {
	// alguns parametros defout do bin validation
	String message() default "Invalid username"; // mensagem do erro 
    Class<?>[] groups() default {}; //grupo de validação 
    Class<? extends Payload>[] payload() default {}; // nivel que vai ocorrer o erro. 

}
