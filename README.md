# cminus-lexer-parser

Build a simply lexer and parser for C minus (subset of C), using Java.

Explore more details in this [blog post](https://lunarwhite.notion.site/Tiny-Lexer-and-Parser-for-CMinus-f5033869faa9425eab94cf8cf2539a92).

## Structure

- Lexer: 
  - Input a C source file to be syntactically analyzed, output the token sequence after the lexical analysis process, and return it with Java's built-in data structure ArrayList to provide a data source for subsequent lexical analysis.
- Parser: 
  - The token sequence obtained from the lexical analysis of a program in the previous experiment is input, and the move-in statute sequence from the LR(1) analysis in the bottom-up syntax analysis is output. 
  - The sequence also shows the contents of the stack and contains error handling and hints. The program can automatically construct an LR(1) analysis table by entering the grammar.

## Todo

- Performance improvement. Too many switch-cases, too many nested if-else.
- Complete unit tests to improve test coverage.
- Simply GUI to improve user ease of use.

## Reference

- [Let's Build a Compiler, by Jack Crenshaw](https://compilers.iecc.com/crenshaw/)
- [The LEMON Parser Generator](https://www.hwaci.com/sw/lemon/)
