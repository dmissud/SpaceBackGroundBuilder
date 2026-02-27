package org.dbs.sbgb;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.dbs.sbgb.config.ContextDescription;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
@OpenAPIDefinition(
        info = @Info(title = "SBGB Api", version = "1.0", description = "API for build a Space Background based on Perlin Algorythme"),
        servers = {@Server(description = "Local", url = "http://localhost:8080/")}
)
@ContextDescription
public class SbgbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbgbApplication.class, args);
    }

}

