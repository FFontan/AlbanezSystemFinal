package br.edu.umfg.teste.spring.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentoValidator implements ConstraintValidator<DocumentoValido, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;

        String documento = value.replaceAll("\\D", "");

        if (documento.length() == 11) {
            return isValidCPF(documento);
        } else if (documento.length() == 14) {
            return isValidCNPJ(documento);
        }
        return false;
    }

    private boolean isValidCPF(String cpf) {
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int soma1 = 0, soma2 = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cpf.charAt(i));
            soma1 += digito * (10 - i);
            soma2 += digito * (11 - i);
        }

        int digito1 = soma1 % 11 < 2 ? 0 : 11 - (soma1 % 11);
        soma2 += digito1 * 2;
        int digito2 = soma2 % 11 < 2 ? 0 : 11 - (soma2 % 11);

        return digito1 == Character.getNumericValue(cpf.charAt(9)) &&
                digito2 == Character.getNumericValue(cpf.charAt(10));
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma1 = 0, soma2 = 0;
        for (int i = 0; i < 12; i++) {
            int digito = Character.getNumericValue(cnpj.charAt(i));
            soma1 += digito * peso1[i];
            soma2 += digito * peso2[i];
        }

        int digito1 = soma1 % 11 < 2 ? 0 : 11 - (soma1 % 11);
        soma2 += digito1 * peso2[12];
        int digito2 = soma2 % 11 < 2 ? 0 : 11 - (soma2 % 11);

        return digito1 == Character.getNumericValue(cnpj.charAt(12)) &&
                digito2 == Character.getNumericValue(cnpj.charAt(13));
    }
}
