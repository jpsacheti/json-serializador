package fema.edu.json.engine;

import java.io.Writer;

/**
 * Parser de classe para JSON. Singleton, para adquirir uma instância válida, chame
 * {@link #getInstance()}
 */

public final class JsonEngine implements Engine {

    private static final Engine INSTANCE = new JsonEngine();

    /**
     * @return uma instancia válida de {@link Engine}
     */
    public static Engine getInstance() {
        return INSTANCE;
    }

    /**
     * Valida e cria um JSON do objeto informado. A classe deve ter um construtor padrão sem
     * argumentos e deve implementar {@link java.io.Serializable}.
     * Por padrão, essa engine adiciona todos fields presentes na classe, exceto aqueles
     * marcados como <code>transcient</code> ou com valor <code>null</code>
     * Profundidade máxima do Json definida em cinco.
     *
     * @param object a ser serializado
     * @param <E>    tipo da classe
     * @return uma {@link String} com o conteudo serializado
     * @throws JsonException caso a validação falhe
     */
    @Override
    public <E> String toJson(E object) {
        return null;
    }

    /**
     * Valida e cria um JSON do objeto informado. A classe deve ter um construtor padrão sem
     * argumentos e deve implementar {@link java.io.Serializable}.
     * Por padrão, essa engine adiciona todos fields presentes na classe, exceto aqueles
     * marcados como <code>transcient</code> ou com valor <code>null</code>
     * Profundidade máxima do Json definida em cinco.
     *
     * @param object a ser serializado
     * @param <E>    tipo da classe
     * @return um {@link Writer} com o conteudo serializado
     * @throws JsonException caso a validação falhe
     */
    @Override
    public <E> Writer toJsonStream(E object) {
        return null;
    }
}
