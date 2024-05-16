def player_register():
    players = []
    print('type "END" to stop the process')
    x = str(input('input player'))
    z = 0
    while x != 'END' and z <= 6:
        players.append(x)
        if z != 6:
            x = str(input('add next player'))
        z += 1

    players.sort()
    return players

#---------------------------------------------------------------------
def build_deck():#dimiourgei to deck
    """
    >>> x = build_deck()
    >>> len(x)
    52
    
    """
    deck = []
    special = ['J','Q','K','A']
    i = 0
    j = 2
    while i <= 3:
        if i != 3:
            deck.append((special[i],'hearts',10))
            deck.append((special[i],'spades',10))
            deck.append((special[i],'diamonds',10))
            deck.append((special[i],'clubs',10))
        else:
            deck.append((special[i],'hearts',11))
            deck.append((special[i],'spades',11))
            deck.append((special[i],'diamonds',11))
            deck.append((special[i],'clubs',11))
        while j <= 10:
            deck.append((str(j),'hearts',j))
            deck.append((str(j),'spades',j))
            deck.append((str(j),'diamonds',j))
            deck.append((str(j),'clubs',j))
            j = j + 1
        i = i + 1

    return deck
#---------------------------------------------------------------------
def matches(card1,card2):
    """
    >>> card1 = ('10','diamonds',10)
    >>> card2 = ('9','diamonds',10)
    >>> matches(card1,card2)
    True
    """
    if card1[0] == card2[0]:
        return True
    elif card1[1] == card2[1]:
        return True
    elif card1[0] == 'A':
        return True
    else:
        return False
        
#---------------------------------------------------------------------
def dealing(pnames,deck):#moirazei kartes stous paiktes kai anakateuei to deck
    import random
    closed_deck = deck
    hands = []
    for i in range(len(pnames)):
        cards = 0
        player_hand = []
        while cards<7:
            r=random.randint(1,len(deck))
            player_hand.append(deck[r-1])
            deck.pop(r-1)
            cards += 1
        hands.append(player_hand)
    shuffle_deck(deck)
    hands.append(deck)
    return hands
#---------------------------------------------------------------------
def create_points(pnames):#dimiourgei lista h  opoia krataei tous pontous tou kathe paixth
    p = []
    i = 0
    while i < len(pnames):
        p.append(0)
        i += 1
    return p 
#---------------------------------------------------------------------
def shuffle_deck(deck):#anakateuei to deck
    import random
    random.shuffle(deck)
    return deck

#---------------------------------------------------------------------
def check_points(points):#elegxei an oi pontoi kapoiou paikth einai megaluteroi tou 50
    i = True
    for k in points:
        if k > 50:
            i = False
    return i 
#---------------------------------------------------------------------
def check(player_cards):#elegxei an einai adeia kapoia lista apo thn lista me ta xeria twn paiktwn
    shot=True
    for i in range(len(player_cards)):
        if player_cards[i]==[]:
            shot=False
    return shot

#---------------------------------------------------------------------
def main_phase(i,seira,open_deck,closed_deck,plcard,player_cards,player_names):#ylopoiei to paixnidi gia kathe seira
    i += 1
    if open_deck[-1][0] == '9':
        print('Next player loses this turn')
        if len(player_names) == 2:
            if len(player_cards[i-1]) == 0:
                player_cards[i-1].append(closed_deck.pop(-1))
            i -= 1
        else:
            i += 1
    elif open_deck[-1][0] == 'A':
        print('The old sign is:',seira)
        seira = str(input('type a new sign in small letters'))
        print('The new sign is:',seira)
    elif open_deck[-1][0] == '8':
        print('This player plays again')
        if len(player_cards[i-1]) == 0:
            player_cards[i-1].append(closed_deck.pop(-1))
        i -= 1
    elif open_deck[-1][0] == '7':
        print('Next player takes 2 cards')
        if i==len(player_names):
            player_cards[0].append(closed_deck.pop(-1))
            player_cards[0].append(closed_deck.pop(-1))
        else:
            player_cards[i].append(closed_deck.pop(-1))
            player_cards[i].append(closed_deck.pop(-1))
    return i,seira,open_deck,closed_deck,plcard,player_cards,player_names

    
