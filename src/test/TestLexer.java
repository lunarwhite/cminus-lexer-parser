package test;

import java.util.ArrayList;

import lexer.Lexer;
import lexer.Token;

public class TestLexer {
	public static void main(String[] args) {
		Lexer lexer = new Lexer("source.c");
		ArrayList<Token> tokens = lexer.getTokenList();
		lexer.output(tokens, "result.txt");
	}
}
