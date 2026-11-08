package dominio;

import excecao.FonteIndisponivelException;

import java.util.Random;

/**
 * Pluviômetro: mede a intensidade da chuva em mm/h.
 * As leituras são simuladas com Random entre 0 e 60 mm/h. Cada leitura parte da anterior
 * ("passeio aleatório"): a chuva engrossa ou afina aos poucos, como na vida real.
 */
public class SensorChuva implements Sensor {

    private final Random random;
    private boolean funcionando = true;
    private double atual = 12; // a simulação começa num dia chuvoso

    public SensorChuva(Random random) {
        this.random = random;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        // 5% de chance de o equipamento não responder
        if (random.nextDouble() < 0.05) {
            funcionando = false;
            throw new FonteIndisponivelException("Pluviômetro sem resposta (verificar alimentação/conexão).");
        }
        funcionando = true;
        // 3% de chance de uma leitura com defeito (valor impossível): a validação da Medicao barra
        if (random.nextDouble() < 0.03) {
            return -1;
        }
        // a próxima leitura = a anterior + um pequeno sorteio (para cima ou para baixo)
        atual = atual + random.nextGaussian() * 4;
        atual = Math.max(0, Math.min(60, atual)); // não deixa sair da faixa simulada
        return atual;
    }

    @Override
    public boolean estaFuncionando() {
        return funcionando;
    }

    @Override
    public TipoSensor getTipo() {
        return TipoSensor.CHUVA;
    }
}
