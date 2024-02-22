package org.dbs.spgb.config;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class MyRouterHandlers {
    public Mono<ServerResponse> notFound(ServerRequest request) {
        // Redirect to your custom error page
        return ServerResponse.status(404).bodyValue("Lien inconnue, ceci n'est pas aaccessible comme cela");
    }
}
