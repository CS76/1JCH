import string
import os
import subprocess
csvFile="/Users/chandu/Desktop/NWCHEM_EXP_SLR.csv"

f = file(csvFile)
data = f.read()
lines = string.split(data,'\r')
print len(lines)
uniqueList =[]
for l in lines:
    l = string.replace(l,'\n',"")
    tempContainer = string.split(l,",")
    if (([tempContainer[0],tempContainer[1],tempContainer[3],tempContainer[4]]) not in uniqueList):
        uniqueList.append([tempContainer[0],tempContainer[1],tempContainer[3],tempContainer[4]])

for m in uniqueList:
    data = "["
    for n in lines:
        n = string.replace(n,'\n',"")
        tempContainer = string.split(n,",")
        if (m[0] == tempContainer[0] and m[1] == tempContainer[1] and m[2] == tempContainer[3] and m[3] == tempContainer[4]):
            data = data + tempContainer[2]+":"
    data = data + "]"
    print m[0]+","+m[1]+","+data+","+m[2]+","+m[3]
    fa = open("/Users/chandu/Desktop/NWCHEM_EXP_SLR_Strict.csv",'a')
    fa.write(m[0]+","+m[1]+","+data+","+m[2]+","+m[3]+"\n")
    fa.close()
                
