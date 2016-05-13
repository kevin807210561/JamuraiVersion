import java.util.ArrayList;

public class SamuraiInfo {
    public int homeX, homeY;
    public int curX, curY;
    public int rank, score, hidden;
    public ArrayList<Integer> possibleX;
    public ArrayList<Integer> possibleY;

    public SamuraiInfo() {
        this.homeX = 0;
        this.homeY = 0;
        this.curX = 0;
        this.curY = 0;
        this.rank = 0;
        this.score = 0;
        this.hidden = 0;
        this.possibleX = new ArrayList<>();
        this.possibleY = new ArrayList<>();
    }
}