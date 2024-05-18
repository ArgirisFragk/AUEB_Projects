import numpy as np

def Naive_Bayes(train_vectors,train_category):
    
    #Caclulate P(C = 1) and P(C = 0)
    positive_critique = 0
    for i in train_category:
        positive_critique = positive_critique + i
    
    Prior_Positive_Prob = positive_critique / len(train_category)
   

    #C = 0 or 1 how many x_i exist
    X_attribute_Exists = np.zeros((2,len(train_vectors[0])),int)
    
    for i in range(0,len(train_category)):#25000
        for j in range(0,len(train_vectors[0])):#oses einai 
            if(train_vectors[i][j] == 1):
                if(train_category[i] == 0):
                    X_attribute_Exists[0][j] = X_attribute_Exists[0][j] + 1
                else:
                     X_attribute_Exists[1][j] =  X_attribute_Exists[1][j] + 1

    sum_of_negative_attributes = 0
    sum_of_positive_attributes = 0

    for j in range(len(train_vectors[0])):#oses einai
        sum_of_negative_attributes = sum_of_negative_attributes + X_attribute_Exists[0][j]
        sum_of_positive_attributes = sum_of_positive_attributes + X_attribute_Exists[1][j]

    Attribute_Possibilities = np.empty((2,len(train_vectors[0])),float)

    for i in range(2):
        for j in range (len(train_vectors[0])):
            if (i == 0):#Attribute Exist while in Negative Critique
                Attribute_Possibilities[i][j] = float((X_attribute_Exists[i][j] + 1)/(2 + sum_of_negative_attributes))
            elif(i == 1):#Attribute Exist while in Positive Critique
                Attribute_Possibilities[i][j] = float((X_attribute_Exists[i][j] + 1)/(2 + sum_of_positive_attributes))
    
    return Prior_Positive_Prob,Attribute_Possibilities

def test(test_vectors,Attribute_Possibilities,Prior_Positive_Prob):

    Our_Predictions = list()
    
    for i in range(len(test_vectors)):
        positive = 1
        negative = 1
        for j in range(len(test_vectors[0])):
            '''
            Attribute_Possibilities[x][i] is referencing the i-th word in the vector and contains the four possible
            situations in which it can be in(see comments above) so here each attribute in the test vector corresponds
            to each attribute in Attribute_Possibilities
            '''
            positive = positive  * (Attribute_Possibilities[1][j]**test_vectors[i][j]) * (1-Attribute_Possibilities[1][j])**(1-test_vectors[i][j])
            negative = negative  * (Attribute_Possibilities[0][j]**test_vectors[i][j]) * (1-Attribute_Possibilities[0][j])**(1-test_vectors[i][j])
        
        positive = positive * Prior_Positive_Prob
        negative = negative * (1 - Prior_Positive_Prob)
        
        if(positive > negative):
            Our_Predictions.append(1)
        else:
            Our_Predictions.append(0)

    return Our_Predictions

