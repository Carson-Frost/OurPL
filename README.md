# OurPL

OurPL is a small programming language in Java, written for CPSC 326, Organization of Programming
Languages, at Gonzaga. It has a hand-written lexer, a recursive-descent parser, and a tree-walking
evaluator, and none of the three is generated from a grammar tool.

You use it through a REPL that prints all three stages instead of only the answer, so you can watch
an expression turn into tokens, then into a tree, then into a value.

```
$ mvn -q compile exec:java

> (2 + 3) * 4
--- Tokens ---
... one line per token ...
--- AST ---
(* (group (+ 2.0 3.0)) 4.0)
--- Result ---
20

> !(1 > 2)
--- AST ---
(! (group (> 1.0 2.0)))
--- Result ---
true

> 1 + "two"
--- Result ---
Operand must be a number.
[line 1]
```

The last case is the one the error handling was written for. A runtime failure carries the token
that caused it and the line that token sat on, so the evaluator reports where the program went
wrong instead of dropping a Java stack trace in front of you.

## What it handles

The language evaluates expressions. It understands number, string, and boolean literals, `nil`, and
parenthesized grouping, and it applies the unary operators `!` and `-`, the four arithmetic
operators, comparison, and equality with the precedence and associativity the grammar calls for.

It does not handle statements, variables, control flow, or functions yet. `Parser.parse()` returns
a single expression, so the `.opl` files in `examples/` are lexer fixtures rather than programs the
project can run. They date from the scanning stage of the course, and every one of them scans
cleanly into tokens.

## Layout

| File | What it does |
|---|---|
| `Lexer.java` | It scans source text into tokens, tracks line numbers, skips comments and whitespace, and recovers from a bad character instead of giving up on the file. |
| `Parser.java` | It walks the expression grammar by recursive descent with one method per precedence level, and it synchronizes in panic mode when it hits an error. |
| `Interpreter.java` | It visits the syntax tree and evaluates each node to a Java value, checking operand types before it coerces anything. |
| `ASTPrinter.java` | It renders a tree in parenthesized form, which is what makes a precedence bug visible at a glance. |
| `Expr.java` | It defines the node types the parser produces: binary, unary, literal, and grouping. |
| `Token.java`, `TokenType.java` | They define the token record and the kinds of token the lexer can emit. |
| `RuntimeError.java` | It carries the offending token so that an error can name the line it happened on. |

## Build and test

```bash
mvn test                     # the lexer suite
mvn -q compile exec:java     # the REPL
```

The test suite is 61 JUnit 5 cases over the lexer. They cover the single and double character
operators, string and number literals, identifiers checked against reserved words, comment and
whitespace handling, line tracking, and error recovery. The parser and the evaluator are exercised
by hand through the REPL, and neither is covered by tests yet.
