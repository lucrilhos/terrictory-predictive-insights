package dominio;

import excecao.FonteIndisponivelException;

import java.util.Random;

/**
 * Sensor de umidade do solo, em %. Solo encharcado não absorve mais água,
 * então a chuva escorre direto para as ruas e o córrego.
 * Leituras simuladas com Random entre 30% e 100%, sempre partindo da leitura anterior.
 */
public class SensorUmidadeSolo implements Sensor {

    private final Random random;
    private boolean funcionando = true;
    private double atual = 72; // começa com o solo já bem úmido

    public SensorUmidadeSolo(Random random) {
        this.random = random;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (random.nextDouble() < 0.05) {
            funcionando = false;
            throw new FonteIndisponivelException("Sensor de umidade do solo sem resposta (bateria fraca?).");
        }
        funcionando = true;
        if (random.nextDouble() < 0.03) {
            return -1;
        }
        // a próxima leitura = a anterior + um pequeno sorteio (para cima ou para baixo)
        atual = atual + random.nextGaussian() * 4;
        atual = Math.max(30, Math.min(100, atual)); // não deixa sair da faixa simulada
        return atual;
    }

    @Override
    public boolean estaFuncionando() {
        return funcionando;
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.UMIDADE_SOLO;
    }
}
