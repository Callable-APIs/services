/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.callableapis.api;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.net.URI;

public class App {
    int port;

    public App(int port) {
        this.port = port;
    }

    public void runServer() {

        URI uri = URI.create("http://0.0.0.0:8080/");
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, new APIApplication());
        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

        try {
            server.start();
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        App app = new App(5000);
        app.runServer();
    }
}
