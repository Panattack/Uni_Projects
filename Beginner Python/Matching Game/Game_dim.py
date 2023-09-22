# kanv import tis synasthseis
from funcs import secure_difficulty, func_1, column, shuffle, print_open_cards, print_hidden_card, print_open_cards, func00, katastasi, hidden_card, nice_col, nice_gr, alphabetical_players, this_is_not_the_end, secure_closed, metatropi, allagh, open_card0, close_card0, equal, rigades, baledes, points, ntama_and_rigas, cards_gr1, cards_gr2, cards_gr3,endgame
import time

import os

def clrscr():
    # Check if Operating System is Mac and Linux or Windows
   if os.name == 'posix':
      _ = os.system('clear')
   else:
      # Else Operating System is Windows (os.name = nt)
      _ = os.system('cls')
    
   

#kaloshruate mpla mpla mpla
print("MATCHING GAME")
print("============================================================================")
time.sleep(1)
players = alphabetical_players()

print("ΚΑΝΟΝΕΣ ΠΑΙΧΝΙΔΙΟΥ")
print("-------------------")
time.sleep(1)
if len(players) == 1:
    print("ΣΚΟΠΟΣ ΤΟΥ ΠΑΙΧΝΙΔΙΟΥ: Βρείτε όλα τα ζευγάρια κάρτών που υπάρχουν στο ταμπλό! ")
else:
    print("ΣΚΟΠΟΣ ΤΟΥ ΠΑΙΧΝΙΔΙΟΥ: Μαζέψτε περισσότερους πόντους από τους αντιπάλους σας βρίσκοντας τα ζευγάρια καρτών στο ταμπλό!")
time.sleep(1.5)
print(" ")
print("ΕΙΔΙΚΕΣ ΠΕΡΙΠΤΩΣΕΙΣ")
time.sleep(1)

print("• Αν κάποιος παίκτης ανοίξει ταυτόχρονα δύο Βαλέδες (J), κερδίζει τους αντίστοιχους πόντους και παίζει ξανά.")
time.sleep(0.5)
print("• Αν κάποιος παίκτης ανοίξει ταυτόχρονα δύο Ρηγάδες (Κ), τότε κερδίζει τους αντίστοιχους πόντους και ο επόμενος παίκτης χάνει τη σειρά του.")
time.sleep(0.5)
print("• Αν κάποιος παίκτης ανοίξει μια Ντάμα και ένα Ρήγα, τότε θα έχει ευκαιρία να ανοίξει και μια τρίτη κάρτα. Οι κάρτες που ταιριάζουν μεταξύ των τριών μένουν ανοικτές και ο παίκτης κερδίζει τους αντίστουχους πόντους. Οι κάρτες που δεν ταιριάζουν κλείνουν.")
time.sleep(3)

print("============================================================================")
#epilejte dyskolia

answer = "ΝΑΙ"
while answer == "ΝΑΙ":
    cho = secure_difficulty()
    card_list = func_1(cho)
    col = column(cho)#choice
    new_card_list = shuffle(card_list, col)#random cards
    if cho == 1:
        grammes = cards_gr1(new_card_list)#choice me upolistes me 4 grammes-upolistes kai 4 sthles-stoixeia
    elif cho == 2:
        grammes = cards_gr2(new_card_list)#choice me upolistes me 4 grammes-upolistes kai 10 sthles-stoixeia
    else:
        grammes = cards_gr3(new_card_list)#choice me upolistes me 4 grammes-upolistes kai 13 sthles-stoixeia



    open_ls = print_open_cards(col, new_card_list)
    hidden_ls = print_hidden_card(col)
    points_lists = [0 for x in range(len(players))]

    
    print("ΚΑΛΗ ΔΙΑΣΚΕΔΑΣΗ!")
    
    time.sleep(0.7)

    func00(hidden_ls)
    kat = katastasi(col)

    time.sleep(0.7)

    lista = []
    space = " "
    k = 0
    cn = 1
    temp = hidden_ls
    ff = False#lista kenh
    open_f = False
    f1 = False
    times = 1
    while this_is_not_the_end(kat):
        
        plcards=[]#oi kartes pou dinei o paikths se motfh string
        plgrcol=[]#oi grammes pou dinei o xrhsths kai oi sthles ne thn prwth thesi na einai h grammi ths prwths kartas kai sthn deuetrh thesi h stili ths prvths kartas kai antixtoixa gia tis epomenes duo theseis h deuterh karta
        x = players[k]  #k = deikths paiktvn
        for i in range(1,3):
            gr = nice_gr(x, i)
            col_choice = nice_col(x, i, col, cho)
            while not(secure_closed(kat, gr, col_choice)):
                print('Αυτή η κάρτα είναι ήδη ανοιχτή!!!')
                print('Δώσε άλλη μια διαφορετική κάρτα')
                gr = nice_gr(x,i)
                col_choice = nice_col(x, i, col, cho)
            plgrcol.append(gr)
            plgrcol.append(col_choice) # lista px. [1, 2, 2, 4] opou 1grammh 2sthlh prvths kartas klp.
            
            plcards.append(grammes[gr-1][col_choice-1])
            
            card_temp = metatropi(hidden_ls, gr, col_choice, grammes)
            
            if i == 1:
                col_choice1 = col_choice
                gr1 = gr
            else:
                col_choice2 = col_choice
                gr2 = gr

            if f1 == False:# stigliotypo kartvn prin anoijv kai tiw duo kartes
                if cn % 2 == 1 and ff == False :#se petaei ejw otan einai oles kleistes
                    if cn == 1:#an epilejv thn prvth karta
                        temp = hidden_ls#oles oi kartes kleistes
                        ff = True # Mpainei 1h fora
                    elif open_f == False:
                        temp_ingamels = in_game_ls#exoume anoixtes kartes kai kathe fora
                        temp_lista = lista
                
                    
            if cn == 1 or lista == hidden_ls:
                in_game_ls, lista = allagh(hidden_ls, card_temp, gr, col_choice, col, True)
            else:
                in_game_ls, lista = allagh(lista, card_temp, gr, col_choice, col, False)

            cn += 1
            
            print(10**4*space)
            func00(in_game_ls)
            

            if i == 1:#bazw to prvto stoixeio sthn lista etsi wste otan eryhei to deutero na mpei sthn secure kai an exei to idio na bgalei sfalma
                open_card0(kat, gr, col_choice)



        if equal(plcards[0],plcards[1]):
            points_lists = points(points_lists, k, plcards[0])
            open_card0(kat, plgrcol[2],plgrcol[3]) #h katastasi ths deuterhs kartas einai anoikth tvra
            ff = False
            open_f = True
            temp_ingamels = in_game_ls
            temp_lista = lista
            


            if rigades(plcards[0], plcards[1]):
                k+=2
                if k==len(players):
                    k=0
                elif k>len(players):
                    k=1
            elif baledes(plcards[0], plcards[1]):
                pass
            else:
                k+=1
                if k == len(players):
                    k = 0
                elif k > len(players):
                    k = 1



        else:
            
            if ntama_and_rigas(plcards[0], plcards[1]):
                open_card0(kat, plgrcol[2],plgrcol[3])#anoije thn deuterh karta gia na mhn epilejei pali thn idia
                gr = nice_gr(x, 3)
                col_choice = nice_col(x, 3, col, cho)
                while not(secure_closed(kat, gr, col_choice)):
                    print('Η κάρτα είναι ήδη ανοιχτή!!!')
                    print('Δώσε άλλη μια διαφορετική κάρτα')
                    gr = nice_gr(x, 3)
                    col_choice = nice_col(x, 3, col, cho)
                plgrcol.append(gr)
                plgrcol.append(col_choice)
                #allagh listas gia 3h karta!!!!!!#metatrepei thn lista twn emfanizomenwn kartwn 
                card_temp = metatropi(hidden_ls, gr, col_choice, grammes)
                in_game_ls, lista = allagh(lista, card_temp, gr, col_choice, col, False)
            
                print(10**4*space)
                func00(in_game_ls)
                time.sleep(0.7)
                
                new_card = grammes[gr-1][col_choice-1]#ftiaxnei se string thn nea karta
                
                flag = False
                k += 1
                print("Ειδική Περίπτωση!")
                if k == len(players):
                    k = 0
                elif k > len(players):
                    k = 1
                time.sleep(1)
                if equal(new_card, plcards[0]):#h prvth me thn trith
                    
                    lista[((plgrcol[2]-1)*4)+plgrcol[3]-1] = hidden_card()
                    
                    close_card0(kat, plgrcol[2], plgrcol[3])#kleise thn deuterh karta
                    open_card0(kat, gr, col_choice)#anoije thn trith karta
                    points_lists = points(points_lists, k, plcards[0])  
                    in_game_ls, lista = allagh(lista, card_temp, gr, col_choice, col, False) 
                    ff = False
                    temp_ingamels = in_game_ls
                    print(10**4*space)
                    func00(temp_ingamels)
                    temp_lista = lista
                    
                    clrscr()

                elif equal(new_card, plcards[1]):#h deuterh me thn trith
                    
                    lista[(plgrcol[0]-1)*4+plgrcol[1]-1] = hidden_card()
                    
                    close_card0(kat, plgrcol[0], plgrcol[1])#diegrace thn prwth karta
                    open_card0(kat, gr, col_choice)#anoije thn trith karta
                    points_lists = points(points_lists, k, plcards[1])
                    in_game_ls, lista = allagh(lista, card_temp, gr, col_choice, col, False)
                    ff = False
                    temp_ingamels = in_game_ls
                    print(10**4*space)
                    func00(temp_ingamels)
                    temp_lista = lista
                    
                    

                else:
                    
                    close_card0(kat, plgrcol[0], plgrcol[1])#kleise thn prwth karta
                    close_card0(kat, plgrcol[2], plgrcol[3])#kleise thn deuterh karta
                    if ff == True:
                        print(10**4*space)
                        lista = temp
                        func00(temp)
                    else:
                        ff = False
                        print(10**4*space)
                        func00(temp_ingamels)
                        lista = temp_lista
                        f1 = True

                

            else:
                flag = False
                # kleinv kartes giati einai lauos + emfanizv tamplo ananeomeno
                print("Λάθος συνδιασμός")
                time.sleep(2.1)
                print(10**4*space)
                if ff == True:
                    func00(temp) #Typvnei kenh lista prvth fora
                    lista = hidden_ls
                else:
                    func00(temp_ingamels)
                    lista = temp_lista
                

                close_card0(kat, gr1, col_choice1)
                close_card0(kat, gr2, col_choice2)

                k+=1
                if k==len(players):
                    k=0
                elif k>len(players):
                    k=1
                
                

        times += 1

    print(space)
    print("Βρήκατε όλες τις κάρτες σε {} προσπάθειες!".format(times))
    endgame(players,points_lists)
    print(space)
    answer = input("Θέλετε να ξαναπαίξετε;(ΝΑΙ/ΟΧΙ)  ")
    
    while answer != "ΝΑΙ" and answer != "ΟΧΙ":
        print("Κάποιο λάθος έχει γίνει...")
        answer = input("Θέλετε να ξαναπαίξετε;(ΝΑΙ/ΟΧΙ)  ")
        




