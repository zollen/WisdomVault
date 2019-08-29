package genetic.maze;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Maze {
	
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
		return twoDToOneD.get(String.valueOf(row) + "," + String.valueOf(col));
	}
	
	public String getLocation(int location) {
		return oneDToTwoD.get(location);
	}
	
	public Set<Integer> getFreedom(int row, int col) {
		return enviornment.get(String.valueOf(row) + "," + String.valueOf(col));	
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

}
