package GameServer;

import io.grpc.StatusException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface CheckeredGame {
    String getWinner() throws StatusException;
    boolean getGameEnded();
    boolean getGameStarted();
    List<Player> getPlayers();
    AtomicInteger getCurrentPlayerAtomic();
    int getPlayerScores(int index);
    void setGameEnded(boolean con);
    void setPlayerScores(int index, int score);
    void setGameStarted(boolean state);

    boolean isReady() throws StatusException;

    int getPlayerIndex(String playerName) throws StatusException;

    String getCurrentPlayer() throws StatusException;

    void makeLine(int row, int col, String ori, int playerIndex) throws StatusException;

    int getPlayerScore(int index);

    CheckeredBoard getBoard();

    boolean isGameOver() throws StatusException;


}
