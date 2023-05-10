package GameClient;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.java.*;

import java.util.Scanner;


public class GameClient {
    static final String host = "localhost";
    static final int port = 1099;

    public CheckeredGameGrpc.CheckeredGameBlockingStub connect() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        return CheckeredGameGrpc.newBlockingStub(channel);
    }

    public GameClient() {
        try {
            start();
        } catch (Exception e) {
            System.err.println("Исключение при старте игры на клиенте:");
            e.printStackTrace();
        }
    }

    public void start() {
        CheckeredGameGrpc.CheckeredGameBlockingStub stub = connect();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ввведите имя игрока:");
        String playerName = scanner.nextLine();

        try {
            addPlayerRequest addPlayerRq = addPlayerRequest.newBuilder().setName(playerName).build();
            System.out.println(stub.addPlayer(addPlayerRq).getStatus());
            System.out.println("Ждем другого игрока...");
            isReadyRequest isReadyRq = isReadyRequest.newBuilder().build();

            while (!stub.isReady(isReadyRq).getReady()) {
                Thread.sleep(1000);
                isReadyRq = isReadyRequest.newBuilder().build();
            }

            System.out.println("Игроки подключились, начинаем игру...");
            getPlayerIndexRequest getPlayerIndexRq = getPlayerIndexRequest.newBuilder().setPlayerName(playerName).build();
            int playerIndex = stub.getPlayerIndex(getPlayerIndexRq).getIndex();
            boolean gameEnded = false;

            while (!gameEnded) {
                getCurentPlayerRequest getCurentPlayerRq = getCurentPlayerRequest.newBuilder().build();
                String currentPlayer = stub.getCurentPlayer(getCurentPlayerRq).getName();
                if (!playerName.equals(currentPlayer)) {
                    System.out.println("Игрок " + currentPlayer + " делает ход...");
                    while (!currentPlayer.equals(playerName)) {
                        Thread.sleep(1000);
                        getCurentPlayerRq = getCurentPlayerRequest.newBuilder().build();
                        currentPlayer = stub.getCurentPlayer(getCurentPlayerRq).getName();
                    }
                }
                getCurentPlayerRq = getCurentPlayerRequest.newBuilder().build();
                currentPlayer = stub.getCurentPlayer(getCurentPlayerRq).getName();
                if (playerName.equals(currentPlayer)) {
                    getPlayerScoreRequest getPlayerScoreRq = getPlayerScoreRequest.newBuilder().setIndex(0).build();
                    int playerScore_1 = stub.getPlayerScore(getPlayerScoreRq).getScore();
                    getPlayerScoreRq = getPlayerScoreRequest.newBuilder().setIndex(1).build();
                    int playerScore_2 = stub.getPlayerScore(getPlayerScoreRq).getScore();
                    System.out.println("Текущий счет:" + playerScore_1 + "," + playerScore_2);
                    System.out.println("Настала ваша очередь! Введите \"t\" для верхней линии, \"b\" для нижней линии, \"r\" для правой, \"l\" для левой:");

                    String input = scanner.nextLine();
                    switch (input) {
                        case "t": {
                            System.out.println("Введите строку и столбец (например, '2 3'):");
                            String[] coords = scanner.nextLine().split(" ");
                            int row = Integer.parseInt(coords[0]) - 1;
                            int col = Integer.parseInt(coords[1]) - 1;
                            makeLineRequest makeLineRq = makeLineRequest
                                    .newBuilder()
                                    .setRow(row)
                                    .setCol(col)
                                    .setOri("t")
                                    .setPindex(playerIndex)
                                    .build();
                            stub.makeLine(makeLineRq);
                            //game.makeLine(row, col, "t", playerIndex);
                            break;
                        }
                        case "b": {
                            System.out.println("Введите строку и столбец (например, '2 3'):");
                            String[] coords = scanner.nextLine().split(" ");
                            int row = Integer.parseInt(coords[0]) - 1;
                            int col = Integer.parseInt(coords[1]) - 1;
                            makeLineRequest makeLineRq = makeLineRequest
                                    .newBuilder()
                                    .setRow(row)
                                    .setCol(col)
                                    .setOri("b")
                                    .setPindex(playerIndex)
                                    .build();
                            stub.makeLine(makeLineRq);
                            //game.makeLine(row, col, "b", playerIndex);
                            break;
                        }
                        case "r": {
                            System.out.println("Введите строку и столбец (например, '2 3'):");
                            String[] coords = scanner.nextLine().split(" ");
                            int row = Integer.parseInt(coords[0]) - 1;
                            int col = Integer.parseInt(coords[1]) - 1;
                            makeLineRequest makeLineRq = makeLineRequest
                                    .newBuilder()
                                    .setRow(row)
                                    .setCol(col)
                                    .setOri("r")
                                    .setPindex(playerIndex)
                                    .build();
                            stub.makeLine(makeLineRq);
                            //game.makeLine(row, col, "r", playerIndex);

                            break;
                        }
                        case "l": {
                            System.out.println("Введите строку и столбец (например, '2 3'):");
                            String[] coords = scanner.nextLine().split(" ");
                            int row = Integer.parseInt(coords[0]) - 1;
                            int col = Integer.parseInt(coords[1]) - 1;
                            makeLineRequest makeLineRq = makeLineRequest
                                    .newBuilder()
                                    .setRow(row)
                                    .setCol(col)
                                    .setOri("l")
                                    .setPindex(playerIndex)
                                    .build();
                            stub.makeLine(makeLineRq);
                            //game.makeLine(row, col, "l", playerIndex);


                            break;
                        }
                        default:
                            System.out.println("Неверный ввод. Попробуйте еще раз.");
                            break;
                    }
                }
                isGameOverRequest isGameOverRq = isGameOverRequest.newBuilder().build();
                if (stub.isGameOver(isGameOverRq).getIsGameOver()) {
                    gameEnded = true;
                    getWinnerRequest getWinnerRq = getWinnerRequest.newBuilder().build();
                    String winner = stub.getWinner(getWinnerRq).getWinner();
                    System.out.println("Игра окончена! Победителем становится " + winner + ".");
                }
            }

        } catch (Exception e) {
            System.err.println("Исключение из клиента игры:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GameClient client = new GameClient();
    }
}
