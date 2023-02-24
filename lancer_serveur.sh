#!/bin/bash

echo Bienvenue

echo -e "Voulez-vous:\n \e[31m[1]\e[0m Lancer le programme \n\n \e[31m[2]\e[0m Lancer le programme avec log\n\n \e[31m[3]\e[0m Stopper le programme \n\n \e[31m[4]\e[0m Redémarrer le programme \n\n \e[31m[5]\e[0m Checker si l$



#while true
#do

read -p "-->" reponse

if [ $reponse -eq 1 ]; then
        echo "lancement du programme..."
        sudo systemctl start verycoolserver.service
        echo -e "\e[32mOk\e[0m"

elif [ $reponse -eq 2 ]; then
        java -jar VeryCoolServer.jar


elif [ $reponse -eq 3 ]; then
        echo "Arrêt du programme..."
        sudo systemctl stop verycoolserver.service
        echo -e "\e[32mOk\e[0m"

elif [ $reponse -eq 4 ]; then
        echo "Redémarrage..."
        sudo systemctl restart verycoolserver.service
        echo -e "\e[32mOk\e[0m"

elif [ $reponse -eq 5 ]; then
        statut=$(systemctl list-units --type=service | grep verycoolserver.service)

        if [[ $statut == *"running"* ]]; then
                echo -e "\e[32mLe programme tourne!\e[0m"

        else
                echo -e "Le programme  \e[31mn'est pas lancé.\e[0m"
        fi

else
        echo "autre"
fi
#done

