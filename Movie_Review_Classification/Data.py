import tensorflow as tf
import numpy as np

def getData(m,n):
    (x_train, y_train), (x_test, y_test) = tf.keras.datasets.imdb.load_data(num_words=m,skip_top = n)
    #Create lists of train and test text
    word_index = tf.keras.datasets.imdb.get_word_index()
    index2word = dict((i + 3, word) for (word, i) in word_index.items())
    index2word[0] = '[pad]'
    index2word[1] = '[bos]'
    index2word[2] = '[oov]'
    x_train = np.array([' '.join([index2word[idx] for idx in text]) for text in x_train])
    x_test = np.array([' '.join([index2word[idx] for idx in text]) for text in x_test])

    #Create Vocabulary
    vocabulary = list()
    for text in x_train:
        tokens = text.split()
        vocabulary.extend(tokens)

    vocabulary = set(vocabulary)

    #Create train and test Vectors
    x_train_binary = list()
    x_test_binary = list()

    for text in x_train:
        tokens = text.split()
        binary_vector = list()
        for vocab_token in vocabulary:
            if vocab_token in tokens:
                binary_vector.append(1)
            else:
                binary_vector.append(0)
        x_train_binary.append(binary_vector)

    x_train_binary = np.array(x_train_binary)

    for text in x_test:
        tokens = text.split()
        binary_vector = list()
        for vocab_token in vocabulary:
            if vocab_token in tokens:
                binary_vector.append(1)
            else:
                binary_vector.append(0)
        x_test_binary.append(binary_vector)

    x_test_binary = np.array(x_test_binary)

    return_data = list()

    return_data.append(x_train)
    return_data.append(x_train_binary)
    return_data.append(y_train)
    return_data.append(x_test)
    return_data.append(x_test_binary)
    return_data.append(y_test)
    return_data.append(vocabulary)

    return return_data





    
