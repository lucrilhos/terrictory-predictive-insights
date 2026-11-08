package servico;

import dominio.NivelAlerta;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;

import java.util.EnumMap;
import java.util.Map;

/**
 * SINGLETON: existe UMA única configuração no sistema inteiro.
 * Assim, o serviço de alertas, o menu e os testes enxergam sempre os mesmos limiares.
 * (Construtor privado: ninguém de fora consegue dar "new ConfiguracaoSistema()".)
 */
public class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    // para cada sensor: {ATENÇÃO, ALERTA, CRÍTICO}
    private final Map<TipoSensor, double[]> limiares = new EnumMap<>(TipoSensor.class);

    private ConfiguracaoSistema() {
        restaurarPadroes();
    }

    public static synchronized ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    /** Volta aos valores de fábrica (usado na inicialização e nos testes). */
    public void restaurarPadroes() {
        limiares.put(TipoSensor.CHUVA, new double[]{10, 25, 50});           // mm/h: moderada, forte, muito forte
        limiares.put(TipoSensor.NIVEL_CORREGO, new double[]{2.0, 3.0, 3.8}); // m: margem do córrego em 4 m
        limiares.put(TipoSensor.UMIDADE_SOLO, new double[]{70, 85, 95});     // %: solo perto de saturar
    }

    public double getLimiar(TipoSensor tipo, NivelAlerta nivel) {
        return limiares.get(tipo)[posicao(nivel)];
    }

    /** Troca um limiar, desde que continue dentro da faixa e em ordem ATENÇÃO < ALERTA < CRÍTICO. */
    public void alterarLimiar(TipoSensor tipo, NivelAlerta nivel, double novoValor) throws DadosInvalidosException {
        if (nivel == NivelAlerta.NORMAL) {
            throw new DadosInvalidosException("o nível NORMAL não tem limiar para alterar.");
        }
        if (novoValor <= 0 || novoValor > tipo.getMaximo()) {
            throw new DadosInvalidosException(String.format("limiar inválido: %.1f (deve ser maior que 0 e até %.0f %s).",
                    novoValor, tipo.getMaximo(), tipo.getUnidade()));
        }
        double[] copia = limiares.get(tipo).clone(); // testa numa cópia antes de valer de verdade
        copia[posicao(nivel)] = novoValor;
        if (!(copia[0] < copia[1] && copia[1] < copia[2])) {
            throw new DadosInvalidosException("limiar inválido: a ordem precisa ser ATENÇÃO < ALERTA < CRÍTICO.");
        }
        limiares.put(tipo, copia);
    }

    /** ATENCAO -> 0, ALERTA -> 1, CRITICO -> 2 (o enum começa em NORMAL, que vale 0). */
    private int posicao(NivelAlerta nivel) {
        if (nivel == NivelAlerta.NORMAL) {
            throw new IllegalArgumentException("NORMAL não tem limiar.");
        }
        return nivel.ordinal() - 1;
    }
}
