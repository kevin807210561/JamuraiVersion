import jamurai.*;
import java.io.*;
import java.util.*;

public class GameInfo {
    public static final int PLAYER_NUM = 6;
    public static BufferedReader stdReader;
    public int turns;
    public int side;
    public int weapon;
    public int width, height;
    public int maxCure;
    public SamuraiInfo[] samuraiInfo;
    public int turn, curePeriod;
    public int[][] field;

    public GameInfo(GameInfo info, Boolean isCopy) {
        if (isCopy) {
            this.turns = info.turns;
            this.side = info.side;
            this.weapon = info.weapon;
            this.width = info.width;
            this.height = info.height;
            this.maxCure = info.maxCure;
            this.turn = info.turn;
            this.curePeriod = info.curePeriod;

            this.samuraiInfo = new SamuraiInfo[GameInfo.PLAYER_NUM];
            for (int i = 0; i < GameInfo.PLAYER_NUM; i++) {
                this.samuraiInfo[i] = new SamuraiInfo();
                this.samuraiInfo[i].homeX = info.samuraiInfo[i].homeX;
                this.samuraiInfo[i].homeY = info.samuraiInfo[i].homeY;
                this.samuraiInfo[i].curX = info.samuraiInfo[i].curX;
                this.samuraiInfo[i].curY = info.samuraiInfo[i].curY;
                this.samuraiInfo[i].rank = info.samuraiInfo[i].rank;
                this.samuraiInfo[i].score = info.samuraiInfo[i].score;
                this.samuraiInfo[i].hidden = info.samuraiInfo[i].hidden;
                for (int j = 0; j < info.samuraiInfo[i].possibleX.size(); j++) {
                    this.samuraiInfo[i].possibleX.add(info.samuraiInfo[i].possibleX.get(j));
                    this.samuraiInfo[i].possibleY.add(info.samuraiInfo[i].possibleY.get(j));
                }
            }

            this.field = new int[this.height][this.width];
            for (int i = 0; i < info.height; i++) {
                for (int j = 0; j < info.width; j++) {
                    this.field[i][j] = info.field[i][j];
                }
            }
        }
    }

    public GameInfo(GameInfo info) {
        this.turns = info.turns;
        this.side = info.side;
        this.weapon = info.weapon;
        this.width = info.width;
        this.height = info.height;
        this.maxCure = info.maxCure;
        this.samuraiInfo = info.samuraiInfo;
        this.turn = info.turn;
        this.curePeriod = info.curePeriod;
        this.field = info.field;
    }

    public GameInfo() {
        GameInfo.stdReader = new BufferedReader(new InputStreamReader(System.in));

        String[] res = this.read();

        this.turns = Integer.parseInt(res[0]);
        this.side = Integer.parseInt(res[1]);
        this.weapon = Integer.parseInt(res[2]);
        this.width = Integer.parseInt(res[3]);
        this.height = Integer.parseInt(res[4]);
        this.maxCure = Integer.parseInt(res[5]);
        this.samuraiInfo = new SamuraiInfo[GameInfo.PLAYER_NUM];
        for (int i = 0; i < GameInfo.PLAYER_NUM; ++i) {
            this.samuraiInfo[i] = new SamuraiInfo();
        }
        for (int i = 0; i < GameInfo.PLAYER_NUM; ++i) {
            res = this.read();
            this.samuraiInfo[i].homeX = Integer.parseInt(res[0]);
            this.samuraiInfo[i].homeY = Integer.parseInt(res[1]);
        }
        for (int i = 0; i < GameInfo.PLAYER_NUM; ++i) {
            res = this.read();
            this.samuraiInfo[i].rank = Integer.parseInt(res[0]);
            this.samuraiInfo[i].score = Integer.parseInt(res[1]);
        }
        this.turn = 0;
        this.curePeriod = 0;
        this.field = new int[this.height][this.width];
        System.out.println("0");
    }

    public String[] read() {
        String line = "";
        try {
            for (line = GameInfo.stdReader.readLine(); line.startsWith("#"); line = GameInfo.stdReader.readLine())
                ;
        } catch (Exception e) {
            e.getStackTrace();
            System.exit(-1);
        }
        return line.split("\\s");
    }

    public void readTurnInfo() {
        String[] res = this.read();

        if (res.length == 0) {
            System.exit(-1);
        }

        this.turn = Integer.parseInt(res[0]);

        if (this.turn < 0) {
            System.exit(-1);
        }

        res = this.read();
        this.curePeriod = Integer.parseInt(res[0]);

        for (int i = 0; i < GameInfo.PLAYER_NUM; ++i) {
            res = this.read();
            this.samuraiInfo[i].curX = Integer.parseInt(res[0]);
            this.samuraiInfo[i].curY = Integer.parseInt(res[1]);
            this.samuraiInfo[i].hidden = Integer.parseInt(res[2]);
        }

        for (int i = 0; i < this.height; ++i) {
            Arrays.fill(this.field[i], 0);
        }

        for (int i = 0; i < this.height; ++i) {
            res = this.read();

            for (int j = 0; j < this.width; ++j) {
                this.field[i][j] = Integer.parseInt(res[j + 1]);
            }
        }
    }

    public Boolean isValid(int action) {
        SamuraiInfo myself = this.samuraiInfo[this.weapon];
        int curX = myself.curX;
        int curY = myself.curY;

        if (action >= 0 && action <= 4) {
            return myself.hidden == 0;
        }

        if (action >= 5 && action <= 8) {
            if (action == 5) {
                curX = curX;
                curY = curY + 1;
            }
            if (action == 6) {
                curX = curX + 1;
                curY = curY;
            }
            if (action == 7) {
                curX = curX;
                curY = curY - 1;
            }
            if (action == 8) {
                curX = curX - 1;
                curY = curY;
            }
            if (curX < 0 || this.width <= curX || curY < 0 || this.height <= curY) {
                return false;
            }
            if (myself.hidden == 1 && this.field[curY][curX] >= 3) {
                return false;
            }
            for (int i = 0; i < GameInfo.PLAYER_NUM; ++i) {
                if (curX == this.samuraiInfo[i].curX && curY == this.samuraiInfo[i].curY) {
                    return false;
                }
                if (i != this.weapon && (curX == this.samuraiInfo[i].homeX && curY == this.samuraiInfo[i].homeY)) {
                    return false;
                }
            }
            return true;
        }

        if (action == 9) {
            if (myself.hidden == 1) {
                return false;
            }
            if (this.field[curY][curX] >= 3) {
                return false;
            }
            return true;
        }

        if (action == 10) {
            if (myself.hidden != 1) {
                return false;
            }
            for (int i = 0; i < GameInfo.PLAYER_NUM; ++i) {
                SamuraiInfo other = this.samuraiInfo[i];
                if (other.hidden != 1 && (other.curX == curX && other.curY == curY)) {
                    return false;
                }
            }
            return true;
        }

        System.exit(-1);
        return false;
    }

    public int[] rotate(int direction, int x0, int y0) {
        int[] res = {0, 0};
        if (direction == 0) {
            res[0] = x0;
            res[1] = y0;
        }
        if (direction == 1) {
            res[0] = y0;
            res[1] = -x0;
        }
        if (direction == 2) {
            res[0] = -x0;
            res[1] = -y0;
        }
        if (direction == 3) {
            res[0] = -y0;
            res[1] = x0;
        }
        return res;
    }

    public void occupy(int direction) {
        SamuraiInfo myself = this.samuraiInfo[this.weapon];
        int curX = myself.curX;
        int curY = myself.curY;
        int[] size = {4, 5, 7};
        int[][] ox = {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 1, 1, 2, 0, 0}, {-1, -1, -1, 0, 1, 1, 1}};
        int[][] oy = {{1, 2, 3, 4}, {1, 2, 0, 1, 0}, {-1, 0, 1, 1, 1, -1, 0}};

        for (int i = 0; i < size[this.weapon]; ++i) {
            int[] pos = this.rotate(direction, ox[this.weapon][i], oy[this.weapon][i]);
            pos[0] += curX;
            pos[1] += curY;
            if (0 <= pos[0] && pos[0] < this.width && 0 <= pos[1] && pos[1] < this.height) {
                Boolean isHome = false;
                for (int j = 0; j < GameInfo.PLAYER_NUM; ++j) {
                    if (this.samuraiInfo[j].homeX == pos[0] && this.samuraiInfo[j].homeY == pos[1]) {
                        isHome = true;
                    }
                }
                if (!isHome) {
                    this.field[pos[1]][pos[0]] = this.weapon;
                    for (int j = 3; j < GameInfo.PLAYER_NUM; ++j) {
                        SamuraiInfo si = this.samuraiInfo[j];
                        if (si.curX == pos[0] && si.curY == pos[1]) {
                            si.curX = si.homeX;
                            si.curY = si.homeY;
                            si.hidden = 0;
                            this.samuraiInfo[j] = si;
                        }
                    }
                }
            }
        }
    }

    public void doAction(int action) {
        SamuraiInfo myself = this.samuraiInfo[this.weapon];
        int curX = myself.curX;
        int curY = myself.curY;

        if (action >= 1 && action <= 4) {
            this.occupy(action - 1);
        }
        if (action == 5) {
            curY += 1;
        }
        if (action == 6) {
            curX += 1;
        }
        if (action == 7) {
            curY -= 1;
        }
        if (action == 8) {
            curX -= 1;
        }
        if (action == 9) {
            myself.hidden = 1;
        }
        if (action == 10) {
            myself.hidden = 0;
        }
        myself.curX = curX;
        myself.curY = curY;
        this.samuraiInfo[this.weapon] = myself;
        System.out.print(action + " ");
    }

    public void virtualDoAction(int action) {
        SamuraiInfo myself = this.samuraiInfo[this.weapon];
        int curX = myself.curX;
        int curY = myself.curY;

        if (action >= 1 && action <= 4) {
            this.occupy(action - 1);
        }
        if (action == 5) {
            curY += 1;
        }
        if (action == 6) {
            curX += 1;
        }
        if (action == 7) {
            curY -= 1;
        }
        if (action == 8) {
            curX -= 1;
        }
        if (action == 9) {
            myself.hidden = 1;
        }
        if (action == 10) {
            myself.hidden = 0;
        }
        myself.curX = curX;
        myself.curY = curY;
        this.samuraiInfo[this.weapon] = myself;
    }
}