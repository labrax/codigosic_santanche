#include <stdio.h>

int main(void)
{
	int i, j;
	printf("Source,Target\n");
	while(scanf(" %d %d", &i, &j) != EOF)
	{
		printf("%d,%d\n", i, j);
	}
	return 0;
}
