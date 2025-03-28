#include<stdio.h>

int a = 10;

int main() {
    a = 20;

    {
        a = 30;
        {
            a = 40;
            printf("%d\n", a); // 40
        }
    }

    return 0;
}
