package jnpf.config;

import jnpf.consts.ApiConst;
import jnpf.properties.GatewayWhite;
import jnpf.properties.GatewayWhiteProperties;
import jnpf.properties.ReportProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ReportAutoConfiguration {


    @Bean
    @ConfigurationProperties(prefix = GatewayWhite.PREFIX)
    public GatewayWhite getGateWhite(){
        return new GatewayWhiteProperties();
    }


    @Bean
    @ConfigurationProperties(prefix = ReportProperties.PREFIX)
    public ReportProperties getReportProperties(){
        return new ReportProperties();
    }


}
