package readability;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        TextReader reader = new TextReader();
        List<String> inputText = reader.readFile(args[0]);
        if (!inputText.isEmpty()) {
            ReadabilityStats stats = new ReadabilityStats(inputText);
            stats.calculateReadStats();
            new Printer().printStats(stats);
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.chooseReadabilityMethod(new UserRequest().getReadabilityMethod(), stats);
        }
    }
}

class UserRequest {
    Scanner in = new Scanner(System.in);

    String getReadabilityMethod() {
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        String readType = in.nextLine();

        in.close();
        System.out.println();
        return readType;
    }
}

class TextReader {

    List<String> readFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try {
            Path fileDir = Path.of(System.getProperty("user.dir"));
            Path filePath = Path.of(String.valueOf(fileDir), fileName);
            if (Files.exists(filePath)) {
                lines = Files.readAllLines(filePath);
                if (lines.isEmpty()) {
                    System.out.println("The file is empty.");
                }
            } else {
                System.out.println("File not found.");
            }
        } catch (IOException ignore) {
        }

        return lines;
    }
}

class Dispatcher {

    void chooseReadabilityMethod(String readabilityMethod, ReadabilityStats stats) {
        Printer p = new Printer();
        switch (readabilityMethod) {
            case "ARI" -> {
                float ariScore = new ARI().getAriScore(stats.getSentenceCt(), stats.getWordCt(), stats.getCharCt());
                System.out.print("Automated Readability Index: ");
                p.printReadability(ariScore);
            }
            case "FK" -> {
                float fxScore = new FK().getFKscore(stats.getWordCt(), stats.getSentenceCt(), stats.getSyllableCt());
                System.out.print("Flesch–Kincaid readability tests: ");
                p.printReadability(fxScore);
            }
            case "CL" -> {
                float clScore = new CL().getCLscore(stats.getSentenceCt(), stats.getWordCt(), stats.getCharCt());
                System.out.print("Coleman–Liau index: ");
                p.printReadability(clScore);
            }
            case "SMOG" -> {
                float smogScore = new SMOG().getSMOGscore(stats.getPolySyllableCt(), stats.getSentenceCt());
                System.out.print("Simple Measure of Gobbledygook: ");
                p.printReadability(smogScore);
            }
            case "all" -> {
                Map<String, Float> readScores = new HashMap<>();

                float ariScore = new ARI().getAriScore(stats.getSentenceCt(), stats.getWordCt(), stats.getCharCt());
                System.out.print("Automated Readability Index: ");
                p.printReadability(ariScore);
                readScores.put("ARI", ariScore);

                float fxScore = new FK().getFKscore(stats.getWordCt(), stats.getSentenceCt(), stats.getSyllableCt());
                System.out.print("Flesch–Kincaid readability tests: ");
                p.printReadability(fxScore);
                readScores.put("FX", fxScore);

                float smogScore = new SMOG().getSMOGscore(stats.getPolySyllableCt(), stats.getSentenceCt());
                System.out.print("Simple Measure of Gobbledygook: ");
                p.printReadability(smogScore);
                readScores.put("SMOG", smogScore);

                float clScore = new CL().getCLscore(stats.getSentenceCt(), stats.getWordCt(), stats.getCharCt());
                System.out.print("Coleman–Liau index: ");
                p.printReadability(clScore);
                readScores.put("CL", clScore);

                p.printAvgReadability(readScores);

            }
            default -> System.out.println("Method not recognized.");
        }
    }
}

class ReadabilityStats {
    SyllabicEvaluator sybEval = new SyllabicEvaluator();

    List<String> inputText;
    List<String> sentences = new ArrayList<>();
    List<String> wordList = new ArrayList<>();

    int sentenceCt;
    int wordCt;
    int charCt;
    int syllableCt;
    int polySyllableCt;

    public ReadabilityStats(List<String> inputText) {
        this.inputText = inputText;
        sentences = makeSentences();
    }

    void calculateReadStats() {
        sentenceCt = calcSentenceCt();
        wordCt = calcWordCt();
        charCt = calcCharCt();

        sybEval.calcSyllables();
        syllableCt = sybEval.getSyllablesCt();
        polySyllableCt = sybEval.getPolysyllablesCt();
    }

    List<String> makeSentences() {
        for (int i = 0; i < inputText.size(); i++) {
            String tabFree = inputText.get(i).replaceAll("\t", " ");
            String nextLineFree = tabFree.replaceAll("\n", " ");
            String currSentence = nextLineFree.replaceAll("[ ]+", " ");
            String[] sent = currSentence.split("(?<=[.!?])([ ])(?=[A-Z0-9])");
            sentences.addAll(Arrays.asList(sent));
        }
        return sentences;
    }

    int calcSentenceCt() {
        return sentences.size();
    }

    int calcWordCt() {
        int wordCt = 0;
        for (int i = 0; i < sentences.size(); i++) {
            String sentence = sentences.get(i).replaceAll("[.,!:;?]", "");
            String[] words = sentence.split(" ");
            wordCt += words.length;
            wordList.addAll(Arrays.asList(words));
        }

        return wordCt;
    }

    int calcCharCt() {
        int charsCt = 0;

        for (int i = 0; i < inputText.size(); i++) {
            String s = inputText.get(i);
            for (int j = 0; j < s.length(); j++) {
                if (!Character.isWhitespace(s.charAt(j)) && s.charAt(j) != '\t' && s.charAt(j) != '\n') {
                    charsCt++;
                }
            }
        }

        return charsCt;
    }

    int getSentenceCt() {
        return sentenceCt;
    }

    int getWordCt() {
        return wordCt;
    }

    int getCharCt() {
        return charCt;
    }

    int getSyllableCt() {
        return syllableCt;
    }

    int getPolySyllableCt() {
        return polySyllableCt;
    }


    class SyllabicEvaluator {
        int syllablesCt;
        int polysyllablesCt;
        int vowelCt = 0;

        Set<Character> vowels = new HashSet<>();

        {
            vowels.add('a');
            vowels.add('e');
            vowels.add('i');
            vowels.add('o');
            vowels.add('u');
        }

        Set<String> diphthongs = new HashSet<>();

        {
            diphthongs.add("oy");
            diphthongs.add("ou");
            diphthongs.add("ay");
            diphthongs.add("ey");
            diphthongs.add("uy");
        }

        void calcSyllables() {
            for (int i = 0; i < wordList.size(); i++) {
                vowelCt = 0;

                String currWord = wordList.get(i);
                currWord = currWord.toLowerCase();
                countVowels(currWord);
                subtractSilentVowels(currWord);
                subtractDiphthongs(currWord);
                addY(currWord);

                if (vowelCt == 0) {
                    vowelCt++;
                }

                syllablesCt += vowelCt;

                if (vowelCt > 2) {
                    polysyllablesCt++;
                }
            }
        }

        void countVowels(String word) {

            for (int i = 0; i < word.length(); i++) {
                if (vowels.contains(word.charAt(i))) {
                    int v = i;
                    //consecutive vowels are being handled here
                    while (v < word.length() && vowels.contains(word.charAt(v))) {
                        v++;
                    }
                    vowelCt++;
                    i = v;
                }
            }

        }

        void subtractSilentVowels(String currWord) {

            //ending with e
            if (currWord.endsWith("e")) {
                vowelCt--;
            }
        }

        void subtractDiphthongs(String currWord) {
            for (int i = 0; i < currWord.length(); i++) {
                if (vowels.contains(currWord.charAt(i)) &&
                        (i + 1 < currWord.length() && diphthongs.contains(currWord.substring(i, i + 2)))) {
                    vowelCt--;
                }
            }
        }

        void addY(String currWord) {
            int lastIndex = currWord.length() - 1;
            if (currWord.endsWith("y") && !diphthongs.contains(currWord.substring(lastIndex - 1, lastIndex + 1))) {
                vowelCt++;
            }

            for (int i = 1; i < currWord.length() - 1; i++) {
                if (currWord.charAt(i) == 'y' && i + 1 < currWord.length() && !vowels.contains(currWord.charAt(i + 1))) {
                    vowelCt++;
                }
            }
        }

        int getSyllablesCt() {
            return syllablesCt;
        }

        int getPolysyllablesCt() {
            return polysyllablesCt;
        }
    }

}

//Automated Readability Index
class ARI {

    final float first = 4.71f;
    final float mid = 0.5f;
    final float last = 21.43f;

    float getAriScore(double sentenceCt, double wordCt, double charCt) {
        return (float) ((first * (charCt / wordCt)) + (mid * (wordCt / sentenceCt)) - last);
    }
}

//Flesch–Kincaid
class FK {
    final float first = 0.39f;
    final float mid = 11.8f;
    final float last = 15.59f;

    float getFKscore(double wordCt, double sentenceCt, double syllableCt) {
        return (float) ((first * (wordCt / sentenceCt)) + (mid * (syllableCt / wordCt)) - last);
    }
}

//Simple Measure of Gobbledygook
class SMOG {
    final float first = 1.043f;
    final float mid = 30f;
    final float last = 3.1291f;

    float getSMOGscore(double polysyllacleCt, double sentenceCt) {
        return (float) ((first * Math.sqrt(polysyllacleCt * (mid / sentenceCt))) + last);
    }
}

//Coleman–Liau
class CL {
    final float first = 0.0588f;
    final float mid = 0.296f;
    final float last = 15.8f;

    float getCLscore(double sentenceCt, double wordCt, double charCt) {
        //L is the average number of characters per 100 words
        double L = (charCt / wordCt) * 100;

        //S is the average number of sentences per 100 words
        double S = (sentenceCt / wordCt) * 100;

        return (float) ((first * L) - (mid * S) - last);
    }
}


class Printer {
    Map<Integer, Integer> ARImap = new HashMap<>();

    {
        ARImap.put(1, 6);
        ARImap.put(2, 7);
        ARImap.put(3, 8);
        ARImap.put(4, 9);
        ARImap.put(5, 10);
        ARImap.put(6, 11);
        ARImap.put(7, 12);
        ARImap.put(8, 13);
        ARImap.put(9, 14);
        ARImap.put(10, 15);
        ARImap.put(11, 16);
        ARImap.put(12, 17);
        ARImap.put(13, 18);
        ARImap.put(14, 22);
    }

    void printStats(ReadabilityStats stats) {
        System.out.println("Words: " + stats.getWordCt());
        System.out.println("Sentences: " + stats.getSentenceCt());
        System.out.println("Characters: " + stats.getCharCt());
        System.out.println("Syllables: " + stats.sybEval.getSyllablesCt());
        System.out.println("Polysyllables: " + stats.sybEval.getPolysyllablesCt());
    }

    void printReadability(float score) {
        int roundedScore = (int) Math.ceil(score);

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);

        if (roundedScore < 1) {
            roundedScore = 1;
        }

        if (roundedScore > 14) {
            roundedScore = 14;
        }

        System.out.println(df.format(score) + " (about " + ARImap.get(roundedScore) + "-year-olds).");
    }

    void printAvgReadability(Map<String, Float> readScores) {

        double avg = 0.0f;

        for (String key : readScores.keySet()) {
            int roundedScore = (int) Math.ceil(readScores.get(key));
            if (roundedScore < 1) {
                roundedScore = 1;
            }

            if (roundedScore > 14) {
                roundedScore = 14;
            }
            avg += ARImap.get(roundedScore);
        }

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.DOWN);

        avg = avg / readScores.size();

        System.out.println();
        System.out.println("This text should be understood in average by " + df.format(avg) + "-year-olds.");

    }

}