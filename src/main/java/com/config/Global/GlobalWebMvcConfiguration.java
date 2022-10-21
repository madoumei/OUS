package com.config.Global;

import com.config.intercept.IpUrlLimitInterceptor;
import com.config.intercept.TokenVerifyInterceptor;
import com.config.listener.CustomObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 功能描述：全局webMVC配置
 */
@Configuration
public class GlobalWebMvcConfiguration implements WebMvcConfigurer {

    @Value("${qcvisit.Authorization}")
    private Boolean Authorization;

    @Autowired
    private TokenVerifyInterceptor tokenVerifyInterceptor;

    @Autowired
    private IpUrlLimitInterceptor ipUrlLimitInterceptor;

    @Autowired
    private CustomObjectMapper customObjectMapper;

    /**
     * 功能描述:配置静态资源,避免静态资源请求被拦截
     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
//        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
//        super.addResourceHandlers(registry);
//    }

    /**
     * 功能描述：添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = Arrays.asList(
                "***/error", "/v2/**", "/swagger-ui.html", "/webjars/**", "/swagger-resources/**", "/doc.html",
                "/templates/**"
        );

        registry.addInterceptor(ipUrlLimitInterceptor)
                .addPathPatterns("/**");

        if (Authorization) {
            registry.addInterceptor(tokenVerifyInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(excludePath);
        }


    }

    /**
     * 消息转换器配置
     *
     * @param converters
     */
//    @Override
//    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
//        httpMessageConverter.setObjectMapper(customObjectMapper);
//        converters.add(httpMessageConverter);
//        super.configureMessageConverters(converters);
//    }


}
