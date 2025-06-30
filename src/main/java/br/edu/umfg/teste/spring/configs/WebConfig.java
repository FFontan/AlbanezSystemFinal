package br.edu.umfg.teste.spring.configs;

import br.edu.umfg.teste.spring.security.JwtRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String imagesPath = System.getProperty("user.dir") + "/images-produtos/";
        registry.addResourceHandler("/images-produtos/**")
                .addResourceLocations("file:" + imagesPath);
    }

    @Bean
    public FilterRegistrationBean<JwtRequestFilter> jwtFilter(JwtRequestFilter filter) {
        FilterRegistrationBean<JwtRequestFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }
}
