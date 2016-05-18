import jamurai.*;
import java.util.ArrayList;

/**
 * Created by kevin on 2016/5/12.
 */
public class EnemyLocaInferer {
    public static void infer(GameInfo curInfo, GameInfo preInfo) {
        Differences diffs = EnemyLocaInferer.searchDiffs(curInfo, preInfo);
        int[] size = {4, 5, 7};
        int[][] ox = {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 1, 1, 2, 0, 0}, {-1, -1, -1, 0, 1, 1, 1}};
        int[][] oy = {{1, 2, 3, 4}, {1, 2, 0, 1, 0}, {-1, 0, 1, 1, 1, -1, 0}};
        for (int enemy = 3; enemy < 6; enemy++) {
            for (int i = 0; i < curInfo.samuraiInfo[enemy].possibleX.size(); i++) {
                curInfo.samuraiInfo[enemy].possibleX.remove(i);
                curInfo.samuraiInfo[enemy].possibleY.remove(i);
            }
        }

        for (int enemy = 3; enemy < 6; enemy++) {
            boolean canInfer1;
            boolean canInfer2 = false;
            boolean canInfer;

            canInfer1 = curInfo.samuraiInfo[enemy].curX == -1 && enemy != curInfo.weapon + 3;
            for (int i = 0; i < diffs.changeID.size(); i++) {
                if (diffs.changeID.get(i) == enemy) {
                    canInfer2 = true;
                    break;
                }
            }
            canInfer = canInfer1 && canInfer2;

            if (canInfer) {
                ArrayList<Integer> tempPossibleX = new ArrayList<>();
                ArrayList<Integer> tempPossibleY = new ArrayList<>();
                int counter1 = 0;

                for (int k = 0; k < diffs.changeID.size(); k++) {
                    if (diffs.changeID.get(k) == enemy) {
                        counter1++;
                    }
                }

                for (int i = 0; i < curInfo.height; i++) {
                    for (int j = 0; j < curInfo.width; j++) {
                        for (int direction = 0; direction < 4; direction++) {
                            boolean isPossible;
                            boolean containAllDiffs = false;
                            boolean allEnemyAndNine = true;
                            int counter2 = 0;

                            for (int k = 0; k < size[enemy - 3]; ++k) {
                                int[] pos = EnemyLocaInferer.rotate(direction, ox[enemy - 3][k], oy[enemy - 3][k]);
                                pos[0] += j;
                                pos[1] += i;
                                if (0 <= pos[0] && pos[0] < preInfo.width && 0 <= pos[1] && pos[1] < preInfo.height) {
                                    Boolean isHome = false;

                                    for (int l = 0; l < GameInfo.PLAYER_NUM; ++l) {
                                        if (preInfo.samuraiInfo[l].homeX == pos[0] && preInfo.samuraiInfo[l].homeY == pos[1]) {
                                            isHome = true;
                                        }
                                    }

                                    if (!isHome) {
                                        for (int l = 0; l < diffs.changeID.size(); l++) {
                                            if (diffs.changeID.get(l) == enemy) {
                                                if (pos[0] == diffs.changedX.get(l) && pos[1] == diffs.changedY.get(l)) {
                                                    counter2++;
                                                    break;
                                                }
                                            }
                                        }

                                        if (curInfo.field[pos[1]][pos[0]] != enemy && curInfo.field[pos[1]][pos[0]] != 9) {
                                            allEnemyAndNine = false;
                                        }
                                    }
                                }
                            }

                            if (counter1 == counter2) {
                                containAllDiffs = true;
                            }

                            isPossible = containAllDiffs && allEnemyAndNine;

                            if (isPossible) {
                                curInfo.samuraiInfo[enemy].possibleX.add(j);
                                curInfo.samuraiInfo[enemy].possibleY.add(i);
                            }
                            if (containAllDiffs) {
                                tempPossibleX.add(j);
                                tempPossibleY.add(i);
                                break;
                            }
                        }
                    }
                }

                if (curInfo.samuraiInfo[enemy].possibleX.size() == 0) {
                    for (int i = 0; i < tempPossibleX.size(); i++) {
                        curInfo.samuraiInfo[enemy].possibleX.add(tempPossibleX.get(i));
                        curInfo.samuraiInfo[enemy].possibleY.add(tempPossibleY.get(i));
                    }
                }

                for (int i = 0; i < curInfo.samuraiInfo[enemy].possibleX.size(); i++) {
                    int x = curInfo.samuraiInfo[enemy].possibleX.get(i);
                    int y = curInfo.samuraiInfo[enemy].possibleY.get(i);
                    if (curInfo.field[y][x] == 8 || curInfo.field[y][x] < 3) {
                        curInfo.samuraiInfo[enemy].possibleX.remove(i);
                        curInfo.samuraiInfo[enemy].possibleY.remove(i);
                    }
                }

                if (curInfo.samuraiInfo[enemy].possibleX.size() == 1) {
                    if (preInfo.samuraiInfo[enemy].curX != -1 && (preInfo.samuraiInfo[enemy].curX != curInfo.samuraiInfo[enemy].possibleX.get(0) || preInfo.samuraiInfo[enemy].curY != curInfo.samuraiInfo[enemy].possibleY.get(0))) {
                        curInfo.samuraiInfo[enemy].curX = curInfo.samuraiInfo[enemy].possibleX.get(0);
                        curInfo.samuraiInfo[enemy].curY = curInfo.samuraiInfo[enemy].possibleY.get(0);
                        curInfo.samuraiInfo[enemy].possibleX.remove(0);
                        curInfo.samuraiInfo[enemy].possibleY.remove(0);
                    }

                    if (preInfo.samuraiInfo[enemy].curX == -1) {
                        curInfo.samuraiInfo[enemy].curX = curInfo.samuraiInfo[enemy].possibleX.get(0);
                        curInfo.samuraiInfo[enemy].curY = curInfo.samuraiInfo[enemy].possibleY.get(0);
                        curInfo.samuraiInfo[enemy].possibleX.remove(0);
                        curInfo.samuraiInfo[enemy].possibleY.remove(0);
                    }
                }
            }
        }
    }

    public static Differences searchDiffs(GameInfo curInfo, GameInfo preInfo) {
        Differences result = new Differences();

        for (int i = 0; i < curInfo.height; i++) {
            for (int j = 0; j < curInfo.width; j++) {
                if (preInfo.field[i][j] != 9) {
                    if (curInfo.field[i][j] != preInfo.field[i][j]) {
                        result.changedX.add(j);
                        result.changedY.add(i);
                        result.changeID.add(curInfo.field[i][j]);
                    }
                }
            }
        }

        return result;
    }

    public static int[] rotate(int direction, int x0, int y0) {
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
}
