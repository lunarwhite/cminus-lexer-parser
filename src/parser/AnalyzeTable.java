package parser;

import java.util.ArrayList;
import java.util.Iterator;

public class AnalyzeTable {
	public static String error = "X";
	public static String acc = "accept";

	public DFA dfa;

	private String[] actionCol;
	private String[] gotoCol;
	public int actionLength;
	public int gotoLength;
	public int stateNum;

	private int[][] gotoTable;
	private String[][] actionTable;

	public AnalyzeTable() {
		createTableHeader();
		this.actionLength = actionCol.length;
		this.gotoLength = gotoCol.length;

		createDFA();
		this.stateNum = dfa.size();
		this.gotoTable = new int[stateNum][gotoLength + actionLength - 1];
		this.actionTable = new String[stateNum][actionLength];
		
		createAnalyzeTable();
	}

	// 建立一个LR(1)语法分析表的表头
	private void createTableHeader() {
		this.actionCol = new String[CFG.VT.size() + 1];
		this.gotoCol = new String[CFG.VN.size() + CFG.VT.size()];

		Iterator<String> iter1 = CFG.VT.iterator();
		Iterator<String> iter2 = CFG.VN.iterator();
		int i = 0;

		while (iter1.hasNext()) {
			String vt = iter1.next();
			if (!vt.equals(CFG.emp)) {
				actionCol[i] = vt;
				gotoCol[i] = vt;
				i++;
			}
		}
		actionCol[i] = "$";
		while (iter2.hasNext()) {
			String vn = iter2.next();
			gotoCol[i] = vn;
			i++;
		}
	}

	private ArrayList<Integer> gotoStart = new ArrayList<>();
	private ArrayList<Integer> gotoEnd = new ArrayList<>();
	private ArrayList<String> gotoPath = new ArrayList<>();

	// 建立用于语法分析的DFA
	private void createDFA() {
		this.dfa = new DFA();
		DFAState state0 = new DFAState(0);
		state0.addNewDerivation(new LRDerivation(getDerivation("S'").get(0), "$", 0));// 首先加入S'->·S,$
		for (int i = 0; i < state0.set.size(); i++) {
			LRDerivation lrd = state0.set.get(i);
			if (lrd.index < lrd.d.list.size()) {
				String A = lrd.d.list.get(lrd.index);// 获取·后面的文法符号
				String b = null;// 紧跟A的一项+a
				if (lrd.index == lrd.d.list.size() - 1) {
					b = lrd.lr;
				} else {
					b = lrd.d.list.get(lrd.index + 1);
				}
				if (CFG.VN.contains(A)) {
					ArrayList<String> firstB = first(b);
					ArrayList<Derivation> dA = getDerivation(A);
					for (int j = 0, length1 = dA.size(); j < length1; j++) {
						for (int k = 0, length2 = firstB.size(); k < length2; k++) {
							LRDerivation lrd1 = new LRDerivation(dA.get(j), firstB.get(k), 0);
							state0.addNewDerivation(lrd1);
						}
					}
				}
			}
		}
		dfa.states.add(state0);
		// state0建立成功后开始递归建立其他的状态
		ArrayList<String> gotoPath = state0.getGotoPath();
		for (String path : gotoPath) {
			ArrayList<LRDerivation> list = state0.getLRDs(path);// 直接通过路径传到下一个状态的情况
			addState(0, path, list);// 开始进行递归，建立用于分析的DFA
		}
	}

	// 通过输入一个从上一个状态传下来的LR产生式的list获取下一个状态
	// 如果该状态已经存在，则不作任何操作，跳出递归
	// 如果该状态不存在，则加入该状态，继续进行递归
	private void addState(int lastState, String path, ArrayList<LRDerivation> list) {
		DFAState temp = new DFAState(0);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).index++;
			temp.addNewDerivation(list.get(i));
		}

		for (int i = 0; i < temp.set.size(); i++) {
			if (temp.set.get(i).d.list.size() != temp.set.get(i).index) {
				String A = temp.set.get(i).d.list.get(temp.set.get(i).index);
				String B = null;
				if (temp.set.get(i).index + 1 == temp.set.get(i).d.list.size()) {
					B = temp.set.get(i).lr;
				} else {
					B = temp.set.get(i).d.list.get(temp.set.get(i).index + 1);
				}

				ArrayList<Derivation> dA = getDerivation(A);
				ArrayList<String> firstB = first(B);
				for (int j = 0; j < dA.size(); j++) {
					for (int k = 0; k < firstB.size(); k++) {
						LRDerivation lrd = new LRDerivation(dA.get(j), firstB.get(k), 0);
						if (!temp.contains(lrd)) {
							temp.addNewDerivation(lrd);
						}
					}
				}
			}
		}

		for (int i = 0; i < dfa.states.size(); i++) {
			if (dfa.states.get(i).equalTo(temp)) {
				gotoStart.add(lastState);
				gotoEnd.add(i);
				gotoPath.add(path);
				return;
			}
		}

		temp.id = dfa.states.size();
		dfa.states.add(temp);
		gotoStart.add(lastState);
		gotoEnd.add(temp.id);
		gotoPath.add(path);

		ArrayList<String> gotoPath = temp.getGotoPath();
		for (String p : gotoPath) {
			ArrayList<LRDerivation> l = temp.getLRDs(p); // 通过路径传到下一个状态的情况
			addState(temp.id, p, l);
		}
	}

	// 获取与一个文法符号相关的产生式
	public ArrayList<Derivation> getDerivation(String v) {
		ArrayList<Derivation> result = new ArrayList<>();
		Iterator<Derivation> iter = CFG.F.iterator();
		while (iter.hasNext()) {
			Derivation d = iter.next();
			if (d.left.equals(v)) {
				result.add(d);
			}
		}
		return result;
	}

	// 获取一个文法符号的first
	private ArrayList<String> first(String v) {
		ArrayList<String> result = new ArrayList<>();
		if (v.equals("$")) {
			result.add("$");
		} else {
			Iterator<String> iter = CFG.firstMap.get(v).iterator();
			while (iter.hasNext()) {
				result.add(iter.next());
			}
		}
		return result;
	}

	// 填充语法分析表的相关内容
	private void createAnalyzeTable() {
		for (int i = 0; i < gotoTable.length; i++) {
			for (int j = 0; j < gotoTable[0].length; j++) {
				gotoTable[i][j] = -1;
			}
		}
		for (int i = 0; i < actionTable.length; i++) {
			for (int j = 0; j < actionTable[0].length; j++) {
				actionTable[i][j] = AnalyzeTable.error;
			}
		}

		// 完善语法分析表的goto部分
		int gotoCount = this.gotoStart.size();
		for (int i = 0; i < gotoCount; i++) {
			int start = gotoStart.get(i);
			int end = gotoEnd.get(i);
			String path = gotoPath.get(i);
			int pathIndex = gotoIndex(path);
			this.gotoTable[start][pathIndex] = end;
		}

		// 完善语法分析表的action部分
		int stateCount = dfa.states.size();
		for (int i = 0; i < stateCount; i++) {
			DFAState state = dfa.get(i); // 获取dfa的单个状态
			for (LRDerivation lrd : state.set) { // 对每一个进行分析
				if (lrd.index == lrd.d.list.size()) {
					if (!lrd.d.left.equals("S'")) {
						int derivationIndex = derivationIndex(lrd.d);
						String value = "r" + derivationIndex;
						actionTable[i][actionIndex(lrd.lr)] = value; // 设为规约
					} else {
						actionTable[i][actionIndex("$")] = AnalyzeTable.acc; // 设为接受
					}
				} else {
					String next = lrd.d.list.get(lrd.index); // 获取·后面的文法符号
					if (CFG.VT.contains(next)) { // 必须是一个终结符号
						if (gotoTable[i][gotoIndex(next)] != -1) {
							actionTable[i][actionIndex(next)] = "s" + gotoTable[i][gotoIndex(next)];
						}
					}
				}
			}
		}
	}

	// 返回goto中的列数
	private int gotoIndex(String s) {
		for (int i = 0; i < gotoLength; i++) {
			if (gotoCol[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	// 返回action中的列数
	private int actionIndex(String s) {
		for (int i = 0; i < actionLength; i++) {
			if (actionCol[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	// 返回是第几个表达式
	private int derivationIndex(Derivation d) {
		int size = CFG.F.size();
		for (int i = 0; i < size; i++) {
			if (CFG.F.get(i).equals(d)) {
				return i;
			}
		}
		return -1;
	}

	public String ACTION(int stateIndex, String vt) {
		int index = actionIndex(vt);
		return actionTable[stateIndex][index];
	}

	public int GOTO(int stateIndex, String vn) {
		int index = gotoIndex(vn);
		return gotoTable[stateIndex][index];
	}

	// 打印语法分析表
	public void print() {
		String colLine = "";
		for (int i = 0; i < actionCol.length; i++) {
			colLine += " | ";
			colLine += actionCol[i];
		}
		for (int j = 0; j < gotoCol.length; j++) {
			colLine += " | ";
			colLine += gotoCol[j];
		}
		System.out.println(colLine);
		int index = 0;
		for (int i = 0; i < dfa.states.size(); i++) {
			String line = String.valueOf(i);
			while (index < actionCol.length) {
				line += " | ";
				line += actionTable[i][index];
				index++;
			}
			index = 0;
			while (index < gotoCol.length) {
				line += " | ";
				if (gotoTable[i][index] == -1) {
					line += "X";
				} else {
					line += gotoTable[i][index];
				}
				index++;
			}
			index = 0;
			line += " | ";
			System.out.println(line);
		}
	}

	public int getStateNum() {
		return dfa.states.size();
	}
}
