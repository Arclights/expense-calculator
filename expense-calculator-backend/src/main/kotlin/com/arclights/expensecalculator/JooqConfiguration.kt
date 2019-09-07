package com.arclights.expensecalculator

import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.DefaultExecuteListenerProvider
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@ComponentScan("com.arclights.expensecalculator.db")
@EnableTransactionManagement
class JooqConfiguration(
        @Value("\${spring.datasource.url}") private val dbUrl: String,
        @Value("\${spring.datasource.username}") private val dbUserName: String,
        @Value("\${spring.datasource.password}") private val dbPassword: String
) {

    @Autowired
    private lateinit var environment: Environment

    @Bean
    fun dataSource(): DataSource = PGSimpleDataSource()
        .apply {
            setURL(dbUrl)
            user = dbUserName
            password = dbPassword
        }

    @Bean
    fun transactionAwareDataSource(dataSource: DataSource): TransactionAwareDataSourceProxy = TransactionAwareDataSourceProxy(dataSource)

    @Bean
    fun transactionManager(dataSource: DataSource): DataSourceTransactionManager = DataSourceTransactionManager(dataSource)

    @Bean
    fun connectionProvider(dataSource: DataSource): DataSourceConnectionProvider = DataSourceConnectionProvider(dataSource)

    @Bean
    fun exceptionTransformer(): ExceptionTranslator = ExceptionTranslator()

    @Bean
    fun dsl(dataSource: DataSource, exceptionTransformer: ExceptionTranslator): DefaultDSLContext = DefaultDSLContext(configuration(dataSource, exceptionTransformer))

    fun configuration(dataSource: DataSource, exceptionTransformer: ExceptionTranslator): DefaultConfiguration = DefaultConfiguration()
        .let {
            it.set(connectionProvider(dataSource))
            it.set(DefaultExecuteListenerProvider(exceptionTransformer))
            it.setSQLDialect(SQLDialect.POSTGRES)
            return it
        }
}