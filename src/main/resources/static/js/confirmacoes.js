let modalCallback = null;

function abrirModalConfirmacao(mensagem, callback) {
  const modal = document.getElementById('modalConfirmacao');
  const texto = document.getElementById('modalConfirmacaoTexto');
  texto.textContent = mensagem;

  modalCallback = callback;
  modal.classList.add('show');
}

function fecharModalConfirmacao() {
  document.getElementById('modalConfirmacao').classList.remove('show');
  modalCallback = null;
}

// Ações dos botões do modal
document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('btnConfirmar').addEventListener('click', () => {
    if (modalCallback) modalCallback();
    fecharModalConfirmacao();
  });

  document.getElementById('btnCancelar').addEventListener('click', fecharModalConfirmacao);
});

// Para formulário
function confirmarEnvioForm(botao, mensagem = "Deseja salvar?") {
  const form = botao.closest("form");
  if (form.checkValidity()) {
    abrirModalConfirmacao(mensagem, () => form.submit());
  } else {
    form.reportValidity();
  }
}

// Para ações em botões
function confirmarBotao(mensagem = "Tem certeza?", callback) {
  abrirModalConfirmacao(mensagem, callback);
}

function abrirConfirmacaoEncerrar() {
  abrirModalConfirmacao("Deseja realmente encerrar esta promoção?", () => {
    document.getElementById("formEncerrarPromocao").submit();
  });
}

