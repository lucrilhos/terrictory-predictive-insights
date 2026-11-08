package dominio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Um alerta emitido: guarda QUAL sensor, o VALOR PREVISTO, o LIMITE estourado
 * e o motivo em texto, para que qualquer pessoa entenda por que ele foi gerado.
 */
public class Alerta {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private long id;
    private final TipoSensor tipo;
    private final NivelAlerta nivel;
    private final double valorPrevisto;
    private final double limite;
    private final String motivo;
    private final LocalDateTime dataHora;

    public Alerta(TipoSensor tipo, NivelAlerta nivel, double valorPrevisto, double limite,
                  String motivo, LocalDateTime dataHora) {
        this.tipo = tipo;
        this.nivel = nivel;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.motivo = motivo;
        this.dataHora = dataHora;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public TipoSensor getTipo() { return tipo; }
    public NivelAlerta getNivel() { return nivel; }
    public double getValorPrevisto() { return valorPrevisto; }
    public double getLimite() { return limite; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s%n      Recomendação: %s",
                nivel.getRotulo(), dataHora.format(FORMATO), motivo, nivel.getRecomendacao());
    }
}
