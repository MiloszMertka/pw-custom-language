grammar HolyJava;

@header {
package pl.edu.pw.ee;
}

programme: (COMMENT|function|(statement? SEMICOLON))*
    ;

statement:    ID '[' expr0 ']' '[' expr0 ']' '=' expr0	    #assignmatrix
            | ID '[' expr0 ']' '=' expr0	                #assignarray
            | ID '=' expr0		                            #assign
            | ID '=' '[' matitem+ ']'                       #matrix
            | ID '=' arr                                    #array
            | PRINT ID   		                            #print
            | READ ID		                                #read
            | RETURN expr0                                  #return
            | (ID '=')? ID '(' (expr0 (',' expr0)*)? ')'    #funcall
            ;

function: FUN (VOID|type) ID params block #fundef
    ;

params: '(' (param (',' param)*)? ')' #funparams
    ;

param: type ID #paramdef
    ;

block: '{' (statement? SEMICOLON)* '}' #statementblock
    ;

matitem: arr #matrixitem
    ;

arr: '[' arritem (',' arritem)* ']' #arraydef
    ;

arritem: expr0 #arrayitem
    ;

expr0:    expr1			        #single0
        | expr0 OR expr1	    #or
        ;

expr1:    expr2			        #single1
        | expr1 XOR expr2	    #xor
        ;

expr2:    expr3			        #single2
        | expr2 AND expr3	    #and
        ;

expr3:    expr4			        #single3
        | expr3 ADD expr4	    #add
        | expr3 SUBSTRACT expr4	#sub
        ;

expr4:    expr5			        #single4
        | expr4 MULT expr5	    #mult
        | expr4 DIVIDE expr5	#div
        ;

expr5:    value                     #val
        | NEG expr5		            #neg
        | TOFLOAT expr2             #tofloat
        | TOINT expr2		        #toint
        | TOLONG expr2		        #tolong
        | TODOUBLE expr2	        #todouble
        | '(' expr0 ')'		        #par
        ;

value:    ID '[' expr0 ']' '[' expr0 ']' #matrixvalue
        | ID '[' expr0 ']'	             #arrayvalue
        | ID                             #id
        | FLOAT			                 #float
        | INT			                 #int
        | LONG			                 #long
        | DOUBLE		                 #double
        | STRING                         #string
        | BOOL                           #bool
        ;

type:     INTTYPE
        | LONGTYPE
        | FLOATTYPE
        | DOUBLETYPE
        | BOOLTYPE
        ;

COMMENT: '/*' .*? '*/' -> channel(HIDDEN)
    ;

FUN: 'fun'
    ;

RETURN: 'return'
    ;

READ: 'read'
    ;

PRINT: 'print'
    ;

TOINT: '(int)'
    ;

TOLONG: '(long)'
    ;

TOFLOAT: '(float)'
    ;

TODOUBLE: '(double)'
    ;

BOOL: 'true' | 'false'
    ;

INTTYPE: 'int'
    ;

LONGTYPE: 'long'
    ;

FLOATTYPE: 'float'
    ;

DOUBLETYPE: 'double'
    ;

BOOLTYPE: 'bool'
    ;

VOID: 'void'
    ;

ID: ('a'..'z'|'A'..'Z')+
   ;

FLOAT: '0'..'9'+'.''0'..'9'+'f'
    ;

DOUBLE: '0'..'9'+'.''0'..'9'+
    ;

INT: '0'..'9'+'i'
    ;

LONG: '0'..'9'+
    ;

STRING :  '"' ( ~('\\'|'"') )* '"'
    ;

ADD: '+'
    ;

SUBSTRACT: '-'
    ;

DIVIDE: '/'
    ;

MULT: '*'
    ;

AND: '&'
    ;

OR: '|'
    ;

XOR: '^'
    ;

NEG: '!'
    ;

SEMICOLON: ';'
    ;

WHITESPACE: (' '|'\t'|'\r'|'\n')+ -> channel(HIDDEN)
    ;
