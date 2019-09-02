package com.arclights.expensecalculator

import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.DefaultExecuteListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import javax.sql.DataSource

@Configuration
class InitialConfiguration {

    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun connectionProvider(): DataSourceConnectionProvider =
        DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))

    @Bean
    fun dsl(): DefaultDSLContext = DefaultDSLContext(configuration())

    fun configuration(): DefaultConfiguration = DefaultConfiguration()
        .let {
            it.set(connectionProvider())
            it.set(DefaultExecuteListener())
            return it
        }
}