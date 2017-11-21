package org.platypus.core.orm

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull


enum class OPERATOR {
    LIKE, ILIKE,
    IS_NULL, IS_NOT_NULL,
    MIN, MAX, LESS, LESS_EQ, GREATER, GREATER_EQ,
    NOT_EQ, EQ, PLUS, MINUS, TIMES, DIV, NOT_LIKE, REGEXP, IN, BETWEEN
}

object lp {

    fun <TYPE> t(f: OPERATOR, column: ExpressionWithColumnType<TYPE>, value: TYPE): Op<Boolean> {
        val comp = when (f) {
            OPERATOR.LIKE ->
                fun(column: ExpressionWithColumnType<TYPE>, value: TYPE): Op<Boolean> {
                    return LikeOp(column, QueryParameter(value, column.columnType))
                }
            OPERATOR.ILIKE ->
                fun(column: ExpressionWithColumnType<TYPE>, value: TYPE): Op<Boolean> {
                    return LikeOp(column.upperCase(), QueryParameter(value, column.columnType))
                }
            OPERATOR.IS_NULL -> TODO()
            OPERATOR.IS_NOT_NULL -> TODO()
            OPERATOR.MIN -> TODO()
            OPERATOR.MAX -> TODO()
            OPERATOR.LESS -> TODO()
            OPERATOR.LESS_EQ -> TODO()
            OPERATOR.GREATER -> TODO()
            OPERATOR.GREATER_EQ -> TODO()
            OPERATOR.NOT_EQ -> TODO()
            OPERATOR.EQ -> TODO()
            OPERATOR.PLUS -> TODO()
            OPERATOR.MINUS -> TODO()
            OPERATOR.TIMES -> TODO()
            OPERATOR.DIV -> TODO()
            OPERATOR.NOT_LIKE -> TODO()
            OPERATOR.REGEXP -> TODO()
            OPERATOR.IN -> TODO()
            OPERATOR.BETWEEN -> TODO()
        }
        return comp(column, value)
    }
}