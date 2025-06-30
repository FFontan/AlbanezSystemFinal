package br.edu.umfg.teste.spring.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<TelefoneValido, String> {

    @Override
    public boolean isValid(String telefone, ConstraintValidatorContext context) {
        if (telefone == null || telefone.isBlank()) return false;

        // Remove espaços e caracteres não numéricos
        String apenasNumeros = telefone.replaceAll("\\D", "");

        // Deve ter 10 ou 11 dígitos (ex: 11999998888 ou 1133334444)
        return apenasNumeros.matches("\\d{10,11}");
    }
}
