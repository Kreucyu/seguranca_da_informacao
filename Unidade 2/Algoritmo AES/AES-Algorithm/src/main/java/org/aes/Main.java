package org.aes;

import org.aes.Models.CypherData;
import org.aes.Models.Menu;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        while(true) {
            CypherData data = Menu.loadMenuAndGetValues(sc);
            if(data == null) continue;

        }
    }
}