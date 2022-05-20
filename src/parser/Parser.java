package parser;

import java.util.ArrayList;
import java.util.Stack;

import lexer.Lexer;
import lexer.Token;
import lexer.Type;

public class Parser {
	private Lexer lexer; // 词法分析器
	private ArrayList<Token> tokenList; // 从词法分析器获得的所有token
	private int length; // tokenlist的长度
	private int index; // 现在所指的token位置

	private AnalyzeTable table; // 构造的语法分析表
	private Stack<Integer> stateStack; // 用于存储相应的状态

	public Parser(String filename) {
		this.lexer = new Lexer(filename);
		this.tokenList = lexer.getTokenList();
		this.tokenList.add(new Token(-1, "$"));
		this.length = this.tokenList.size();
		this.index = 0;
		this.table = new AnalyzeTable();
		this.stateStack = new Stack<Integer>();
		this.stateStack.push(0);
		this.table.dfa.printAllStates();
		this.table.print();
	}

	public Token readToken() {
		if (index < length) {
			return tokenList.get(index++);
		} else {
			return null;
		}
	}

	public void analyze() {
		while (true) {
			Token token = readToken();
			int valueType = token.type;
			String value = getValue(valueType);
			int state = stateStack.lastElement();
			String action = table.ACTION(state, value);
			System.out.print("ACTION: " + action + "|");
			if (action.startsWith("s")) {
				int newState = Integer.parseInt(action.substring(1));
				stateStack.push(newState);
				System.out.print("移入" + "|");
				System.out.print("状态表:" + stateStack.toString() + "|");
				System.out.print("输入:");
				printInput();
				System.out.println();
			} else if (action.startsWith("r")) {
				Derivation derivation = CFG.F.get(Integer.parseInt(action.substring(1)));
				int r = derivation.list.size();
				index--;
				for (int i = 0; i < r; i++) {
					stateStack.pop();
				}
				int s = table.GOTO(stateStack.lastElement(), derivation.left);
				stateStack.push(s);
				System.out.print("规约" + "|");
				System.out.print("状态表:" + stateStack.toString() + "|");
				System.out.print("输入:");
				printInput();
				System.out.println();
			} else if (action.equals(AnalyzeTable.acc)) {
				System.out.print("语法分析完成" + "|");
				System.out.print("状态表:" + stateStack.toString() + "|");
				System.out.print("输入:");
				printInput();
				System.out.println();
				return;
			} else {
				error();
				return;
			}
		}
	}

	private String getValue(int valueType) {
		switch (valueType) {
			case Type.ADD:
				return "+";
			case Type.SUB:
				return "-";
			case Type.MUL:
				return "*";
			case Type.DIV:
				return "/";
			case Type.ID:
				return "<id>";
			case Type.NUM:
				return "<num>";
			case Type.IF:
				return "if";
			case Type.ELSE:
				return "else";
			case Type.SEMICOLON:
				return ";";
			case Type.PARENTHESIS_L:
				return "(";
			case Type.PARENTHESIS_R:
				return ")";
			case Type.GE:
				return ">=";
			case Type.ASSIGN:
				return "=";
			case -1:
				return "$";
			default:
				return null;
		}
	}

	public void error() {
		System.out.println("[error] 第" + (index - 1) + "处词法分析元素" );
		System.out.println("<" + tokenList.get(index - 1).type + "," + tokenList.get(index - 1).value + ">");
	}

	private void printInput() {
		String output = "";
		for (int i = index; i < tokenList.size(); i++) {
			output += tokenList.get(i).value;
			output += " ";
		}
		System.out.print(output);
	}
}
