package server;

import spark.*;

public class Server {
    private final Handler handler;

    public Server() {
        // create a new handler for all the endpoints
        this.handler = new Handler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        // for the web files
        Spark.staticFiles.location("web");

        // TODO: maybe add more error handling later?
        
        // all the endpoints we need for the chess server
        // register
        Spark.post("/user", handler::register);
        // login
        Spark.post("/session", handler::login);
        // logout
        Spark.delete("/session", handler::logout);
        // list games
        Spark.get("/game", handler::listGames);
        // create game
        Spark.post("/game", handler::createGame);
        // join game
        Spark.put("/game", handler::joinGame);
        // clear db - for testing
        Spark.delete("/db", handler::clear);

        // start up the server
        Spark.init();

        // wait until server is ready
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        // shut everything down when we're done
        Spark.stop();
        Spark.awaitStop();
    }
}
