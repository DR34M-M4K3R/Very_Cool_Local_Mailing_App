# Very Cool Local Mailing App
Voici le code complet d'une application d'échange de messages se rapprochant des mails, faite pour être implémentée sur un réseau local (chez moi sur un raspberry pi)

## Fonctionnement
### Protocole

Le client et le serveur échangent grâce au `VeryCoolProtocol`, qui est décrit dans les deux fichiers VeryCoolProtocol.java (un pour le serveur et un pour le client).

Voici des exemples d'échanges:
</br>

Le client vérifie ses messages</br>
`Server: | Client: Hello`</br>
`Server: Hello | Client: Request messages`</br>
`Server: Messages:Vous n'avez aucun nouveau message | Client: Goodbye`</br>

Le client envoie un message</br>
`Server:  | Client: Hello`</br>
`Server: Hello | Client: Send message: pc_bureau;/;debian;/;Coucou, ça va?`</br>
`Server: Message sent | Client: Goodbye`</br>

## Interface
![client](https://user-images.githubusercontent.com/67145585/221230117-0c95d9a6-c760-43e4-8668-874307a89be4.png)

Client^

![serveur](https://user-images.githubusercontent.com/67145585/221232133-00ffd67b-f7bc-4411-9207-9275fea50812.png)

Serveur en mode log ^
