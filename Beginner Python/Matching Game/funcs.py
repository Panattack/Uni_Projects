
#OPEN CARD
l2=("┌─────────┐", 
    "│{}{}      │",
    "│         │",
    "│         │",
    "│    {}    │",
    "│         │",
    "│         │",
    "│      {}{}│",
    "└─────────┘")

#HIDDEN CARD
l0 =("┌─────────┐", 
     "│▓▓▓▓▓▓▓▓▓│",
     "│▓▓▓▓▓▓▓▓▓│",
     "│▓▓▓▓▓▓▓▓▓│",
     "│▓▓▓▓▓▓▓▓▓│",
     "│▓▓▓▓▓▓▓▓▓│",
     "│▓▓▓▓▓▓▓▓▓│",
     "│▓▓▓▓▓▓▓▓▓│",
     "└─────────┘")

"""SHMEIVSEIS
All the characters after the \r escape sequence move to the left and overwrite exactly those number of characters present at the starting of the statement.

ALT-CODES
Symbol	AltCode	
┐	191	
└	192	
─	196
┌	218
│	179
┘	217
▒	177
▓	178

The join() method takes all items in an iterable and joins them into one string.
A string must be specified as the separator.


TELOS SHMEIVSEIS"""

#STOIXEIA
symbols = ['A', 2, 3, 4, 5, 6, 7, 8, 9, 10, 'J', 'Q', 'K']
CLUB = "\u2663"#spathi
HEART = "\u2665"#koupa
DIAMOND = "\u2666"#karo
SPADE = "\u2660"#mpastouni
line = [CLUB, HEART, DIAMOND, SPADE]
card_value = { 
    'A':1, '2':2, '3':3, '4':4, '5':5, '6':6, '7':7, '8':8, '9':9, '10':10, 'J':10, 'Q':10, 'K':10 
}


club_strings = [ 'A'+line[0], '2'+line[0], '3'+line[0], '4'+line[0], '5'+line[0], '6'+line[0], \
    '7'+line[0], '8'+line[0], '9'+line[0], '10'+line[0], 'J'+line[0], 'Q'+line[0], 'K'+line[0] ]
    
heart_strings = [ 'A'+line[1], '2'+line[1], '3'+line[1], '4'+line[1], '5'+line[1], '6'+line[1], \
    '7'+line[1], '8'+line[1], '9'+line[1], '10'+line[1], 'J'+line[1], 'Q'+line[1], 'K'+line[1] ]

diamond_strings = [ 'A'+line[2], '2'+line[2], '3'+line[2], '4'+line[2], '5'+line[2], '6'+line[2], \
    '7'+line[2], '8'+line[2], '9'+line[2], '10'+line[2], 'J'+line[2], 'Q'+line[2], 'K'+line[2] ]

spade_strings = [ 'A'+line[3], '2'+line[3], '3'+line[3], '4'+line[3], '5'+line[3], '6'+line[3], \
    '7'+line[3], '8'+line[3], '9'+line[3], '10'+line[3], 'J'+line[3], 'Q'+line[3], 'K'+line[3] ]

card_string = [club_strings, heart_strings, diamond_strings, spade_strings]

status = {
    "a":"open", "b":"closed"
    }
list_11 = []
"""
cards = [symbols, line, card_value, card_string, status["b"]]
"""
#TELOS_STOIXEIA

#SYNARTHSEIS

def secure_difficulty():
    choice = int(input("Επίλεξε τον βαθμό δυσκολίας (1:Εύκολο, 2:Μέτριο, 3:Δύσκολο)  "))
    if choice>=1 and choice<=3 and isinstance(choice, int):
        return choice
    else:
        print('Ξαναπροσπάθησε')
        return secure_difficulty()

def func_1(cho):
    if cho == 1:
        card_list = card_string[0][9:] + card_string[1][9:] + card_string[2][9:] + card_string[3][9:] 
    elif cho == 2:
        card_list = card_string[0][3:] + card_string[1][3:] + card_string[2][3:] + card_string[3][3:] 
    else:
        card_list = card_string[0] + card_string[1] + card_string[2] + card_string[3] 
    return card_list

def column(cho):
    if cho == 1:
        col = 4
    elif cho == 2:
        col = 10
    else:
        col = 13
    return col

def shuffle(list, col):
    """
    Shuffles the elements of a list and makes them a list(ex.    [[1, 2, 3, 4], [5, 6, 7, 8]] )
    """
    import random
    random.shuffle(list)
    new_list = list
    return new_list


def hidden_card():
    l0 =("┌─────────┐", 
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "└─────────┘")

    ls = [[x] for x in l0]
    return ls


def print_hidden_card(col):
    l0 =("┌─────────┐", 
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "└─────────┘")
    
    count = 1
    stt = '{} '*col
    result = []
    hidden_card = [[x] for x in l0]
    while count <= 4: 
        if col == 4:
            for xxx in range(len(hidden_card)):
                result += ['\n'.join(stt.format(x, y, z, k) for x, y, z, k in zip(hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx]))]
        elif col == 10:
            for xxx in range(len(hidden_card)):
                result += ['\n'.join(stt.format(x, y, z, k, a, b, c, d, e, f) for x, y, z, k, a, b, c, d, e, f  in zip(hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx]))]
        else:
            for xxx in range(len(hidden_card)):
                result += ['\n'.join(stt.format(x, y, z, k, a, b, c, d, e, f, n, m, sk) for x, y, z, k, a, b, c, d, e, f, n, m, sk  in zip(hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx], hidden_card[xxx]))]
        count += 1
    return result
    
def open_card(kk, ls): # epistrefei thn lista card

    
    #card
    l2=("┌─────────┐", 
        "│{}{}      │",
        "│         │",
        "│         │",
        "│    {}    │",
        "│         │",
        "│         │",
        "│      {}{}│",
        "└─────────┘")

    card = [[x] for x in l2]
    n = ls[kk]
    if len(n) == 3:
        card[1][0] = card[1][0].format(n, '')
    else:
        card[1][0] = card[1][0].format(n, ' ')
    for k in line:
        if k in n:
            card[4][0] = card[4][0].format(k)
    if len(n) == 3:
        card[7][0] = card[7][0].format('', n)
    else:
        card[7][0] = card[7][0].format(' ', n)   
    return card


def open_card1(gr_choice, col_choice, ls): # epistrefei thn lista card

    
    #card
    l2=("┌─────────┐", 
        "│{}{}      │",
        "│         │",
        "│         │",
        "│    {}    │",
        "│         │",
        "│         │",
        "│      {}{}│",
        "└─────────┘")

    card = [[x] for x in l2]
    n = ls[gr_choice-1][col_choice-1]
    if len(n) == 3:
        card[1][0] = card[1][0].format(n, '')
    else:
        card[1][0] = card[1][0].format(n, ' ')
    for k in line:
        if k in n:
            card[4][0] = card[4][0].format(k)
    if len(n) == 3:
        card[7][0] = card[7][0].format('', n)
    else:
        card[7][0] = card[7][0].format(' ', n)   
    return card



def print_open_cards(col, ls):
    result = []
    st = '{} '*col
    count = 1
    i = 0
    while count <= 4:
        if col == 4:
            card_1 = open_card(i, ls)
            card_2 = open_card(i+1, ls)
            card_3 = open_card(i+2, ls)
            card_4 = open_card(i+3, ls)
            for xx in range(len(card_1)):# einai len(card_1) == 9
                result += ['\n'.join(st.format(x, y, z, k) for x, y, z, k in zip(card_1[xx], card_2[xx], card_3[xx], card_4[xx]))]
            i += 4
        elif col == 10:
            card_1 = open_card(i, ls)
            card_2 = open_card(i+1, ls)
            card_3 = open_card(i+2, ls)
            card_4 = open_card(i+3, ls)
            card_5 = open_card(i+4, ls)
            card_6 = open_card(i+5, ls)
            card_7 = open_card(i+6, ls)
            card_8 = open_card(i+7, ls)
            card_9 = open_card(i+8, ls)
            card_10 = open_card(i+9, ls)
            for xx in range(len(card_1)):
                result += ['\n'.join(st.format(x, y, z, k, a, b, c, d, e, f) for x, y, z, k, a, b, c, d, e, f  in zip(card_1[xx], card_2[xx], card_3[xx], card_4[xx], card_5[xx], card_6[xx], card_7[xx], card_8[xx], card_9[xx], card_10[xx]))]
            i += 10
        else:
            card_1 = open_card(i, ls)
            card_2 = open_card(i+1, ls)
            card_3 = open_card(i+2, ls)
            card_4 = open_card(i+3, ls)
            card_5 = open_card(i+4, ls)
            card_6 = open_card(i+5, ls)
            card_7 = open_card(i+6, ls)
            card_8 = open_card(i+7, ls)
            card_9 = open_card(i+8, ls)
            card_10 = open_card(i+9, ls)
            card_11 = open_card(i+10, ls)
            card_12 = open_card(i+11, ls)
            card_13 = open_card(i+12, ls)
            for xx in range(len(card_1)):
                result += ['\n'.join(st.format(x, y, z, k, a, b, c, d, e, f, n, m, sk) for x, y, z, k, a, b, c, d, e, f, n, m, sk  in zip(card_1[xx], card_2[xx], card_3[xx], card_4[xx], card_5[xx], card_6[xx], card_7[xx], card_8[xx], card_9[xx], card_10[xx], card_11[xx], card_12[xx], card_13[xx]))]
            i += 13
        count += 1
    return result


def func00(ls):
    coun = 0
    for ii in range(4):
        for nn in range(9):
            print(ls[nn + coun])
        coun += 9







def metatropi(ls_h, gr_choice, col_choice, ls_o, flag = True):
    """
    - to flag to pairnei apo to equal, kai an einai True shmainei eite oti to emganizoume gia ton paikth,eite einai ises oi kartes
    """
    if flag:
        card_temp = open_card1(gr_choice, col_choice, ls_o) #new_cards_list OXI open_ls
    else:
        card_temp = hidden_card()
    return card_temp


def allagh(lista, card_temp, gr_choice, col_choice, col, ff = True):
    l0 =("┌─────────┐", 
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "│▓▓▓▓▓▓▓▓▓│",
         "└─────────┘")


    count = 1
    stt = '{} '*col
    result = []
    ls = []
    hidden_card = [[x] for x in l0]
    while count <= 4:
        
        if ff:
            c = [hidden_card] * col
        else:
            c = lista[:col]
            
            
        if col == 4:
            if gr_choice == count:
                c[col_choice-1] = card_temp
                ls += c
            else:
                ls += c
            
            for xxx in range(9):
                result += ['\n'.join(stt.format(x, y, z, k) for x, y, z, k in zip(c[0][xxx], c[1][xxx], c[2][xxx], c[3][xxx]))]
            
        elif col == 10:#NA TA KOITAJV
            
            if gr_choice == count:
                c[col_choice-1] = card_temp
                ls += c

            else:
                ls += c
            
            
            for xxx in range(9):
                result += ['\n'.join(stt.format(x, y, z, k, a, b, c, d, e, f) for x, y, z, k, a, b, c, d, e, f  in zip(c[0][xxx], c[1][xxx], c[2][xxx], c[3][xxx], c[4][xxx], c[5][xxx], c[6][xxx], c[7][xxx], c[8][xxx], c[9][xxx]))]
        else:
            if gr_choice == count:
                c[col_choice-1] = card_temp
                ls += c
            else:
                ls += c
            for xxx in range(9):
                result += ['\n'.join(stt.format(x, y, z, k, a, b, c, d, e, f, n, m, sk) for x, y, z, k, a, b, c, d, e, f, n, m, sk  in zip(c[0][xxx], c[1][xxx], c[2][xxx], c[3][xxx], c[4][xxx], c[5][xxx], c[6][xxx], c[7][xxx], c[8][xxx], c[9][xxx], c[10][xxx], c[11][xxx], c[12][xxx]))]
        count += 1
        lista = lista[col:]
    return result, ls


def cards_gr1(ls):
    new_ls = []
    i = 0
    for k in range(4):
        new_ls += [[x for x in ls[:4]]]
        i += 1
        ls = ls[4:]
    return new_ls

def cards_gr2(ls):
    new_ls = []
    i = 0
    for k in range(4):
        new_ls += [[x for x in ls[:10]]]
        i += 1
        ls = ls[10:]
    return new_ls

def cards_gr3(ls,):
    new_ls = []
    i = 0
    for k in range(4):
        new_ls += [[x for x in ls[:13]]]
        i += 1
        ls = ls[13:]
    return new_ls




"""
The zip() function returns a zip object, which is an iterator of tuples where the first item in each passed iterator is paired together, and then the second item in each passed iterator are paired together etc.
"""

def katastasi(col):

    """
    ex. [[closed, closed, closed, closed], [.....], [.....], [.....]]
    """

    kat = [['closed' for x in range(col)],['closed' for x in range(col)],['closed' for x in range(col)],['closed' for x in range(col)]]
    return kat


def open_card0(kat, gr, col):
    #βαζει μια καρτα ανοικτη
    kat[gr-1][col-1] = 'open'
    return kat


def close_card0(kat, gr, col):
    #βαζει μια καρτα κλειστη
    kat[gr-1][col-1] = 'closed'
    return kat


def okay_col(col,  choice):
    """
    
    """
    if choice==1:
        if col>4 or col<1:
            return False
        else:return True
    elif choice==2:
        if col>10  or col<1 :
            return False
        else:return True
    else:
        if col>13 or col<1 :
            return False
        else:return True


def nice_gr(x, i, f=True):
    
    """
    Ζητα καρτες απο τον παικτη γραμμη
    """
    
    while f:
        gr = int(input(f'{x}:Δώσε γραμμή {i}ης κάρτας (πχ. 1,2,3,4):'))
        if gr<=4 and gr>=1:
            return gr
        else:
            print('Επέλεξε σωστή γραμμή')
            continue


def nice_col(x, i, col, choice, f=True):
    
    """
    Ζητα απο τον παικτη την στηλη.
    """
    
    while f:
        col_choice=int(input(f'{x}:Δώσε στήλη {i}ης κάρτας (πχ. {range(1,col)}):'))
        if okay_col(col, choice):
            return col_choice
        else:
            print('Επέλεξε σωστή στήλη')
            continue


def secure_closed(kat, gr, col): #True --> Closed, False --> Open
    x = kat[gr-1][col-1]
    if x == 'open':
        return False
    else:
        return True


def alphabetical_players():
    #βαζει τους παικτες σε αλφαβητικη σειρα
    f = True
    while f == True:
        metritis = int(input('Δώστε αριθμο παικτων:  '  ))
        secure_player = lambda player:isinstance(player, int)
        if secure_player:
            break
        else:
            continue
    players=[]
    for i in range(metritis):
        player=(input(f'Δώσε όνομα {i+1}ου παίκτη:  '  ))
        players.append(player)
    players.sort()
    print('Η σειρά των παικτών είναι:'  )
    for k in players:
        print(f'{k}''\n')
    return players


def this_is_not_the_end(kat):
    for x in kat:
        for i in x:
            if i == 'closed':
                return True
    return False


def equal(card1, card2):#pairnei duo sting kartwn και ελεγχει εαν ειναι ιδια ως προς το συμβολο
    l1 = len(card1)
    l2 = len(card2)
    if l1 == l2  and isinstance(card1[0], int) == isinstance(card2[0], int) and card1[0] == card2[0]:
        return True #ayth einai otan exv monochfio kai integer kai to 10
    elif l1==l2 and  isinstance(card1[0], str)==isinstance(card2[0], str)and card1[0]==card2[0]:
        return True #ayth einai otan exv string dld K,Q,J
    else:
        return False


#eidikes kartes sto paixnidi
'''
πρωτη περιπτωση οι βαλεδες
'''
def baledes(card1, card2):
    if 'J'in card1 and 'J'in card2:return True 
    else:return False
'''
deuterh kathgoria
'''
def rigades(card1, card2):
    if 'K'in card1 and 'K'in card2:return True
    else:return False


def points(points_lists, seira,card1):#prosthetei pontous ston katallhlonpaikth
    point = anagnwrisi_pontwn(card1)
    points_lists[seira] = points_lists[seira]+point
    return points_lists


def anagnwrisi_pontwn(card1):

    if  len(card1) == 3:
        p = 10
    else:
        p = card_value[card1[0]]
    return p


def ntama_and_rigas(card1, card2):
    if 'K'in card1 and 'Q'in card2:return True
    elif 'Q'in  card1 and 'K'in card2:return True
    else:return False   

def endgame(players, points_lists):
    m=max(points_lists)
    winners = []
    n=0
    for j in points_lists:
        if m == j:
            winners.append(players[n])
        n+=1
    if len(winners)>1:
        print('Οι παικτες ήρθαν ισόπαλοι')
        for i in winners:print(i, end='')
    else:
        print(f'Ο παικτης {winners[0]} νικησε με {m} πόντους !!!')
    for i in players:
        if i not in winners:
            print(f'Ο παικτης {i} πήρε {points_lists[players.index(i)]}')