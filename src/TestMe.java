import genetic.maze.Maze;
import genetic.maze.MazeLoader;

public class TestMe {
	
	public static void main(String[] args) throws Exception {
		
		final MazeLoader loader = new MazeLoader("data/toymaze.txt");
		
		Maze maze = loader.build();
		
		System.out.println(maze);
	}
	
}
