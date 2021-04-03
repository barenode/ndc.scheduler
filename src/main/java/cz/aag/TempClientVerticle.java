package cz.aag;

import java.util.UUID;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;

public class TempClientVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> promise) throws Exception {
      vertx.createHttpServer()
        .requestHandler(this::handleRequest)
        .listen(8888)
        .onFailure(th -> promise.fail(th.getMessage()))
        .onSuccess(ok -> {
          System.out.println("Server started on port 8080");
        });
    }

    private void handleRequest(HttpServerRequest request) {
      request.response().end("{\"_id\": \"" + UUID.randomUUID().toString() + "\"}");
    }    
}
