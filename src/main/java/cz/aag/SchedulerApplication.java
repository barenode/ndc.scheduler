package cz.aag;

import io.vertx.core.Vertx;

public class SchedulerApplication {
    
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new SchedulerVerticle());
        Vertx.vertx().deployVerticle(new TempClientVerticle());
    }
}
