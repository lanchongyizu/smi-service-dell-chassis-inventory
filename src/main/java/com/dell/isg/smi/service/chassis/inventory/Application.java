/**
 * Copyright � 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.service.chassis.inventory;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Locale;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableAutoConfiguration
@EnableSwagger2
@EnableDiscoveryClient
@EnableAsync
@ComponentScan("com.dell")
public class Application extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }


    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }


    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("chassisInventory").apiInfo(apiInfo()).select().paths(regex("/api.*")).build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("SMI Micro-service : Chassis Inventory").description("Micro-service for getting chassis inventory data via racadm.").termsOfServiceUrl("http://www.dell.com/smi/chassis/inventory").license("Dell SMI License Version 1.0").licenseUrl("www.dell.com/smi").version("1.0").build();
    }


    @Bean
    public EmbeddedServletContainerFactory servletContainerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();

        // factory.addConnectorCustomizers(connector ->
        // ((AbstractProtocol) connector.getProtocolHandler()).setConnectionTimeout(10000));

        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                ((AbstractProtocol) connector.getProtocolHandler()).setMaxThreads(1000);
                ((AbstractProtocol) connector.getProtocolHandler()).setMaxConnections(1000);
                ((AbstractProtocol) connector.getProtocolHandler()).setMinSpareThreads(200);
                ((AbstractProtocol) connector.getProtocolHandler()).setBacklog(1000);
                ((AbstractProtocol) connector.getProtocolHandler()).setThreadPriority(Thread.MAX_PRIORITY);
            }
        });

        // configure some more properties

        return factory;
    }
}
