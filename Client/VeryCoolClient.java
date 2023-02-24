/*
 * Copyright © 2023 DR34M-M43KR
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software
 * without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import com.sun.org.apache.xalan.internal.xsltc.dom.AdaptiveResultTreeImpl;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;


public class VeryCoolClient {


    public static final int port = 1234;//   <---PORT DE FONCTIONNEMENT DU SERVEUR

    public static final String address = "<ADRESSE DU SERVEUR SUR LE RÉSEAU LOCAL (192.168.X.XX...)>";


    public final static String selfHostName;

    static {
        try {
            selfHostName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public final static int INFO = 0;
    public final static int PROCESS = 1;
    public final static int QUESTION = 2;
    public final static int SUCCESS = 3;
    public final static int WARNING = 4;


    public static void newConnection(String connectionType, String ... messageToSend) throws IOException {

        try {
            Socket veryCoolSocket = new Socket(address, port);

            PrintWriter out = new PrintWriter(veryCoolSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(veryCoolSocket.getInputStream()));
            String fromUser, fromServer = "";

            VeryCoolProtocol vcp = new VeryCoolProtocol();
            vcp.requestType=connectionType;
            vcp.messageToSend=messageToSend;
            vcp.hostName=selfHostName;


            do{
                fromUser = vcp.clientProcessInput(fromServer);
                if (fromUser != null) {
                    out.println(fromUser);
                }

                if (fromServer.startsWith("Messages:")) {
                    System.out.println(fromServer.replace('@', '\n'));


                }else if (fromServer.equalsIgnoreCase("Goodbye")) {
                    break;

                }else if (fromServer.contains("Error")){
                    veryCoolSocket.close();
                    output("Erreur de protocole de la part du client", WARNING);
                    break;
                } else if (fromServer.equalsIgnoreCase("Message not sent")) {
                    output("Message non envoyé", WARNING);

                } else if (fromServer.equalsIgnoreCase("Message sent")) {
                    output("Message envoyé", INFO);

                }
                //System.out.println("Server: "+fromServer+" | Client: "+fromUser);
            }while ((fromServer = in.readLine()) != null);

        }catch(ConnectException e){
            output("Erreur de connexion", WARNING);

        }

    }


    public static void output(String output_text, int output_type) {

        switch (output_type) {

            case INFO:
                System.out.println(ansi().fg(BLUE).a("[-] ").reset() + output_text);
                break;

            case PROCESS:
                System.out.print(ansi().fg(YELLOW).a("[*] ").reset() + output_text);
                break;

            case QUESTION:
                System.out.print(ansi().fg(YELLOW).a("[?] ").reset() + output_text + ">");
                break;

            case SUCCESS:
                System.out.println(ansi().fg(GREEN).a(" Done!").reset() + output_text);
                break;

            case WARNING:
                System.out.println(ansi().fg(RED).a("[!] ").reset() + output_text);
                break;

        }

    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        AnsiConsole.systemInstall();


        System.out.println("\n\n|BIENVENUE|\n");
        output(("Votre nom sur le réseau: "+selfHostName), INFO );
        System.out.print("\n");
        output("Souhaitez-vous:  [1]- Envoyer un message | [2]- Lire vos messages", QUESTION);
        String reponse = sc.nextLine();
        String connectionType="";
        String recipient=null;
        String messageContent=null;

        if(reponse.equalsIgnoreCase("1")){

            while(recipient==null || messageContent==null) {
                System.out.print("\n");
                output("Envoyer un message à: ", QUESTION);
                recipient = sc.nextLine();
                output("Contenu du message: ", QUESTION);
                messageContent = sc.nextLine();

                if (recipient.contains(" ") || recipient.contains(";/;")) {
                    recipient=null;
                    output("Nom de destinataire illégal.", WARNING);
                } else if (messageContent.contains(";/;") || messageContent.contains("@")) {
                    messageContent=null;
                    output("Contenu de message illégal.", WARNING);
                }

            }
            connectionType="Send";
            newConnection(connectionType, recipient, selfHostName, messageContent);

        }else if (reponse.equalsIgnoreCase("2")){
            System.out.print("\n");
            connectionType="Receive";
            newConnection(connectionType);

        }
    }
}
