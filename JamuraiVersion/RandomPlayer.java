import jamurai.*;
import java.util.*;

public class RandomPlayer extends Player {
	public final int[] cost = { 0, 4, 4, 4, 4, 2, 2, 2, 2, 1, 1 };
	public final int maxPower = 7;
	public Random rnd;

	public RandomPlayer() {
		this.rnd = new Random();
	}

	public GameInfo play(GameInfo info) {
		ArrayList<ArrayList<Integer>> action = possibleAction();
		int[] grade = new int[action.size()];
		int bestOne = 0;
		
		for(int i = 0; i < action.size(); i++){
			GameInfo infoCopy = new GameInfo(info, true);
			
			for(int j = 0; j < action.get(i).size(); j++){
				if(infoCopy.isValid(action.get(i).get(j))){
					infoCopy.virtualDoAction(action.get(i).get(j));
				}
			}
			
			grade[i] = evaluate(infoCopy);
		}
		
		for(int i = 0; i < grade.length; i++){
			if(grade[i] > grade[bestOne]){
				bestOne = i;
			}
		}
		
		for(int i = 0; i < action.get(bestOne).size(); i++){
			if(info.isValid(action.get(bestOne).get(i))){
				info.doAction(action.get(bestOne).get(i));
			}
		}

		return new GameInfo(info);
	}

	public GameInfo play1(GameInfo info) {
		int power = this.maxPower;

		if (info.samuraiInfo[info.weapon].hidden == 1) {
			if (cost[10] <= power && info.isValid(10)) {
				power -= cost[10];
				info.doAction(10);
			}
		}

		while (power >= 2) {
			int[] possibleAction = { 3, 2, 4, 1, 7, 6, 8, 5 };
			ArrayList<Integer> validAction = new ArrayList<Integer>();
			ArrayList<GameInfo> situation = new ArrayList<GameInfo>();
			ArrayList<Integer> grade = new ArrayList<Integer>();
			int bestChoice = 0;

			for (int action : possibleAction) {
				if (cost[action] <= power && info.isValid(action)) {
					validAction.add(action);
				}
			}

			for (int i = 0; i < validAction.size(); i++) {
				GameInfo infoCopy = new GameInfo(info, true);
				infoCopy.virtualDoAction(validAction.get(i));
				situation.add(new GameInfo(infoCopy));
			}

			for (int i = 0; i < situation.size(); i++) {
				grade.add(this.evaluate(situation.get(i)));
			}

			for (int i = 0; i < grade.size(); i++) {
				if (grade.get(i) > grade.get(bestChoice)) {
					bestChoice = i;
				}
			}

			power = power - cost[validAction.get(bestChoice)];
			info.doAction(validAction.get(bestChoice));
		}

		if (power > 0) {
			if (cost[9] <= power && info.isValid(9)) {
				power -= cost[9];
				info.doAction(9);
			}
		}

		return new GameInfo(info);
	}

	public GameInfo play0(GameInfo info) {
		int power = maxPower;
		int action;

		Boolean canAttack = false;
		int samuraiID = 0;
		int[][] attackField = this.markAttackArea(info);

		for (int i = 3; i < 6; i++) {
			if (info.samuraiInfo[i].curX != -1
					&& !(info.samuraiInfo[i].curX == info.samuraiInfo[i].homeX
							&& info.samuraiInfo[i].curY == info.samuraiInfo[i].homeY)
					&& attackField[info.samuraiInfo[i].curY][info.samuraiInfo[i].curX] == 6) {
				canAttack = true;
				samuraiID = i;
				break;
			}
		}

		if (canAttack) {
			int[] attackAction = this.attack(info, samuraiID);

			for (int i = 0; attackAction[i] != 0; i++) {
				power -= cost[attackAction[i]];
				info.doAction(attackAction[i]);
			}
		}

		while (power >= 2) {
			action = this.rnd.nextInt(10) + 1;

			if (cost[action] <= power && info.isValid(action)) {
				power -= cost[action];
				info.doAction(action);
			}
		}

		if (power != 0) {
			if (rnd.nextInt(11) > 8) {
				action = info.samuraiInfo[info.weapon].hidden == 1 ? 10 : 9;
				if (info.isValid(action)) {
					info.doAction(action);
				}
			}
		}

		return new GameInfo(info);
	}

	public int evaluate(GameInfo info) {
		int result = 0;

		for (int i = 3; i < GameInfo.PLAYER_NUM; i++) {
			if (info.samuraiInfo[i].curX == info.samuraiInfo[i].homeX
					&& info.samuraiInfo[i].curY == info.samuraiInfo[i].homeY) {
				result = result + 10000;
			}

			if (info.samuraiInfo[i].curX != -1) {
				int x = Math.max(info.samuraiInfo[info.weapon].curX, info.samuraiInfo[i].curX)
						- Math.min(info.samuraiInfo[info.weapon].curX, info.samuraiInfo[i].curX);
				int y = Math.max(info.samuraiInfo[info.weapon].curY, info.samuraiInfo[i].curY)
						- Math.min(info.samuraiInfo[info.weapon].curY, info.samuraiInfo[i].curY);

				if (x + y < 3) {
					result = result - 10000;
				}
			}
		}

		for (int i = 0; i < info.height; i++) {
			for (int j = 0; j < info.width; j++) {
				if (0 <= info.field[i][j] && info.field[i][j] <= 2) {
					result = result + 500;
				}
				if (3 <= info.field[i][j] && info.field[i][j] <= 5) {
					result = result - 500;
				}
			}
		}
        
        int dx = info.samuraiInfo[info.weapon].curX - 7;
		int dy = info.samuraiInfo[info.weapon].curY - 7;
		result = result - 5 * (dx * dx + dy * dy);

		return result;
	}

	public ArrayList<ArrayList<Integer>> possibleAction() {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>(2000);

		for (int i = 1; i <= 10; i++) {
			for (int j = 1; j <= 10; j++) {
				for (int m = 1; m <= 10; m++) {
					for (int n = 1; n <= 10; n++) {
						int allCost = cost[i] + cost[j] + cost[m] + cost[n];
						if (allCost <= maxPower) {
							result.add(new ArrayList<Integer>());
							result.get(result.size() - 1).add(i);
							result.get(result.size() - 1).add(j);
							result.get(result.size() - 1).add(m);
							result.get(result.size() - 1).add(n);
						}
					}
				}
			}
		}

		for (int i = 1; i <= 10; i++) {
			for (int j = 1; j <= 10; j++) {
				for (int m = 1; m <= 10; m++) {
					int allCost = cost[i] + cost[j] + cost[m];
					if (allCost <= maxPower) {
						result.add(new ArrayList<Integer>());
						result.get(result.size() - 1).add(i);
						result.get(result.size() - 1).add(j);
						result.get(result.size() - 1).add(m);
					}
				}
			}
		}

		for (int i = 1; i <= 10; i++) {
			for (int j = 1; j <= 10; j++) {
				int allCost = cost[i] + cost[j];
				if (allCost <= maxPower) {
					result.add(new ArrayList<Integer>());
					result.get(result.size() - 1).add(i);
					result.get(result.size() - 1).add(j);
				}
			}
		}

		for (int i = 1; i <= 10; i++) {
			int allCost = cost[i];
			if (allCost <= maxPower) {
				result.add(new ArrayList<Integer>());
				result.get(result.size() - 1).add(i);
			}
		}

		return result;
	}

	public int[][] markAttackArea(GameInfo info) {
		int[][] result = new int[info.height][info.width];

		for (int i = 0; i < info.height; i++) {
			for (int j = 0; j < info.width; j++) {
				result[i][j] = info.field[i][j];
			}
		}

		for (int i = 0; i < info.height; i++) {
			for (int j = 0; j < info.width; j++) {
				if (info.samuraiInfo[info.weapon].curY - 5 <= i && i <= info.samuraiInfo[info.weapon].curY + 5) {
					int distance = Math.max(i, info.samuraiInfo[info.weapon].curY)
							- Math.min(i, info.samuraiInfo[info.weapon].curY);

					switch (distance) {
					case 5:
						result[i][info.samuraiInfo[info.weapon].curX] = 6;
						break;
					case 4:
					case 3:
					case 2:
						if (info.samuraiInfo[info.weapon].curX - 1 <= j
								&& j <= info.samuraiInfo[info.weapon].curX + 1) {
							result[i][j] = 6;
						}
						break;
					case 1:
						if (info.samuraiInfo[info.weapon].curX - 4 <= j
								&& j <= info.samuraiInfo[info.weapon].curX + 4) {
							result[i][j] = 6;
						}
						break;
					case 0:
						if (info.samuraiInfo[info.weapon].curX - 5 <= j
								&& j <= info.samuraiInfo[info.weapon].curX + 5) {
							result[i][j] = 6;
						}
						break;
					}
				}
			}
		}

		return result;
	}

	public int[] attack(GameInfo info, int samuraiID) {
		int[] result = new int[3];

		int i = info.samuraiInfo[info.weapon].curX - info.samuraiInfo[samuraiID].curX;
		int j = info.samuraiInfo[info.weapon].curY - info.samuraiInfo[samuraiID].curY;

		if (i == 0 || j == 0) {
			if (i == 0) {
				if (-4 <= j && j <= 4) {
					if (j < 0) {
						result[0] = 1;
						result[1] = 0;
					} else {
						result[0] = 3;
						result[1] = 0;
					}
				} else {
					if (j < 0) {
						result[0] = 5;
						result[1] = 1;
						result[2] = 0;
					} else {
						result[0] = 7;
						result[1] = 3;
						result[2] = 0;
					}
				}
			} else {
				if (-4 <= i && i <= 4) {
					if (i < 0) {
						result[0] = 2;
						result[1] = 0;
					} else {
						result[0] = 4;
						result[1] = 0;
					}
				} else {
					if (i < 0) {
						result[0] = 6;
						result[1] = 2;
						result[2] = 0;
					} else {
						result[0] = 8;
						result[1] = 4;
						result[2] = 0;
					}
				}
			}
		} else {
			if (-1 <= i && i <= 1) {
				switch (i) {
				case -1:
					if (j < 0) {
						result[0] = 6;
						result[1] = 1;
						result[2] = 0;
					} else {
						result[0] = 6;
						result[1] = 3;
						result[2] = 0;
					}
					break;
				case 1:
					if (j < 0) {
						result[0] = 8;
						result[1] = 1;
						result[2] = 0;
					} else {
						result[0] = 8;
						result[1] = 3;
						result[2] = 0;
					}
					break;
				}
			} else {
				switch (j) {
				case -1:
					if (i < 0) {
						result[0] = 5;
						result[1] = 2;
						result[2] = 0;
					} else {
						result[0] = 5;
						result[1] = 4;
						result[2] = 0;
					}
					break;
				case 1:
					if (i < 0) {
						result[0] = 7;
						result[1] = 2;
						result[2] = 0;
					} else {
						result[0] = 7;
						result[1] = 4;
						result[2] = 0;
					}
					break;
				}
			}
		}

		return result;
	}
}