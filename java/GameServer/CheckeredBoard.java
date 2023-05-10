package GameServer;

public class CheckeredBoard {

    private static CheckeredSquare[][] squares;

    public CheckeredBoard(int x, int y) {
        squares = new CheckeredSquare[x][y];
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                squares[i][j] = new CheckeredSquare();
            }
        }
    }

    public static CheckeredSquare getSquare(int row, int col) {
        return squares[row][col];
    }

    public void setSquare(int row, int col, CheckeredSquare square) {
        squares[row][col] = square;
    }

    public static void setBorder(int row, int col, String border, boolean value) {
        CheckeredSquare square = getSquare(row, col);
        square.setBorder(border, value);
        if (border.equals("t") && row > 0) {
            getSquare(row - 1, col).setBorder("b", value);
        } else if (border.equals("b") && row < squares.length - 1) {
            getSquare(row + 1, col).setBorder("t", value);
        } else if (border.equals("l") && col > 0) {
            getSquare(row, col - 1).setBorder("r", value);
        } else if (border.equals("r") && col < squares[row].length - 1) {
            getSquare(row, col + 1).setBorder("l", value);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String BORDER = "+-----";
        final String CELL = "|     ";

        // Верхняя граница
        sb.append(BORDER.repeat(squares[0].length));
        sb.append("+\n");

        // Ряды
        for (int row = 0; row < squares.length; row++) {
            // клетки в рядах
            sb.append("|");
            for (int col = 0; col < squares[row].length; col++) {
                CheckeredSquare square = getSquare(row, col);

                // иконка
                sb.append(" ");
                int hasCheck = square.getHasCheck();
                if (hasCheck == -1) {
                    sb.append(" ");
                } else if (hasCheck == 0) {
                    sb.append("X");
                } else if (hasCheck == 1) {
                    sb.append("O");
                } else {
                    sb.append(" ");
                }
                sb.append(" ");

                // правая граница
                if (square.getRightBorder()) {
                    sb.append("|");
                } else {
                    sb.append(" ");
                }
            }
            sb.append("\n");

            // нижняя граница
            sb.append(BORDER.repeat(squares[row].length));
            sb.append("+\n");
        }
        return sb.toString();
    }
}