package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

public class CFG {
	private static String inputPath = "res/input/";
	
	public static String emp = "ε";
	public static String end = "$";

	public static TreeSet<String> keywords = new TreeSet<>(); // 保留字集
	public static TreeSet<String> VN = new TreeSet<>(); // 非终结符集
	public static TreeSet<String> VT = new TreeSet<>(); // 终结符集
	public static ArrayList<Derivation> F = new ArrayList<>(); // 产生式集

	public static HashMap<String, TreeSet<String>> firstMap = new HashMap<>(); // first
	public static HashMap<String, TreeSet<String>> followMap = new HashMap<>(); // follow

	static {
		read("cfg.txt");

		// 添加C语言的保留字
		String[] keyword = {
				"auto", "double", "int", "struct", "break", "else", "long", "switch",
				"case", "enum", "register", "typedef", "char", "return", "union", "const",
				"extern", "float", "short", "unsigned", "continue", "for", "signed", "void",
				"default", "goto", "sizeof", "volatile", "do", "if", "static", "while"
		};
		
		for (String k : keyword) {
			keywords.add(k);
		}

		// S->if B S;|if B S; else S;|<id>=E|S;S
		// B->B >= B|<num>|<id>
		// E->E+E|E*E|<num>|<id>

		// 添加非终结符
		VN.add("S'");
		VN.add("S");
		VN.add("B");
		VN.add("E");
		VT.add("if");
		VT.add("else");
		VT.add(";");
		VT.add("=");
		VT.add(">=");
		VT.add("<num>");
		VT.add("<id>");
		VT.add("*");
		VT.add("+");
		VT.add("(");
		VT.add(")");

		addFirst();
	}

	// 从文件中读取文法并且存储到CFG类的静态容器中，编号就是容器的index
	private static void read(String filename) {
		File file = new File(inputPath + filename);
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] div = line.split("->");
				String[] right = div[1].split("\\|"); // 将合并书写的多个表达式解析成多个
				for (String r : right) {
					Derivation derivation = new Derivation(div[0] + "->" + r);
					F.add(derivation); // 存储到静态的容器中
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 计算所有符号的first集合，中间需要若干步推导的使用一个递归方法解决问题
	private static void addFirst() {
		// 将所有的终结符的first都设为本身
		Iterator<String> iterVT = VT.iterator();
		while (iterVT.hasNext()) {
			String vt = iterVT.next();
			firstMap.put(vt, new TreeSet<>());
			firstMap.get(vt).add(vt);
		}

		// 计算所有非终结符的first集合
		Iterator<String> iterVN = VN.iterator();
		while (iterVN.hasNext()) {
			String vn = iterVN.next();
			firstMap.put(vn, new TreeSet<>()); // 因为后续操作没有交叉涉及firstMap，所以不必分成两个while循环，合成一趟即可
			int dSize = F.size();
			for (int i = 0; i < dSize; i++) {
				Derivation d = F.get(i);
				if (d.left.equals(vn)) {
					if (VT.contains(d.list.get(0))) { // 如果是产生式右端第一个文法符号是一个终结符，则直接添加
						firstMap.get(vn).add(d.list.get(0));
					} else  { // 如果产生式右端第一个文法符号是个非终结符，则需要进行递归查找
						firstMap.get(vn).addAll(findFirst(d.list.get(0)));
					}
				}
			}
		}
	}

	// 递归查找first集合
	private static TreeSet<String> findFirst(String vn) {
		TreeSet<String> set = new TreeSet<>();
		for (Derivation d : F) {
			if (d.left.equals(vn)) {
				if (VT.contains(d.list.get(0))) { // 如果是个终结符，则直接加入
					set.add(d.list.get(0));
				} else {
					if (!vn.equals(d.list.get(0))) { // 去除类似于E->E*E这样的左递归，从而有效避免栈溢出
						set.addAll(findFirst(d.list.get(0))); // 再次递归
					}
				}
			}
		}
		return set;
	}
}
