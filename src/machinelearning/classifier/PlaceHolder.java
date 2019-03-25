package machinelearning.classifier;

public class PlaceHolder<T> {

	private T data;

	public PlaceHolder() {
	}

	public PlaceHolder(T data) {
		this.data = data;
	}

	public T data() {
		return data;
	}

	public void data(T data) {
		this.data = data;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlaceHolder<T> other = (PlaceHolder<T>) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
