grammar HolyJava;

@header {
package pl.edu.pw.ee;
}

programme: ( statement? SEMICOLON )*
;

statement:    ID '=' expr0		#assign
            | PRINT ID   		#print
            | READ ID		    #read
;

expr0:    expr1			        #single0
        | expr1 ADD expr1	    #add
        | expr1 SUBSTRACT expr1	#sub
        ;

expr1:    expr2			        #single1
        | expr2 MULT expr2	    #mult
        | expr2 DIVIDE expr2	#div
        ;

expr2:    value             #val
        | TOINT expr2		#toint
        | TOREAL expr2		#toreal
        | '(' expr0 ')'		#par
        ;

value:    ID            #id
        | INT			#int
        | REAL			#real
        | STRING        #string
        ;

READ: 'read'
    ;

PRINT: 'print'
    ;

TOINT: '(int)'
    ;

TOREAL: '(real)'
    ;

ID: ('a'..'z'|'A'..'Z')+
   ;

REAL: '0'..'9'+'.''0'..'9'+
    ;

INT: '0'..'9'+
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

SEMICOLON: ';'
    ;

WHITESPACE: (' '|'\t'|'\r'|'\n')+ { skip(); }
    ;
