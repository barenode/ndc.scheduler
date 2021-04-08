package cz.aag;


import java.util.concurrent.TimeUnit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

public class SchedulerVerticle extends AbstractVerticle {
    
  private WebClient webClient;

  @Override
  public void start(Promise<Void> promise) throws Exception {
    WebClientOptions options = new WebClientOptions()
      .setConnectTimeout(1000)
      .setIdleTimeout(3000)
      .setIdleTimeoutUnit(TimeUnit.MILLISECONDS);
    webClient = WebClient.create(vertx, options);
    vertx.createHttpServer()
      .requestHandler(this::handleRequest)
      .listen(8080)
      .onFailure(th -> promise.fail(th.getMessage()))
      .onSuccess(ok -> {
        System.out.println("Server started on port 8080");
      });
  }

  private void handleRequest(HttpServerRequest request) {
    CompositeFuture.all(
      read(8888), 
      read(8888), 
      read(8888))
    .onSuccess(data -> {
      request.response().end(aggregate(data).encode());
    })      
    .onFailure(err -> {
      request.response().setStatusCode(500).end();
    });
  }

  private JsonObject aggregate(CompositeFuture future) {
    JsonObject result = new JsonObject();
    JsonArray data = new JsonArray();
    future.list().forEach(o -> data.add((JsonObject)o));
    result.put("data", data);
    return result;
  }


  private Future<JsonObject> readSimple(int port) {
    return webClient
      .get(port, "localhost", "/")
      .expect(ResponsePredicate.SC_SUCCESS)        
      .as(BodyCodec.jsonObject())
      .send()   
      .map(HttpResponse::body);  
  }


  private Future<JsonObject> read(int port) {
    Promise<JsonObject> promise = Promise.promise();
    webClient
      .get(port, "localhost", "/")
      .expect(ResponsePredicate.SC_SUCCESS)        
      .as(BodyCodec.jsonObject())
      .send()   
      .map(HttpResponse::body)
      .onSuccess(res -> {
        promise.complete(res);          
      })
      .onFailure(err -> {
        err.printStackTrace();
        promise.complete(new JsonObject());          
      });
    return promise.future();       
  }
}
