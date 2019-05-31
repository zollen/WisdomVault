package machinelearning.neuralnetwork.words;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

public class WordDataSetIterator implements DataSetIterator {
	
	private static final long serialVersionUID = 1L;
	
	private int current;       		// current batch order
	private int batchSize;			// number of samples per batch
	private int sampleLength;       // sample size
	private Map<String, Integer> wordToIndex = new HashMap<String, Integer>();
	private Map<Integer, String> indexToWord = new HashMap<Integer, String>();
	private List<String> novel = new ArrayList<String>();
	private Set<String> words = new LinkedHashSet<String>();
	
	public WordDataSetIterator(File file, int batchSize, int sampleLength) throws IOException {
		
		List<String> lines = Files.readAllLines(file.toPath());
		
		init(lines, batchSize, sampleLength);
	}
	
	public WordDataSetIterator(String buffer, int batchSize, int sampleLength) {
		
		String [] ll = buffer.split("\\r?\\n");
		
		List<String> lines = new ArrayList<String>();
		
		for (int i = 0; ll != null && i < ll.length; i++) {
			ll[i] = ll[i].trim();
			if (ll[i].length() > 0)
				lines.add(ll[i].trim());
		}
		
		init(lines, batchSize, sampleLength);
	}

	private void init(List<String> lines, int batchSize, int sampleLength) {
		this.batchSize = batchSize;
		this.sampleLength = sampleLength;
		this.current = 0;
		
		for (String line : lines) {
			
			String [] tokens = line.toLowerCase().replaceAll("[^a-z0-9\\p{Space}]+", " ").trim().split("[\\p{Space}]+");
			for (int i = 0; tokens != null && i < tokens.length; i++) {
				tokens[i] = tokens[i].trim();
				if (tokens[i].length() > 0) {
					novel.add(tokens[i]);
					words.add(tokens[i]);
				}
			}
		}
		
		int idx = 0;
		for (String token : words) {
			wordToIndex.put(token, idx);
			indexToWord.put(idx, token);
			idx++;
		}
	}
	
	public Map<String, Integer> getIdx() {
		return wordToIndex;
	}
	
	public int toIdx(String word) {
		return wordToIndex.get(word.toLowerCase().trim());
	}
	
	public String toWord(int index) {
		return indexToWord.get(index);
	}
	
	public INDArray toINDArray(String [] statements) {
		
		INDArray arr = Nd4j.zeros(statements.length, this.inputColumns(), statements[0].length());
		
		for (int i = 0; i < statements.length; i++) {
			
			String [] tokens = statements[i].toLowerCase()
					.replaceAll("[^a-z0-9\\p{Space}]+", " ").trim().split("[\\p{Space}]+");
			
			for (int j = 0; tokens != null && j < tokens.length; j++) {
				
				tokens[j] = tokens[j] != null ? tokens[j].trim() : "";
				
				if (tokens[j].length() > 0) {
					arr.putScalar(new int[] { i, this.toIdx(tokens[j]), j }, 1.0);
				}
			}
		}
		
		return arr;
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return current * batchSize * sampleLength < novel.size() - 1;
	}

	@Override
	public DataSet next() {
		// TODO Auto-generated method stub
		return next(current++);
	}

	@Override
	public DataSet next(int num) {
		// TODO Auto-generated method stub
		INDArray inputs = Nd4j.zeros(batchSize, this.inputColumns(), sampleLength);
		INDArray labels = Nd4j.zeros(batchSize, this.totalOutcomes(), sampleLength);
		
		for (int sample = 0; sample < this.batchSize; sample++) {
			
			int start = num * batchSize * sampleLength + sample * sampleLength;
			int end = start + sampleLength < novel.size() - 1 ? 
							start + sampleLength : novel.size() - 1;
			
			for (int position = start, sequence = 0; position < end; position++, sequence++) {
				
				int curr = this.toIdx(novel.get(position));
				int next = this.toIdx(novel.get(position + 1));
				
				inputs.putScalar(new int[] { sample, curr, sequence }, 1.0);
				labels.putScalar(new int[] { sample, next, sequence }, 1.0);
			}
		
		}
		
		return new DataSet(inputs, labels);
	}

	@Override
	public int inputColumns() {
		// TODO Auto-generated method stub
		return words.size();
	}

	@Override
	public int totalOutcomes() {
		// TODO Auto-generated method stub
		return words.size();
	}

	@Override
	public boolean resetSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean asyncSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		current = 0;
	}

	@Override
	public int batch() {
		// TODO Auto-generated method stub
		return batchSize;
	}
	
	@Override
	public List<String> getLabels() {
		// TODO Auto-generated method stub
		return new ArrayList<String>(words);
	}

	@Override
	public void setPreProcessor(DataSetPreProcessor preProcessor) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public DataSetPreProcessor getPreProcessor() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Not implemented");
	}

}
