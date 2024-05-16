from functions import *

player_names = player_register()#zitaei ta onomata wn paiktwn
deck = build_deck()#h sinartish dimiourgei to deck
player_cards = dealing(player_names,deck)#oi kartes moirazontai stous paixtes,diladi se lista meta xeria twn paiktwn
closed_deck = player_cards.pop(len(player_names))#dimiourgeitai h kleisth trapoula
open_deck = [closed_deck.pop(-1)]#dimiourgeitai h anoixth trapoula

points = create_points(player_names)
seira = open_deck[0][1]

play_again=False
while check_points(points) == True or play_again:
    if check(player_cards)==False:
        for z in range(len(player_names)):
            for k in range(len(player_cards[z])):
                points[z] += player_cards[z][k][2]
        print('SCORE')
        for z in range(len(points)):
            print(player_names[z],':',points[z])
        deck = build_deck()
        player_cards = dealing(player_names,deck)
        closed_deck = player_cards.pop(len(player_names))
        open_deck = [closed_deck.pop(-1)]
        if check_points(points):
            print('NEW GAME')
    else:
        i = 0
        while i < len(player_names) and check(player_cards):
            if check(player_cards):#epistrefei true an den einai adeia h lista player_cards
                print(open_deck[-1])
                print('---------------------------------------------------------------------------------------------------')
                print(player_names[i],':::',player_cards[i])
                plcard = input('play a card by typing its number or type "PASS" :>> ')
                if plcard=='PASS':
                    plcard=str(plcard)
                else:
                    plcard=int(plcard)    
                    if open_deck[-1][0] == 'A' and len(open_deck) != 1 and seira == player_cards[i][plcard-1][1]:
                        open_deck.append(player_cards[i].pop(plcard-1))
                        i,seira,open_deck,closed_deck,plcard,player_cards,player_names = main_phase(i,seira,open_deck,closed_deck,plcard,player_cards,player_names)    
                    elif matches(player_cards[i][plcard-1],open_deck[-1])==True:
                        open_deck.append(player_cards[i].pop(plcard-1))
                        i,seira,open_deck,closed_deck,plcard,player_cards,player_names = main_phase(i,seira,open_deck,closed_deck,plcard,player_cards,player_names)
                if plcard == 'PASS':
                    if plcard == 'PASS':
                        player_cards[i].append(closed_deck.pop(-1))
                        i += 1    
            
                if len(closed_deck) == 0:
                    x = open_deck.pop(-1)
                    closed_deck.append(open_deck)
                    open_deck.append(x)
    
    winner=player_names[0]
    winner_points=points[0]
    for player in range(1,len(player_names)):
        if points[player]<winner_points:
            winner=player_names[player]
            winner_points=points[player]
    new_name_list=[]
    count=0
    for player in range(len(player_names)):
        if points[player]==winner_points:
            play_again=True
            new_name_list.append(player_names[player])
            count+=1
    if count>1:
        player_names=new_name_list
    else:
        play_again=False

print('The winner is :',winner)