package parser;

import java.util.ArrayList;

public class Derivation {
	public String left;
	public ArrayList<String> list = new ArrayList<>();

	public Derivation(String s) {
		String[] div = s.split("->");
		this.left = div[0];

		String[] v = div[1].split(" ");
		for (int i = 0; i < v.length; i++) {
			list.add(v[i]);
		}
	}

	public String toString() {
		String result = left + "->";
		for (String r : list) {
			result += r;
			result += " ";
		}
		return result.trim();
	}

	public boolean equalTo(Derivation d) {
		if (this.toString().equals(d.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public void print() {
		System.out.println(this.toString());
	}
}
