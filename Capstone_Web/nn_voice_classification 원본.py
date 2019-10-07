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
        count = 0
        
        for f in onlyfiles :
            if( f[-3:] == "wav" ) :
                count += 1
        print("count : ",count)

        if( count == 10 ) :
            print("already conversion function operated")
        elif( count == 9 ) :
            sound = AudioSegment.from_file(mypath + "\\auth_recorded.m4a")
            file_name = f[:-4]
            print(file_name)
            sound.export(mypath + ".\\auth_recorded.wav", format="wav")
        else :
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

# preprocessing part
#my_student_number = cgi.FieldStorage()
#my_student_number = my_student_number.getvalue('sname')

my_student_number = sys.argv[1]
#my_student_number = "14000001"
my_s_n = my_student_number
print("my_student_number : ",my_student_number)
my_student_number = hashlib.sha512(str(my_student_number).encode('utf-8')).hexdigest()
my_labeling = -1

f = open("./voicedata/" + my_s_n + "/result.txt", 'w')
f.close()

parent_dir = 'voicedata'

tr_sub_dirs = ["14000001", "14000002", "14000003", "14000004"]
ts_sub_dirs = ["14000001", "14000002", "14000003", "14000004"]

for i in range(len(tr_sub_dirs)):
    if( tr_sub_dirs[i] == my_s_n ) :
        my_labeling = i

print("my_labeling : " ,my_labeling)

# m4a to wav
m4a_to_wav(parent_dir, tr_sub_dirs)
#print("conversion finished!")

train_file_name = ".\\" + parent_dir + "\\train_voice.csv"
print("train_file_name : ",train_file_name)
test_file_name = ".\\" + parent_dir + "\\test_voice.csv"
print("test_file_name : ",test_file_name)

tr_features, tr_labels = tr_parse_audio_files(parent_dir, tr_sub_dirs, 0)

with open(train_file_name, "w", newline="") as f:
    writer = csv.writer(f)
    writer.writerows(tr_features)
    
ts_features, ts_labels = ts_parse_audio_files(parent_dir, ts_sub_dirs, 0)

with open(test_file_name, "w", newline="") as f:
    writer = csv.writer(f)
    writer.writerows(ts_features)      

tr_labels = one_hot_encode(tr_labels)
ts_labels = one_hot_encode(ts_labels)

print("tr_labels_len : ",len(tr_labels))
print("ts_labels_len : ",len(ts_labels))

training_epochs = 10000
n_dim = tr_features.shape[1]
n_classes = 4

# csv train_labeling
lists = []

with open(train_file_name, 'r') as file:
   reader = csv.reader(file)
   lists = list(reader)
file.close()

num_of_train_file = 8
index = 0
length = n_classes * num_of_train_file

for i in range(length):
    if (i//num_of_train_file == index) :
        lists[i].append(index)
    else :
        index += 1
        lists[i].append(index)

with open(train_file_name, 'w', newline='') as file:
  writer = csv.writer(file)
  for i in range(length):
      writer.writerow(lists[i])
file.close()

# csv test_labeling
lists = []

with open(test_file_name, 'r') as file:
   reader = csv.reader(file)
   lists = list(reader)
file.close()

num_of_test_file = 1
index = 0
length = n_classes * num_of_test_file

for i in range(length):
    if (i//num_of_test_file == index) :
        lists[i].append(index)
    else :
        index += 1
        lists[i].append(index)

with open(test_file_name, 'w', newline='') as file:
  writer = csv.writer(file)
  for i in range(length):
      writer.writerow(lists[i])
file.close()

# neural network part
n_hidden_units_one = 250
n_hidden_units_two = 300
n_hidden_units_three = 200

sd = 1 / np.sqrt(n_dim)
learning_rate = 0.0001

X = tf.placeholder(tf.float32,[None,n_dim])
Y = tf.placeholder(tf.float32,[None,n_classes])

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
y_ = tf.nn.softmax(tf.matmul(h_3,W) + b)

init = tf.global_variables_initializer()

cost_function = -tf.reduce_sum(Y * tf.log(y_))
optimizer = tf.train.GradientDescentOptimizer(learning_rate).minimize(cost_function)

correct_prediction = tf.equal(tf.argmax(y_,1), tf.argmax(Y,1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

cost_history = np.empty(shape=[1],dtype=float)
y_true, y_pred = None, None

with tf.Session() as sess:
    sess.run(init)
    for epoch in range(training_epochs):            
        _,cost = sess.run([optimizer,cost_function],feed_dict={X:tr_features,Y:tr_labels})
        cost_history = np.append(cost_history,cost)
    
    y_pred = sess.run(tf.argmax(y_,1),feed_dict={X: ts_features})
    y_true = sess.run(tf.argmax(ts_labels,1))

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

