#!/usr/bin/python
# -*- coding: utf-8 -*-

"""

"""

import sys
from Queue import PriorityQueue
from analysis import AnalysisAlgorithm

class OutputAnalysis:
    def __init__(self, analysis, headers=[], delimeter=', ', output=sys.stdout):
        self.analysis = []
        if type(analysis) == type([]) and isinstance(analysis[0], AnalysisAlgorithm):
            for i in analysis:
                self.analysis.append(i.get_results())
        else:
            self.analysis = analysis
        self.headers = headers
        self.delimeter = delimeter
        if type(output) == type(str()):
            try:
                self.output = open(output, "w")
            except Error as e:
                print e, "thus using stdout"
                self.output = sys.stdout
        else:
            self.output = output
    def __exit__(self):
        if type(self.output).__name__ == 'file':
            self.output.close()
    def write(self):
        if type(self.headers) == type(str()):
            self.output.write(str(self.headers)[1:-1] + '\n')
        elif self.headers and type(self.headers[0]) == type(str()):
            output = self.headers[0]
            for h in self.headers[1:]:
                output += self.delimeter + h
            output += '\n'
            self.output.write(output)
        elif self.headers:
            output = self.headers[0].__name__
            for h in self.headers[1:]:
                output += self.delimeter + h.__name__
            output += '\n'
            self.output.write(output)

        if type(self.analysis[0]) == type([]): #multiple analysis
            queue = PriorityQueue()
            
            curr_index = self.analysis[0][1]
            curr_values = ['0'] * len(self.analysis)
            
            for a in range(0, len(self.analysis)):
                queue.put((self.analysis[a][0][0], a, 1))
                if curr_index > self.analysis[a][0][0]:
                    curr_index = self.analysis[a][0][0]
            while not queue.empty():
                e = queue.get()
                if e[0] != curr_index:
                    self.output.write(str(curr_index) + self.delimeter + self.delimeter.join(curr_values) + '\n')
                    curr_index = e[0]
                curr_values[e[1]] = str(self.analysis[e[1]][e[2]][1])
                if e[2] < len(self.analysis[e[1]]):
                    queue.put((self.analysis[e[1]][e[2]][0], e[1], e[2]+1))
        elif type(self.analysis[0]) == type(tuple()): #data with tuple format
            for e in self.analysis:
                self.output.write(str(e[0]) + self.delimeter + str(e[1]) + '\n')
        else: #data with only values
            for i in self.analysis:
                self.output.write(str(i) + '\n')
