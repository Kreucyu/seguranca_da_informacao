package org.aes;

import org.aes.Core.AES;
import org.aes.Models.CypherData;
import org.aes.Models.Menu;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        CypherData cypherData;
        AES aes;
        while(true) {
            cypherData = Menu.loadMenuAndGetValues(sc);
            if(cypherData == null) continue;
            aes = new AES();
            aes.start(cypherData);
        }
    }
}