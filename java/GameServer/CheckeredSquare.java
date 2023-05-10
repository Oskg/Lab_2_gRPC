package GameServer;

public class CheckeredSquare {
    private boolean topBorder;
    private boolean bottomBorder;
    private boolean leftBorder;
    private boolean rightBorder;
    private int hasCheck;


    public CheckeredSquare() {
        this.topBorder = false;
        this.bottomBorder = false;
        this.leftBorder = false;
        this.rightBorder = false;
        this.hasCheck = -1;
    }

    public void setBorder(String border, boolean value) {
        if (border.equals("t")) {
            this.setTopBorder(value);
        }
        if (border.equals("b")) {
            this.setBottomBorder(value);
        }
        if (border.equals("r")) {
            this.setRightBorder(value);
        }
        if (border.equals("l")) {
            this.setLeftBorder(value);
        }
    }

    public boolean getTopBorder() {
        return topBorder;
    }

    public void setTopBorder(boolean topBorder) {
        this.topBorder = topBorder;
    }

    public boolean getBottomBorder() {
        return bottomBorder;
    }

    public void setBottomBorder(boolean bottomBorder) {
        this.bottomBorder = bottomBorder;
    }

    public boolean getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(boolean leftBorder) {
        this.leftBorder = leftBorder;
    }

    public boolean getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(boolean rightBorder) {
        this.rightBorder = rightBorder;
    }

    public int getHasCheck() {
        return hasCheck;
    }

    public void setHasCheck(int hasCheck) {
        this.hasCheck = hasCheck;
    }

    @Override
    public String toString() {
        String top = topBorder ? "_" : " ";
        String bottom = bottomBorder ? "_" : " ";
        String left = leftBorder ? "|" : " ";
        String right = rightBorder ? "|" : " ";
        String check = "";
        if (hasCheck == -1) {
            check = " ";
        }
        if (hasCheck == 0) {
            check = "X";
        }
        if (hasCheck == 1) {
            check = "O";
        }
        return String.format(" %s%s%s \n%s%s%s%s%s%s", top, top, top, left, check, " ", right, bottom, bottom);
    }
}