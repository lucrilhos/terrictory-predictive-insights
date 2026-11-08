package dominio;

import excecao.DadosInvalidosException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Uma leitura de sensor. O construtor VALIDA o valor: uma medição inválida
 * nem chega a existir, então nunca é gravada no banco.
 */
public class Medicao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private long id;                      // preenchido pelo banco (0 = ainda não gravada)
    private final TipoSensor tipo;
    private final double valor;
    private final LocalDateTime dataHora;

    public Medicao(TipoSensor tipo, double valor, LocalDateTime dataHora) throws DadosInvalidosException {
        if (tipo == null || dataHora == null) {
            throw new DadosInvalidosException("Medição sem tipo de sensor ou sem data/hora.");
        }
        if (Double.isNaN(valor) || valor < tipo.getMinimo() || valor > tipo.getMaximo()) {
            throw new DadosInvalidosException(String.format(
                    "medição de %s fora da faixa: %.1f %s (aceito de %.0f a %.0f %s).",
                    tipo.getNome().toLowerCase(), valor, tipo.getUnidade(),
                    tipo.getMinimo(), tipo.getMaximo(), tipo.getUnidade()));
        }
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public TipoSensor getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public String toString() {
        return String.format("%s | %-17s | %s", dataHora.format(FORMATO), tipo.getNome(), tipo.formatar(valor));
    }
}
