package dominio;

import excecao.DadosInvalidosException;

import java.util.List;

/**
 * Compara as 3 leituras mais recentes com as 3 anteriores para saber se o valor
 * está SUBINDO ou DESCENDO, e projeta esse movimento a partir da última leitura:
 *   previsão = última leitura + (média recente - média anterior)
 * Reage rápido a mudanças (bom para tempestades que crescem depressa), mas pode exagerar.
 */
public class PrevisaoComparacao extends EstrategiaPrevisao {

    private static final int JANELA = 6; // 3 recentes x 3 anteriores

    public PrevisaoComparacao() {
        super("Comparação de tendência");
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        super.prever(historico); // validação do histórico fica na classe mãe
        int n = Math.min(historico.size(), JANELA);
        List<Double> janela = historico.subList(historico.size() - n, historico.size());

        int meio = n / 2;
        double mediaAnterior = media(janela.subList(0, meio));
        double mediaRecente = media(janela.subList(meio, n));
        double ultima = historico.get(historico.size() - 1);

        double previsao = ultima + (mediaRecente - mediaAnterior);
        return Math.max(0, previsao); // chuva/nível/umidade não ficam negativos
    }

    @Override
    public String explicar() {
        return "compara as leituras recentes com as anteriores e projeta a subida/descida (reage rápido, mas pode exagerar)";
    }
}
