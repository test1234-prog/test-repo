package uz.momoit.makesense_dbridge.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@OpenAPIDefinition(info = @Info(title = "Makesense API", version = "v1", description = "${api.description}"), servers = @Server(url = "${server.base.url}"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer")
public class OpenAPI30Configuration {

    @Bean
    public GroupedOpenApi storeOpenApi() {
        String[] path = {
                "/api/**"
        };
        return GroupedOpenApi.builder().group("Admin page Documentation").pathsToMatch(path).build();
    }
}
