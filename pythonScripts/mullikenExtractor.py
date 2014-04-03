import string
import os
import subprocess
folder="C:\\Users\\CS76\\Desktop\\dataExtracted\\1jch\\final"

for currentDir, subDirectories, fileNames in os.walk(folder):
    for subDirectory in subDirectories:
        folder_new = os.path.join(currentDir, subDirectory)          
        for currentDir_sub, subDirectories_sub, fileNames_sub in os.walk(folder_new):
            for outputFile in fileNames_sub:
                if outputFile == "output.txt":
                    print outputFile
                    f = file(os.path.join(folder_new, outputFile))
                    data = f.read()
                    lines = string.split(data,'\n')
                    print len(lines)
                    content = ""
                    for a in range (0,len(lines)):
                        if ('Mulliken population analysis' in lines[a]):
                            print lines[a]
                            content += lines[a] + "\n"
                            for c in range (a+1,len(lines)):
                                if ("Task  times  cpu:" in lines[c]):
                                    content += lines[c] + "\n"
                                    break
                                else:
                                    content += lines[c] + "\n"
                    print content
                    fa = open(os.path.join(currentDir_sub,"mullikenOutput.txt"),'w')
                    fa.write(content)
                    
             
             
        

