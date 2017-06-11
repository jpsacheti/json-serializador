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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class JsonEngine implements Engine {

    private static final String ABRE_CHAVES = "{";
    private static final String FECHA_CHAVES = "}";
    private static final String ABRE_COLCHETES = "[";
    private static final String FECHA_COLCHETES = "]";
    private static final String TEXT_IN_QUOTES = "\"{0}\"";
    private static final MessageFormat FORMATTER = new MessageFormat(TEXT_IN_QUOTES);
    private static final String DOIS_PONTOS = " : ";
    private Integer iterationCounter = 1;

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
        if (iterationCounter > 20) {
            throw new IllegalStateException("Can't serialize more than 20 levels deep!");
        }
        StringBuilder sb = new StringBuilder();
        if (!(object instanceof Serializable)) {
            throw new NotSerializableException();
        }
        final Class<?> classe = object.getClass();
        inserirNome(sb, classe);
        sb.append(ABRE_CHAVES);
        List<Field> fields = Stream.of(classe.getDeclaredFields())
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .collect(Collectors.toList());
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(object) == null) {
                    field.setAccessible(true);
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
        iterationCounter++;
        return sb.toString();
    }

    private <E> void processValue(E object, StringBuilder sb, Field field) {
        sb.append(FORMATTER.format(field.getName()));
        sb.append(DOIS_PONTOS);
        try {
            Object member = field.get(object);
            if (field.getDeclaringClass().isPrimitive()) {
                sb.append(member);
            } else if (member instanceof Collection) {
                sb.append(ABRE_COLCHETES);
                Collection<?> clct = (Collection<?>) member;
                for (Object item : clct) {
                    sb.append(toJson(item));
                    sb.append(", ");
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sb.append(FECHA_COLCHETES);
            } else if (member instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) member;
                sb.append(ABRE_CHAVES);
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    sb.append(FORMATTER.format(e.getKey()));
                    sb.append(DOIS_PONTOS);
                    sb.append(toJson(e.getValue()));
                }
                sb.append(FECHA_CHAVES);
            } else {
                sb.append(FORMATTER.format(member.toString()));
            }
            sb.append(", ");
        } catch (IllegalAccessException | NotSerializableException e) {
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
