package genetic.maze;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Maze {
	
	public static final int TOP_E = 1;
	public static final int DOWN_E = 3;
	public static final int LEFT_E = 5;
	public static final int RIGHT_E = 10;
	
	public static final Map<Integer, Integer> STATES = new LinkedHashMap<Integer, Integer>();
	public static final Map<Integer, String> EMISSIONS = new LinkedHashMap<Integer, String>();
	
	static {
		setup();
	}
	
	
	private Map<String, Set<Integer>> enviornment = null;
	private Map<String, Integer> twoDToOneD = null;
	private Map<Integer, String> oneDToTwoD = null;
	private char [][] map = null;
	private int spaces = 0;
	
	public Maze(char [][] map, int spaces, Map<String, 
			Set<Integer>> environment, Map<String, Integer> twoDToOneD, Map<Integer, String> oneDToTwoD) {
		this.map = map;
		this.spaces = spaces;
		this.enviornment = environment;
		this.twoDToOneD = twoDToOneD;
		this.oneDToTwoD = oneDToTwoD;
	}
	
	public Map<String, Set<Integer>> getEnviornment() {
		return enviornment;
	}

	public Map<String, Integer> getPositions() {
		return twoDToOneD;
	}
	
	public Map<Integer, String> getLocations() {
		return oneDToTwoD;
	}
	
	public int getPosition(int row, int col) {
		return twoDToOneD.get(String.valueOf(row) + ":" + String.valueOf(col));
	}
	
	public String getLocation(int location) {
		return oneDToTwoD.get(location);
	}
	
	public Set<Integer> getFreedom(int row, int col) {
		return enviornment.get(String.valueOf(row) + ":" + String.valueOf(col));	
	}
	
	public int getHeight() {
		return map.length;
	}
	
	public int getWidth() {
		return map[0].length;
	}
	
	public int getSpaces() {
		return spaces;
	}

	public char [][] getMap() {
		return map;
	}
	
	public Maze clone() {
		
		char mmap[][] = new char[map.length][map[0].length];
		
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				mmap[i][j] = map[i][j];
			}
		}
		
		return new Maze(mmap, spaces, enviornment, twoDToOneD, oneDToTwoD);
	}
	
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < getWidth() + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		for (int i = 0; i < getHeight(); i++) {
			
			builder.append("|");
			for (int j = 0; j <getWidth(); j++) {
				builder.append(getMap()[i][j]);
			}
			builder.append("|\n");
		}
		
		
		for (int i = 0; i < getWidth() + 2; i++)
			builder.append("-");
		
		builder.append("\n");
		
		builder.append("Total Free Cells: [" + spaces + "]\n");
		
		builder.append(enviornment.entrySet().stream().map(p -> 
				"{" + 
					p.getKey() + ":" + 
					p.getValue().stream().map(k -> MazeGame.Move.ACTIONS[k])
						.collect(Collectors.joining("|")) +
				"}").collect(Collectors.joining(", ")));
		
		builder.append("\n");
		
		builder.append("2D => 1D: ");
		builder.append(twoDToOneD.entrySet().stream().map(
				p -> "{" + p.getKey() + "=>" + p.getValue() + "}")
						.collect(Collectors.joining(", ")));
		
		builder.append("\n");
		
		builder.append("1D => 2D: ");
		
		builder.append(oneDToTwoD.entrySet().stream().map(
				p -> "{" + p.getKey() + "=>" + p.getValue() + "}")
						.collect(Collectors.joining(", ")));
		
		builder.append("\n");
		
		return builder.toString();
	}
	
	
	private static void setup() {
		
		EMISSIONS.put(TOP_E, MazeGame.Move.ACTIONS[MazeGame.UP]);
		EMISSIONS.put(DOWN_E, MazeGame.Move.ACTIONS[MazeGame.DOWN]);
		EMISSIONS.put(RIGHT_E, MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		EMISSIONS.put(LEFT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT]);
		
		EMISSIONS.put(TOP_E + DOWN_E, MazeGame.Move.ACTIONS[MazeGame.UP] + MazeGame.Move.ACTIONS[MazeGame.DOWN]);
		EMISSIONS.put(TOP_E + LEFT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT] + MazeGame.Move.ACTIONS[MazeGame.UP]);
		EMISSIONS.put(TOP_E + RIGHT_E, MazeGame.Move.ACTIONS[MazeGame.UP] + MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		EMISSIONS.put(DOWN_E + LEFT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT] + MazeGame.Move.ACTIONS[MazeGame.DOWN]);
		EMISSIONS.put(DOWN_E + RIGHT_E, MazeGame.Move.ACTIONS[MazeGame.DOWN] + MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		EMISSIONS.put(LEFT_E + RIGHT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT] + MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		
		EMISSIONS.put(LEFT_E + TOP_E + RIGHT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT] + MazeGame.Move.ACTIONS[MazeGame.UP] + MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		EMISSIONS.put(LEFT_E + DOWN_E + RIGHT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT] + MazeGame.Move.ACTIONS[MazeGame.DOWN] + MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		EMISSIONS.put(TOP_E + DOWN_E + RIGHT_E, MazeGame.Move.ACTIONS[MazeGame.UP] + MazeGame.Move.ACTIONS[MazeGame.DOWN] + MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		EMISSIONS.put(TOP_E + DOWN_E + LEFT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT] + MazeGame.Move.ACTIONS[MazeGame.UP] + MazeGame.Move.ACTIONS[MazeGame.DOWN]);
		
		EMISSIONS.put(RIGHT_E + TOP_E + DOWN_E + LEFT_E, MazeGame.Move.ACTIONS[MazeGame.LEFT] + MazeGame.Move.ACTIONS[MazeGame.UP] + MazeGame.Move.ACTIONS[MazeGame.DOWN] + MazeGame.Move.ACTIONS[MazeGame.RIGHT]);
		
		STATES.put(TOP_E, 0);
		STATES.put(DOWN_E, 1);
		STATES.put(RIGHT_E, 2);
		STATES.put(LEFT_E, 3);
		
		STATES.put(TOP_E + DOWN_E, 4);
		STATES.put(TOP_E + LEFT_E, 5);
		STATES.put(TOP_E + RIGHT_E, 6);
		STATES.put(DOWN_E + LEFT_E, 7);
		STATES.put(DOWN_E + RIGHT_E, 8);
		STATES.put(LEFT_E + RIGHT_E, 9);
		
		STATES.put(LEFT_E + TOP_E + RIGHT_E, 10);
		STATES.put(LEFT_E + DOWN_E + RIGHT_E, 11);
		STATES.put(TOP_E + DOWN_E + RIGHT_E, 12);
		STATES.put(TOP_E + DOWN_E + LEFT_E, 13);
		
		STATES.put(RIGHT_E + TOP_E + DOWN_E + LEFT_E, 14);
	}

}
