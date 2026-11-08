package excecao;

/**
 * Erro de dado inválido: medição fora da faixa, população negativa, limiar inválido,
 * histórico insuficiente para prever ou código de acesso errado.
 *
 * É "checked" (extends Exception): o compilador obriga quem chama a tratar o erro.
 */
public class DadosInvalidosException extends Exception {

    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
