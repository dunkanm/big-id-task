package com.bigid.filesearch;

import com.bigid.filesearch.consumer.Occurrence;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerTest {

    private static final String SEARCH_TERMS = "James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey,Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger";
    private static final int EXPECTED_OCCURRENCES = 165;

    private Controller searchController;

    @Test
    void startSearchTest() throws URISyntaxException, ExecutionException, InterruptedException {
        Controller searchController = new Controller();
        URL res = ControllerTest.class.getClassLoader().getResource("testFile.txt");
        File file = null;
        Map<String, List<Occurrence.Coordinates>> result = null;
        if (res != null) {
            file = new File(res.toURI());
            result = searchController.startSearch(file, SEARCH_TERMS);
        }
        assertEquals(EXPECTED_OCCURRENCES, result.values().stream().mapToInt(coordinates -> coordinates.size()).sum());
    }
}