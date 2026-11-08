package dominio;

import excecao.DadosInvalidosException;

import java.util.List;

/**
 * Previsão = média das últimas 5 leituras.
 * Suaviza picos isolados (uma leitura estranha não dispara alerta sozinha).
 */
public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    private static final int JANELA = 5;

    public PrevisaoMediaMovel() {
        super("Média móvel");
    }

    @Override
    public double prever(List<Double> historico) throws DadosInvalidosException {
        super.prever(historico); // validação do histórico fica na classe mãe
        List<Double> ultimas = historico.subList(historico.size() - JANELA, historico.size());
        return media(ultimas);
    }

    @Override
    public String explicar() {
        return "média das últimas " + JANELA + " leituras (estável, mas demora a reagir a mudanças bruscas)";
    }
}
