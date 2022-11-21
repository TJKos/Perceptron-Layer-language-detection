import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static final int lowerAscii = 65;
    static final int upperAscii = 90;

    public static void main(String[] args) {
        int vecSize = 26;
        int failures = 0;
        double accuracyLimit = 0;
        double accuracy = 0;

        double alpha = Double.parseDouble(args[0]);
        int iterations = Integer.parseInt(args[1]);
        File trainFolder = new File(args[2]);
        File testFolder = new File(args[3]);

        PerceptronLayer perceptronLayer = new PerceptronLayer(vecSize);
        List<LanguageLetters> letterCountLists = new ArrayList<>();

        // UCZENIE
        getDirectoryStream(trainFolder).forEach(e -> {
            System.out.println(e.getFileName().toString());
            Perceptron perceptron = new Perceptron(alpha, vecSize, e.getFileName().toString());
            perceptronLayer.addPerceptron(perceptron);

            try {
                Files.walk(e)
                        .filter(Files::isRegularFile)
                        .forEach(txt -> {
                            try {
                                letterCountLists.add(new LanguageLetters(getLetterCount(Files.readAllLines(txt)), e.getFileName().toString()));
                            } catch (IOException fileNotFoundException) {
                                fileNotFoundException.printStackTrace();
                            }

                        });
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        for (int i = 0; i < iterations; i++) {
            for (LanguageLetters languageLetters : letterCountLists) {
                for (Perceptron perceptron : perceptronLayer.getPerceptronList()) {
                    perceptron.setInput(languageLetters.letterCount);
                    perceptron.setRawExpectedOutput(languageLetters.language);
                    perceptron.train();
                }
            }
        }
        letterCountLists.clear();

        // ZBIOR TESTOWY
        getDirectoryStream(testFolder).forEach(e -> {
            System.out.println(e.getFileName().toString());
            try {
                Files.walk(e)
                        .filter(Files::isRegularFile)
                        .forEach(txt -> {
                            try {
                                letterCountLists.add(new LanguageLetters(getLetterCount(Files.readAllLines(txt)), e.getFileName().toString()));
                            } catch (IOException fileNotFoundException) {
                                fileNotFoundException.printStackTrace();
                            }

                        });
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        for (LanguageLetters languageLetters : letterCountLists) {
            for (Perceptron perceptron : perceptronLayer.getPerceptronList()) {
                perceptron.setInput(languageLetters.letterCount);
                perceptron.setRawExpectedOutput(languageLetters.language);
                perceptron.updateOutput();
                System.out.println(perceptron.getOutput());
                System.out.println("Net: " + perceptron.getNet());
                System.out.println(perceptron.toResult());
            }
            System.out.println("\nWinning Perceptron: (net comparison)");
            System.out.println(perceptronLayer.getWinner().toResult());
            System.out.println("\n\n");
        }


        // TESTOWANIE POJEDYNCZYCH
        System.out.println("--- --- --- --- --- --- --- --- ---");
        for (Perceptron perceptron : perceptronLayer.getPerceptronList()) {
//            List<String> s = null;
//            try {
//                s = Files.readAllLines(Paths.get("lang/Polish/1.txt"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            perceptron.setInput(getLetterCount(s));
//            perceptron.setInput(getLetterCount(""));
//            perceptron.setInput(getLetterCount("lubie bardzo lubie spac jeść oglądać project cupid"));
//            perceptron.setInput(getLetterCount("siema, z tej strony polski polak dzien dobry. o tej porze każdy wypić może jakby nie było jest bardzo miło"));
            perceptron.setInput(getLetterCount("Zu meiner Familie gehören vier Personen. Die Mutter bin ich und dann gehört natürlich mein Mann dazu. Wir haben zwei Kinder, einen Sohn, der sechs Jahre alt ist und eine dreijährige Tochter."));
//            perceptron.setInput(getLetterCount("It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like)."));
            perceptron.setRawExpectedOutput("German");
            perceptron.updateOutput();
            System.out.println(perceptron.getOutput());
            System.out.println("Net: " + perceptron.getNet());
            System.out.println(perceptron.toResult());

        }
        System.out.println("\nWinning Perceptron: (net comparison)");
        System.out.println(perceptronLayer.getWinner().toResult());

        // TESTOWANIE PRZEZ INTERFEJS
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("Insert 'q' to exit.");
//            String test = scan.nextLine();
            String inserted = "";
            while (inserted.equals("")) {
                System.out.println("Insert text: ");

                inserted = scan.nextLine();
            }
            System.out.println("inserted: " + inserted);
            if (inserted.equals("q"))
                return;

            String term = "";
            while (term.equals("")) {
                System.out.print("Insert term: ");
                term = scan.next();
                scan.nextLine();
            }
            System.out.println("term: " + term);


            if (term.equals("q"))
                return;


            for (Perceptron perceptron : perceptronLayer.getPerceptronList()) {
                perceptron.setInput(getLetterCount(inserted));
                perceptron.setRawExpectedOutput(term);
                perceptron.updateOutput();
                System.out.println(perceptron.getOutput());
                System.out.println("Net: " + perceptron.getNet());
                System.out.println(perceptron.toResult());
            }
            System.out.println("\nWinning Perceptron: (net comparison)");
            System.out.println(perceptronLayer.getWinner().toResult());

        }
    }

    public static Stream<Path> getDirectoryStream(File file) {
        try {

            return Files.walk(file.toPath())
                    .filter(path -> {
                        try {
                            return !Files.isSameFile(path, file.toPath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .filter(Files::isDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static List<Double> getLetterCount(String s) {
        int vecSize = upperAscii - lowerAscii + 1;
        List<Double> letterCount = new ArrayList<>(Collections.nCopies(vecSize, 0.0));
        for (char c : s.toUpperCase().toCharArray()) {
            if (c >= lowerAscii && c <= upperAscii) {
                letterCount.set(c - lowerAscii, letterCount.get(c - lowerAscii) + 1);
            }
        }
        return letterCount;
    }

    public static List<Double> getLetterCount(List<String> strings) {
        int vecSize = upperAscii - lowerAscii + 1;
        List<Double> letterCount = new ArrayList<>(Collections.nCopies(vecSize, 0.0));
        for (String s : strings) {
            for (char c : s.toUpperCase().toCharArray()) {
                if (c >= lowerAscii && c <= upperAscii) {
                    letterCount.set(c - lowerAscii, letterCount.get(c - lowerAscii) + 1);
                }
            }
        }
        return letterCount;
    }
}