package GameServer;

import io.grpc.Status;
import io.grpc.StatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



public class CheckeredGameImpl implements CheckeredGame {
    private CheckeredBoard board;
    private final AtomicInteger currentPlayer = new AtomicInteger(-1);
    private int numPlayers;
    private final List<Player> players = new ArrayList<>();
    private int[] playerScores;
    private boolean gameEnded;
    private boolean gameStarted = false;
    Object lock;

    public void setGameEnded(boolean con){this.gameEnded = con;}
    public int getPlayerScores(int index){
        return  this.playerScores[index];
    }
    public void setPlayerScores(int index, int score){
        this.playerScores[index] += score;
    }

    public AtomicInteger getCurrentPlayerAtomic(){
        return this.currentPlayer;
    }
    public void setGameStarted(boolean state){
        this.gameStarted = state;
    }
    public boolean getGameStarted(){
        return this.gameStarted;
    }
    public List<Player> getPlayers(){
        return this.players;
    }

    @Override
    public CheckeredBoard getBoard() {
        return this.board;
    }

    @Override
    public int getPlayerScore(int index) {
        return this.playerScores[index];
    }

    public CheckeredGameImpl(int numPlayers) {
        super();
        this.board = new CheckeredBoard(8, 8);
        this.currentPlayer.set(0);
        this.numPlayers = numPlayers;
        this.playerScores = new int[numPlayers];
        this.gameEnded = false;
        lock = new Object();
    }

    @Override
    public String getWinner() throws StatusException {
        if (!gameEnded) {
            return null;
        }

        int maxScore = -1;
        int winnerIndex = -1;
        for (int i = 0; i < numPlayers; i++) {
            if (playerScores[i] > maxScore) {
                maxScore = playerScores[i];
                winnerIndex = i;
            }
        }

        return players.get(winnerIndex).getName();
    }


    @Override
    public boolean isReady() throws StatusException {
        return (players.size() == numPlayers);
    }

    public int getPlayerIndex(String playerName) throws StatusException {
        synchronized (lock) {
            for (int i = 0; i < this.numPlayers; i++) {
                if (this.players.get(i).getName().equals(playerName)) {
                    return i;
                }
            }
            return -1;
        }
    }

    public String getCurrentPlayer() throws StatusException {
        synchronized (lock) {
            return players.get(currentPlayer.get()).getName();
        }
    }


    public void makeLine(int row, int col, String ori, int playerIndex) throws StatusException {
        synchronized (lock) {
            System.out.print(playerScores[0]);
            System.out.println("," + playerScores[1]);
            if (playerIndex != this.currentPlayer.get()) {
                Status status = Status.FAILED_PRECONDITION.withDescription("Сейчас не ваш ход!");
                throw new StatusException(status);
            }
            if (playerIndex == this.currentPlayer.get()) {
                board.setBorder(row, col, ori, true);
                if (board.getSquare(row, col).getRightBorder() && board.getSquare(row, col).getLeftBorder() && board.getSquare(row, col).getTopBorder() && board.getSquare(row, col).getBottomBorder() && (board.getSquare(row, col).getHasCheck() == -1)) {
                    board.getSquare(row, col).setHasCheck(currentPlayer.get());
                    playerScores[currentPlayer.get()] += 1;
                }
            }
            System.out.println(board.toString());
            if ((playerScores[currentPlayer.get()] >= 1) || (playerScores[0] + playerScores[1] == 8)) {
                gameEnded = true;
            }

            passTurn();

        }
    }


    void passTurn() {
        synchronized (lock) {
            if (currentPlayer.get() == 0) {
                currentPlayer.set(1);
            } else {
                currentPlayer.set(0);
            }
        }
    }
    public boolean getGameEnded(){
        return this.gameEnded;
    }
    public boolean isGameOver() throws StatusException {
        return gameEnded;
    }
}
