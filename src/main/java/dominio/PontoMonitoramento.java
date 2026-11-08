package dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Componente concreto: um ponto vulnerável a alagamento (ex.: um trecho de córrego
 * próximo a casas) com seus sensores instalados.
 */
public class PontoMonitoramento extends ComponenteTerritorial {

    private final List<Sensor> sensores = new ArrayList<>();

    public PontoMonitoramento(int id, String nome, String regiao) {
        super(id, nome, regiao); // reaproveita o construtor da classe mãe
    }

    public void adicionarSensor(Sensor sensor) {
        sensores.add(sensor);
    }

    /** Devolve uma cópia: quem está de fora não consegue apagar sensores da lista. */
    public List<Sensor> getSensores() {
        return new ArrayList<>(sensores);
    }

    /**
     * O ponto está OPERANTE se todos os sensores funcionam, em FALHA PARCIAL se algum falhou
     * e SEM SENSORES se nenhum foi instalado.
     */
    @Override
    public String verificarStatus() {
        if (sensores.isEmpty()) {
            setStatus("SEM SENSORES");
            return getStatus();
        }
        int comFalha = 0;
        for (Sensor sensor : sensores) {
            if (!sensor.estaFuncionando()) {
                comFalha++;
            }
        }
        setStatus(comFalha == 0 ? "OPERANTE" : "FALHA PARCIAL (" + comFalha + " sensor(es) sem resposta)");
        return getStatus();
    }
}
