package games.cultivate.mcmmocredits.converters;

/**
 * Potential Converter data sources.
 * Conversions are one-way, data cannot be exported to an external source.
 * Conversion types:
 * internal -> internal
 * external -> internal
 */
public enum ConverterType {
    INTERNAL_SQLITE, INTERNAL_H2, INTERNAL_MYSQL, EXTERNAL_MORPH, EXTERNAL_GRM, EXTERNAL_CSV;
}
