package org.dbs.spgb;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(title = "My API", version = "1.0", description = "My REST API"),
        servers = {@Server(description = "Local", url = "http://localhost:8080/")}
)
public class SbgbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbgbApplication.class, args);
    }

}
