#include <cstdio>
#include <cstdlib>
#include <cassert>

#define AMOUNT_TRAITS 5
#define MAX_TRAITS_VALUE 10
#define GRID_SIZE 10
#define AMOUNT_INTERACTIONS 10

int getRandom(int max);

//element for the grid
typedef struct _elem
{
	int traits[AMOUNT_TRAITS];
	int changed;
} elem;

//declaration of grid!
elem * grid[GRID_SIZE][GRID_SIZE];

//create element for the grid with random traits
elem * newElem(void)
{
	elem * will_ret = (elem *) malloc(sizeof(elem));
	will_ret->changed = 0;
	for(int i = 0; i < AMOUNT_TRAITS; i++)
	{
		will_ret->traits[i] = getRandom(MAX_TRAITS_VALUE);
	}
	return will_ret;
}

//amount of equal traits
int getSimilarity(elem * a, elem * b)
{
	int amount = 0;
	for(int i = 0; i < AMOUNT_TRAITS; i++)
	{
		if(a->traits[i] == b->traits[i])
			amount++;
	}
	return amount;
}

//make the culture closer
void friendfy(elem * a, elem * b)
{
	//first check if they differ something
	int amount = 0;
	for(int i = 0; i < AMOUNT_TRAITS; i++)
	{
		if(a->traits[i] == b->traits[i])
			amount++;
	}
	if(amount == AMOUNT_TRAITS)
		return; //they are already equal
	
	while(1)
	{
		int trait = getRandom(AMOUNT_TRAITS);
		if(a->traits[trait] != b->traits[trait])
		{
			a->traits[trait] = b->traits[trait];
			a->changed = 1;
			break;
		}
	}
}

//random element
int getRandom(int max)
{
	return rand()%max;
}

//print grid
void printGrid(void)
{
	for(int i = 0; i < GRID_SIZE; i++)
	{
		for(int j = 0; j < GRID_SIZE; j++)
		{
			for(int e = 0; e < AMOUNT_TRAITS; e++)
			{
				printf("%d", grid[i][j]->traits[e]);
			}
			if(grid[i][j]->changed == 1)
				printf("*");
			else
				printf(" ");
			printf(" ");
		}
		printf("\n");
	}
}

int main(int argc, char * argv[])
{
	int amount_interactions = 0;
	if(argc > 1)
		amount_interactions = atoi(argv[1]);
	else
		amount_interactions = AMOUNT_INTERACTIONS;

	printf("Amount interactions is %d\n", amount_interactions);
	printf("run \"%s <amount_interactions>\" to change\n", argv[0]);

	//start random seed
	srand(0);

	//create grid
	for(int i = 0; i < GRID_SIZE; i++)
	{
		for(int j = 0; j < GRID_SIZE; j++)
		{
			grid[i][j] = newElem();
		}
	}

	printf("Initial grid:\n");
	printGrid();

	//interact between elements
	for(int i = 0; i < amount_interactions; i++)
	{
		printf("\rInteraction %d/%d", i+1, amount_interactions);
		int y = getRandom(GRID_SIZE);
		int x = getRandom(GRID_SIZE);

		assert(x >= 0 && x < GRID_SIZE);
		assert(y >= 0 && y < GRID_SIZE);

		int y2 = 0;
		int x2 = 0;

		//will select a valid neighbour within 4-neighbourhood
		while(1)
		{
			int direction = getRandom(4);

			switch(direction)
			{
				case 0:
					x2 = x;
					y2 = y-1;
					break;
				case 1:
					x2 = x;
					y2 = y+1;
					break;
				case 2:
					x2 = x-1;
					y2 = y;
					break;
				case 3:
					x2 = x+1;
					y2 = y;
					break;
				default:
					break;
			}

			//go out of loop, we have a valid place
			if(x2 >= 0 && x2 < GRID_SIZE && y2 >= 0 && y2 < GRID_SIZE)
				break;
		}
		if(getRandom(100) < (getSimilarity(grid[y][x], grid[y2][x2])*100)/AMOUNT_TRAITS)
		{
			friendfy(grid[y][x], grid[y2][x2]);
		}
	}
	printf("\n");

	printf("Final grid:\n");
	printGrid();

	//destroy grid
	for(int i = 0; i < GRID_SIZE; i++)
		for(int j = 0; j < GRID_SIZE; j++)
			free(grid[i][j]);
	return 0;
}
