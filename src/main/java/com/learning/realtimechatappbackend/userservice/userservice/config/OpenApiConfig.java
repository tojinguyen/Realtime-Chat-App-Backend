package com.learning.userservice.userservice.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

        @Value("${openapi.dev-url:http://localhost:8080}")
        private String devUrl;

        @Value("${server.servlet.context-path:/}")
        private String contextPath;

        @Bean
        public OpenAPI myOpenAPI() {
                Server devServer = new Server();
                devServer.setUrl(devUrl + (contextPath.equals("/") ? "" : contextPath));
                devServer.setDescription("Server URL in Development environment");

                Contact contact = new Contact();
                contact.setName("User Service API");
                contact.setEmail("info@user-service.com");
                contact.setUrl("https://github.com/your-repository");

                License mitLicense = new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT");

                Info info = new Info()
                                .title("User Service API")
                                .version("1.0")
                                .description(
                                                "This API provides endpoints to manage users, authentication, and profiles for the food ordering system.")
                                .contact(contact)
                                .license(mitLicense);

                return new OpenAPI()
                                .info(info)
                                .servers(List.of(devServer));
        }
}
