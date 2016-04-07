package exercises;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Chapter2 {

    private static final String WORDS_FILE = "./resources/words.txt";


    public static void main(String[] args){
        List<String> words = loadWords();
        exercise1(words);
        try {
            exercise1NoJava8(words);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<String> loadWords(){
        try {
            String contents = new String(Files.readAllBytes(Paths.get(WORDS_FILE)), StandardCharsets.UTF_8);
            return Arrays.asList(contents.split("[\\P{L}]+"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static void exercise1(List<String> words) {
        long oldCount = runOldVersion(words);
        long parallelCount = runParallelVersion(words);
        System.out.println("oldCount: "+oldCount);
        System.out.println("newCount: "+parallelCount);
    }
    private static long runParallelVersion(List<String> words) {
        return words.parallelStream().filter((w)-> w.length() > 10).count();
    }

    private static long runOldVersion(List<String> words) {
        int count = 0;
        for(String w: words){
            if(w.length() > 10){
                count++;
            }
        }
        return count;
    }

    public static void exercise1NoJava8(List<String> words) throws InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("cores: "+cores);
        int blockSize = (words.size()/cores) + ((words.size() % cores == 0) ? 0 : 1);

        List<WordCounter> tasks = new ArrayList<>();
        int start = 0;
        int end = blockSize;
        for(int i=0; i< cores; i++){
            tasks.add(new WordCounter(words,start,end));
            start = end;
            end += blockSize;
            if(start >= words.size()){
                break;
            }
            if(end > words.size()){
                end = words.size();
            }
        }
        for(WordCounter w: tasks){
            Thread t = new Thread(w);
            t.start();
            t.join();
        }

        int count = 0;
        for(WordCounter counter: tasks){
            count += counter.getCount();
        }

        System.out.println("Count: "+count);
    }

    private static class WordCounter implements Runnable {
        private final List<String> words;
        private final int start;
        private final int end;
        private int count = 0;
        public WordCounter(List<String> words, int startIndex, int endIndex){
            System.out.println("WordCounter("+startIndex+","+endIndex+")");
            this.words = words;
            start = startIndex;
            end = endIndex;
        }

        @Override
        public void run() {
            final List<String> strings = words.subList(start, end);

            for(String w: strings){
                if(w.length() > 10) {
                    count++;
                }
            }
        }

        public long getCount() {
            return count;
        }
    }
}
