package com.fx.reggie_take_out.config;

import com.fx.reggie_take_out.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.text.ExtendedMessageFormat;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

    }

    /*
    * mvc框架的消息转换器
    * */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
//        设置对象转换器
        messageConverter.setObjectMapper(new JacksonObjectMapper());
//        将上面的消息转换器追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);

    }
}
