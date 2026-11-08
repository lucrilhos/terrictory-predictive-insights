package dominio;

import excecao.DadosInvalidosException;
import servico.ConfiguracaoSistema;

/**
 * Dados do território monitorado. Tudo é privado (encapsulamento):
 * só dá para mudar pelos métodos, que validam antes de aceitar.
 * O território é descrito por REGIÃO, nunca por pessoas.
 */
public class DadosTerritorio {

    private String regiao;
    private int populacaoEmRisco;
    private final String codigoAcesso; // exigido para mexer nos limiares de alerta

    public DadosTerritorio(String regiao, int populacaoEmRisco, String codigoAcesso) throws DadosInvalidosException {
        if (codigoAcesso == null || codigoAcesso.isBlank()) {
            throw new DadosInvalidosException("o código de acesso não pode ser vazio.");
        }
        this.codigoAcesso = codigoAcesso;
        setRegiao(regiao);
        setPopulacaoEmRisco(populacaoEmRisco);
    }

    public String getRegiao() { return regiao; }
    public int getPopulacaoEmRisco() { return populacaoEmRisco; }

    public void setRegiao(String regiao) throws DadosInvalidosException {
        if (regiao == null || regiao.isBlank()) {
            throw new DadosInvalidosException("a região não pode ficar em branco.");
        }
        this.regiao = regiao.trim();
    }

    public void setPopulacaoEmRisco(int populacaoEmRisco) throws DadosInvalidosException {
        if (populacaoEmRisco < 0) {
            throw new DadosInvalidosException("a população em risco não pode ser negativa (recebido: " + populacaoEmRisco + ").");
        }
        this.populacaoEmRisco = populacaoEmRisco;
    }

    /**
     * Muda um limiar de alerta. Primeiro confere o código de acesso;
     * a validação do valor fica com a ConfiguracaoSistema (lugar único dos limiares).
     */
    public void alterarLimiar(String codigoInformado, TipoSensor tipo, NivelAlerta nivel, double novoValor)
            throws DadosInvalidosException {
        if (!codigoAcesso.equals(codigoInformado)) {
            throw new DadosInvalidosException("código de acesso incorreto: limiar NÃO alterado.");
        }
        ConfiguracaoSistema.getInstancia().alterarLimiar(tipo, nivel, novoValor);
    }

    @Override
    public String toString() {
        return "Região: " + regiao + " | População em área de risco (estimativa): " + populacaoEmRisco + " pessoas";
    }
}
