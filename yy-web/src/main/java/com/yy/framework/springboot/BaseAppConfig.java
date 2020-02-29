package com.yy.framework.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 类名称: BaseAppConfig<br>
 * 类描述: spring-boot配置app的基类<br>
 * <p>此类包装了一下基本配置</p>
 * <ul>
 * 		<li>swagger的配置</li>
 * 		<li>mappingJackson2HttpMessageConverter的配置</li>
 * </ul>
 * 创建人: mateng@eversec.cn<br>
 * 创建时间: 2016年11月25日上午9:46:00<br>
 * 修改人: <br>
 * 修改时间: <br>
 */
public class BaseAppConfig {
	
	//扫描swagger的package
	@Value("${spring.swagger.package}")
	private String swaggerPackage;
	
	@Bean
    public Docket createRestApi() {
		if(StringUtils.isBlank(swaggerPackage)) {
			swaggerPackage = "com.eversec";
		}
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
//                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage(swaggerPackage))
                .paths(PathSelectors.any())
                .build();
    }
	
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		List<MediaType> mediaTypes = new ArrayList<>();
		// 防止IE JSON数据变成下载
//		mediaTypes.add(new MediaType("text", "html", Charset.forName("UTF-8")));
		mediaTypes.add(new MediaType("application", "json", Charset.forName("UTF-8")));
		converter.setSupportedMediaTypes(mediaTypes);
		converter.setObjectMapper(objectMapper);
		return converter;
	}
//
//	@Bean
//	public HttpMessageConverters httpMessageConverters(MappingJackson2HttpMessageConverter jsonConverter,
//			StringHttpMessageConverter stringConverter) {
//		return new HttpMessageConverters(jsonConverter, stringConverter);
//	}
	
    protected ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger2构建RESTful APIs")
                .description("Swagger2构建RESTful APIs")
                .version("1.0")
                .build();
    }

}
