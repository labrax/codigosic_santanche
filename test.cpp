/**
 * 
 * this is a code to test a complete network: a node connects to all others
 * 
 */
 
#include <vector>
#include <set>
#include <iostream>

using namespace std;

struct node_ {
	set<struct node_ *> myset;
	set<struct node_ *> getSet()
	{
		return myset;
	}
};

vector<struct node_ * > nodes;

void first()
{
	long int i = 0;
	for(vector<struct node_ *>::iterator it = nodes.begin(); it != nodes.end(); ++it)
	{
		for(set<struct node_ *>::iterator it2 = (*it)->getSet().begin(); it2 != (*it)->getSet().end(); ++it2)
		{
			cout << *it << ',' << *it2 << endl;
			i++;
		}
	}
	cout << i << endl;
}

void second()
{
	long int i = 0;
	for(vector<struct node_ *>::iterator it = nodes.begin(); it != nodes.end(); ++it)
	{
		set<struct node_ *> myset = (*it)->getSet();
		for(set<struct node_ *>::iterator it2 = myset.begin(); it2 != myset.end(); ++it2)
		{
			cout << *it << ',' << *it2 << endl;
			i++;
		}
	}
	cout << i << endl;
}

int main(void)
{
	
	for(int i = 0; i < 10; i++)
	{
		struct node_ * nnode = new struct node_();
		nodes.insert(nodes.end(), nnode);
	}

	for(int i = 0; i < 10; i++)
	{
		if(i > 1)
			nodes[i]->myset.insert(nodes[i-1]);
	}

	first();
	second();
	return 0;
}
