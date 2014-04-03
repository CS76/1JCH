import string
import os
import subprocess
sourceFile="C:\\Users\\CS76\\Desktop\\dataExtracted\\mergedCMLfiles\\all_NWChem_1JCH_mulliken_CDK.cml"

f = file(sourceFile)
data = f.read()
lines = string.split(data,'\n')
print len(lines)
data = ""
for a in range (0,len(lines)):
    if ('<atom id="' in lines[a]):
        indLine = lines[a].replace("      ","")
        atomId = string.split(indLine," ")[1]
        ID = string.split(atomId,"=")[1].replace('"','')
        if ('a' in ID):
            data = data + lines[a] + "\n"
        else:
            find = 'id="'+ID+'"'
            replace = 'id="a'+str(int(ID)+1)+'"'
            abc = "      "+indLine.replace(find,replace)
            data = data + abc +"\n"
    elif('<bond id="' in lines[a]):
        string1 = string.split(lines[a],"atomRefs2")[1]
        string2 = string.split(string1,"order")[0].lstrip().rstrip()
        if ('a' in string2):
            data = data + lines[a] +"\n"
        else:
            idToken_ = string2.replace("=","")
            idToken = idToken_.replace('"','')
            ids = string.split(idToken," ")
            newID1 = int(ids[0])+1
            newID2 = int(ids[1])+1
            findID = idToken_
            replaceId = '"a'+str(newID1)+" a"+str(newID2)+'"'
            newBondID = lines[a].replace(findID,replaceId)
            data = data + newBondID +"\n"
    else:
        data = data + lines[a] +"\n"
fa = open("C:\\Users\\CS76\\Desktop\\abc.cml",'w')
fa.write(data)
fa.close()
