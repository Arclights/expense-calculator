package com.arclights.expensecalculator

import org.jooq.ExecuteContext
import org.jooq.impl.DefaultExecuteListener
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator


class ExceptionTranslator : DefaultExecuteListener() {
    override fun exception(context: ExecuteContext): Unit {
        val dialect = context.configuration().dialect()
        val translator = SQLErrorCodeSQLExceptionTranslator(dialect.name)
        context.exception(translator.translate("Access database using jOOQ", context.sql(), context.sqlException()))
    }
}