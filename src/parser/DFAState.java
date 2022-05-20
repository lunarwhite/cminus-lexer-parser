package parser;

import java.util.ArrayList;
import java.util.Iterator;

public class DFAState {
	public int id;
	public ArrayList<LRDerivation> set = new ArrayList<>();

	public DFAState(int id) {
		this.id = id;
	}

	public boolean addNewDerivation(LRDerivation d) {
		if (contains(d)) {
			return false;
		} else {
			set.add(d);
			return true;
		}
	}

	public void print() {
		Iterator<LRDerivation> iter = set.iterator();
		while (iter.hasNext()) {
			iter.next().print();
		}
	}

	public boolean contains(LRDerivation lrd) {
		for (LRDerivation l : set) {
			if (l.equalTo(lrd)) {
				return true;
			}
		}
		return false;
	}

	public boolean equalTo(DFAState state) {
		if (this.toString().hashCode() == state.toString().hashCode()) {
			return true;
		} else {
			return false;
		}
	}

	public String toString() {
		String result = "";
		for (int i = 0; i < set.size(); i++) {
			result += set.get(i);
			if (i < set.size() - 1) {
				result += "|";
			}
		}
		return result;
	}

	public ArrayList<String> getGotoPath() {
		ArrayList<String> result = new ArrayList<>();
		for (LRDerivation lrd : set) {
			if (lrd.d.list.size() == lrd.index) {
				continue;
			}
			String s = lrd.d.list.get(lrd.index);
			if (!result.contains(s)) {
				result.add(s);
			}
		}
		return result;
	}

	public ArrayList<LRDerivation> getLRDs(String s) {
		ArrayList<LRDerivation> result = new ArrayList<>();
		for (LRDerivation lrd : set) {
			if (lrd.d.list.size() != lrd.index) {
				String s1 = lrd.d.list.get(lrd.index);
				if (s1.equals(s)) {
					result.add((LRDerivation) lrd.clone());
				}
			}
		}
		return result;
	}
}
