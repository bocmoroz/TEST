package org.warehouse.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.title: REST Api Warehouse app}")
    private String title;
    @Value("${swagger.description: Documentation API for warehouse app}")
    private String description;
    @Value("${swagger.version: 1.0}")
    private String version;
    @Value("${swagger.termOfServiceUrl: https://www.gnu.org/licenses/gpl-3.0.html}")
    private String termOfServiceUrl;
    @Value("${swagger.contact.name: Mokeev Andrey}")
    private String contactName;
    @Value("${swagger.contact.url: https://www.linkedin.com/in/andrey-mokeev/}")
    private String contactUrl;
    @Value("${swagger.contact.mail: mokeevandrey1996@gmail.com}")
    private String contactMail;
    @Value("${swagger.license: GNU General Public License}")
    private String license;
    @Value("${swagger.license: https://www.gnu.org/licenses/gpl-3.0.html}")
    private String licenseUrl;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build().apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(title, description, version, termOfServiceUrl, new Contact(contactName, contactUrl, contactMail),
                license, licenseUrl, Collections.emptyList());
    }
}
