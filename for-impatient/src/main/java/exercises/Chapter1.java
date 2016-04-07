package exercises;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Chapter1 {
    public static void main(String[] args) {
//        exercise1();
//        exercise2(new File("C:\\Program Files"));
//        exercise3(new File("D:\\programming-code\\intellij-workspace\\src\\chapter1"), ".java");
//        exercise4(new File("C:\\").listFiles());
//        exercise6();
//        exercise7();
//        exercise8();
//        exercise9();
    }



    public static void exercise1() {
        long threadId = Thread.currentThread().getId();
        long[] lambdaThreadId = new long[]{-1};
        String[] words = {"Ala", "ma", "kota"};
        Arrays.sort(words,
                (String x, String y) -> {
                    if (lambdaThreadId[0] == -1) {
                        lambdaThreadId[0] = Thread.currentThread().getId();
                    }
                    return x.compareToIgnoreCase(y);
                }
        );

        System.out.println("Same thread: " + (threadId == lambdaThreadId[0]));
    }

    public static void exercise2(File root) {
        System.out.println("Excerice 2");
        File[] oldWay = root.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });


        File[] lambdaWay = root.listFiles(file -> file.isDirectory());
        File[] methodWay = root.listFiles(File::isDirectory);

        print(oldWay);
        print(lambdaWay);
        print(methodWay);
        System.out.println();
    }

    public static void print(File[] list) {
//        System.out.println(Arrays.asList(list));
        Arrays.asList(list).forEach(System.out::println);
    }

    public static void exercise3(File root, String extension) {
        System.out.println("Excerice 3");
        String[] lambdaWay = root.list((dir,name) -> name.endsWith(extension));
        Arrays.asList(lambdaWay).forEach(System.out::print);
        System.out.println();
    }

    public static void exercise4(File[] fileList) {
        System.out.println("Exercise 4");
        File[] oldWayResult = new File[fileList.length];
        System.arraycopy(fileList,0,oldWayResult,0,fileList.length);

        Arrays.sort(oldWayResult, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {

                if (o1.isDirectory() && o2.isDirectory() || o1.isFile() && o2.isFile()) {
                    return o1.getPath().compareTo(o2.getPath());
                } else if (o1.isDirectory() && o2.isFile()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        File[] lambdaResult = new File[fileList.length];
        System.arraycopy(fileList,0,lambdaResult,0,fileList.length);

        Arrays.sort(lambdaResult, (o1, o2) -> {
            if (o1.isDirectory() && o2.isDirectory() || o1.isFile() && o2.isFile()) {
                return o1.getPath().compareTo(o2.getPath());
            } else if (o1.isDirectory() && o2.isFile()) {
                return -1;
            } else {
                return 1;
            }
        });

        print(oldWayResult);
        print(lambdaResult);
    }

    private static void exercise6(){
        System.out.println("Exercise 6");
        new Thread(uncheck(() -> {
            System.out.println("Zzz");
            Thread.sleep(1000);
        })).start();
    }

    public static Runnable uncheck(RunnableEx runner){
        return () -> {
            try {
                runner.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    @FunctionalInterface
    interface RunnableEx {
        void run() throws Exception;
    }


    public static void exercise7() {
        System.out.println("Exercise 7");
        Runnable then = andThen(() -> System.out.println("r1"), ()-> System.out.println("r2"));
        new Thread(then).start();
    }

    public static Runnable andThen(Runnable r1, Runnable r2) {
        return () -> {
            r1.run();
            r2.run();
        };
    }

    public static void exercise8() {
        System.out.println("Exercise 8");
        String[] names = {"Peter", "Paul", "Mary"};
        List<Runnable> runnersEnhancedLoop = new ArrayList();
        for(String name: names){
            runnersEnhancedLoop.add(() -> System.out.println(name));
        }

        // my answer:
        // each runner has separate name
        // no difference in loops -> WRONG -> CANNOT USE i IN lambda as 'variables in lambda expressions must be effectively final'
        // would have to use a trick (String name = names[i]);
        for(Runnable r: runnersEnhancedLoop){
            new Thread(r).start();
        }

        List<Runnable> runnersNormalLoop = new ArrayList<>();
        for(int i=0; i< names.length; i++){
            String name = names[i];
            runnersNormalLoop.add(() -> System.out.println(name));
        }

        for(Runnable r: runnersNormalLoop){
            new Thread(r).start();
        }
    }

    public static void exercise9(){
        System.out.println("Exercise 9");
        String[] words = {"012345","0123456","01234567","012345678","0123456789"};
        MyList<String> myWordList = new MyList<>();
        myWordList.addAll(Arrays.asList(words));

        myWordList.forEachIf(System.out::println,(elem) -> elem.length() > 7);

    }

    private interface Collection2<T> extends Collection<T> {
        default void forEachIf(Consumer<T> action , Predicate<T> filter) {
            forEach((element) -> {
                if(filter.test(element)) {
                    action.accept(element);
                }
            });
        }
    }

    private static class MyList<T> extends ArrayList<T> implements Collection2<T> {

    }

}