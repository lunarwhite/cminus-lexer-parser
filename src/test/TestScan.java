package test;

import lexer.Scan;

public class TestScan {
	public static void main(String[] args) {
		Scan scan = new Scan("source.c");
		System.out.println(scan.input);
	}
}
