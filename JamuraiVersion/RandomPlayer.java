import jamurai.*;
import java.util.*;

public class RandomPlayer extends Player {
	public final int[] cost = { 0, 4, 4, 4, 4, 2, 2, 2, 2, 1, 1 };
	public final int maxPower = 7;
	public Random rnd;

	public RandomPlayer() {
		this.rnd = new Random();
	}

	public GameInfo play(GameInfo Info) {
		ArrayList<ArrayList<Integer>> action = possibleAction(Info);
		int[] grade = new int[action.size()];
		int bestOne = 0;

		for(int i = 0; i < action.size(); i++){
			GameInfo infoCopy = new GameInfo(Info, true);

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
			if(Info.isValid(action.get(bestOne).get(i))){
				Info.doAction(action.get(bestOne).get(i));
			}
		}

		return new GameInfo(Info);
	}

	public int evaluate(GameInfo info) {
		int result = 0;
		int[] size = { 13, 7, 6 };
		int[][] ox = { {-1, -1, -1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1 }, { 0, 0, 0, 0, 1, 1, 2 }, { -1, 0, 0, 0, 1, 1 } };
		int[][] oy = { {2, 3, 4, 0, 1, 2, 3, 4, 5, 1, 2, 3, 4 }, { 0, 1, 2, 3, 1, 2, 1 }, { 2, 0, 1, 2, 1, 2 } };

		for (int enemy = 3; enemy < GameInfo.PLAYER_NUM; enemy++) {
			if (info.samuraiInfo[enemy].curX == info.samuraiInfo[enemy].homeX
					&& info.samuraiInfo[enemy].curY == info.samuraiInfo[enemy].homeY) {
				result = result + 1000000;
			}

			ArrayList<Integer> X = new ArrayList<>();
			ArrayList<Integer> Y = new ArrayList<>();
			if(info.samuraiInfo[enemy].curX != -1){
				X.add(info.samuraiInfo[enemy].curX);
				Y.add(info.samuraiInfo[enemy].curY);
			}else {
				for (int i = 0; i < info.samuraiInfo[enemy].possibleX.size(); i++) {
					X.add(info.samuraiInfo[enemy].possibleX.get(i));
					Y.add(info.samuraiInfo[enemy].possibleY.get(i));
				}
			}
			int counter = 0;

			for (int k = 0; k < X.size(); k++) {
				for (int direction = 0; direction < 4; direction++) {
					for (int i = 0; i < size[enemy - 3]; ++i) {
						int[] pos = this.rotate(direction, ox[enemy - 3][i], oy[enemy - 3][i]);
						pos[0] += X.get(k);
						pos[1] += Y.get(k);
						if (0 <= pos[0] && pos[0] < info.width && 0 <= pos[1] && pos[1] < info.height) {
							Boolean isHome = false;
							for (int j = 0; j < GameInfo.PLAYER_NUM; ++j) {
								if (info.samuraiInfo[j].homeX == pos[0] && info.samuraiInfo[j].homeY == pos[1]) {
									isHome = true;
								}
							}
							if (!isHome) {
								if(info.samuraiInfo[info.weapon].curX == pos[0] && info.samuraiInfo[info.weapon].curY == pos[1]){
									result = result - 500000;
									counter++;
								}
							}
						}
					}
				}
			}
			if (counter > 1){
				result = result - counter * 500000 + 500000;
			}
		}

		for (int i = 0; i < info.height; i++) {
			for (int j = 0; j < info.width; j++) {
				if (0 <= info.field[i][j] && info.field[i][j] <= 2) {
					result = result + 500;
				}
			}
		}

		int dx = info.samuraiInfo[info.weapon].curX - 7;
		int dy = info.samuraiInfo[info.weapon].curY - 7;
		result = result - 5 * (dx * dx + dy * dy);

		return result;
	}

	public ArrayList<ArrayList<Integer>> possibleAction(GameInfo info) {
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

		for(int i = 0; i < result.size() - 1; i++){
			GameInfo infoCopy1 = new GameInfo(info, true);

			for (int k = 0; k < result.get(i).size(); k++) {
				if (infoCopy1.isValid(result.get(i).get(k))) {
					infoCopy1.virtualDoAction(result.get(i).get(k));
				}
			}

			for(int j = i + 1; j < result.size(); j++){
				GameInfo infoCopy2 = new GameInfo(info, true);

				for (int k = 0; k < result.get(j).size(); k++) {
					if (infoCopy2.isValid(result.get(j).get(k))) {
						infoCopy2.virtualDoAction(result.get(j).get(k));
					}
				}

				if(infoCopy1.isSameAs(infoCopy2)){
					result.remove(j);
					j--;
				}
			}
		}

		return result;
	}

	public int[] rotate(int direction, int x0, int y0) {
		int[] res = { 0, 0 };
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