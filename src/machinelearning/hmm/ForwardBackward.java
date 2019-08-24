package machinelearning.hmm;

import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.nd4j.linalg.primitives.Pair;

import machinelearning.hmm.HMMAlgothrim.UnderFlowStrategy;
import machinelearning.hmm.HMMAlgothrim.VirterbiAlgorithm;

public class ForwardBackward  {
	
	private Forward forward;
	private Backward backward;
	private Viterbi viterbi; 
	private List<Pair<Integer, Double>> states;
	private List<Pair<Integer, DMatrixRMaj>> fpass;
	private List<Pair<Integer, DMatrixRMaj>> bpass;
	private List<Pair<Integer, DMatrixRMaj>> fbpass;
	private List<Pair<Integer, Double>> posterior;
	private UnderFlowStrategy strategy;
	
	private ForwardBackward(UnderFlowStrategy strategy, VirterbiAlgorithm algorithm) {
		this.forward = new Forward(strategy);
		this.backward = new Backward(strategy);
		this.viterbi = new Viterbi(algorithm, strategy);

		this.fpass = null;
		this.bpass = null;
		this.fbpass = null;
		this.states = null;
		this.posterior = null;
		this.strategy = strategy;
	}
	
	public static class Builder {
		
		private UnderFlowStrategy ualgo = UnderFlowStrategy.NONE;
		private VirterbiAlgorithm valgo = VirterbiAlgorithm.BAYES_RULES_ALGO;
		
		public Builder() {}
		
		public Builder setUnderFlowStrategy(boolean flag) {
			
			if (flag)
				this.ualgo = UnderFlowStrategy.ENABLED;
			
			return this; 
		}
		
		public Builder setWikiViterbiAlgorithm() {
			
			this.valgo = VirterbiAlgorithm.WIKI_PROPOSED_ALGO;
			return this;
		}
		
		public Builder setBayesViterbiAlgorithm() {
			
			this.valgo = VirterbiAlgorithm.BAYES_RULES_ALGO;
			return this;
		}
		
		public ForwardBackward build() {
			return new ForwardBackward(ualgo, valgo);
		}
	}
	
	public void fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		this.fpass = forward.fit(converter, S, T, E);
		this.bpass = backward.fit(converter, S, T, E);
		this.states = viterbi.fit(converter, S, T, E);
		
		this.posterior = new ArrayList<Pair<Integer, Double>>();
		this.fbpass = new ArrayList<Pair<Integer, DMatrixRMaj>>();
		
		for (int index = 0; index < this.fpass.size() && index < this.bpass.size(); index++) {
			
			Pair<Integer, DMatrixRMaj> fpair = this.fpass.get(index);
			Pair<Integer, DMatrixRMaj> bpair = this.bpass.get(index);
			
			DMatrixRMaj tmp = new DMatrixRMaj(T.numRows, 1);

			CommonOps_DDRM.elementMult(fpair.getSecond(), bpair.getSecond(), tmp);
			
			if (this.strategy == UnderFlowStrategy.ENABLED) {
				CommonOps_DDRM.scale(1.0 / CommonOps_DDRM.elementSum(tmp), tmp);
			}
			
			this.fbpass.add(new Pair<>(converter[index], tmp));	
			this.posterior.add(new Pair<>(converter[index], CommonOps_DDRM.elementSum(tmp)));
		}
	}
	
	public List<Pair<Integer, Double>> viterbi() {
		return states;
	}
	
	public List<Pair<Integer, DMatrixRMaj>> forward() {
		return fpass;
	}
	
	public double forward(List<Pair<Integer, DMatrixRMaj>> list) {
		return forward.probability(list);
	}
	
	public List<Pair<Integer, DMatrixRMaj>> backward() {
		return bpass;
	}
	
	public List<Pair<Integer, DMatrixRMaj>> forwardBackward() {
		return fbpass;
	}
	
	public List<Pair<Integer, Double>> posterior() {
		return posterior;
	}
}
