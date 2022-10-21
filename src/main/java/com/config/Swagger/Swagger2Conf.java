package com.config.Swagger;

import com.config.exception.ErrorEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableSwagger2WebMvc
@Configuration
public class Swagger2Conf {

    @Value("${qcvisit.baseUrl}")
    private String baseUrl;

    @Value("${qcvisit.version}")
    private String version;

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        //token头
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        ticketPar.name("x-coolvisit-token")
                .description("访问凭据")    //Token 以及Authorization 为自定义的参数，session保存的名字是哪个就可以写成那个
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build(); //header中的ticket参数非必填，传空也可以
        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数

        String groupName=version;
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                //.host("https://www.baidu.com")
                .apiInfo(apiInfo())
                .groupName(groupName)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com"))
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .enableUrlTemplating(false)
                .globalOperationParameters(pars)
                ;
        return docket;
    }

//    @Bean
//    public Docket createRestApi() {
//        //添加全局响应状态码
//        List<ResponseMessage> responseMessageList = new ArrayList<>();
//        Arrays.stream(ErrorEnum.values()).forEach(errorEnums -> {
//            responseMessageList.add(
//                    new ResponseMessageBuilder().code(errorEnums.getCode()).message(errorEnums.getMsg()).responseModel(
//                            new ModelRef(errorEnums.getMsg())).build()
//            );
//        });
//
//
//        ParameterBuilder ticketPar = new ParameterBuilder();
//
//        List<Parameter> pars = new ArrayList<Parameter>();
//        ticketPar.name("x-coolvisit-token")
//                .description("访问凭据")    //Token 以及Authorization 为自定义的参数，session保存的名字是哪个就可以写成那个
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")
//                .required(false)
//                .build(); //header中的ticket参数非必填，传空也可以
//        pars.add(ticketPar.build());    //根据每个方法名也知道当前方法在设置什么参数
//
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())   //生产环境 要关闭
//                .enable(enableSwagger)
//                .pathMapping("/")
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com"))
//                .paths(PathSelectors.any())
//                .build()
//                .globalResponseMessage(RequestMethod.POST,responseMessageList)
//                .globalOperationParameters(pars);
//    }

//    /**
//     * 声明基础包
//     *
//     * @param basePackage 基础包路径
//     * @return
//     */
//    public static Predicate<RequestHandler> basePackage(final String... basePackage) {
//        return input -> declaringClass(input).transform(handlerPackage(basePackage)).or(true);
//    }
//
//    /**
//     * 校验基础包
//     *
//     * @param basePackage 基础包路径
//     * @return
//     */
//    private static Function<Class<?>, Boolean> handlerPackage(final String... basePackage) {
//        return input -> {
//            for (String strPackage : basePackage) {
//                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
//                if (isMatch) {
//                    return true;
//                }
//            }
//            return false;
//        };
//    }
//
//    /**
//     * 检验基础包实例
//     *
//     * @param requestHandler 请求处理类
//     * @return
//     */
//    @SuppressWarnings("deprecation")
//    private static Optional<? extends Class<?>> declaringClass(RequestHandler requestHandler) {
//        return Optional.fromNullable(requestHandler.declaringClass());
//    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("来访通访客系统 开发文档API")
                .description("SpringBoot整合qcvisit开发文档......")
                .version(this.version)
                .contact(new Contact("来访通", baseUrl, "bd@coolvisit.com"))
                .build();
    }

}