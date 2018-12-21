from pathlib import Path
from getFiles import getfiles
import os
from shutil import copyfile

baseDir = '/Users/ericho/Downloads/a'
parentPath = '/Users/ericho/workspace/JX/JX_App_Android/app/src/main/res'


def getUserInput():
    global baseDir, parentPath
    print('Searching for a specific file and renaming files in the Directory recursively ...')
    if baseDir == '':
        print('Enter the base directory path')
        baseDir = input()
    if parentPath == '':
        print('New parent path: ')
        parentPath = input()


def mkdir(replaced):
    file = Path(replaced)
    if not os.path.exists(file.parent):
        os.makedirs(file.parent)


def move(mfiles):
    for x in mfiles:
            oldfilepath = str(x.absolute())
            oldfileparent = str(x.parents[1])
            dist = oldfilepath.replace(oldfileparent, parentPath)
            mkdir(dist)
            copyfile(oldfilepath, dist)
            print('moved files: ' + str(x.absolute()))


def run():
    getUserInput()
    p = Path(baseDir)
    dirs = [x for x in p.iterdir() if x.is_dir()]
    print('Directories found: ')
    print(dirs)
    getfiles(dirs, move)


run()

