##['Mol ID' 0,'SMILES' 1, 'Atom ID' 2, 'Aromatic' 3,
## 'Strained' 4, 'C_Mulliken Charge' 5,
## 'AtomDegree' 6, 'C_Effective Atom Polarizability' 7,
## 'C_Partial Pi Charge ' 8, 'C_Pi Electronegativity' 9,
## 'C_SigmaElectonegativity' 10, 'Hybridization' 11,
## 'H_mulliken_charge' 12, 'H_Effective Atom Polarizability' 13,
## 'H_sigmaElectroNegativityDescriptor' 14, 'NWChem' 15,
## 'Avg_NWChem' 16, 'BondLength' 17, 'Exp1JCH' 18, 'SigmaDeltaTheta' 19,
## 'SigmaAbsDelthaTheta' 20, 'Omega' 21, 'MaxDeltaTheta' 22]

import string
import os
import subprocess
csvFile="C:\\Users\\CS76\\Documents\\NetBeansProjects\\1JCH\\Data\\WorkingDirectory\\rawData.csv"

f = file(csvFile,'r')
data = f.read()
lines = string.split(data,'\n')
print len(lines)
uniqueList =[]
uni =[]
for i in range(1,len(lines)):
    if (len(lines[i]) != 0):
        l=lines[i]
        l = string.replace(l,'\n',"")
        tempContainer = string.split(l,",")
        if ( [tempContainer[0],tempContainer[15],tempContainer[19]] not in uniqueList):
            uniqueList.append([tempContainer[0],tempContainer[15],tempContainer[19]] )
            print tempContainer[0],",",tempContainer[15],",",tempContainer[19]
            fa = open("C:\\Users\\CS76\\Documents\\NetBeansProjects\\1JCH\\Data\\WorkingDirectory\\modelData.csv",'a')
            fa.write(l+"\n")
            fa.close()
print len(uniqueList)

##print len(uniqueList)
##print (uniqueList)
##
##for m in uniqueList:
##    dataContainer = []
##    count=0
##    for j in range(1,len(lines)):
##        n=lines[j]
##        n = string.replace(n,'\n',"")
##        tempContainer = string.split(n,",")
##        if (m == "{0:0.1f}".format(float(tempContainer[15]))):
##            data = ""
##            for i in range(0,len(tempContainer)):
##                if i != 2 :
##                    data = data + tempContainer[i]+","
##            dataContainer.append(data)
##    dataContainer = list(set(dataContainer))
##    if len(dataContainer) != 1:
##        tempAvgContainer = string.split(dataContainer[0],",")
##        for b in range (1,len(dataContainer)):
##            tempHolder =(string.split(dataContainer[b],","))
##            for c in range (0,len(tempAvgContainer)):
##                if ( c not in [0,1,2,3,10,22]):
##                    tempAvgContainer[c] = (float(tempAvgContainer[c]) + float(tempHolder[c]))/2
##        newData =""
##        for i in range(0,len(tempAvgContainer)):
##            newData = newData + str(tempAvgContainer[i])+","
##        dataContainer=[newData]
##        #print (dataContainer)
##    dataToWrite = ""
##    for i in range(0,len(dataContainer)):
##            dataToWrite = dataToWrite + str(dataContainer[i])+","
##    #print (dataToWrite)
##    fa = open("/Users/chandu/Desktop/new.csv",'a')
##    fa.write(dataToWrite+"\n")
##    fa.close()
##
