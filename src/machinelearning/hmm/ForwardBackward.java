package machinelearning.hmm;

import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.nd4j.linalg.primitives.Pair;

import machinelearning.hmm.HMMAlgothrim.UnderFlowStrategy;
import machinelearning.hmm.HMMAlgothrim.VirterbiAlgorithm;

public class ForwardBackward  {
	
	private VirterbiAlgorithm algorithm;
	private UnderFlowStrategy strategy;
	
	private ForwardBackward(UnderFlowStrategy strategy, VirterbiAlgorithm algorithm) {
		this.strategy = strategy;
		this.algorithm = algorithm;
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
	
	public static class HMMResult {
		
		private List<Pair<Integer, Double>> vpass;
		private List<Pair<Integer, DMatrixRMaj>> fpass;
		private List<Pair<Integer, DMatrixRMaj>> bpass;
		private List<Pair<Integer, DMatrixRMaj>> fbpass;
		private List<Pair<Integer, Double>> ppass;
		private Forward forward;
		private Backward backward;
		private Viterbi viterbi;
		
		public HMMResult(Forward forward, Backward backward, Viterbi viterbi,
				List<Pair<Integer, Double>> vpass,
				List<Pair<Integer, DMatrixRMaj>> fpass, 
				List<Pair<Integer, DMatrixRMaj>> bpass,
				List<Pair<Integer, DMatrixRMaj>> fbpass,
				List<Pair<Integer, Double>> ppass) {
			this.forward = forward;
			this.backward = backward;
			this.viterbi = viterbi;
			this.vpass = vpass;
			this.fpass = fpass;
			this.bpass = bpass;
			this.fbpass = fbpass;
			this.ppass = ppass;
		}
		
		public Forward forward() {
			return forward;
		}
		
		public Backward backward() {
			return backward;
		}
		
		public Viterbi viterbi() {
			return viterbi;
		}
		
		public List<Pair<Integer, Double>> vlist() {
			return vpass;
		}
		
		public List<Pair<Integer, DMatrixRMaj>> flist() {
			return fpass;
		}
		
		public List<Pair<Integer, DMatrixRMaj>> blist() {
			return bpass;
		}
		
		public List<Pair<Integer, DMatrixRMaj>> fblist() {
			return fbpass;
		}
		
		public List<Pair<Integer, Double>> plist() {
			return ppass;
		}
	}
	
	public HMMResult fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		Forward forward = new Forward(strategy);
		Backward backward = new Backward(strategy);
		Viterbi viterbi = new Viterbi(algorithm, strategy);

		
		List<Pair<Integer, DMatrixRMaj>> fpass = forward.fit(converter, S, T, E);
		List<Pair<Integer, DMatrixRMaj>> bpass = backward.fit(converter, S, T, E);
		List<Pair<Integer, Double>> states = viterbi.fit(converter, S, T, E);
		
		List<Pair<Integer, Double>> ppass = new ArrayList<Pair<Integer, Double>>();
		List<Pair<Integer, DMatrixRMaj>> fbpass = new ArrayList<Pair<Integer, DMatrixRMaj>>();
		
		for (int index = 0; index < fpass.size() && index < bpass.size(); index++) {
			
			Pair<Integer, DMatrixRMaj> fpair = fpass.get(index);
			Pair<Integer, DMatrixRMaj> bpair = bpass.get(index);
			
			DMatrixRMaj tmp = new DMatrixRMaj(T.numRows, 1);

			CommonOps_DDRM.elementMult(fpair.getSecond(), bpair.getSecond(), tmp);
			
			if (this.strategy == UnderFlowStrategy.ENABLED) {
				CommonOps_DDRM.scale(1.0 / CommonOps_DDRM.elementSum(tmp), tmp);
			}
			
			fbpass.add(new Pair<>(converter[index], tmp));	
			ppass.add(new Pair<>(converter[index], CommonOps_DDRM.elementSum(tmp)));
		}
		
		return new HMMResult(forward, backward, viterbi, states, fpass, bpass, fbpass, ppass);
	}
}
