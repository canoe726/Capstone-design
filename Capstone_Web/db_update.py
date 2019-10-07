#!D:\Program Files\Python\python.exe
print("Content-Type: text/html; charset-utf-8")
print()

import sys
import pymysql
import hashlib
import requests

my_student_number = sys.argv[1]
my_file_path = my_student_number
print("my_student_number : ",my_student_number)
my_student_number = hashlib.sha512(str(my_student_number).encode('utf-8')).hexdigest()

conn = pymysql.connect(host="localhost", user="root", password="123456", db="authproj", charset='utf8')
curs = conn.cursor(pymysql.cursors.DictCursor)
#sql = "SELECT * FROM student WHERE StudentName = 'virtual'"
sql = "SELECT * FROM student WHERE StudentID='" + my_student_number + "'"
curs.execute(sql)

rows = curs.fetchall()
value = rows[0]['VoicePrint']

if( value == 1 ) :
    #sql = "UPDATE student SET VoicePrint = '0' WHERE StudentName = 'virtual'"
    sql = "UPDATE student SET VoicePrint = '0' WHERE StudentID = '" + my_student_number + "'"
    curs.execute(sql)
    conn.commit()
    conn.close()
    #print("DB update complete!")

    
