grammar Expr;

@header{
package ru.dm_ushakov.picturizer.antlr;
}

expr        : '(' expr ')' # parenthesis
            | BOOL_NOT expr # boolNot
            | expr op=(MUL|DIV) expr # mulDiv
            | expr op=(PLUS|MINUS) expr # plusMinus
            | expr op=(COMP_NEQ|COMP_EQ|COMP_GTE|COMP_LTE|COMP_GT|COMP_LT) expr # comparation
            | expr op=(BOOL_AND|BOOL_OR) expr # boolOperation
            | funcCall # funcCallExpr
            | expr '?' expr ':' expr # ternary
            | ID # variable
            | NUM # number
            | MINUS NUM # negativeNumber
            ;

funcCall: ID '(' (expr (',' expr)*)? ')';

BOOL_AND    : 'and';
BOOL_OR     : 'or';
BOOL_NOT    : 'not';

ID          : [a-zA-Z][a-zA-Z0-9]*;
NUM         : [0-9]+ ('.' [0-9]+)?;
WS          : [ \t]+ -> skip;

ASSIGN      : '=';
MUL         : '*';
DIV         : '/';
PLUS        : '+';
MINUS       : '-';

COMP_EQ     : '==';
COMP_NEQ     : '!=';
COMP_GTE    : '>=';
COMP_LTE    : '<=';
COMP_GT     : '>';
COMP_LT     : '<';