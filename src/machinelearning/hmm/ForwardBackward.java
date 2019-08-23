package machinelearning.hmm;

import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.nd4j.linalg.primitives.Pair;

public class ForwardBackward {
	
	private Forward forward;
	private Backward backward;
	private List<Pair<Integer, DMatrixRMaj>> fpass;
	private List<Pair<Integer, DMatrixRMaj>> bpass;
	private List<Pair<Integer, DMatrixRMaj>> fbpass;
	
	public ForwardBackward() {
		this.forward = new Forward();
		this.backward = new Backward();
		this.fpass = null;
		this.bpass = null;
		this.fbpass = null;
	}
	
	public void fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		this.fpass = forward.fit(converter, S, T, E);
		this.bpass = backward.fit(converter, S, T, E);
		this.fbpass = new ArrayList<Pair<Integer, DMatrixRMaj>>();
		
		for (int i = 0; i < this.fpass.size() && i < this.bpass.size(); i++) {
			
			Pair<Integer, DMatrixRMaj> fpair = this.fpass.get(i);
			Pair<Integer, DMatrixRMaj> bpair = this.bpass.get(i);
			
			DMatrixRMaj tmp = new DMatrixRMaj(fpair.getSecond().numRows, 1);	
			CommonOps_DDRM.elementMult(fpair.getSecond(), bpair.getSecond(), tmp);
			
			this.fbpass.add(new Pair<>(fpair.getFirst(), tmp));		
		}
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
	
	public List<Pair<Integer, DMatrixRMaj>> reestimate() {
		return null;
	}
}
