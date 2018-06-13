//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import static org.junit.Assert.*;
//
//public class CYKChartTest {
//
//    @Test
//    public void fillChart() {
//        Grammar g = TestUtils.loadGrammar("test_res/7.4");
//        CYKChart chart = new CYKChart(g, "ccaab");
//        chart.fillChart();
//        System.out.println(chart);
//        System.out.println(chart.canProduce());
//
//        chart = new CYKChart(g, "aabcc");
//        chart.fillChart();
//        System.out.println(chart);
//        System.out.println(chart.canProduce());
//    }
//}