package machinelearning.neuralnetwork;

import java.util.ArrayList;
import java.util.List;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public class MultIteratorsIterator implements DataSetIterator {
	
	private static final long serialVersionUID = 1L;
	
	private List<DataSetIterator> iterators = new ArrayList<DataSetIterator>();
	private int current = 0;
	
	public MultIteratorsIterator(DataSetIterator ... iters) {
		
		for (DataSetIterator iter : iters) {
			this.iterators.add(iter);
		}
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return iterators.stream().anyMatch(p -> p.hasNext());
	}

	@Override
	public DataSet next() {
		// TODO Auto-generated method stub
		return next(current);
	}

	@Override
	public DataSet next(int num) {
		// TODO Auto-generated method stub
		DataSet data = null;
		
		for (int i = num; i < iterators.size(); i++) {
			
			if (iterators.get(i).hasNext()) {
				data = iterators.get(i).next();
				this.current = i;
				break;
			}
		}
		
		return data;
	}

	@Override
	public int inputColumns() {
		// TODO Auto-generated method stub
		return iterators.get(0).inputColumns();
	}

	@Override
	public int totalOutcomes() {
		// TODO Auto-generated method stub
		return iterators.get(0).totalOutcomes();
	}

	@Override
	public boolean resetSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean asyncSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		iterators.stream().forEach(p -> p.reset());
		this.current = 0;
	}

	@Override
	public int batch() {
		// TODO Auto-generated method stub
		return iterators.get(0).batch();
	}

	@Override
	public void setPreProcessor(DataSetPreProcessor preProcessor) {
		// TODO Auto-generated method stub
		iterators.stream().forEach(p -> p.setPreProcessor(preProcessor));
	}

	@Override
	public DataSetPreProcessor getPreProcessor() {
		// TODO Auto-generated method stub
		return iterators.get(0).getPreProcessor();
	}

	@Override
	public List<String> getLabels() {
		// TODO Auto-generated method stub
		return iterators.get(0).getLabels();
	}

}
