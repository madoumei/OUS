package com.config.dataSource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.config.qicool.common.persistence.interceptor.PaginationInterceptor;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = {"com.client.dao2"},sqlSessionTemplateRef = "defaultSqlSessionTemplate2") //扫描的mapper
public class PostgisDataSourceConfig {


    /**
     * 默认数据源配置
     * @return
     */
    @Bean("postgisDataSource")
    @ConfigurationProperties("spring.datasource.postgis")
    public DataSource PostgisDataSourceConfig() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 默认连接工厂
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean("defaultSqlSessionFactory2")
    public SqlSessionFactory defaultSqlSessionFactory(@Qualifier("postgisDataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//        sessionFactoryBean.setDataSource(dataSource);
//        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapping/**/*.xml"));
//        sessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
//        sessionFactoryBean.setTypeHandlers(new AESEncryptHandler());
//        return sessionFactoryBean.getObject();
        String mapperLocations = "classpath:mapping/track/*.xml";
        VFS.addImplClass(SpringBootVFS.class);
        final MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
//        sessionFactory.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        sessionFactory.setTypeHandlers(new AESEncryptHandler());
        sessionFactory.setTypeAliasesPackage("com.client.bean");
        sessionFactory.setPlugins(new PaginationInterceptor());
        return sessionFactory.getObject();

    }

    /**
     * 创建事物
     * @param dataSource
     * @return
     */
    @Bean(name = "defaultTransactionManager2")
    public DataSourceTransactionManager testTransactionManager(@Qualifier("postgisDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    /**
     * 创建模板
     * @param sqlSessionFactory
     * @return
     * @throws Exception
     */
    @Bean(name = "defaultSqlSessionTemplate2")
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("defaultSqlSessionFactory2") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor2() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
