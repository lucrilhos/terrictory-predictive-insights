package dominio;

import excecao.FonteIndisponivelException;

/**
 * Contrato de qualquer sensor: ler um valor, dizer se está funcionando e dizer seu tipo.
 * O resto do sistema só conhece esta interface, e não as classes concretas
 * (polimorfismo: dá para trocar ou adicionar sensores sem mexer no serviço).
 */
public interface Sensor {

    /** Faz uma leitura. Lança exceção se o sensor estiver com defeito. */
    double lerValor() throws FonteIndisponivelException;

    /** true se o sensor está respondendo normalmente. */
    boolean estaFuncionando();

    /** Qual grandeza este sensor mede. */
    TipoSensor getTipo();
}
