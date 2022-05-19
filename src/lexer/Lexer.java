package lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// 词法分析

public class Lexer {
	private static String outputPath = "res/output/";

	private String[] keyword = {
			"auto", "double", "int", "struct", "break", "else", "long", "switch",
			"case", "enum", "register", "typedef", "char", "return", "union", "const",
			"extern", "float", "short", "unsigned", "continue", "for", "signed", "void",
			"default", "goto", "sizeof", "volatile", "do", "if", "static", "while"
	};

	private boolean flag = false; // 用于判别双引号之间的字符串

	private Scan scan;

	public Lexer(String filename) {
		this.scan = new Scan(filename);
	}

	// 将词法分析结果输出到文件中
	public void output(ArrayList<Token> list, String filename) {
		filename = Lexer.outputPath + filename;
		File file = new File(filename);

		if (file.exists()) {
			file.delete();
		}

		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (PrintWriter pw = new PrintWriter(file)) {
			for (int i = 0; i < list.size(); i++) {
				String str = "<" + list.get(i).type + "," + list.get(i).value + ">";
				pw.println(str);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 通过语法分析获得Token序列
	public ArrayList<Token> getTokenList() {
		ArrayList<Token> result = new ArrayList<Token>();
		int index = 0;
		while (index < scan.getLength()) {
			Token token = analyze(index);
			result.add(token);
			index = scan.getIndex();
		}
		this.scan.retract(scan.getLength() - 1);
		return result;
	}

	// 对单个Token的某一位置进行词法分析
	private Token analyze(int index) {
		int length = scan.getLength();
		int type = -1;
		String value = "";

		while (index < length) {
			char ch = scan.getNextChar();
			index++;

			if (isDigit(ch)) { // 判断是否为一个数字
				if (Type.isCalc(type)) {
					scan.retract(1);
					break;
				}
				if (value == "") {
					value = Character.valueOf(ch).toString();
					type = Type.NUM;
				} else {
					value += Character.valueOf(ch).toString();
				}
			} else if (isLetter(ch)) {
				if (Type.isCalc(type)) {
					scan.retract(1);
					break;
				}
				if (flag) {
					value = scan.getStringInQuotation(index);
					type = Type.ID;
					scan.move(value.length() - 1);
					return new Token(type, value);
				}
				if (type == Type.ID) {
					value += Character.valueOf(ch).toString();
					continue;
				}
				String str = scan.getTestString(index);
				String val = null;

				if (str.startsWith("include")) {
					val = "include";
					type = Type.INCLUDE;
				} else {
					for (int i = 0; i < keyword.length; i++) {
						if (str.startsWith(keyword[i])) {
							val = keyword[i];
							type = i;
							break;
						}
					}
				}
				if (val == null) {
					type = Type.ID;
					if (value == "") {
						value = Character.valueOf(ch).toString();
					} else {
						value += Character.valueOf(ch).toString();
					}
				} else {
					value = val;
					scan.move(value.length() - 1);
					return new Token(type, value);
				}

			} else {
				if (type == Type.NUM || type == Type.ID) {
					scan.retract(1);
					return new Token(type, value);
				}
				switch (ch) {
					case '=':
						if (type == -1) {
							type = Type.ASSIGN;
							value = "=";
						} else if (type == Type.LT) { // <=
							type = Type.LE;
							value = "<=";
						} else if (type == Type.GT) { // >=
							type = Type.GE;
							value = ">=";
						} else if (type == Type.ASSIGN) {// ==
							type = Type.EQUAL;
							value = "==";
						} else if (type == Type.NOT) { // !=
							type = Type.NE;
							value = "!=";
						} else if (type == Type.ADD) { // +=
							type = Type.INCREASEBY;
							value = "+=";
						} else if (type == Type.SUB) { // -=
							type = Type.DECREASEBY;
							value = "-=";
						} else if (type == Type.DIV) { // /=
							type = Type.DIVBY;
							value = "/=";
						} else if (type == Type.MUL) { // *=
							type = Type.MULBY;
							value = "*=";
						}
						break;
					case '+':
						if (type == -1) {
							type = Type.ADD;
							value = "+";
						} else if (type == Type.ADD) {// ++
							type = Type.INCREASE;
							value = "++";
						}
						break;
					case '-':
						if (type == -1) {
							type = Type.SUB;
							value = "-";
						} else if (type == Type.SUB) { // --
							type = Type.DECREASEBY;
							value = "--";
						}
						break;
					case '*':
						if (type == -1) {
							type = Type.MUL;
							value = "*";
						}
						break;
					case '/':
						if (type == -1) {
							type = Type.DIV;
							value = "/";
						}
						break;
					case '<':
						if (type == -1) {
							type = Type.LT;
							value = "<";
						}
						break;
					case '>':
						if (type == -1) {
							type = Type.GT;
							value = ">";
						}
						break;
					case '!':
						if (type == -1) {
							type = Type.NOT;
							value = "!";
						}
						break;
					case '|':
						if (type == -1) {
							type = Type.OR_1;
							value = "|";
						} else if (type == Type.OR_1) {
							type = Type.OR_2;
							value = "||";
						}
						break;
					case '&':
						if (type == -1) {
							type = Type.AND_1;
							value = "&";
						} else if (type == Type.AND_1) {
							type = Type.AND_2;
							value = "&&";
						}
						break;
					case ';':
						if (type == -1) {
							type = Type.SEMICOLON;
							value = ";";
						}
						break;
					case '{':
						if (type == -1) {
							type = Type.BRACE_L;
							value = "{";
						}
						break;
					case '}':
						if (type == -1) {
							type = Type.BRACE_R;
							value = "}";
						}
						break;
					case '[':
						if (type == -1) {
							type = Type.BRACKET_L;
							value = "[";
						}
						break;
					case ']':
						if (type == -1) {
							type = Type.BRACKET_R;
							value = "]";
						}
						break;
					case '(':
						if (type == -1) {
							type = Type.PARENTHESIS_L;
							value = "(";
						}
						break;
					case ')':
						if (type == -1) {
							type = Type.PARENTHESIS_R;
							value = ")";
						}
						break;
					case '#':
						if (type == -1) {
							type = Type.POUND;
							value = "#";
						}
						break;
					case ',':
						if (type == -1) {
							type = Type.COMMA;
							value = ",";
						}
						break;
					case '\'':
						if (type == -1) {
							type = Type.SINGLE_QUOTAOTION;
							value = "\'";
						}
						break;
					case '"':
						if (flag == false) {
							flag = true;// 这是配对的双引号中的第一个
						} else {
							flag = false;
						}
						if (type == -1) {
							type = Type.DOUBLE_QUOTATION;
							value = "\"";
						}
						break;
					default:
						break;
				}
				if (!Type.isCalc(type)) {
					break;
				}
			}
		}
		if (value.length() > 1) {
			scan.move(value.length() - 1);
		}
		Token token = new Token(type, value);
		return token;
	}

	private boolean isDigit(char c) {
		if ((c <= '9' && c >= '0') || c == '.') {
			return true;
		} else {
			return false;
		}
	}

	private boolean isLetter(char c) {
		if ((c >= 'a' && c <= 'z') || c == '_' || (c >= 'A' && c <= 'Z')) {
			return true;
		} else {
			return false;
		}
	}
}
