import string
import os
import subprocess
csvFile="/Users/chandu/Desktop/updatedData/dataFinal.csv"

f = file(csvFile)
data = f.read()
lines = string.split(data,'\r')
print len(lines)
uniqueList =[]
uni =[]
for i in range(1,len(lines)):
    l=lines[i]
    l = string.replace(l,'\n',"")
    tempContainer = string.split(l,",")
    if ("{0:0.1f}".format(float(tempContainer[15])) not in uniqueList):
        uniqueList.append("{0:0.1f}".format(float(tempContainer[15])))
        
print len(uniqueList)
print (uniqueList)

for m in uniqueList:
    dataContainer = []
    count=0
    for j in range(1,len(lines)):
        n=lines[j]
        n = string.replace(n,'\n',"")
        tempContainer = string.split(n,",")
        if (m == "{0:0.1f}".format(float(tempContainer[15]))):
            data = ""
            for i in range(0,len(tempContainer)):
                if i != 2 :
                    data = data + tempContainer[i]+","
            dataContainer.append(data)
    dataContainer = list(set(dataContainer))
    if len(dataContainer) != 1:
        tempAvgContainer = string.split(dataContainer[0],",")
        for b in range (1,len(dataContainer)):
            tempHolder =(string.split(dataContainer[b],","))
            for c in range (0,len(tempAvgContainer)):
                if ( c not in [0,1,2,3,10,22]):
                    tempAvgContainer[c] = (float(tempAvgContainer[c]) + float(tempHolder[c]))/2
        newData =""
        for i in range(0,len(tempAvgContainer)):
            newData = newData + str(tempAvgContainer[i])+","
        dataContainer=[newData]
        #print (dataContainer)
    dataToWrite = ""
    for i in range(0,len(dataContainer)):
            dataToWrite = dataToWrite + str(dataContainer[i])+","
    #print (dataToWrite)
    fa = open("/Users/chandu/Desktop/new.csv",'a')
    fa.write(dataToWrite+"\n")
    fa.close()
