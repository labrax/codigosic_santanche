#!/usr/bin/python
# -*- coding: utf-8 -*-

"""Programa para avaliar as contaminações nas relações predator-presa no fishbase"""

import csv
from Queue import Queue
#import sys #sys.stdout.write

__predator_names = "predator-name-all.csv"
__predator_relations = "predator-prey-all.csv"

predators = {}

class Predator:
    def __init__(self, num, name):
        self.id = num
        self.name = name
        self.predats_list = []
        self.prey_of_list = []
        self.contamina_id = 0
    
    def prey_of(self, predator):
        self.prey_of_list.append(predator)
    
    def predats(self, prey):
        self.predats_list.append(prey)
        
    def get_prey_of_list(self):
        return self.prey_of_list
    
    def get_predats_list(self):
        return self.predats_list
        
    def contamina(self, id):
        self.contamina_id = id

with open(__predator_names) as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        predators[row['Id']] = Predator(row['Id'], row['Label'])

#for i in predators:
#    print predators[i].id, predators[i].name, predators[i].predats, predators[i].prey_of

with open(__predator_relations) as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        if row['Source'] not in predators.keys():
            predators[row['Source']] = Predator(row['Source'], '')
        if row['Target'] not in predators.keys():
            predators[row['Target']] = Predator(row['Target'], '')
        predators[row['Source']].predats(predators[row['Target']])
        predators[row['Target']].prey_of(predators[row['Source']])

class TesteContaminacao:
    #contamina mais um peixe :'(
    def contamina(self, fish):
        fish.contamina(self.iteration_id)
        self.contagem_contaminados = self.contagem_contaminados + 1
    #pega os predadores do peixe atual
    def get_alvo_contaminacao(self, fish):
        return fish.get_prey_of_list()
    #verifica se já passou por este peixe
    def passou(self, fish):
        if fish.contamina_id == self.iteration_id:
            return True
        else:
            return False
    #percorre por breadth-first search
    def bfs(self, fish):
        q = Queue()
        fish.depth = 0
        q.put(fish)
        while q.empty() == False:
            item = q.get()
            self.last_depth = item.depth #para bfs sempre será válido
            if self.passou(item) == False:
                self.contamina(item)
                for i in self.get_alvo_contaminacao(item):
                    if self.passou(i) == False:
                        i.depth = item.depth + 1
                        q.put(i)
    def teste(self):
        self.iteration_id = 0
#        print "SpecCode,Name,Contaminated,Fraction,MaximumDepth"
        for i in predators:
            self.iteration_id = self.iteration_id + 1
            self.contagem_contaminados = 0
            self.bfs(predators[i])
#            for v in range(1,self.contagem_contaminados):
#                sys.stdout.write(predators[i].name + " ")
            print "%s,%s,%s,%f,%s" % (i, predators[i].name, self.contagem_contaminados, float(self.contagem_contaminados)/len(predators), self.last_depth)

if __name__ == "__main__":
    t = TesteContaminacao()
    t.teste()
