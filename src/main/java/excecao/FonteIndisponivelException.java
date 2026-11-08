package excecao;

/**
 * Erro de fonte de dados: um sensor está com defeito ou o gerador de leituras falhou.
 */
public class FonteIndisponivelException extends Exception {

    public FonteIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
