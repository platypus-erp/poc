package org.platypus.modules.parser

/**
 * @author chmuchme
 * @since 0.1
 * on 04/09/17.
 */
val types = mapOf(
        Pair("string", "String"),
        Pair("integer", "Int"),
        Pair("boolean", "Boolean"),
        Pair("text", "String"),
        Pair("date", "LocalDate"),
        Pair("datetime", "LocalDateTime"),
        Pair("decimal", "BigDecimal"),
        Pair("time", "LocalTime"),
        Pair("one2many", "One2Many"),
        Pair("many2many", "Many2Many"),
        Pair("many2one", "Many2One")
)