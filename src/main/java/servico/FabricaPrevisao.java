package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * FACTORY: único lugar do sistema que sabe "qual classe criar para cada nome".
 * Em vez de uma escada de if/else, um mapa nome -> construtor.
 * Nova estratégia = uma linha nova no mapa; o resto do código não muda.
 */
public final class FabricaPrevisao {

    private static final Map<String, Supplier<EstrategiaPrevisao>> CATALOGO = new LinkedHashMap<>();

    static {
        CATALOGO.put("MEDIA_MOVEL", PrevisaoMediaMovel::new);
        CATALOGO.put("COMPARACAO", PrevisaoComparacao::new);
    }

    private FabricaPrevisao() {
        // classe utilitária: não precisa de objeto
    }

    public static EstrategiaPrevisao criar(String nome) throws DadosInvalidosException {
        Supplier<EstrategiaPrevisao> construtor = (nome == null) ? null : CATALOGO.get(nome.trim().toUpperCase());
        if (construtor == null) {
            throw new DadosInvalidosException("estratégia desconhecida: '" + nome + "'. Opções: " + CATALOGO.keySet());
        }
        return construtor.get();
    }

    /** Nomes disponíveis, na ordem em que foram cadastrados (o menu numera a partir daqui). */
    public static List<String> nomesDisponiveis() {
        return new ArrayList<>(CATALOGO.keySet());
    }
}
