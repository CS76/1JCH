import string
import os
import subprocess
sourceFolder="C:\\Users\\CS76\\Desktop\\dataExtracted\\SP3_extracted"
destFolder = "C:\\Users\\CS76\\Desktop\\nwChem_GeoOpt_MPA_input\\SP3"

for currentDir, subDirectories, fileNames in os.walk(sourceFolder):
    for subDirectory in subDirectories:
        folder_new = os.path.join(currentDir, subDirectory)
        for currentDir_sub, subDirectories_sub, fileNames_sub in os.walk(folder_new):
            for nwFile in fileNames_sub:
                if nwFile == subDirectory+".nw":
                    if not os.path.exists(destFolder+"\\"+subDirectory):
                        os.makedirs(destFolder+"\\"+subDirectory)
                    f = file(os.path.join(folder_new, nwFile))
                    data = f.read()
                    lines = string.split(data,'\n')
                    content = ""
                    for a in range (0,len(lines)):
                        if ('title' in lines[a]):
                            content += "title \""+subDirectory+"_GeoOpt_MPA\"" 
                            a += 1
                        if ('spinspin' in lines[a]):
                            a += 1
                        else:
                            content += lines[a] + "\n"
                    print content
                    fa = open(os.path.join(destFolder+"\\"+subDirectory,subDirectory+".nw"),'w')
                    fa.write(content)
                    fa.close()
