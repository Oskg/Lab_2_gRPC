package GameServer;


import io.grpc.Status;
import org.example.java.CheckeredGameGrpc;

public class CheckeredGameService extends CheckeredGameGrpc.CheckeredGameImplBase {
    int numPlayers = 2;

    CheckeredGame game = new CheckeredGameImpl(numPlayers);

    @Override
    public void addPlayer(org.example.java.addPlayerRequest request,
                          io.grpc.stub.StreamObserver<org.example.java.appPlayerResponse> responseObserver)  {
        if (game.getGameStarted()) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Игра уже началась!");
            responseObserver.onError(status.asRuntimeException());

        }

        if (game.getPlayers().size() == numPlayers) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Максимум игроков достигнут");
            responseObserver.onError(status.asRuntimeException());
        }

        if (game.getPlayers().stream().anyMatch(p -> p.getName().equals(request.getName()))) {
            Status status = Status.FAILED_PRECONDITION.withDescription("Имя уже существует");
            responseObserver.onError(status.asRuntimeException());

        }

        game.getPlayers().add(new Player(request.getName()));
        System.out.println("Игрок '" + request.getName() + "' присоединился.");

        if (game.getPlayers().size() == 2) {
            game.setGameStarted(true);
            System.out.println("Все подключились - начинаем!");
        }
        org.example.java.appPlayerResponse response = org.example.java.appPlayerResponse.newBuilder()
                .setStatus(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void getPlayerIndex(org.example.java.getPlayerIndexRequest request,
                               io.grpc.stub.StreamObserver<org.example.java.getPlayerIndexResponse> responseObserver) {

            for (int i = 0; i < 2; i++) {
                if (game.getPlayers().get(i).getName().equals(request.getPlayerName())) {
                    org.example.java.getPlayerIndexResponse response = org.example.java.getPlayerIndexResponse.newBuilder()
                            .setIndex(i)
                            .build();
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                    return;
                }
            }
            org.example.java.getPlayerIndexResponse response = org.example.java.getPlayerIndexResponse.newBuilder()
                    .setIndex(-1)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();


    }

    @Override
    public void getCurentPlayer(org.example.java.getCurentPlayerRequest request,
                                io.grpc.stub.StreamObserver<org.example.java.getCurentPlayerResponse> responseObserver) {
            org.example.java.getCurentPlayerResponse response = org.example.java.getCurentPlayerResponse.newBuilder()
                    .setName(this.game.getPlayers().get(this.game.getCurrentPlayerAtomic().get()).getName())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
    }

    @Override
    public void makeLine(org.example.java.makeLineRequest request,
                         io.grpc.stub.StreamObserver<org.example.java.makeLineResponse> responseObserver)  {
            System.out.print(game.getPlayerScores(0));
            System.out.println("," + game.getPlayerScores(1));
            if (request.getPindex() != this.game.getCurrentPlayerAtomic().get()) {
                Status status = Status.FAILED_PRECONDITION.withDescription("Сейчас не ваш ход!");
                responseObserver.onError(status.asRuntimeException());
            }
            if (request.getPindex() == this.game.getCurrentPlayerAtomic().get()) {
                this.game.getBoard().setBorder(request.getRow(), request.getCol(), request.getOri(), true);
                if (this.game.getBoard().getSquare(request.getRow(), request.getCol()).getRightBorder() && this.game.getBoard().getSquare(request.getRow(), request.getCol()).getLeftBorder() && this.game.getBoard().getSquare(request.getRow(), request.getCol()).getTopBorder() && this.game.getBoard().getSquare(request.getRow(), request.getCol()).getBottomBorder() && (this.game.getBoard().getSquare(request.getRow(), request.getCol()).getHasCheck() == -1)) {
                    this.game.getBoard().getSquare(request.getRow(), request.getCol()).setHasCheck(this.game.getCurrentPlayerAtomic().get());
                    this.game.setPlayerScores(this.game.getCurrentPlayerAtomic().get(), 1);
                }
            }
            System.out.println(this.game.getBoard().toString());
            if ((this.game.getPlayerScores(this.game.getCurrentPlayerAtomic().get()) >= 1) || (this.game.getPlayerScores(0) + this.game.getPlayerScores(1) == 8)) {
                this.game.setGameEnded(true);
            }


            if (this.game.getCurrentPlayerAtomic().get() == 0) {
                this.game.getCurrentPlayerAtomic().set(1);
            } else {
                this.game.getCurrentPlayerAtomic().set(0);
            }
        org.example.java.makeLineResponse response = org.example.java.makeLineResponse.newBuilder()
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void isGameOver(org.example.java.isGameOverRequest request,
                           io.grpc.stub.StreamObserver<org.example.java.isGameOverResponse> responseObserver) {
        org.example.java.isGameOverResponse response = org.example.java.isGameOverResponse.newBuilder()
                .setIsGameOver(this.game.getGameEnded())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    @Override
    public void getPlayerScore(org.example.java.getPlayerScoreRequest request,
                               io.grpc.stub.StreamObserver<org.example.java.getPlayerScoreResponse> responseObserver) {
        org.example.java.getPlayerScoreResponse response = org.example.java.getPlayerScoreResponse.newBuilder()
                .setScore(this.game.getPlayerScores(request.getIndex()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }
    @Override
    public void isReady(org.example.java.isReadyRequest request,
                        io.grpc.stub.StreamObserver<org.example.java.isReadyResponse> responseObserver) {
        org.example.java.isReadyResponse response = org.example.java.isReadyResponse.newBuilder()
                .setReady(this.game.getPlayers().size() == 2)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getWinner(org.example.java.getWinnerRequest request,
                          io.grpc.stub.StreamObserver<org.example.java.getWinnerResponse> responseObserver) {
        if (!this.game.getGameEnded()) {
            org.example.java.getWinnerResponse response = org.example.java.getWinnerResponse.newBuilder()
                    .setWinner(null)
                    .build();
            responseObserver.onNext(response);
        }

        int maxScore = -1;
        int winnerIndex = -1;
        for (int i = 0; i < numPlayers; i++) {
            if (this.game.getPlayerScores(i) > maxScore) {
                maxScore = this.game.getPlayerScores(i);
                winnerIndex = i;
            }
        }
        org.example.java.getWinnerResponse response = org.example.java.getWinnerResponse.newBuilder()
                .setWinner(this.game.getPlayers().get(winnerIndex).getName())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}

