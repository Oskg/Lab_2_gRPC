package GameServer;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.java.CheckeredGameGrpc;


public class GameServer extends CheckeredGameGrpc.CheckeredGameImplBase {
    public static void main(String[] args) {
        try {
            Server server = ServerBuilder.forPort(1099)
                    .addService(new CheckeredGameService())
                    .build();

            System.out.println("Игровой сервер запущен!");
            server.start();
            server.awaitTermination();
        } catch (Exception e) {
            System.err.println("Сервер вызвал исключение:");
            e.printStackTrace();
        }
    }
}

