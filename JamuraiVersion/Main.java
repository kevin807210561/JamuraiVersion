import jamurai.*;
import java.io.*;

public class Main{
    public static void main(String[] argv){
	GameInfo info = new GameInfo();
	Player p = new RandomPlayer();

	while (true){
	    info.readTurnInfo();
	    System.out.println("# Turn "+info.turn);
	    if (info.curePeriod != 0){
		System.out.println("0");
	    }
	    else {
		p.play(info);
		System.out.println("0");
	    }
	}
    }
}
