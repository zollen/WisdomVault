package machinelearning.hmm;

import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.nd4j.linalg.primitives.Pair;

public class ForwardBackward extends HMMAlgothrim {
	
	private Forward forward;
	private Backward backward;
	private Viterbi viterbi; 
	private List<Pair<Integer, Double>> statesProb;
	private List<Pair<Integer, DMatrixRMaj>> fpass;
	private List<Pair<Integer, DMatrixRMaj>> bpass;
	private List<Pair<Integer, DMatrixRMaj>> fbpass;
	private List<Pair<Integer, Double>> posteriorProb;
	
	private ForwardBackward(UnderFlowAlgorithm ualgo, VirterbiAlgorithm valgo) {
		this.forward = new Forward(ualgo);
		this.backward = new Backward(ualgo);
		this.viterbi = new Viterbi(valgo);

		this.fpass = null;
		this.bpass = null;
		this.fbpass = null;
		this.statesProb = null;
		this.posteriorProb = null;
	}
	
	public static class Builder {
		
		private UnderFlowAlgorithm ualgo = UnderFlowAlgorithm.NONE;
		private VirterbiAlgorithm valgo = VirterbiAlgorithm.BAYES_RULES_ALGO;
		
		public Builder() {}
		
		public Builder setUnderFlowAlgorithm(UnderFlowAlgorithm flag) {

			this.ualgo = flag;			
			return this; 
		}
		
		public Builder setViterbiAlgorithm(VirterbiAlgorithm flag) {
			
			this.valgo = flag;
			return this;
		}
		
		public ForwardBackward build() {
			return new ForwardBackward(ualgo, valgo);
		}
	}
	
	public void fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		this.fpass = forward.fit(converter, S, T, E);
		this.bpass = backward.fit(converter, S, T, E);
		this.statesProb = viterbi.fit(converter, S, T, E);
		
		this.posteriorProb = new ArrayList<Pair<Integer, Double>>();
		this.fbpass = new ArrayList<Pair<Integer, DMatrixRMaj>>();
		
		for (int index = 0; index < this.fpass.size() && index < this.bpass.size(); index++) {
			
			Pair<Integer, DMatrixRMaj> fpair = this.fpass.get(index);
			Pair<Integer, DMatrixRMaj> bpair = this.bpass.get(index);
			
			DMatrixRMaj tmp = new DMatrixRMaj(T.numRows, 1);	
			CommonOps_DDRM.elementMult(fpair.getSecond(), bpair.getSecond(), tmp);
			
			this.fbpass.add(new Pair<>(converter[index], tmp));	
			this.posteriorProb.add(new Pair<>(converter[index], CommonOps_DDRM.elementSum(tmp)));
		}
	}
	
	public List<Pair<Integer, Double>> viterbi() {
		return statesProb;
	}
	
	public List<Pair<Integer, DMatrixRMaj>> forward() {
		return fpass;
	}
	
	public List<Pair<Integer, DMatrixRMaj>> backward() {
		return bpass;
	}
	
	public List<Pair<Integer, DMatrixRMaj>> forwardBackward() {
		return fbpass;
	}
	
	public List<Pair<Integer, Double>> posteriorProb() {
		return posteriorProb;
	}
}
