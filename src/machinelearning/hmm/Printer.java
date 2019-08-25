package machinelearning.hmm;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.nd4j.linalg.primitives.Pair;

public class Printer {
	
	private DecimalFormat ff;
	
	public Printer(DecimalFormat ff) {
		this.ff = ff;
	}
	
	public <T> String display(String [] output, List<Pair<Integer, T>> list) {
		
		return list.stream().map(p -> { 
				StringBuilder builder = new StringBuilder();
				builder.append("{");
				builder.append(output[p.getFirst()]);
				builder.append("}: ");
				
				if (p.getSecond() instanceof Double) {
					builder.append(ff.format(p.getSecond()));
				}
				else {
					DMatrixRMaj mat = (DMatrixRMaj) p.getSecond();
					
					builder.append("[");
					
					for (int row = 0; row < mat.numRows; row++) {
						
						if (row > 0)
							builder.append(", ");
						
						builder.append(ff.format(mat.get(row, 0)));
					}
					
					builder.append("]");
				}
				
				return builder.toString();
				
		}).collect(Collectors.joining(", "));
		
	}

}
