from newspaper import Article
from konlpy.tag import Kkma
from konlpy.tag import Twitter
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.preprocessing import normalize
import numpy as np
import os

class SentenceTokenizer(object):
    def __init__(self):
        self.kkma = Kkma()
        self.twitter = Twitter()
        self.stopwords = []

        with open('stopwords.txt', 'r') as f:
            for line in f:
                self.stopwords.append(line[:-1])

    def url2sentences(self, url):
        article = Article(url, language='ko')
        article.download()
        article.parse()

        sentences = self.kkma.sentences(article.text)

        for idx in range(0, len(sentences)):
            if len(sentences[idx]) <= 10:
                sentences[idx-1] += (' ' + sentences[idx])
                sentences[idx] = ''
        return sentences

    def text2sentences(self, text):
        sentences = self.kkma.sentences(text)      
        for idx in range(0, len(sentences)):
            if len(sentences[idx]) <= 10:
                sentences[idx-1] += (' ' + sentences[idx])
                sentences[idx] = ''
        return sentences

    def get_nouns(self, sentences):
        nouns = []
        for sentence in sentences:
            if sentence is not '':
                nouns.append(' '.join([noun for noun in self.twitter.nouns(str(sentence)) 
                                       if noun not in self.stopwords and len(noun) > 1]))
        return nouns

class GraphMatrix(object):
    def __init__(self):
        self.tfidf = TfidfVectorizer()
        self.cnt_vec = CountVectorizer()
        self.graph_sentence = []

    def build_sent_graph(self, sentence):
        tfidf_mat = self.tfidf.fit_transform(sentence).toarray()
        self.graph_sentence = np.dot(tfidf_mat, tfidf_mat.T)
        return  self.graph_sentence

    def build_words_graph(self, sentence):
        cnt_vec_mat = normalize(self.cnt_vec.fit_transform(sentence).toarray().astype(float), axis=0)
        vocab = self.cnt_vec.vocabulary_
        return np.dot(cnt_vec_mat.T, cnt_vec_mat), {vocab[word] : word for word in vocab}


class Rank(object):
    def get_ranks(self, graph, d=0.85): # d = damping factor
        A = graph
        matrix_size = A.shape[0]
        for id in range(matrix_size):
            A[id, id] = 0 # diagonal 부분을 0으로 
            link_sum = np.sum(A[:,id]) # A[:, id] = A[:][id]

            if link_sum != 0:
                A[:, id] /= link_sum

            A[:, id] *= -d
            A[id, id] = 1

        B = (1-d) * np.ones((matrix_size, 1))
        ranks = np.linalg.solve(A, B) # 연립방정식 Ax = b
        return {idx: r[0] for idx, r in enumerate(ranks)}

class TextRank(object):
    def __init__(self, text):
        self.sent_tokenize = SentenceTokenizer()

        if text[:5] in ('http:', 'https'):
            self.sentences = self.sent_tokenize.url2sentences(text)
        else:
            self.sentences = self.sent_tokenize.text2sentences(text)

        self.nouns = self.sent_tokenize.get_nouns(self.sentences)

        self.graph_matrix = GraphMatrix()
        self.sent_graph = self.graph_matrix.build_sent_graph(self.nouns)
        self.words_graph, self.idx2word = self.graph_matrix.build_words_graph(self.nouns)

        self.rank = Rank()
        self.sent_rank_idx = self.rank.get_ranks(self.sent_graph)
        self.sorted_sent_rank_idx = sorted(self.sent_rank_idx, key=lambda k: self.sent_rank_idx[k], reverse=True)
        self.word_rank_idx =  self.rank.get_ranks(self.words_graph)
        self.sorted_word_rank_idx = sorted(self.word_rank_idx, key=lambda k: self.word_rank_idx[k], reverse=True)       

    def summarize(self, sent_num=5):
        summary = []
        index=[]

        for idx in self.sorted_sent_rank_idx[:sent_num]:
            index.append(idx)
        index.sort()

        for idx in index:
            summary.append(self.sentences[idx])
        return summary

    def keywords(self, word_num=10):
        rank = Rank()
        rank_idx = rank.get_ranks(self.words_graph)
        sorted_rank_idx = sorted(rank_idx, key=lambda k: rank_idx[k], reverse=True)

        keywords = []
        index=[]

        for idx in sorted_rank_idx[:word_num]:
            index.append(idx)

        #index.sort()
        for idx in index:
            keywords.append(self.idx2word[idx])

        return keywords

video_script_path = "./video_script/"
video_keyword_path = "./video_keyword/"

filenames = os.listdir(video_script_path)
for filename in filenames:

    fname = filename[:-4]
    
    path = video_script_path + filename
    video_script = open(path, 'r', -1, "UTF-8")
    video_script_txt = video_script.read()

    textrank = TextRank(video_script_txt)

    index = 1
    for row in textrank.summarize():
        print("[", index, "]", "summrized sentences : " ,row)
        index += 1
    print()
    
    keywords = []
    index = 1
    for row in textrank.keywords(80):
        keywords.append(row)
        print("[", index , "]", "keywords : ", row)
        index += 1

    output_file_name = video_keyword_path + fname + ".txt"
    output_file = open(output_file_name, 'w', -1, "UTF-8")
    output_file.write(keywords[0]+'\n')
    output_file.write(keywords[-1]+'\n')
    output_file.write(keywords[-2]+'\n')
    output_file.write(keywords[-3]+'\n')
    output_file.close()

#how to use url text rank
#url = 'https://news.v.daum.net/v/20190412175753235'
#textrank = TextRank(url)


    