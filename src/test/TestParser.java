package test;

import parser.Parser;

public class TestParser {
    public static void main(String[] args) {
		Parser parser = new Parser("source.c");
		parser.analyze();
	}
}
