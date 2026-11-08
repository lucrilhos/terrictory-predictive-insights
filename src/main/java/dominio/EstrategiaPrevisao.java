package dominio;

import excecao.DadosInvalidosException;

import java.util.List;

/**
 * Molde de toda estratégia de previsão. Cada filha calcula de um jeito,
 * mas todas passam pela MESMA checagem do histórico (feita aqui na mãe).
 */
public abstract class EstrategiaPrevisao {

    /** Com menos que isso o sistema prefere ficar calado a chutar. */
    public static final int MINIMO_LEITURAS = 5;

    private final String nome;

    protected EstrategiaPrevisao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /**
     * Versão base: valida o histórico (em ordem cronológica, do mais antigo ao mais novo)
     * e devolve a última leitura ("amanhã será igual a hoje").
     * As filhas chamam super.prever(...) para reaproveitar a validação e depois fazem a conta delas.
     */
    public double prever(List<Double> historico) throws DadosInvalidosException {
        if (historico == null || historico.size() < MINIMO_LEITURAS) {
            int quantidade = (historico == null) ? 0 : historico.size();
            throw new DadosInvalidosException("histórico insuficiente: " + quantidade + " leitura(s), mínimo "
                    + MINIMO_LEITURAS + ". O sistema não emite previsão com tão poucos dados.");
        }
        return historico.get(historico.size() - 1);
    }

    /** Frase curta dizendo, em português simples, o que a estratégia faz. */
    public abstract String explicar();

    /** Média simples de uma lista (usada pelas filhas). */
    protected static double media(List<Double> valores) {
        double soma = 0;
        for (double v : valores) {
            soma += v;
        }
        return soma / valores.size();
    }
}
