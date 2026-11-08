package dominio;

/**
 * Níveis de criticidade, do mais leve ao mais grave.
 * NORMAL não gera alerta; os outros três geram.
 */
public enum NivelAlerta {

    NORMAL("Normal", "Sem ação necessária."),
    ATENCAO("ATENÇÃO", "Acompanhar com mais frequência."),
    ALERTA("ALERTA", "Avaliar medidas preventivas (avisar moradores, liberar bueiros)."),
    CRITICO("CRÍTICO", "Avaliação prioritária da Defesa Civil / coordenação local.");

    private final String rotulo;
    private final String recomendacao;

    NivelAlerta(String rotulo, String recomendacao) {
        this.rotulo = rotulo;
        this.recomendacao = recomendacao;
    }

    public String getRotulo() { return rotulo; }
    public String getRecomendacao() { return recomendacao; }
}
