#!/usr/bin/python
# dupFinder.py
import os, sys
import hashlib
 
def remove_file(filename):
    print("\t\t >>>>> will remove >>>>>>> " + filename + " ("+str(os.stat(filename).st_size)+ ")")
    os.remove(filename)

def findDup(parentFolder):
    # Dups in format {hash:[names]}
    dups = {}
    for dirName, subdirs, fileList in os.walk(parentFolder):
        print('Scanning %s...' % dirName)
        for filename in fileList:
            # Get the path to the file
            path = os.path.join(dirName, filename)
            # Calculate hash
            file_hash = hashfile(path)
            # Add or append the file path
            if file_hash in dups:
                dups[file_hash].append(path)
            else:
                dups[file_hash] = [path]
    return dups
 
 
# Joins two dictionaries
def joinDicts(dict1, dict2):
    for key in dict2.keys():
        if key in dict1:
            dict1[key] = dict1[key] + dict2[key]
        else:
            dict1[key] = dict2[key]
 
 
def hashfile(path, blocksize = 65536):
    afile = open(path, 'rb')
    hasher = hashlib.md5()
    buf = afile.read(blocksize)
    while len(buf) > 0:
        hasher.update(buf)
        buf = afile.read(blocksize)
    afile.close()
    return hasher.hexdigest()
 
 
def printResults(dict1):
    print('___________________ findDup results IN ___________________')

    results = list(filter(lambda x: len(x) > 1, dict1.values()))
    if len(results) > 0:
        for result in results:
            max_len = 0
            file_to_delete = result[0]
            count = 0
            for subresult in result:
                print("\t\t" + subresult + "-"+str(os.stat(subresult).st_size))
                if (len(subresult) > max_len):
                    max_len = len(subresult)
                    file_to_delete = subresult
            count = count + os.stat(file_to_delete).st_size
            remove_file(file_to_delete)
        print('>>>> '+ str(count/1024)+ "kb to remove...")
    else:   
        print('No duplicate files found.')

    print('___________________ findDup results OUT ___________________ ')
 
# look for duplicated files posfix "(1)" and remove the largest one :)
def substring_file_finder(parentFolder):
    print('___________________ substring_file_finder IN ___________________ ')
    count = 0
    files = os.listdir(parentFolder)
    for i in range(0, len(files)):
        name1, extension1 = os.path.splitext(files[i])
        if (name1.find("(1)") == -1):
            continue

        for j in range(0, len(files)):
            name2, extension2 = os.path.splitext(files[j])

            if ((name1.find(name2) >=0) and (name1 != name2)):
                size1 = os.stat(files[i]).st_size
                size2 = os.stat(files[j]).st_size
                print("names="+name1+extension1 +" / "+name2+extension2+
                      "    size="+str(size1/(1024))+"/"+str(size2/(1024))
                      )
                if (size1 > size2):
                    remove_file(name1+extension1)
                    count = count + size1
                else:
                    remove_file(name2+extension2)
                    count = count + size2
                print('___________________')

    print (">>>> " + str(count/(1024))+ " kb could be saved")
    print('___________________ substring_file_finder OUT ___________________ ')


# look for duplicated files with different extensions 
# IPHONE re-saves video files in MOV or in M4V extensions
# if we find them, we must delete the largest 
def find_duplicated_videos_with_different_ext(parentFolder):
    print('___________________ find_duplicated_videos_with_different_ext IN ___________________ ')
    bytes = 0
    files = os.listdir(parentFolder)
    for i in range(0, len(files)-1):
        name1, extension1 = os.path.splitext(files[i])

        for j in range(i+1, len(files)):
            name2, extension2 = os.path.splitext(files[j])

            if ((name1.lower() == name2.lower()) and (extension1 != extension2)):
                size1 = os.stat(files[i]).st_size
                size2 = os.stat(files[j]).st_size
                print("names="+name1 +"/"+name2+
                      "    ext="+extension1+"/"+extension2+
                      "    size="+str(size1/(1024))+"/"+str(size2/(1024))
                      )
                found = 0
                if (extension1.lower().find("mov") >= 0):
                    remove_file(name1+extension1)
                    bytes = bytes + size1
                    found = 1
                elif (extension2.lower().find("mov") >=0 ):
                    remove_file(name2+extension2)
                    bytes = bytes + size2
                    found = 1
                if (found == 1):
                    continue
                if (extension1.find("m4v") >= 0):
                    remove_file(name1+extension1)
                    bytes = bytes + size1
                elif (extension2.find("m4v") >=0 ):
                    remove_file(name2+extension2)
                    bytes = bytes + size2


    print (">>>> " + str(bytes/(1024))+ " kb could be saved")
    print('___________________ find_duplicated_videos_with_different_ext OUT ___________________ ')

        
if __name__ == '__main__':
    if len(sys.argv) > 1:
        dups = {}
        folders = sys.argv[1:]
        for i in folders:
            # Iterate the folders given
            if os.path.exists(i):
                # Find the duplicated files and append them to the dups - based on file hash :)
                joinDicts(dups, findDup(i))
            else:
                print('%s is not a valid path, please verify' % i)
                sys.exit()
        printResults(dups)
        substring_file_finder(folders[0])
        find_duplicated_videos_with_different_ext(folders[0])

    else:
        print('Usage: python dupFinder.py folder or python dupFinder.py folder1 folder2 folder3')										
	
