@.str = private unnamed_addr constant [13 x i8] c"Hello World\0A\00", align 1
%1 = call i32 (ptr, ...) @printf(ptr noundef @.str)