package br.edu.umfg.teste.spring.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InscricaoEstadualGenericValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InscricaoEstadualValid {
    String message() default "Inscrição Estadual inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
