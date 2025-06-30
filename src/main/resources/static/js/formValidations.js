class FormValidations {
  // Máscara para CPF/CNPJ
  static cpfCnpjMask(input) {
    let v = input.value.replace(/\D/g, "");
    if (v.length <= 11) { // CPF
      v = v.replace(/(\d{3})(\d)/, "$1.$2");
      v = v.replace(/(\d{3})(\d)/, "$1.$2");
      v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
      input.value = v.substring(0, 14);
    } else { // CNPJ
      v = v.replace(/^(\d{2})(\d)/, "$1.$2");
      v = v.replace(/^(\d{2})\.(\d{3})(\d)/, "$1.$2.$3");
      v = v.replace(/\.(\d{3})(\d)/, ".$1/$2");
      v = v.replace(/(\d{4})(\d)/, "$1-$2");
      input.value = v.substring(0, 18);
    }
  }

  // Máscara para telefone brasileiro
  static telefoneMask(input) {
    let v = input.value.replace(/\D/g, "");
    if (v.length > 10) {
      // (99) 99999-9999
      v = v.replace(/^(\d{2})(\d{5})(\d{4}).*/, "($1) $2-$3");
    } else if (v.length > 5) {
      // (99) 9999-9999
      v = v.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, "($1) $2-$3");
    } else if (v.length > 2) {
      // (99) 9999
      v = v.replace(/^(\d{2})(\d{0,5})/, "($1) $2");
    } else {
      // (99
      v = v.replace(/^(\d*)/, "($1");
    }
    input.value = v;
  }

  static cepMask(input) {
    let v = input.value.replace(/\D/g, "");
    if (v.length > 5) {
      v = v.replace(/^(\d{5})(\d)/, "$1-$2");
    }
    input.value = v.substring(0, 9);
  }
}
