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

def m4a_to_wav(parent_dir, sub_dirs):
    file_ext = ".m4a"
    for d in sub_dirs :
        print("d : ",d)
        mypath = ".\\" + parent_dir + "\\" + d
        onlyfiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]
        #print("onlyfiles : ",onlyfiles)

        for f in onlyfiles :
            if( f[-3:] != "txt" ) :
                sound = AudioSegment.from_file(mypath + "\\" + f)
                file_name = f[:-4]
                print(file_name)
                sound.export(mypath + ".\\" + file_name + ".wav", format="wav")
            

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

parent_dir = 'voicedata'

tr_sub_dirs = ["14000001", "14000002", "14000003", "14000004"]

m4a_to_wav(parent_dir, tr_sub_dirs)
#print("conversion finished!")

tr_features, tr_labels = tr_parse_audio_files(parent_dir, tr_sub_dirs, 0)

tr_labels = one_hot_encode(tr_labels)

print("tr_labels_len : ",len(tr_labels))

training_epochs = 10000
n_dim = tr_features.shape[1]
n_classes = 4

# neural network part
n_hidden_units_one = 280
n_hidden_units_two = 300
n_hidden_units_three = 200

sd = 1 / np.sqrt(n_dim)
learning_rate = 0.0001

X = tf.placeholder(tf.float32,[None,n_dim], name = "input")
Y = tf.placeholder(tf.float32,[None,n_classes], name = "output")

"""
W_1 = tf.Variable(tf.random_normal([n_dim,n_hidden_units_one], mean = 0, stddev=sd))
b_1 = tf.Variable(tf.random_normal([n_hidden_units_one], mean = 0, stddev=sd))
h_1 = tf.nn.tanh(tf.matmul(X,W_1) + b_1)

W_2 = tf.Variable(tf.random_normal([n_hidden_units_one,n_hidden_units_two], mean = 0, stddev=sd))
b_2 = tf.Variable(tf.random_normal([n_hidden_units_two], mean = 0, stddev=sd))
h_2 = tf.nn.sigmoid(tf.matmul(h_1,W_2) + b_2)

W = tf.Variable(tf.random_normal([n_hidden_units_two,n_classes], mean = 0, stddev=sd))
b = tf.Variable(tf.random_normal([n_classes], mean = 0, stddev=sd))
y_ = tf.nn.softmax(tf.matmul(h_2,W) + b, name = 'y_')

"""
W_1 = tf.Variable(tf.random_normal([n_dim,n_hidden_units_one], mean = 0, stddev=sd))
b_1 = tf.Variable(tf.random_normal([n_hidden_units_one], mean = 0, stddev=sd))
h_1 = tf.nn.tanh(tf.matmul(X,W_1) + b_1)

W_2 = tf.Variable(tf.random_normal([n_hidden_units_one,n_hidden_units_two], mean = 0, stddev=sd))
b_2 = tf.Variable(tf.random_normal([n_hidden_units_two], mean = 0, stddev=sd))
h_2 = tf.nn.sigmoid(tf.matmul(h_1,W_2) + b_2)

W_3 = tf.Variable(tf.random_normal([n_hidden_units_two,n_hidden_units_three], mean = 0, stddev=sd))
b_3 = tf.Variable(tf.random_normal([n_hidden_units_three], mean = 0, stddev=sd))
h_3 = tf.nn.sigmoid(tf.matmul(h_2,W_3) + b_3)

W = tf.Variable(tf.random_normal([n_hidden_units_three,n_classes], mean = 0, stddev=sd))
b = tf.Variable(tf.random_normal([n_classes], mean = 0, stddev=sd))
y_ = tf.nn.softmax(tf.matmul(h_3,W) + b, name = 'y_')

init = tf.global_variables_initializer()

cost_function = -tf.reduce_sum(Y * tf.log(y_))
optimizer = tf.train.GradientDescentOptimizer(learning_rate).minimize(cost_function)

correct_prediction = tf.equal(tf.argmax(y_,1), tf.argmax(Y,1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

cost_history = np.empty(shape=[1],dtype=float)

with tf.Session() as sess:
    sess.run(init)
    for epoch in range(training_epochs):            
        _,cost = sess.run([optimizer,cost_function],feed_dict={X:tr_features,Y:tr_labels})
        cost_history = np.append(cost_history,cost)

    saver = tf.train.Saver()
    save_path = saver.save(sess, './model/train_data.ckpt')
    print('Trained Model Saved.')
