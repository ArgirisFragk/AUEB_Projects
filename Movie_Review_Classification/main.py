import Data 
import Naive_Bayes
import ID3
import matplotlib.pyplot as plt
import sys

def calcAccuracy(train_results,test_results):
    sum = 0
    for i in range(len(train_results)):
        if(train_results[i] == test_results[i]):
            sum +=1
    return sum/len(train_results)

def calcRecall(results,category):
    true_positive = 0
    false_negative = 0
    for i in range(len(category)):
        if(category[i] == 1):
            if(results[i] == 1):
                true_positive += 1
            else:
                false_negative += 1
    return true_positive /(true_positive + false_negative)

def calcPrecision(results,category):
    true_positive = 0
    false_positive = 0
    for i in range(len(results)):
        if(results[i] == 1):
            if(category[i] == 1):
                true_positive += 1
            else:
                false_positive += 1
    return true_positive /(true_positive + false_positive)

def calcF1(results,category):
    Precision = calcPrecision(results,category)
    Recall = calcRecall(results,category)
    return 2*(Recall * Precision)/ (Recall + Precision)

print('Give the value of m: ')
m = input()
m = int(m)
print('Give the value of n: ')
n = input()
n = int(n)

dataset = Data.getData(m,n)#25000
sys.setrecursionlimit(2000)

#Naive_Bayes
plot_accuracy_percentage_Naive_Bayes_train = []
plot_accuracy_percentage_Naive_Bayes_test = []
plot_precision_percentage_Naive_Bayes = []
plot_recall_percentage_Naive_Bayes = []
plot_F1_Naive_Bayes = []
plot_set_size_Naive_Bayes = [] 

for i in range(100,len(dataset[1]),100):
    
    data = [[0] * i,[0] * i,[0] * i,[0] * i,[0] * i,[0] * i]

    for j in range(i):
        data[1][j] = dataset[1][j]
        data[2][j] = dataset[2][j]
        data[4][j] = dataset[4][j]
        data[5][j] = dataset[5][j]
        
    Naive_Bayes_train_vectors,Naive_Bayes_train_category,Naive_Bayes_test_vectors,Naive_Bayes_test_category= data[1],data[2],data[4],data[5]

    Prior_Positive_Prob,Attribute_Possibilities = Naive_Bayes.Naive_Bayes(Naive_Bayes_train_vectors,Naive_Bayes_train_category)

    Naive_Bayes_train_results = Naive_Bayes.test(Naive_Bayes_train_vectors,Attribute_Possibilities,Prior_Positive_Prob)
    Naive_Bayes_test_results = Naive_Bayes.test(Naive_Bayes_test_vectors,Attribute_Possibilities,Prior_Positive_Prob)

    Accuracy_Percentage_Naive_Bayes_train = calcAccuracy(Naive_Bayes_train_results,Naive_Bayes_train_category)
    Accuracy_Percentage_Naive_Bayes_test = calcAccuracy(Naive_Bayes_test_results,Naive_Bayes_test_category)

    Precision_Percentage_Naive_Bayes = calcPrecision(Naive_Bayes_test_results,Naive_Bayes_test_category)
    Recall_Percentage_Naive_Bayes = calcRecall(Naive_Bayes_test_results,Naive_Bayes_test_category)
    F1_Naive_Bayes = calcF1(Naive_Bayes_test_results,Naive_Bayes_test_category)

    plot_accuracy_percentage_Naive_Bayes_train.append(Accuracy_Percentage_Naive_Bayes_train)
    plot_accuracy_percentage_Naive_Bayes_test.append(Accuracy_Percentage_Naive_Bayes_test)
    plot_precision_percentage_Naive_Bayes.append(Precision_Percentage_Naive_Bayes)
    plot_recall_percentage_Naive_Bayes.append(Recall_Percentage_Naive_Bayes)
    plot_F1_Naive_Bayes.append(F1_Naive_Bayes)
    plot_set_size_Naive_Bayes.append(i)

    
plot1 = plt.figure(1)
plt.plot(plot_set_size_Naive_Bayes,plot_accuracy_percentage_Naive_Bayes_train,color ="r")
plt.ylim([0,1])
plt.xlim([0,25000])
plot2 = plt.figure(2)
plt.plot(plot_set_size_Naive_Bayes,plot_accuracy_percentage_Naive_Bayes_test,color="green")
plt.ylim([0,1])
plt.xlim([0,25000])
plot3 = plt.figure(3)
plt.plot(plot_set_size_Naive_Bayes,plot_precision_percentage_Naive_Bayes,color ="purple")
plt.ylim([0,1])
plt.xlim([0,25000])
plot4 = plt.figure(4)
plt.plot(plot_set_size_Naive_Bayes,plot_recall_percentage_Naive_Bayes,color ="orange")
plt.ylim([0,1])
plt.xlim([0,25000])
plt5 = plt.figure(5)
plt.plot(plot_set_size_Naive_Bayes,plot_F1_Naive_Bayes,color ="yellow")
plt.ylim([0,1])
plt.xlim([0,25000])
plt.show()

#ID3
plot_accuracy_percentage_ID3_train = []
plot_accuracy_percentage_ID3_test = []
plot_precision_percentage_ID3 = []
plot_recall_percentage_ID3 = []
plot_F1_ID3 = []
plot_set_size_ID3 = [] 

for i in range(1000,len(dataset[1]),1000):

    data = [[0] * i,[0] * i,[0] * i,[0] * i,[0] * i,[0] * i]

    for j in range(i):
        data[1][j] = dataset[1][j]
        data[2][j] = dataset[2][j]
        data[4][j] = dataset[4][j]
        data[5][j] = dataset[5][j]
    
    ID3_train_dataset = []
    ID3_test_dataset = []
    for j in range(i):
        ID3_train_dataset.append([data[1][j],data[2][j]])
        ID3_test_dataset.append([data[4][j],data[5][j]])

    ID3_item = ID3.ID3()
    not_used_data = [x for x in range(len(ID3_train_dataset[0][0]))]
    maxIG,max_Pos = ID3_item.getMaxIG(ID3_train_dataset,not_used_data)
    ID3_tree = ID3_item.buildTree(ID3_train_dataset,not_used_data,max_Pos)

    ID3_train_results = ID3.ID3.test(ID3_tree,ID3_train_dataset)
    ID3_test_results = ID3.ID3.test(ID3_tree,ID3_test_dataset)

    ID3_train_category = []
    ID3_test_category = []
    for i in range(len(ID3_train_dataset)):
        ID3_train_category.append(ID3_train_dataset[i][1])
        ID3_test_category.append(ID3_test_dataset[i][1])

    Accuracy_Percentage_ID3_train = calcAccuracy(ID3_train_results,ID3_train_category)
    Accuracy_Percentage_ID3_test = calcAccuracy(ID3_test_results,ID3_test_category)
    Precision_Percentage_ID3 = calcPrecision(ID3_test_results,ID3_test_category)
    Recall_Percentage_ID3 = calcRecall(ID3_test_results,ID3_test_category)
    F1_Id3 = calcF1(ID3_test_results,ID3_test_category)

    plot_accuracy_percentage_ID3_train.append(Accuracy_Percentage_ID3_train)
    plot_accuracy_percentage_ID3_test.append(Accuracy_Percentage_ID3_test)
    plot_precision_percentage_ID3.append(Precision_Percentage_ID3)
    plot_recall_percentage_ID3.append(Recall_Percentage_ID3)
    plot_F1_ID3.append(F1_Id3)
    plot_set_size_ID3.append(i)

    
plot1 = plt.figure(1)
plt.plot(plot_set_size_ID3,plot_accuracy_percentage_ID3_train,color ="r")
plt.ylim([0,1])
plt.xlim([0,25000])
plot2 = plt.figure(2)
plt.plot(plot_set_size_ID3,plot_accuracy_percentage_ID3_test,color="green")
plt.ylim([0,1])
plt.xlim([0,25000])
plot3 = plt.figure(3)
plt.plot(plot_set_size_ID3,plot_precision_percentage_ID3,color ="purple")
plt.ylim([0,1])
plt.xlim([0,25000])
plot4 = plt.figure(4)
plt.plot(plot_set_size_ID3,plot_recall_percentage_ID3,color ="orange")
plt.ylim([0,1])
plt.xlim([0,25000])
plt5 = plt.figure(5)
plt.plot(plot_set_size_ID3,plot_F1_ID3,color ="yellow")
plt.ylim([0,1])
plt.xlim([0,25000])
plt.show()
