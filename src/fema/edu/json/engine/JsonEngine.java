package fema.edu.json.engine;

import fema.edu.json.annotation.ElementConverter;
import fema.edu.json.annotation.ElementValidator;
import fema.edu.json.annotation.JsonRoot;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parser de classe para JSON. Singleton, para adquirir uma instância válida, chame
 * {@link #getInstance()}
 */

public final class JsonEngine implements Engine {

    public static final String ABRE_CHAVES = "{";
    public static final String FECHA_CHAVES = "}";
    private static final Engine INSTANCE = new JsonEngine();
    private static final String TEXT_IN_QUOTES = "\"{0}\"";
    private static final MessageFormat FORMATTER = new MessageFormat(TEXT_IN_QUOTES);

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
     * @throws JsonException            caso a validação falhe
     * @throws NotSerializableException caso a classe não implemente {@link Serializable}
     */
    //TODO: Implementar tratamento de coleções
    @Override
    public <E> String toJson(E object) throws NotSerializableException {
        StringBuilder sb = new StringBuilder();
        if (!(object instanceof Serializable)) {
            throw new NotSerializableException();
        }
        final Class<?> classe = object.getClass();
        inserirNome(sb, classe);
        sb.append(ABRE_CHAVES);
        List<Field> fields = Stream.of(classe.getDeclaredFields()).filter(f -> !(Modifier.isTransient(f.getModifiers()))).collect(Collectors.toList());
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(object) == null) {
                    field.setAccessible(false);
                    continue;
                }
            } catch (IllegalAccessException e) {
                throw new JsonException(e);
            }
            if (field.isAnnotationPresent(ElementValidator.class)) {
                processValidator(object, field);
            }
            if (field.isAnnotationPresent(ElementConverter.class)) {
                processConvertion(object, sb, field);
            } else {
                processValue(object, sb, field);
            }
            field.setAccessible(false);
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append(FECHA_CHAVES);
        return sb.toString();
    }

    private <E> void processValue(E object, StringBuilder sb, Field field) {
        sb.append(FORMATTER.format(field.getName()));
        try {
            if (field.getDeclaringClass().isPrimitive()) {
                sb.append(field.get(object));
            } else {
                sb.append(FORMATTER.format(field.get(object).toString()));
            }
            sb.append(", ");
        } catch (IllegalAccessException e) {
            throw new JsonException(e);
        }
    }

    private <E> void processValidator(E object, Field field) {
        ElementValidator elementValidator = field.getAnnotation(ElementValidator.class);
        try {
            boolean valid = elementValidator.validator().newInstance().validate(object);
            if (!valid)
                throw new JsonException("Invalid field: " + field.getName() + " with value " + field.get(object));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JsonException(e);
        }
    }

    private <E> void processConvertion(E object, StringBuilder sb, Field field) {
        ElementConverter elementConverter = field.getAnnotation(ElementConverter.class);
        try {
            String obj = elementConverter.converter().newInstance().convert(object);
            sb.append(FORMATTER.format(field.getName())).append(" : ").append(FORMATTER.format(obj));
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JsonException(e);
        }
    }

    private void inserirNome(StringBuilder sb, Class<?> classe) {
        if (classe.isAnnotationPresent(JsonRoot.class)) {
            JsonRoot root = classe.getAnnotation(JsonRoot.class);
            String nomeObjeto = root.name();
            if (!nomeObjeto.isEmpty()) {
                sb.append(FORMATTER.format(root.name())).append(" ");
            }
        }
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
    public <E> Writer toJsonStream(E object) throws NotSerializableException {
        String value = toJson(object);
        StringWriter sw = new StringWriter();
        sw.write(value);
        return sw;
    }
}
