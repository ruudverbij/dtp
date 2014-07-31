import numpy as np
import inspect, os
import sys
import csv

f = open('data.txt')
reader = csv.reader(f)
writer = csv.writer(open('newfile.txt', 'w+'))

bankIter = 0
banks    = 0
trans    = 0
locking  = ''
maxTime  = 0
avgExc   = 0

rowzero  = True
for row in reader:
	if bankIter == 0:
		if not rowzero:
			writer.writerow([banks, trans, locking, avgExc/banks, maxTime])
		banks    = int(row[0])
		bankIter = int(row[0])-1
		trans    = int(row[1])
		locking  = row[2]
		maxTime  = 0
		avgExc   = 0
	else:
		bankIter = bankIter - 1
		avgExc   = avgExc + int(row[3])
		maxTime  = max(maxTime, int(row[4]))
	rowzero = False
f.close()