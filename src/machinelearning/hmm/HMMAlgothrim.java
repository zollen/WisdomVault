package machinelearning.hmm;

import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.nd4j.linalg.primitives.Pair;

public interface HMMAlgothrim<T> {
	
	public enum UnderFlowStrategy {
		NONE, ENABLED
	}
	
	public enum VirterbiAlgorithm {
		BAYES_RULES_ALGO, WIKI_PROPOSED_ALGO
	}
	
	public List<Pair<Integer, T>> fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E);
	
	public double posterior(List<Pair<Integer, T>> list);

}
