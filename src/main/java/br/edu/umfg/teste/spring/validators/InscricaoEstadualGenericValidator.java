package br.edu.umfg.teste.spring.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InscricaoEstadualGenericValidator implements ConstraintValidator<InscricaoEstadualValid, String> {

    @Override
    public void initialize(InscricaoEstadualValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(String ie, ConstraintValidatorContext context) {
        if (ie == null || ie.isBlank()) {
            return false;
        }
        String numeros = ie.replaceAll("\\D", "");

        return numeros.length() >= 8 && numeros.length() <= 14;
    }
}
