from pathlib import Path
from os.path import abspath, join, pardir
from getFiles import getfiles


baseDir = ''
oldName = ''
newName = ''


def getUserInput():
    print('Searching for a specific file and renaming files in the Directory recursively ...')
    print('Enter the base directory path')
    global baseDir, oldName, newName
    baseDir = input()
    print('File to rename: ')
    oldName = input()
    print('New Names: ')
    newName = input()


def rename(mfiles):
    for x in mfiles:
        if x.name == oldName:
            replaced = abspath(join(x, pardir)) + "/" + newName
            x.rename(replaced)
            print('renamed files: ' + str(x.absolute()))


def run():
    getUserInput()
    p = Path(baseDir)
    dirs = [x for x in p.iterdir() if x.is_dir()]
    print('Directories found: ')
    print(dirs)
    getfiles(dirs, rename)


run()


