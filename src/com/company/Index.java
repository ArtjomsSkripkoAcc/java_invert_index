package com.company;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Index {

    private Map<String, List<Integer>> index;
    private final Map<Integer, List<String>> wordsPerDocumentNumber;

    public Index() {
        index = new HashMap<>();
        wordsPerDocumentNumber = new HashMap<>();
    }

    public void buildIndex(String filePath) {
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line = file.readLine();
            while (line != null) {
                String[] words = line.split("\\s+");
                Integer documentNumber = Integer.parseInt(words[0]);
                wordsPerDocumentNumber.put(documentNumber, new ArrayList<>());
                Stream.of(words).skip(1).forEach(word -> {
                    if (!index.containsKey(word)) {
                        index.put(word, new ArrayList<>());
                    }
                    index.get(word).add(documentNumber);
                    wordsPerDocumentNumber.get(documentNumber).add(word);
                });
                line = file.readLine();
            }
            index = index.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                    //sort and remove duplicates
                    e -> e.getValue().stream()
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList())));
            System.out.println("Dictionary successfully created");
        } catch (IOException e) {
            System.out.printf("File path [%s] is invalid.%n", filePath);
        } catch (Exception e) {
            System.out.println("Selected file is corrupted");
        }
    }

    public void getPostings(String filePath) {
        List<String> allWords = new ArrayList<>();
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line = file.readLine();
            while (line != null) {
                String[] words = line.split("\\s+");
                allWords.addAll(Arrays.asList(words));
                line = file.readLine();
            }
            allWords.sort(String.CASE_INSENSITIVE_ORDER);

            PrintWriter writer = new PrintWriter(new FileWriter("result.txt", true));
            writer.println("GetPostings");
            allWords.forEach(word -> {
                writer.println(word);
                writer.println(find(word));
            });
            writer.close();
        } catch (IOException e) {
            System.out.printf("File path [%s] is invalid.%n", filePath);
        }
    }

    public void queryAnd(String filePath) {
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line = file.readLine();
            PrintWriter writer = new PrintWriter(new FileWriter("result.txt", true));
            while (line != null) {
                String[] words = line.split("\\s+");
                writer.println("QueryAnd");
                System.out.println("QueryAnd");
                String result = "empty";
                List<List<Integer>> groupedDocumentNumbers = Arrays.stream(words)
                    .map(this::findDocumentNumbers)
                    .collect(Collectors.toList());
                Set<Integer> resultNumbers = new HashSet<>();
                if (groupedDocumentNumbers.size() > 0) {
                    resultNumbers = new HashSet<>(groupedDocumentNumbers.get(0));
                    for (List<Integer> numbers : groupedDocumentNumbers) {
                        resultNumbers.retainAll(numbers);
                    }
                }

                List<String> resultNumbersSorted = resultNumbers.stream()
                    .sorted()
                    .map(Object::toString)
                    .collect(Collectors.toList());
                if (resultNumbers.size() > 0) {
                    result = String.join(" ", resultNumbersSorted);
                }
                writer.println(line);
                System.out.println(line);
                System.out.println("Results: " + result);
                writer.println("Results: " + result);
                line = file.readLine();
            }
            writer.close();

        } catch (IOException e) {
            System.out.printf("File path [%s] is invalid.%n", filePath);
        }
    }

    public void queryOr(String filePath) {
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line = file.readLine();
            PrintWriter writer = new PrintWriter(new FileWriter("result.txt", true));
            while (line != null) {
                String[] words = line.split("\\s+");
                writer.println("QueryOr");
                System.out.println("QueryOr");
                String result = "empty";
                List<String> uniqueDocumentNumbers = Arrays.stream(words)
                    .map(this::findDocumentNumbers)
                    .flatMap(Collection::stream)
                    .distinct()
                    .sorted()
                    .map(Object::toString)
                    .collect(Collectors.toList());
                if (uniqueDocumentNumbers.size() > 0) {
                    result = String.join(" ", uniqueDocumentNumbers);
                }

                writer.println(line);
                System.out.println(line);
                System.out.println("Results: " + result);
                writer.println("Results: " + result);
                line = file.readLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.printf("File path [%s] is invalid.%n", filePath);
        }
    }

    public void TFIDF(String filePath) {
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line = file.readLine();
            PrintWriter writer = new PrintWriter(new FileWriter("result.txt", true));
            while (line != null) {
                String[] words = line.split("\\s+");
                writer.println("TF-IDF");
                System.out.println("TF-IDF");
                String result = "empty";
                Map<String, List<Integer>> groupedDocumentNumbers = Arrays.stream(words)
                    .collect(Collectors.toMap(Function.identity(), this::findDocumentNumbers));

                List<String> rankedDocuments = groupedDocumentNumbers.entrySet().stream()
                    .map(entry -> getTFIDFForDocumentsForOneWord(entry.getKey(), entry.getValue()))
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .sorted(Comparator.comparingDouble(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .map(Object::toString)
                    .collect(Collectors.toList());

                if (rankedDocuments.size() > 0) {
                    Collections.reverse(rankedDocuments);
                    result = String.join(" ", rankedDocuments);
                }

                writer.println(line);
                System.out.println(line);
                System.out.println("Results: " + result);
                writer.println("Results: " + result);
                line = file.readLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.printf("File path [%s] is invalid.%n", filePath);
        }
    }

    public void wildCard(String filePath) {
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String line = file.readLine();
            PrintWriter writer = new PrintWriter(new FileWriter("result.txt", true));
            while (line != null) {
                //Only one query word per line expected
                String word = line.split("\\s+")[0].toLowerCase();
                String[] wordSplitByWildCards = word.split("\\*");
                writer.println("Wild card");
                writer.println(word);
                System.out.println("Wild card");
                System.out.println(word);
                System.out.println("Results:");
                writer.println("Results:");
                index.entrySet().stream()
                    .filter(entry -> wordSplitByWildCards.length > 1 ?
                        entry.getKey().toLowerCase().startsWith(wordSplitByWildCards[0]) &&
                        entry.getKey().toLowerCase().endsWith(wordSplitByWildCards[1]) :
                        entry.getKey().toLowerCase().startsWith(wordSplitByWildCards[0])
                        )
                    .forEach(entry -> {
                        List<String> relatedDocuments = entry.getValue().stream()
                            .map(Object::toString)
                            .collect(Collectors.toList());
                        System.out.println(entry.getKey());
                        writer.println(entry.getKey());
                        System.out.println("Postings: " + String.join(" ", relatedDocuments));
                        writer.println("Postings: " + String.join(" ", relatedDocuments));
                    });
                line = file.readLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.printf("File path [%s] is invalid.%n", filePath);
        }

    }

    private String find(String word) {
        System.out.println(word);
        String postings = findDocumentNumbers(word).stream()
            .map(Object::toString)
            .collect(Collectors.joining(" "));
        System.out.printf("Postings : %s%n", postings);
        return "Postings: " + postings;
    }

    private List<Integer> findDocumentNumbers(String word) {
        return index.get(word) == null ? new ArrayList<>() : index.get(word);
    }

    private Map<Integer, Double> getTFIDFForDocumentsForOneWord(String word, List<Integer> relatedDocuments) {
        int totalCountOfDocuments = wordsPerDocumentNumber.keySet().size();
        int relatedDocumentsCount = relatedDocuments.size();
        return relatedDocuments.stream()
            .collect(Collectors.toMap(Function.identity(), d -> {
                List<String> wordsOfDocument = wordsPerDocumentNumber.get(d);
                int totalWordCountInDocument = wordsOfDocument.size();
                int countOfWordInCurrentDocument = (int) wordsOfDocument.stream().filter(w -> w.equals(word)).count();
                //TF(t) = (termina t skaits dokumentā) / (kopējais terminu skaits dokumentā)
                Double tf = (double) countOfWordInCurrentDocument / (double) totalWordCountInDocument;
                //IDF(t) = (kopējais dokumentu skaits) / (dokumentu skaits, kas satur terminu t)
                Double idf = (double) totalCountOfDocuments / (double) relatedDocumentsCount;
                return tf * idf;
            }));
    }
}
