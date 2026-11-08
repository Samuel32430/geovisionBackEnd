package pe.edu.upeu.geovision_backend.config;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import pe.edu.upeu.geovision_backend.enums.atencion.TipoMedidaEnum;

/** Defaults for omitted record components; explicit JSON null remains null. */
public final class DtoDefaultsJackson3 {
    private DtoDefaultsJackson3() {}

    private abstract static class MissingValue<T> extends StdDeserializer<T> {
        private final Class<T> valueType;
        private final T defaultValue;

        protected MissingValue(Class<T> valueType, T defaultValue) {
            super(valueType);
            this.valueType = valueType;
            this.defaultValue = defaultValue;
        }

        @Override
        public T deserialize(JsonParser parser, DeserializationContext context)  {
            return context.readValue(parser, valueType);
        }

        @Override
        public T getAbsentValue(DeserializationContext context) {
            return defaultValue;
        }
    }

    public static final class One extends MissingValue<Integer> {
        public One() { super(Integer.class, 1); }
    }

    public static final class Zero extends MissingValue<Integer> {
        public Zero() { super(Integer.class, 0); }
    }

    public static final class Bearer extends MissingValue<String> {
        public Bearer() { super(String.class, "Bearer"); }
    }

    public static final class Whatsapp extends MissingValue<String> {
        public Whatsapp() { super(String.class, "WHATSAPP"); }
    }

    public static final class Lejos extends MissingValue<TipoMedidaEnum> {
        public Lejos() { super(TipoMedidaEnum.class, TipoMedidaEnum.LEJOS); }
    }
}
