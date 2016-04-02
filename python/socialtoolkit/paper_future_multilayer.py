#!/usr/bin/python
# -*- coding: utf-8 -*-

"""

"""

import networkx as nx

from social_experiment import EqualMultilayerExperiment
from convergence import Convergence
from population import normal_distribution
from expandable_model import ExpandableModel
from analysis import CommandAnalysis, AmountIterationLayerAnalysis

from graph_util import get_grid_groups
from util import get_cultural_groups, overlap_similarity_layer

from time import clock

if __name__ == "__main__":
    width = 32
    height = 32
    features = 6
    traits = 2
    layers = 3
    max_iterations = 5*10**5
    step_check = 10**4
    step_analysis = 10**3
    
    all_G = []
    for i in range(layers):
        all_G.append((nx.grid_2d_graph, [width, height]))
    convergence = Convergence(max_iterations, step_check)
    model = ExpandableModel
    population = (normal_distribution, [width*height, features, traits])
    experiment = EqualMultilayerExperiment(all_G, population, model, convergence, layers)
    
    analysis = [
        CommandAnalysis(0, step_analysis, get_grid_groups, [experiment.all_G[0], experiment._population]),
        CommandAnalysis(0, step_analysis, get_grid_groups, [experiment.all_G[1], experiment._population]),
        CommandAnalysis(0, step_analysis, get_grid_groups, [experiment.all_G[2], experiment._population]),
        CommandAnalysis(0, step_analysis, get_cultural_groups, [experiment._population]),
        AmountIterationLayerAnalysis(experiment._curr, layers)]
    experiment.add_analysis(analysis)
    for i in range(0, layers):
        experiment.all_model[i].overlap_function(overlap_similarity_layer, [i, layers])
    
    start = clock()
    val = experiment.converge()
    end = clock()
    
    print end-start, val
    #print "final", get_grid_groups(experiment._G, experiment._population)
    #print "final", get_cultural_groups(experiment._population)
    print "get_grid_groups[0]:", analysis[0].get_results()
    print "get_grid_groups[1]:", analysis[1].get_results()
    print "get_grid_groups[2]:", analysis[2].get_results()
    print "get_cultural_groups:", analysis[3].get_results()
    print "iterations for each layer:", analysis[4].get_results()
    
