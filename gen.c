#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>

#define N 1*1000
#define p 0.015 //p 0.03

int adj[N][N];

int distr[N];

int main(void)
{
	int criou = 0;
	int seed = 0;//time(NULL);
	//printf("seed = %d\n", seed);
	srand(seed);
	memset(adj, 0, sizeof(int) * N * N);
	memset(distr, 0, sizeof(int) * N);
	int i, j;
	for(i = 0; i < N; i++)
	{
		for(j = 0; j < N; j++)
		{
			if(i == j)
				continue;
			if( (rand()%1000) < 1000*p)
			{
				adj[i][j] = 1;
				//adj[j][i] = 1;
				criou++;
			}
		}
	}

	/*for(i = 0; i < N; i++)
	{
		int amt = 0;
		for(j = 0; j < N; j++)
		{
			if(adj[i][j] == 1)
				amt++;
		}
		distr[amt]++;
	}*/

	/*printf("número de arestas = %d\n", criou);
	printf("média = %f\n", ((float) criou)/N);

	for(i = 0; i < N; i++)
	{
		if(distr[i] != 0)
			printf("%d, %d\n", i, distr[i]);
	}*/

	printf("Source,Target\n");
	for(i = 0; i < N; i++)
	{
		for(j = 0; j < N; j++)
		{
			if(adj[i][j] == 1)
				printf("%d,%d\n", i, j);
		}
	}

	return 0;
}
