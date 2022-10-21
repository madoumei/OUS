package com.config.dataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class camundaDataSourceConfig {

    @Bean("camundaBpmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.camunda")
    public DataSource camundaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("camundaSqlSessionFactory")
    public SqlSessionFactory defaultSqlSessionFactory(@Qualifier("camundaBpmDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
//        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapping/**/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "camundaTransactionManager")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("camundaBpmDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "camundaSqlSessionTemplate")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("camundaSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
