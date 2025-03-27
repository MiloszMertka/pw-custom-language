grammar HolyJava;

@header {
package pl.edu.pw.ee;
}

programme: ( statement? SEMICOLON )*
;

statement:    ID '[' expr0 ']' '=' expr0	#assignarray
            | ID '=' expr0		            #assign
            | PRINT ID   		            #print
            | READ ID		                #read
;

expr0:    expr1			        #single0
        | expr1 ADD expr1	    #add
        | expr1 SUBSTRACT expr1	#sub
        ;

expr1:    expr2			        #single1
        | expr2 MULT expr2	    #mult
        | expr2 DIVIDE expr2	#div
        ;

expr2:    value                         #val
        | '[' item (',' item)* ']'      #array
        | TOFLOAT expr2                 #tofloat
        | TOINT expr2		            #toint
        | TOLONG expr2		            #tolong
        | TODOUBLE expr2	            #todouble
        | '(' expr0 ')'		            #par
        ;

item: expr0 #arrayitem
    ;

value:    ID '[' expr0 ']'	#arrayvalue
        | ID                #id
        | FLOAT			    #float
        | INT			    #int
        | LONG			    #long
        | DOUBLE		    #double
        | STRING            #string
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

SEMICOLON: ';'
    ;

WHITESPACE: (' '|'\t'|'\r'|'\n')+ { skip(); }
    ;
