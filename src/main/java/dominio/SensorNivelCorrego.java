package dominio;

import excecao.FonteIndisponivelException;

import java.util.Random;

/**
 * Régua/sensor de nível do córrego, em metros.
 * Leituras simuladas com Random entre 0,3 m e 4,6 m, sempre partindo da leitura anterior
 * (o córrego sobe e desce aos poucos).
 */
public class SensorNivelCorrego implements Sensor {

    private final Random random;
    private boolean funcionando = true;
    private double atual = 1.8; // começa com o córrego um pouco cheio

    public SensorNivelCorrego(Random random) {
        this.random = random;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (random.nextDouble() < 0.05) {
            funcionando = false;
            throw new FonteIndisponivelException("Sensor de nível do córrego sem resposta (possível obstrução por detritos).");
        }
        funcionando = true;
        if (random.nextDouble() < 0.03) {
            return -1;
        }
        // a próxima leitura = a anterior + um pequeno sorteio (para cima ou para baixo)
        atual = atual + random.nextGaussian() * 0.3;
        atual = Math.max(0.3, Math.min(4.6, atual)); // não deixa sair da faixa simulada
        return atual;
    }

    @Override
    public boolean estaFuncionando() {
        return funcionando;
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.NIVEL_CORREGO;
    }
}
