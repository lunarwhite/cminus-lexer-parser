package lexer;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

// 扫描和缓存类

public class Scan {
	private static String inputPath = "res/input/";

	public String input;
	public int pointer;

	public Scan(String filename) {
		File sourceFile = new File(Scan.inputPath + filename);
		ArrayList<Character> trans = new ArrayList<>();

		try (FileInputStream in = new FileInputStream(sourceFile)) {
			char ch = ' ';
			char endCh = ' '; // 验证是否为注释结尾or引号内结尾

			while (in.available() > 0) {
				if (endCh != ' ') {
					ch = endCh;
				} else {
					ch = (char) in.read();
				}

				if (ch == '\'') { // 避免删除空白时将''包含的空白字符删除
					trans.add(ch);
					trans.add((char) in.read());
					trans.add((char) in.read());
				} else if (ch == '\"') { // 避免将字符串中的空白删除
					trans.add(ch);
					while (in.available() > 0) {
						ch = (char) in.read();
						trans.add(ch);
						if (ch == '\"') {
							break;
						}
					}
				} else if (ch == '/') { // 删除字符串
					endCh = (char) in.read();
					if (endCh == '/') {
						while (in.available() > 0) {
							endCh = (char) in.read();
							if (endCh == '\n') {
								break;
							}
						}
						endCh = ' ';
					} else if (endCh == '*') {
						while (in.available() > 0) {
							ch = (char) in.read();
							if (ch == '*') {
								endCh = (char) in.read();
								if (endCh == '/') {
									break;
								}
							}
						}
					} else {
						if (endCh == ' ') {
							while (endCh == ' ') {
								endCh = (char) in.read();
							}
						}
						trans.add(ch);
						trans.add(endCh);
						endCh = ' ';
					}
				} else if (ch == ' ') {
					if (trans.get(trans.size() - 1) == ' ') {
						continue;
					}
				} else {
					if ((int) ch != 13 && (int) ch != 10 && (int) ch != 32) { // 去除换行
						trans.add(ch);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		char[] chStr = new char[trans.size()];
		for (int i = 0; i < trans.size(); i++) {
			chStr[i] = trans.get(i);
		}
		String result = new String(chStr);

		this.input = result;
		this.pointer = 0;
	}

	public char getNextChar() {
		if (pointer == input.length()) {
			return (char) 0;
		} else {
			return input.charAt(pointer++);
		}
	}

	// 回退n个字符
	public void retract(int n) {
		for (int i = 0; i < n; i++) {
			pointer--;
		}
	}

	public int getIndex() {
		return pointer;
	}

	public int getLength() {
		return this.input.length();
	}

	public String getSubStr(int index, int length) {
		if ((index + length - 1) >= this.input.length()) {
			return null;
		} else {
			String result = this.input.substring(index, index + length);
			return result;
		}
	}

	public String getTestString(int index) {
		int temp = index;
		int len = 1;
		while (isLetterOrDigit(input.charAt(temp)) && (temp <= (input.length() - 1))) {
			temp++;
			len++;
		}
		String result = input.substring(index - 1, index - 1 + len);
		return result;
	}

	public boolean isLetterOrDigit(char c) {
		if (c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
			return true;
		} else {
			return false;
		}
	}

	public String getLeftStr(int index) {
		if (index == input.length() - 1) {
			return null;
		} else {
			return input.substring(index);
		}
	}

	public void move(int n) {
		for (int i = 0; i < n; i++) {
			pointer++;
		}
	}

	public String getStringInQuotation(int index) {
		int temp = index;
		while (input.charAt(temp - 1) != '\"') {
			temp--;
		}

		StringBuilder sb = new StringBuilder();
		while (input.charAt(temp) != '\"') {
			sb.append(input.charAt(temp));
			temp++;
		}
		return sb.toString();
	}
}
