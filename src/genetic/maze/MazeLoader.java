package genetic.maze;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MazeLoader {
	
	private Map<String, Set<Integer>> environment;
	private Map<String, Integer> twoDToOneD;
	private Map<Integer, String> oneDToTwoD;
	private List<char []> map;
	private int spaces;
	private int height;
	private int width;
	private MazeGame.Move end;
	private boolean done;

	
	public MazeLoader(String file) {
		
		this.environment = new LinkedHashMap<String, Set<Integer>>();
		this.twoDToOneD = new LinkedHashMap<String, Integer>();
		this.oneDToTwoD = new LinkedHashMap<Integer, String>();
		this.map = new ArrayList<char []>();
		this.width = 0;
		this.height = 0;
		this.spaces = 0;
		this.done = false;
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			String line = reader.readLine();
			while (line != null) {
				process(line);
				line = reader.readLine();
			}
			
			process(this.map.toArray(new char[0][0]));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void process(char [][] map) {
		
		int current = 0;
		
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				
				if (map[row][col] == ' ') {
					
					String key = String.valueOf(row) + "," + String.valueOf(col);
					
					Set<Integer> moves = new HashSet<Integer>();
					
					if (row - 1 >= 0 && map[row - 1][col] == ' ') {
						moves.add(MazeGame.UP);
					}

					if (row + 1 < height && map[row + 1][col] == ' ') {
						moves.add(MazeGame.DOWN);
					}

					if (col - 1 >= 0 && map[row][col - 1] == ' ') {
						moves.add(MazeGame.LEFT);
					}

					if (col + 1 < width && map[row][col + 1] == ' ') {
						moves.add(MazeGame.RIGHT);
					}
					
					spaces++;
					
					twoDToOneD.put(String.valueOf(row) + "," + String.valueOf(col), current);
					oneDToTwoD.put(current, String.valueOf(row) + ":" + String.valueOf(col));
					
					current++;
					
					environment.put(key, moves);
				}
			}
		}
		
		end = new MazeGame.Move(MazeGame.NONE, height - 1, width - 1);
	}
	
	private void process(String line) {
		
		if (line == null)
			return;
		
		if (line.indexOf("---") >= 0 && width == 0 && !done) {
			line = line.trim();
			width = line.length();
			return;
		}
		else
		if (line.indexOf("---") >= 0 && width > 0 && !done) {
			done = true;
			return;
		}
		else 
		if (!done){
			
			height++;
			
			char [] data = new char[width];
			
			for (int i = 0; i < width; i++) {
				data[i] = (i < line.length() ? line.charAt(i) : ' ');
			}
			
			map.add(data);
		}
	}
	
	public Maze build() {
		return new Maze(map.toArray(new char[0][0]), spaces, environment, twoDToOneD, oneDToTwoD);
	}
	
	public MazeGame create() {
		
		return new MazeGame.Builder()
				.setRow(0)
				.setCol(0)
				.setEnd(end)
				.setMaze(new Maze(map.toArray(new char[0][0]), spaces, environment, twoDToOneD, oneDToTwoD))
				.build();
	}
	
}
