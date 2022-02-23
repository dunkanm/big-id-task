package com.bigid.filesearch;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class Main {

    private static final String SEARCH_TERMS = "James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey,Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";

    /**
     * What can be improved:
     * 1. Threads number is static
     * 2. Processing based on lines, if a file is a 1gb one-liner we are dead
     *      It will be better to just read chunks of bytes, but I will have to make sure I won't cut it in the middle
     *      of the word, and it is a bit tricky.
     *      Also, it will be hard to track line numbers (it is required by task description).
     * 3. How to test it?
     * 4. Aggregator is just waiting 100ms for something to pop up in queue, it does not mean processing is finished
     * 5. Match separate words
     *
     *
     * What is good:
     * 1. We are not reading entire file in memory at once.
     * 2. Producer-consumer based on blocking queue is relatively simple and effective.
     * 3. No 3rd party stuff used (it may be also bad:))
     *
     *
     * NOTES:
     * 1. Logging
     * 2. It is not necessary to use executors
     *
     */

    public static void main(String[] args) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
       /* System.out.println("arg[0]: " + args[0] + " arg[1]: " + args[1]);
        String bigFilePath = args[0];
        String targetsFilePath = args[1];*/
        URL res = Main.class.getClassLoader().getResource("middle.txt");
        File file = null;
        if (res != null) {
            file = new File(res.toURI());
            Controller searchController = new Controller();
            searchController.startSearch(file, SEARCH_TERMS);
        }

    }


}
