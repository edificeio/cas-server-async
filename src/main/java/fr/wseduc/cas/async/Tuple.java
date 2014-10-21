package fr.wseduc.cas.async;

public class Tuple<X, Y> {

	public final X _1;
	public final Y _2;

	public Tuple(X _1, Y _2) {
		this._1 = _1;
		this._2 = _2;
	}

	@Override
	public String toString() {
		return "(" + _1 + "," + _2 + ")";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof Tuple)){
			return false;
		}
		Tuple<X,Y> other_ = (Tuple<X,Y>) other;
		return other_._1 == this._1 && other_._2 == this._2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_1 == null) ? 0 : _1.hashCode());
		result = prime * result + ((_2 == null) ? 0 : _2.hashCode());
		return result;
	}

}
