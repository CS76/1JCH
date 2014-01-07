import string
import os
import subprocess
folder="/Users/chandu/Desktop/rem"

for currentDir, subDirectories, fileNames in os.walk(folder):
    for subDirectory in subDirectories:
        folder_new = os.path.join(currentDir, subDirectory)
        for currentDir_sub, subDirectories_sub, fileNames_sub in os.walk(folder_new):
            for cmlFile in fileNames_sub:
                if cmlFile == subDirectory+"_all.cml":
                    f = file(os.path.join(folder_new, cmlFile))
                    cmlData = f.read()
                    lines = string.split(cmlData,'\r')
                    print len(lines)
                    for a in range (0,len(lines)):
                        if ('---------------------- Optimization converged ----------------------' in lines[a]):
                            start = a
                            break
                    coordData='<cml>'
                    print start
                    for b in range (start,len(lines)):
                        if ('<molecule id="' in lines[b]):
                            for c in range (b,len(lines)):
                                if ('</molecule>' in lines[c]):
                                    coordData = coordData + lines[c]+"</cml>"
                                    fa = open(os.path.join(folder_new,subDirectory)+"_NWChem_coord.cml",'a')
                                    fa.write(coordData)
                                    break
                                coordData = coordData + lines[c]
