package dominio;

/**
 * Os tipos de sensor do recorte "chuva e alagamento".
 * Cada tipo sabe seu nome, sua unidade e a faixa física aceitável (mínimo e máximo).
 * Uma leitura fora dessa faixa é considerada defeito do sensor.
 */
public enum TipoSensor {

    CHUVA("Chuva", "mm/h", 0, 200),
    NIVEL_CORREGO("Nível do córrego", "m", 0, 10),
    UMIDADE_SOLO("Umidade do solo", "%", 0, 100);

    private final String nome;
    private final String unidade;
    private final double minimo;
    private final double maximo;

    TipoSensor(String nome, String unidade, double minimo, double maximo) {
        this.nome = nome;
        this.unidade = unidade;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public String getNome() { return nome; }
    public String getUnidade() { return unidade; }
    public double getMinimo() { return minimo; }
    public double getMaximo() { return maximo; }

    /** Formata um valor com a unidade do sensor. Ex.: "32,5 mm/h". */
    public String formatar(double valor) {
        return String.format("%.1f %s", valor, unidade);
    }
}
