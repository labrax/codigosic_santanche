#!/bin/usr/python
# -*- coding: utf-8 -*-

from BeautifulSoup import BeautifulSoup
import urllib2
import urllib

from time import sleep

url = "http://www.dac.unicamp.br/sistemas/catalogos/grad/catalogo2015/TiposDisciplinas.html"
base_url = "http://www.dac.unicamp.br/sistemas/catalogos/grad/catalogo2015/"

materias = []

def pega_materias_pagina(url):
	print "pagina: \"" + url + "\""
	page=urllib2.urlopen(url)
	soup = BeautifulSoup(page.read())
	courses = soup.findAll("div", {'class': 'div10b'})
	courses_str = ""
	for eachcourse in courses:
		courses_str += str(eachcourse) + " "

	newsoup = BeautifulSoup(courses_str)

	links = newsoup.findAll("a")
	for materia in links:
		#print "Materia " + materia.string
		materias.append(materia.string)
	

def pega_codigos():
	page=urllib2.urlopen(url)
	soup = BeautifulSoup(page.read())
	courses = soup.findAll("div", {'class': 'div10b'})
	courses_str = ""
	for eachcourse in courses:
		courses_str += str(eachcourse) + " "

	newsoup = BeautifulSoup(courses_str)

	links = newsoup.findAll("a")
	for l in links:
		#print "pegando " + l.string
		pega_materias_pagina(base_url + l['href'].replace(" ", "%20"))
		sleep(0.1)

def print_materias(file_name):
	f = open(file_name, 'w')
	for i in materias:
		f.write(i + "\n")
	f.close()

'''
graduação:
cboSubG
	1  1º semestre
	2  2º semestre
pos-graduação:
cboSubP
    21  1º semestre
    22  1º semestre
cboAno
    2015 --- ver com o catálogo (talvez tenha limite de anos gde: 2009)
txtDisciplina
	código da disciplina
'''
def consulta(materia):
	url_to_submit = '/altmatr/conspub_situacaovagaspordisciplina.do'
	data = urllib.urlencode({'org.apache.struts.taglib.html.TOKEN' : "3a7d44f929e7346fb92fa4ab59375f11" ,'cboSubG': '1', 'cboSubP' : '-1', 'cboAno': '2015', 'txtDisciplina': str(materia), 'txtTurma': 'V'})
