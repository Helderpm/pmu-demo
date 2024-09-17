package com.pmu2.exec.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This class is responsible for configuring the OpenAPI specification for the application.
 * It provides information about the API, contact details, license, and server URLs.
 */
@Configuration
public class swaggerConfig {

    /**
     * The URL of the development server.
     * This value is read from the application's properties file using the @Value annotation.
     */
    @Value("${pmu-exec.openapi.dev-url}")
    private String devUrl;

    /**
     * The URL of the production server.
     * This value is read from the application's properties file using the @Value annotation.
     */
    @Value("${pmu-exec.openapi.prod-url}")
    private String prodUrl;

    /**
     * This method creates and returns an instance of OpenAPI,
     * which contains the API's configuration details.
     *
     * @return an instance of OpenAPI with the specified configuration
     */
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("bezkoder@gmail.com");
        contact.setName("BezKoder");
        contact.setUrl("https://www.bezkoder.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Tutorial Management API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage tutorials.")
                .termsOfService("https://www.bezkoder.com/terms")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
