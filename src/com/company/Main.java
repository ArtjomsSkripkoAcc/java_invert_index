package com.company;

import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(new InputStreamReader(System.in));
        System.out.println("Welcome to inverted index commandline app.");
        Index index = new Index();

        while (true) {
            System.out.println("- To create/update a dictionary - type 'Dictionary'");
            System.out.println("- To get postings from the dictionary - type 'Postings'");
            System.out.println("- To get AND query - type 'AND'");
            System.out.println("- To get OR query - type 'OR'");
            System.out.println("- To get TF-IDF query - type 'TF-IDF'");
            System.out.println("- To get WILDCARD query - type 'WILDCARD'");
            System.out.println("- To exit application - type 'Exit'");
            String command = sc.nextLine().toUpperCase();
            switch (command) {
                case "DICTIONARY":
                    System.out.println("Provide path to file for dictionary creation and press enter");
                    index.buildIndex(sc.nextLine());
                    break;
                case "POSTINGS":
                    System.out.println("Provide path to query file and press enter");
                    index.getPostings(sc.nextLine());
                    break;
                case "AND":
                    System.out.println("Provide path to query file and press enter");
                    index.queryAnd(sc.nextLine());
                    break;
                case "OR":
                    System.out.println("Provide path to query file and press enter");
                    index.queryOr(sc.nextLine());
                    break;
                case "TF-IDF":
                    System.out.println("Provide path to query file and press enter");
                    index.TFIDF(sc.nextLine());
                    break;
                case "WILDCARD":
                    System.out.println("Provide path to query file and press enter");
                    index.wildCard(sc.nextLine());
                case "EXIT":
                    System.out.println("Thank you for using this app. You will be charged for $500!");
                    System.exit(0);
            }
            System.out.println("------------------------------------------------------------------");
        }
    }
}
