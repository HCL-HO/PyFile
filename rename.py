from pathlib import Path
from os.path import abspath, join, pardir
from getFiles import getfiles
import sys
from config import *

MODE_RENAME = 'rename'
MODE_RENAME_ALL = 'all'


def getUserInput():
    global baseDir, oldName, newName
    print('Searching for a specific file and renaming files in the Directory recursively ...')
    if baseDir == '':
        print('Enter the base directory path')
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


def rename_multiple(mfiles):
    for x in mfiles:
        print(oldName)
        print(x.name)
        if oldName in str(x.name):
            replaced_name = str(x.name).replace(oldName, newName)
            print(replaced_name)
            replaced = abspath(join(x, pardir)) + "/" + replaced_name
            x.rename(replaced)
            print('renamed files: ' + str(x.absolute()))


def run_rename():
    getUserInput()
    p = Path(baseDir)
    dirs = [x for x in p.iterdir() if x.is_dir()]
    print('Directories found: ')
    print(dirs)
    getfiles(dirs, rename)


def run_rename_mulitiple():
    getUserInput()
    p = Path(baseDir)
    dirs = [x for x in p.iterdir() if x.is_dir()]
    print('Directories found: ')
    print(dirs)
    getfiles(dirs, rename_multiple)


def main():
    if len(sys.argv) > 1:
        mode = sys.argv[1]
        print(mode)
        if mode == MODE_RENAME:
            run_rename()
        elif mode == MODE_RENAME_ALL:
            print('Running in rename all mode...')
            run_rename_mulitiple()
        else:
            run_rename()
    else:
        run_rename()


main()
