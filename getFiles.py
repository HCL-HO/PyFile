from pathlib import Path


def getfiles(mdirs, handlefiles):
    foundfiles = []
    for x in mdirs:
        subdir = Path(x)
        foundfiles.extend(list(subdir.glob('*.png')))
    handlefiles(foundfiles)
