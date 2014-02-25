import string
import os
import subprocess
folder="C:\Users\CS76\Desktop\clusterJobs\SP"

for currentDir, subDirectories, fileNames in os.walk(folder):
    for files in fileNames:
        inputFile = os.path.join(currentDir, files)
        f = file(inputFile)
        data = f.read()
        lines = string.split(data,'\n')
        content =""
        for l in lines:
            if "Subject:" in l:
                
##                molId = string.split(l,"SP2")[2].replace("_","").replace("/","").replace("N","").replace("M","")
                term = string.split(l,"/")
                ml=len(term)
                molId = string.split(term[ml-1],"SP")[0].replace(" ","").replace("_","")
                print molId               
            if "TERM_OWNER:" in l:
                content = content  + l+ "\n"
            if "Exited with exit code" in l:
                content = content  + l+ "\n"
            if "CPU time :  " in l:
                content = content  + l+ "\n"
            if "Max Memory :  " in l:
               content = content  + l+ "\n"
        print "===================================="
        content = content + "=============================" + "\n"
        output = os.path.join(currentDir, str(molId)+"_extracted.txt")
        fa = open(output,'a')
        fa.write(content)
    fa.close()
        
##                    print len(lines)
##                if files == "output.txt":
##                    print files
##                    input1 = os.path.join(currentDir_sub, files)
##                    output1 = os.path.join(currentDir_sub, subDirectory+"_all.cml")                      
##                    cmd="java -jar /Users/chandu/jumbo/jumbo-converters-compchem/jumbo-converters-compchem-nwchem/target/jumbo-converters-compchem-nwchem-0.3-SNAPSHOT-jar-with-dependencies.jar "+input1+" "+output1
##                    print cmd
##                    process = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
##                    out, err = process.communicate()
##                    if(process.returncode==0):
##                        print out
##                    else:
##                        print err
##                   
##                    content=""       
##                    f = file(input1)
##                    data = f.read()
##                    lines = string.split(data,'\n')
##                    print len(lines)
##                    content += lines[0]+'\n'
##                    for a in range (0,len(lines)):
##                        if ('Summary of "ao basis" ->' in lines[a]):
##                            content += lines[a] + "\n"
##                            for c in range (a+1,len(lines)):
##                                if (len(lines[c].lstrip().rstrip()) > 0):
##                                    content += lines [c] +"\n"
##                                else:
##                                    content += lines [c] + "\n"
##                                    a=c
##                                    break
##                                   
##                        if ("Indirect Spin-Spin Tensors (in Hz)" in lines[a]):
##                            data = ""
##                            num=[0,0]
##                            for b in range (a,len(lines)):
##                                if ("Isotropic Spin-Spin Coupling =" in lines[b]):
##                                    jch = " Isotropic J Value: "+string.split(lines[b],"=")[1].lstrip().rstrip()
##                                    content += data + jch +'\n'
##                                        
##                                elif ("Atom " in lines[b]):
##                                    data = ""
##                                    atoms = string.split(lines[b],"and")
##                                    num =[0,0]
##                                    for indAtom in range (0,len(atoms)) :
##                                        fields = string.split(atoms[indAtom],":")
##                                        num[indAtom] =int(string.split(fields[0],"Atom")[1].lstrip().rstrip())
##                                        data = data + "Atom "+str(indAtom+1)+" :" + string.split(fields[0],"Atom")[1].lstrip().rstrip()
##                                        data = data + " (" + fields[1].lstrip().rstrip()+")   "
##                                    
##                                elif ("NWChem Input Module" in lines[b]):
##                                     a=b
##                                     break
##                            content += "=========================================================" +'\n'
##                    print content
##                    fa = open(os.path.join(currentDir_sub,"extractedJCH.txt"),'a')
##                    fa.write(content)
