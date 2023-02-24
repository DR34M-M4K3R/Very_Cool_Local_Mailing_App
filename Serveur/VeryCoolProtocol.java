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


import java.util.Objects;

public class VeryCoolProtocol {
    public static String readRequest="Receive";
    public static String sendRequest="Send";

    public String requestType;

    public String[] messageToSend;

    public String hostName;

    private int state = WAITING;
    private static final int WAITING = 0;
    private static final int GREETED = 1;
    private static final int MESSAGESSENT = 2;
    private static final int MESSAGESREQUESTED = 2;
    private static final int SAIDGOODBYE= 3;


    public static String clientHostName;

    public static String sendMessageRequest(){return("Send message: ");}

    public String serverProcessInput(String input){

        String output= null;

        if(state==WAITING){
            if (input.equalsIgnoreCase("Hello")){
                output="Hello";
                state=GREETED;

            }else{
                output="Erreur: mauvais protocole.";

            }


        } else if (state==GREETED) {
            if (input.equalsIgnoreCase("Request messages")){


                output="Messages:"+VeryCoolServer.printMessages(clientHostName);
                state=MESSAGESSENT;

            }else if (input.startsWith(sendMessageRequest())){
                if(VeryCoolServer.newMessage(input)){
                    output="Message sent";
                }else{
                    output="Message not sent";
                }
                state=MESSAGESSENT;

            }

            else{
                output="Erreur: mauvais protocole.";

            }

        } else if (state==MESSAGESSENT) {
            if (input.equalsIgnoreCase("Goodbye")) {
                output = "Goodbye";
                state = SAIDGOODBYE;

            } else {
                output="Erreur: mauvais protocole: ";

            }

        }
        return(output);
    }

}

