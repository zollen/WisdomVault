package genetic.maze;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MazeLoader {
	
	private Map<String, Set<Integer>> allowed;
	private List<char []> map;
	private int height;
	private int width;
	private MazeGame.Move end;
	private boolean done;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MazeLoader loader = new MazeLoader("data/mymaze.txt");
		
		MazeGame game = loader.create().random();
		
		System.out.println (game);
	}
	
	public MazeLoader(String file) {
		
		this.allowed = new HashMap<String, Set<Integer>>();
		this.map = new ArrayList<char []>();
		this.width = 0;
		this.height = 0;
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
		
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				
				if (map[row][col] == ' ') {
					
					String key = String.valueOf(row) + String.valueOf(col);
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
					
					allowed.put(key, moves);
					end = new MazeGame.Move(MazeGame.NONE, row, col);
				}
			}
		}
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
	
	public MazeGame create() {
		
		return new MazeGame.Builder()
				.setWidth(width)
				.setHeight(height)
				.setRow(0)
				.setCol(0)
				.setEnd(end)
				.setMap(map.toArray(new char[0][0]))
				.setAllowed(allowed)
				.build();
	}
	
}
