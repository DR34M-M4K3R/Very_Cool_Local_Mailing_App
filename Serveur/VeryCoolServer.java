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

import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;



public class VeryCoolServer {


    public static final String absoluteStoredMessagesPath="<CHEMIN POUR LE STOCKAGE DES MESSAGES SUR LE SERVEUR>";
    public static final int port = 1234;//   <---PORT DE FONCTIONNEMENT DU SERVEUR


    public static String printMessages(String id){
        StringBuilder msgString = new StringBuilder();
        //Accessing the folder path
        File myFolder = new File(absoluteStoredMessagesPath+"/stored_messages/"+id);
        File[] listOfFiles = myFolder.listFiles();
        String fileName, line = null;
        BufferedReader br;
        try{
        for (File eachFile : listOfFiles) {
            if (eachFile.isFile()) {
                try {
                    String sender = eachFile.getName().split("_")[0];
                    String day = eachFile.getName().split("_")[1];
                    String hour = eachFile.getName().split("_")[2];
                    hour = hour.substring(0, hour.length()-4);


                    msgString.append("_________ De : ").append(sender).append(", le ").append(day).append(" à ").append(hour).append(" _________");

                    //System.out.println(eachFile.getName());
                    fileName = eachFile.getAbsolutePath();
                    br = new BufferedReader(new FileReader(fileName));

                    try {
                        while ((line = br.readLine()) != null) {
                            msgString.append("@").append(line);
                        }
                        msgString.append("@@_____________________________________________________________@@");
                    } catch (IOException ignored) {}
                } catch (FileNotFoundException ignored) {}
            }
        }
            output("Messages envoyés", INFO);
            return(msgString.toString());
        }catch(Exception e){
            output("Vous n'avez aucun nouveau message", INFO);
            return("Vous n'avez aucun nouveau message");
        }
    }


    public static boolean newMessage(String input){

        String message = input.substring(VeryCoolProtocol.sendMessageRequest().length());
        String[] messageArray = message.split(";/;");
        String messageRecipient = messageArray[0].toLowerCase();
        String messageSender = messageArray[1].toLowerCase();
        String messageContent = messageArray[2];


        try{
            Files.createDirectories(Paths.get(absoluteStoredMessagesPath+"/stored_messages/"+messageRecipient));
            
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH'h'mm");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);



            try {
                FileWriter myWriter = new FileWriter(absoluteStoredMessagesPath+"/stored_messages/"+messageRecipient+"/"+messageSender+"_"+date+".txt");
                myWriter.write(messageContent);
                myWriter.close();
                output("Message enregistré", INFO);
            } catch (IOException e) {
                output("Erreur lors de l'écriture du fichier.", WARNING);
                e.printStackTrace();
                return(false);
            }


            return(true);
        }catch(Exception e){
            return(false);
        }


    }

    public final static int INFO = 0;
    public final static int PROCESS = 1;
    public final static int QUESTION = 2;
    public final static int SUCCESS = 3;
    public final static int WARNING = 4;


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

    public static void main(String[] args) throws InterruptedException, IOException {
        AnsiConsole.systemInstall();
        output("Lancement du service...", PROCESS);
        ServerSocket serverSocket = new ServerSocket(port);
        output("", SUCCESS);
        output("Attente de clients...", INFO);

        while(true) {

            Socket clientSocket = serverSocket.accept();

            VeryCoolProtocol vcp = new VeryCoolProtocol();

            InetAddress addr = clientSocket.getInetAddress();
            String hostName=addr.getHostName();
            VeryCoolProtocol.clientHostName =hostName.substring(0, (hostName.length()-5));

            output("Client connecté: "+hostName, INFO);

            String inputLine, outputLine;

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));





            while ((inputLine = in.readLine()) != null) {
                outputLine = vcp.serverProcessInput(inputLine);
                out.println(outputLine);

                if (outputLine.equals("Goodbye")) {
                    break;
                }else if (outputLine.contains("Error")){
                    clientSocket.close();
                    output("Erreur de protocole de la part du client", WARNING);
                    break;
                }
            }
            //TimeUnit.SECONDS.sleep(30);
            clientSocket.close();
            output("Connexion fermée.", INFO);
            System.out.print("\n");
        }
    }
}
