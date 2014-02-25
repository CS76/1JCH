import string
import os
import subprocess
csvFile="C:\\Users\\CS76\\Desktop\\mp.csv"

f = file(csvFile,'r')
data = f.read()
lines = string.split(data,'\n')
uniqueLigandList =[]
ligandFreq = {}
ligandProteinDic = {}
proteinList=[]
proteinDic = {}

## loops through all the lines then splits the lines in to individual ligand id's finally creates the foollowing
    ## Dictionary of protein with corresponding ligands
    ## Ligand frequency dictionary
    ## protein list
    ## unique ligand list
    
for i in range(0,len(lines)):
    if (len(lines[i]) != 0):
        if (i > 0):            
            l=lines[i]
            l = string.replace(l,'\n',"")
            tempList = string.split(l,",")
            print tempList
            for j in range (0,len(tempList)):
                if tempList[j] not in uniqueLigandList:
                    uniqueLigandList.append(tempList[j])
                    ligandFreq[tempList[j]] = 1
                else:
                    ligandFreq[tempList[j]] = ligandFreq[tempList[j]]+1
                proteinDic[proteinList[j]].append(tempList[j]) 
        else:
            l=lines[i]
            l = string.replace(l,'\n',"")
            proteinList = string.split(l,",")
            for s in proteinList:
                proteinDic[s] = []
print ligandFreq

## To list out the ligands present in all the proteins

for lig in uniqueLigandList:
    omniPresent = True
    for key, value in proteinDic.iteritems():
        if lig not in value:
            omniPresent = False
            break

    if (omniPresent):
        print lig
        

