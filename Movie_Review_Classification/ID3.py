import math
import copy
import numpy

class ID3:
    
    class TreeNode:

        def __init__(self,children,value,category,position):
            self.children = children
            self.value = value
            self.category = category
            self.position = position


    def calculateEntropy(self,data):
        
        positive_reviews = 0
        for i in range(len(data)):
            if(data[i][1] == 1):
                positive_reviews += 1
        
        negative_reviews = len(data) - positive_reviews

        if(positive_reviews == 0 or negative_reviews == 0):
            return 0
        
        return ( - (positive_reviews/len(data) * math.log2(positive_reviews/len(data)) ) - (negative_reviews/len(data) * math.log2(negative_reviews/len(data)) ) )


    def IGs(self,data,not_used):

        entropy = self.calculateEntropy(data)
        #print(data)
        exist_in_negative = [0] * len(data[0][0])
        exist_in_positive = [0] * len(data[0][0])
        not_exist_in_positive = [0] * len(data[0][0])
        not_exist_in_negative = [0] * len(data[0][0])
        for i in range(len(data)):
            for j in range(len(data[0][0])):
                if(data[i][0][j] == 1):#Exist

                    if(data[i][1] == 0):#Exist in Negative Review
                        exist_in_negative[j] += 1
                    elif(data[i][1] == 1):#Exist in Positive Review
                        exist_in_positive[j] += 1
                elif(data[i][0][j] == 0):#Does not Exist

                    if(data[i][1] == 0):#Does not Exist in Negative Review
                        not_exist_in_negative[j] += 1
                    elif(data[i][1] == 1):#Does not Exist in Positive Review
                        not_exist_in_positive[j] += 1
        
        IGs = list()

        for i in range(len(data[0][0])):
            if(i  in not_used):
                exist_all = exist_in_positive[i] + exist_in_negative[i]
                not_exist_all = not_exist_in_positive[i] + not_exist_in_negative[i]

                if(exist_in_positive[i] != 0 and exist_in_negative[i] != 0):# check if word sums (where a certain word exists) are not 0 so we can calculate below
                    exist = ( - ((exist_in_positive[i]/exist_all) * math.log2(exist_in_positive[i]/exist_all)) - ((exist_in_negative[i]/exist_all) * math.log2(exist_in_negative[i]/exist_all)) ) 
                else:
                    exist = 0
                if(not_exist_in_positive[i] != 0 and not_exist_in_negative[i] != 0):# check if word sums (where a certain word does not exist) are not 0 so we can calculate
                    not_exist = ( - ((not_exist_in_positive[i]/not_exist_all) * math.log2(not_exist_in_positive[i]/not_exist_all)) - ((not_exist_in_negative[i]/not_exist_all) * math.log2(not_exist_in_negative[i]/not_exist_all)) ) 
                else:
                    not_exist = 0

                exist_I = (exist_all/(exist_all + not_exist_all)) * exist
                not_exist_I = ((not_exist_all)/(exist_all + not_exist_all)) * not_exist

                ig = abs(entropy - (exist_I + not_exist_I))
                IGs.append(ig)
            elif(i not in not_used):
                IGs.append(-1)
        
        return IGs


    def getSubSet(self,data,num,value):
            
        subset = list()
        for i in range(len(data)):
            if(data[i][0][num] == value):
                subset.append(data[i])
                    
        return subset
    
    def getMaxIG(self,data,not_used):
        IGs = self.IGs(data,not_used)

        maxIG = IGs[0]
        Pos_IG = 0

        for i in range(1, len(IGs)):
            if(IGs[i] > maxIG):
                maxIG = IGs[i]
                Pos_IG = i

        return maxIG,Pos_IG

    def CheckCategory(examples):
        sameCategory = True
        category = examples[0][1]
        for i in range(1,len(examples)):
            if(examples[i][1] != category):
                sameCategory = False
        return [sameCategory,category]
    
    def MostCommonCategory(examples):
        positive_examples = 0
        for i in range(len(examples)):
            if(examples[i][1] == 1):
                positive_examples += 1
        negative_examples = len(examples) - positive_examples

        if(positive_examples > negative_examples):
            return 1
        else:
            return 0

    def CheckUsedData(not_used_data):
        for i in range(len(not_used_data)):
            if(not_used_data[i] != -1):
                return False
        return True

    def buildTree(self,examples,not_used_data,Selected_Attribute):
        if (len(examples) == 0):
            return Selected_Attribute
        elif(ID3.CheckCategory(examples)[0]):
            return ID3.CheckCategory(examples)[1]
        elif(ID3.CheckUsedData(not_used_data)):
            return ID3.MostCommonCategory(examples)
        else:
            maxIG,max_Pos = self.getMaxIG(examples,not_used_data)
            not_used_data[max_Pos] = -1
            root = self.TreeNode([None,None],maxIG,ID3.MostCommonCategory(examples),max_Pos)
            for i in range(2):
                subset = self.getSubSet(examples,max_Pos,i)
                root.children[i] = self.buildTree(subset,not_used_data,ID3.MostCommonCategory(subset))
            return root


    def test(root,test_set):
        Our_results = list()


        for i in range(len(test_set)):
            temp = copy.deepcopy(root)
            while(type(temp) != int and type(temp) != numpy.int64):
                if(test_set[i][0][temp.position] == 1):
                    temp = temp.children[1]
                else:
                    temp = temp.children[0]
            Our_results.append(temp)
        
        return Our_results





