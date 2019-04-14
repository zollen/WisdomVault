package optimization.graph;

public class BIEdge {
	
	private static final int PRIME1 = 31;
	private static final int PRIME2 = 37;
	
	private static String [] LABELS = null;

	private int from;		
	private int to;	
	private int weight;
	
	public BIEdge(int to, int from, int weight) {
		this.to = to;
		this.from = from;
		this.weight = weight;
	}
	
	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = (from + PRIME1) * (to + PRIME1);
		result = result + PRIME2 * weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		BIEdge other = (BIEdge) obj;
		if (from == other.from && to == other.to && weight == other.weight)
			return true;
		
		if (from == other.to && to == other.from && weight == other.weight)
			return true;
			
		return false;
	}
	
	@Override
	public String toString() {
		return "Edge [From=" + LABELS[from] + " <==> " + "To=" + 
							LABELS[to] + ", Weight=" + weight + "]";
	}
	
	public static void setLABELS(String [] labels) {
		LABELS = labels;
	}
}