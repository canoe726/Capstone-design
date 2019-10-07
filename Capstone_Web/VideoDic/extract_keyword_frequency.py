from konlpy.tag import Okt
from collections import Counter
import os
 
def get_tags(text, ntags=50):
    # konlpy의 Okt객체
    spliter = Okt()

    # nouns 함수를 통해서 text에서 명사만 분리/추출
    nouns = spliter.nouns(text)

    # Counter객체를 생성하고 참조변수 nouns할당
    count = Counter(nouns)
    
    return_list = []  # 명사 빈도수 저장할 변수
    
    # most_common 메소드는 정수를 입력받아 객체 안의 명사중 빈도수
    # 큰 명사부터 순서대로 입력받은 정수 갯수만큼 저장되어있는 객체 반환
    # 명사와 사용된 갯수를 return_list에 저장합니다.
    for n, c in count.most_common(ntags):
        temp = {'tag': n, 'count': c}
        return_list.append(temp)
    
    return return_list
 
def main():
    video_script_path = "./video_script/"
    video_keyword_path = "./video_keyword/"

    filenames = os.listdir(video_script_path)
    for filename in filenames:

        fname = filename[:-4]
        
        # 분석할 파일
        text_file_name = video_script_path + fname + ".txt"
        
        # 최대 많은 빈도수 부터 100개 명사 추출
        noun_count = 100
        
        # video_keyword dic 에 저장
        output_file_name = video_keyword_path + fname + ".txt"
        
        # 분석할 파일을 open 
        open_text_file = open(text_file_name, 'r',-1,"utf-8")
      
        text = open_text_file.read() #파일을 읽습니다.
        tags = get_tags(text, noun_count) # get_tags 함수 실행
        open_text_file.close()   #파일 close

        # 결과로 쓰일 result.txt 열기
        #open_output_file = open(output_file_name, 'w',-1,"utf-8")
        
        # 불용어 전처리
        stopwords = []

        with open('stopwords.txt', 'r') as f:
            for line in f:
                stopwords.append(line[:-1])

        size = 0
        exist = 0

        result_tags = []
        index = 1
        for tag in tags:
            noun = tag['tag']
            for s in stopwords:
                if( noun == s ):
                    exist = 1

            if (exist == 0) :
                result_tags.append(noun)
                count = tag['count']

                print("[", index , "]", "keywords : ", noun)
                size += 1
                index += 1
                #open_output_file.write('{} {} {}\n'.format(size, noun, count))
            else :
                exist = 0
            
        #open_output_file.close()

        output_file = open(output_file_name, 'w', -1, 'utf-8')
        output_file.write(result_tags[0]+'\n')
        output_file.write(result_tags[-1]+'\n')
        output_file.write(result_tags[-2]+'\n')
        output_file.write(result_tags[-3]+'\n')
        output_file.close()

if __name__ == '__main__':
    main()
