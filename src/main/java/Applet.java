import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Applet extends PApplet {

    public enum Algo {
        RANDOM, LEXICALORDER, GENETIC
    }

    private float x = 5;
    private float y = 5;
    private float w = 70;
    private float h = 30;
    private boolean inReset = false;
    private int totalCities = 6;

    //RANDOM
    private List<Vector> randomCities = new ArrayList<>(totalCities);
    private float randomRecordDistance;
    private List<Integer> randomBestEver = new ArrayList<>(totalCities);
    private List<Integer> randomOrder = new ArrayList<>(totalCities);
    private int randomtime;
    private boolean randomIsBest;

    //LEXICALORDER
    private List<Vector> lexicalCities = new ArrayList<>(totalCities);
    private float lexicalRecordDistance;
    private double lexicalTotalPermutations;
    private List<Integer> lexicalBestEver = new ArrayList<>(totalCities);
    private List<Integer> lexicalOrder = new ArrayList<>(totalCities);
    private int lexicalCount;
    private int lexicaltime, lexicaltimeHidden;
    private boolean lexicalEnd;

    //GENETIC
    private List<Vector> genericCities = new ArrayList<>(totalCities);
    private int geneticPopulationCount = 10;
    private List<List<Integer>> geneticPopulation = new ArrayList<>(geneticPopulationCount);
    private List<Double> geneticFitness = new ArrayList<>();


    public static void main(String args[]) {
        PApplet.main("Applet");
    }

    @Override
    public void settings() {
        size(1200, 600);
    }

    @Override
    public void setup() {
        clear();
        frameRate(100);
        reset();
    }

    private void reset() {
        //RANDOM
        randomCities.clear();
        randomBestEver.clear();
        randomOrder.clear();
        for (int i = 0; i < totalCities; i++) {
            randomCities.add(new Vector(random(width / 3), random(height / 2)));
            randomOrder.add(i);
            randomBestEver.add(i);
        }
        randomRecordDistance = calcDistance(randomCities, randomOrder);
        randomtime = millis();
        randomIsBest = false;

        //LEXICALORDER
        lexicalCities.clear();
        lexicalOrder.clear();
        lexicalBestEver.clear();
        lexicalCount = 1;
        lexicalEnd = false;
        for (int i = 0; i < totalCities; i++) {

            lexicalCities.add(new Vector(randomCities.get(i).getX() + (width / 3), randomCities.get(i).getY()));
            lexicalOrder.add(i);
            lexicalBestEver.add(i);
        }
        lexicalRecordDistance = calcDistance(lexicalCities, lexicalOrder);
        lexicalTotalPermutations = factorial(totalCities);
        lexicaltime = millis();


        //GENETIC
//        for (Vector v : lexicalCities) {
//            v.setX(random(width / 3, 2 * width / 3));
//            v.setY(random(height / 2));
//        }
//        for (int i = 0; i < lexicalOrder.size(); i++) {
//            lexicalOrder.set(i, i);
//            lexicalbestEver.set(i, i);
//        }
//        lexicalRecordDistance = calcDistance(lexicalCities, lexicalOrder);
//        lexicalCount = 0;
//        for (int i = 0; i < populationCount; i++) {
//            population.add(new ArrayList<>(order));
//            Collections.shuffle(population.get(i));
//        }
//            System.out.println(population);
//
//
//        totalPermutations = factorial(totalCities);
    }

    @Override
    public void mousePressed() {
        if (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h) {
            inReset = true;
            reset();
            inReset = false;
        }
    }

    @Override
    public void draw() {
        if (!inReset) {
            clear();
            background(55, 55, 55);
            fill(0, 150, 150);
            strokeWeight(1);
            rect(x, y, w, h);
            fill(0);
            textSize(12);
            text("Reset", x - (textWidth("Reset") / 2) + (w / 2), y + 6 + (h / 2));

            drawRandom();

            drawLexical();
        }
    }

    private void drawRandom() {
        //POINTS
        stroke(0);
        strokeWeight(1);
        fill(255);
        for (Vector v : randomCities) {
            ellipse(v.getX(), v.getY(), 7, 7);
        }

        //BEST
        stroke(255, 0, 255);
        strokeWeight(2);
        for (int i = 0; i < randomBestEver.size() - 1; i++) {
            Vector v1 = randomCities.get(randomBestEver.get(i));
            Vector v2 = randomCities.get(randomBestEver.get(i + 1));
            line(v1.getX(), v1.getY(), v2.getX(), v2.getY());
        }

        //NOW
        translate(0, height / 2);
        stroke(255);
        for (int i = 0; i < randomCities.size() - 1; i++) {
            Vector v1 = randomCities.get(randomOrder.get(i));
            Vector v2 = randomCities.get(randomOrder.get(i + 1));
            line(v1.getX(), v1.getY(), v2.getX(), v2.getY());
        }

        //TEXT
        textSize(12);
        fill(255);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < randomOrder.size(); i++) {
            s.append(randomOrder.get(i));
        }
        text(s.toString() + "\n" + randomtime + "ms", 0, (height / 2) - 24);

        randomSwap(randomOrder);

        float d = calcDistance(randomCities, randomOrder);
        if (d < randomRecordDistance) {
            randomRecordDistance = d;
            Collections.copy(randomBestEver, randomOrder);
            randomtime = millis();
        }

        if (lexicalEnd) {
            System.out.println(lexicaltimeHidden);
            lexicaltime = 0;
            if(randomRecordDistance <= lexicalRecordDistance && !randomIsBest) {
                randomIsBest = true;
                randomtime = randomtime - lexicaltimeHidden;
            }else if(randomRecordDistance > lexicalRecordDistance && !randomIsBest){
                randomtime = millis() - lexicaltimeHidden;
            }
        }
    }

    private void drawLexical() {
        translate(0, -(height / 2));
        stroke(0);
        strokeWeight(1);
        fill(255);
        for (Vector v : lexicalCities) {
            ellipse(v.getX(), v.getY(), 7, 7);
        }

        stroke(255, 0, 255);
        strokeWeight(2);
        for (int i = 0; i < lexicalBestEver.size() - 1; i++) {
            Vector v1 = lexicalCities.get(lexicalBestEver.get(i));
            Vector v2 = lexicalCities.get(lexicalBestEver.get(i + 1));
            line(v1.getX(), v1.getY(), v2.getX(), v2.getY());
        }

        translate(0, height / 2);
        stroke(255);
        for (int i = 0; i < lexicalCities.size() - 1; i++) {
            Vector v1 = lexicalCities.get(lexicalOrder.get(i));
            Vector v2 = lexicalCities.get(lexicalOrder.get(i + 1));
            line(v1.getX(), v1.getY(), v2.getX(), v2.getY());
        }

        textSize(12);
        fill(255);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < lexicalOrder.size(); i++) {
            s.append(lexicalOrder.get(i));
        }

        double percent = 100 * (lexicalCount / lexicalTotalPermutations);
        //System.out.println(lexicalCount);
        text(String.valueOf(round(percent, 2)) + "% completed\n" + s + "\n" + lexicaltime + "ms", width / 3, (height / 2) - 44);

        if (percent < 100) {
            lexicalCount = lexicalOrderSwap(lexicalOrder, lexicalCount);
            float d = calcDistance(lexicalCities, lexicalOrder);
            if (d < lexicalRecordDistance) {
                lexicalRecordDistance = d;
                Collections.copy(lexicalBestEver, lexicalOrder);
                lexicaltime = millis();
            }
        } else if(!lexicalEnd) {
            lexicalEnd = true;
            lexicaltimeHidden = lexicaltime;
        }
    }

    private int lexicalOrderSwap(List<Integer> order, int count) {
        return nextOrder(order, count);
    }

    private void randomSwap(List l) {
        int i = (int) Math.floor(random(l.size()));
        int j = (int) Math.floor(random(l.size()));
        Collections.swap(l, i, j);
    }


    private float calcDistance(List<Vector> points, List<Integer> order) {
        float sum = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            Vector v1 = points.get(order.get(i));
            Vector v2 = points.get(order.get(i + 1));
            float d = distance(v1.getX(), v1.getY(), v2.getX(), v2.getY());
            sum += d;
        }
        return sum;
    }

    private float distance(float x, float y, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x - x2), 2) + Math.pow((y - y2), 2));
    }

    private int nextOrder(List<Integer> array, int count) {
        // Find longest non-increasing suffix
        int i = array.size() - 1;
        while (i > 0 && array.get(i - 1) >= array.get(i))
            i--;
        // Now i is the head index of the suffix

        // Are we at the last permutation already?
        if (i <= 0)
            return ++count;

        // Let array.get(i - 1) be the pivot
        // Find rightmost element that exceeds the pivot
        int j = array.size() - 1;
        while (array.get(j) <= array.get(i - 1))
            j--;
        // Now the value array.get(j) will become the new pivot
        // Assertion: j >= i

        // Swap the pivot with j
        int temp = array.get(i - 1);
        array.set(i - 1, array.get(j));
        array.set(j, temp);

        // Reverse the suffix
        j = array.size() - 1;
        while (i < j) {
            temp = array.get(i);
            array.set(i, array.get(j));
            array.set(j, temp);
            i++;
            j--;
        }

        // Successfully computed the next permutation
        return ++count;
    }

    private double factorial(int n) {
        if (n == 1) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
