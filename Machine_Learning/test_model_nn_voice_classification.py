#!D:\Program Files\Python\python.exe
print("Content-Type: text/html; charset-utf-8")
print()

import glob
import os
import sys
from os import listdir
from os.path import isfile, join
from pydub import AudioSegment
import librosa
import librosa.display
import numpy as np
import matplotlib.pyplot as plt
import tensorflow as tf
from matplotlib.pyplot import specgram
from sklearn.metrics import precision_score
import csv
import pymysql
import hashlib
import cgi

def extract_feature(file_name):
    X, sample_rate = librosa.load(file_name)
    stft = np.abs(librosa.stft(X))
    mfccs = np.mean(librosa.feature.mfcc(y=X, sr=sample_rate, n_mfcc=40).T,axis=0)
    chroma = np.mean(librosa.feature.chroma_stft(S=stft, sr=sample_rate).T,axis=0)
    mel = np.mean(librosa.feature.melspectrogram(X, sr=sample_rate).T,axis=0)
    contrast = np.mean(librosa.feature.spectral_contrast(S=stft, sr=sample_rate).T,axis=0)
    tonnetz = np.mean(librosa.feature.tonnetz(y=librosa.effects.harmonic(X),
    sr=sample_rate).T,axis=0)
    return mfccs,chroma,mel,contrast,tonnetz

def tr_parse_audio_files(parent_dir, sub_dirs, labeling, file_ext="*.wav"):
    features, labels = np.empty((0,193)), np.empty(0)
    
    for label, sub_dir in enumerate(sub_dirs):
        for fn in glob.glob(os.path.join(parent_dir, sub_dir, file_ext)):
            if( fn[-17:] != "auth_recorded.wav" ) :
                print("tr_fn : ",fn,", label : ", labeling)
                try:
                  mfccs, chroma, mel, contrast,tonnetz = extract_feature(fn)
                except Exception as e:
                  print ("Error encountered while parsing file: ", fn)
                  continue
                ext_features = np.hstack([mfccs,chroma,mel,contrast,tonnetz])
                features = np.vstack([features,ext_features])
                
                labels = np.append(labels, labeling)
        labeling += 1
            
    return np.array(features), np.array(labels, dtype = np.int)

def ts_parse_audio_files(parent_dir, sub_dirs, labeling, file_ext="*.wav"):
    features, labels = np.empty((0,193)), np.empty(0)
    
    for label, sub_dir in enumerate(sub_dirs):
        for fn in glob.glob(os.path.join(parent_dir, sub_dir, file_ext)):
            if( fn[-17:] == "auth_recorded.wav" ) :
                print("ts_fn : ",fn,", label : ", labeling)
                try:
                  mfccs, chroma, mel, contrast,tonnetz = extract_feature(fn)
                except Exception as e:
                  print ("Error encountered while parsing file: ", fn)
                  continue
                ext_features = np.hstack([mfccs,chroma,mel,contrast,tonnetz])
                features = np.vstack([features,ext_features])
                
                labels = np.append(labels, labeling)
        labeling += 1
            
    return np.array(features), np.array(labels, dtype = np.int)

def one_hot_encode(labels):
    n_labels = len(labels)
    n_unique_labels = len(np.unique(labels))
    one_hot_encode = np.zeros((n_labels,n_unique_labels))
    one_hot_encode[np.arange(n_labels), labels] = 1
    return one_hot_encode

my_student_number = sys.argv[1]
#my_student_number = "14000001"
my_s_n = my_student_number
print("my_student_number : ",my_student_number)
my_student_number = hashlib.sha512(str(my_student_number).encode('utf-8')).hexdigest()
my_labeling = -1

f = open("./voicedata/" + my_s_n + "/result.txt", 'w')
f.close()

parent_dir = 'voicedata'

ts_sub_dirs = ["14000001", "14000002", "14000003", "14000004"]

for i in range(len(ts_sub_dirs)):
    if( ts_sub_dirs[i] == my_s_n ) :
        my_labeling = i

print("my_labeling : ",my_labeling)

ts_features, ts_labels = ts_parse_audio_files(parent_dir, ts_sub_dirs, 0)

ts_labels = one_hot_encode(ts_labels)

print("ts_labels_len : ",len(ts_labels))

n_classes = 4

# neural network part
n_hidden_units_one = 250
n_hidden_units_two = 300
n_hidden_units_three = 200

learning_rate = 0.0001

y_true, y_pred = None, None

sess = tf.InteractiveSession()

new_saver = tf.train.import_meta_graph('./model/train_data.ckpt.meta')
new_saver.restore(sess, './model/train_data.ckpt')

tf.get_default_graph()

X = sess.graph.get_tensor_by_name('input:0')
Y = sess.graph.get_tensor_by_name('output:0')

y_ = sess.graph.get_tensor_by_name('y_:0')

y_pred = sess.run(tf.argmax(y_,1),feed_dict={X: ts_features})
y_true = sess.run(tf.argmax(ts_labels,1))

correct_prediction = tf.equal(tf.argmax(y_,1), tf.argmax(Y,1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

print("y_pred : ",y_pred)
print("y_true : ",y_true)

print("Test accuracy: ",round(sess.run(accuracy,feed_dict={X: ts_features,Y: ts_labels}),2))

# update voice print data to mysql
if( y_pred[my_labeling] == y_true[my_labeling] ):
    conn = pymysql.connect(host="localhost", user="root", password="123456", db="authproj", charset='utf8')
    curs = conn.cursor()
    #sql = "UPDATE student SET VoicePrint = '1' WHERE StudentName = 'virtual'"
    sql = "UPDATE student SET VoicePrint = '1' WHERE StudentID = '" + my_student_number + "'"
    curs.execute(sql)
    conn.commit()
    conn.close()
    print("DB update complete!")

    f = open("./voicedata/" + my_s_n + "/result.txt", 'w')
    print("value : ",1)
    f.write(str(1))
    f.close()
else :
    print("Wrong Voiceprint!")
    f = open("./voicedata/" + my_s_n + "/result.txt", 'w')
    print("value : ",0)
    f.write(str(0))
    f.close()

# delete auth_audio file
file1 = "./voicedata/" + my_s_n + "/auth_recorded.wav"
file2 = "./voicedata/" + my_s_n + "/auth_recorded.m4a"
print("file1 : ",file1)
if os.path.isfile(file1):
    os.remove(file1)
    print("remove wav")
if os.path.isfile(file2):
    os.remove(file2)
    print("remove m4a")

sess.close()
