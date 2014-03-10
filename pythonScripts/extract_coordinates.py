import string
import os
import subprocess
folder="C:\Users\CS76\Desktop\SP2_1_extracted"

for currentDir, subDirectories, fileNames in os.walk(folder):
    for subDirectory in subDirectories:
        folder_new = os.path.join(currentDir, subDirectory)
        for currentDir_sub, subDirectories_sub, fileNames_sub in os.walk(folder_new):
            for cmlFile in fileNames_sub:
                if cmlFile == subDirectory+".nw":
                    print cmlFile
