import shutil

import cv2
import numpy as np
import os
import glob
from handshape_feature_extractor import HandShapeFeatureExtractor
from frameextractor import frameExtractor
from scipy.spatial.distance import euclidean, cityblock, cosine

# for test
i = 0
videos = glob.glob(os.path.join('test', '*.mp4'))
for video in videos:
    framePath = os.path.join(os.getcwd(), 'testframes')
    frameExtractor(video, framePath, i)
    i = i + 1

framePath = os.path.join(os.getcwd(), 'testframes')
model = HandShapeFeatureExtractor.get_instance()
frames = glob.glob(os.path.join(os.getcwd(), 'testframes', '*.png'))
testFrameVectors = np.array([])
fileName2 = 'testset_penLayer.csv'
for frame in frames:
    img = cv2.imread(frame)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    results = model.extract_feature(img).tolist()
    testFrameVectors = np.concatenate((testFrameVectors, np.array(results)),axis=0) if testFrameVectors.size else np.array(results)
    np.savetxt(fileName2, testFrameVectors, delimiter=",")


#for train
i = 0
videos = glob.glob(os.path.join('traindata', '*.mp4'))
for video in videos:
    framePath = os.path.join(os.getcwd(),'trainframes')
    frameExtractor(video, framePath, i)
    i = i + 1

framePath = os.path.join(os.getcwd(),'trainframes')
model = HandShapeFeatureExtractor.get_instance()
frames = glob.glob(os.path.join(os.getcwd(),'trainframes', '*.png'))
trainFrameVectors = np.array([])
fileName = 'trainingset_penLayer.csv'
for frame in frames:
    img = cv2.imread(frame)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    results = model.extract_feature(img).tolist()
    trainFrameVectors = np.concatenate((trainFrameVectors, np.array(results)), axis=0) if trainFrameVectors.size else np.array(results)
    np.savetxt(fileName, trainFrameVectors, delimiter=",")

shutil.rmtree(os.path.join(os.getcwd(), "testframes"))
shutil.rmtree(os.path.join(os.getcwd(), "trainframes"))

#cosine similarity
gestureResult = []
differenceValues = []
differenceValues2 = []
differenceValues3 = []
maxdistances = []
maxdistances2 = []
maxdistances3 = []

for (i,testrow) in enumerate(testFrameVectors):
    distances = []
    distances2 = []
    distances3 = []
    for trainrow in trainFrameVectors:
        distances3.append(cosine(testrow, trainrow))
        distances2.append(euclidean(testrow, trainrow))
        distances.append(cityblock(testrow,trainrow))

    maxdistances3.append(np.array(distances3).argsort())
    maxdistances2.append(np.array(distances2).argsort())
    maxdistances.append(np.array(distances).argsort())

    differenceValues = np.diff(np.array(distances)[np.array(distances).argsort()])
    differenceValues2 = np.diff(np.array(distances2)[np.array(distances2).argsort()])
    differenceValues3 = np.diff(np.array(distances3)[np.array(distances3).argsort()])
    if abs(differenceValues[0]) < 0.05*abs(np.mean(differenceValues)):
        differenceValues = np.diff(np.array(distances2)[np.array(distances2).argsort()])
        if abs(differenceValues[0] ) < 0.05*abs(np.mean(differenceValues)):
            differenceValues = np.diff(np.array(distances3)[np.array(distances3).argsort()])
            if abs(differenceValues[-1] ) < 0.05*abs(np.mean(differenceValues)):
                gestureResult.append(np.argmin(np.array(distances) * np.array(distances2) / np.array(distances3)))
            else:
                gestureResult.append(np.argmax(np.array(distances3)))
                continue
        else:
            gestureResult.append(np.argmin(np.array(distances2)))
            continue
    else:
        gestureResult.append(np.argmin(np.array(distances)))
        continue

real_index = 0
for (i,val) in enumerate(gestureResult):
    real_index = 0
    for (j,arr2) in enumerate(maxdistances):
        if maxdistances[i][j] == maxdistances2[i][j]:
            real_index = maxdistances[i][j]
            break
        if maxdistances3[i][j] == maxdistances2[i][j]:
            real_index = maxdistances3[i][j]
            break
        if maxdistances3[i][j] == maxdistances[i][j]:
            real_index = maxdistances[i][j]
            break
        if j > 5:
            real_index = -1
            break
    if real_index == -1:
        continue
    gestureResult[i] = real_index

np.savetxt('Results.csv', gestureResult, delimiter=',', fmt='%d')