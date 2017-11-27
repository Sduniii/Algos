import ekotech.Vector;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Applet extends PApplet {

    public enum Algo {
        RANDOM, LEXICALORDER, GENETIC
    }

    private float x = 5;
    private float y = 5;
    private float w = 70;
    private float h = 30;
    private boolean inReset = false;
    private int totalCities = 7;
    private long starttime;
    private List<Vector> cities = new ArrayList<>(totalCities);
    private Estimate estimate;
    private long runtime;
    private float fps = 200;

    //RANDOM
    private float randomRecordDistance;
    private List<Integer> randomBestEver = new ArrayList<>(totalCities);
    private List<Integer> randomOrder = new ArrayList<>(totalCities);
    private long randomtime;
    private boolean randomIsBest;

    //LEXICALORDER
    private float lexicalRecordDistance;
    private double lexicalTotalPermutations;
    private List<Integer> lexicalBestEver = new ArrayList<>(totalCities);
    private List<Integer> lexicalOrder = new ArrayList<>(totalCities);
    private int lexicalCount;
    private long lexicaltime;
    private boolean lexicalEnd;

    //GENETIC
    private int geneticPopulationCount = 500;
    private List<List<Integer>> geneticPopulation = new ArrayList<>(geneticPopulationCount);
    private List<Float> geneticFitness = new ArrayList<>();
    private List<Integer> geneticStandartOrder = new ArrayList<>(totalCities);
    private List<Integer> geneticBestEver = new ArrayList<>();
    private List<Integer> geneticCurrentBest = new ArrayList<>();
    private float geneticRecordDistance;
    private long genetictime;
    private boolean geneticIsBest;
    private float mutationRate = 0.2f;


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
        frameRate(fps);
        reset();
    }

    private void reset() {
        starttime = System.currentTimeMillis();
        estimate = new Estimate();
        estimate.setTimeStart(starttime);
        runtime = starttime;
        cities.clear();
        for (int i = 0; i < totalCities; i++) {
            cities.add(new Vector(random(width / 3), random(50, (height / 2) - 70)));
        }

        //RANDOM
        randomBestEver.clear();
        randomOrder.clear();
        for (int i = 0; i < totalCities; i++) {
            randomOrder.add(i);
            randomBestEver.add(i);
        }
        randomRecordDistance = calcDistance(cities, randomOrder);
        randomtime = 0;
        randomIsBest = false;

        //LEXICALORDER
        lexicalOrder.clear();
        lexicalBestEver.clear();
        lexicalCount = 1;
        lexicalEnd = false;
        for (int i = 0; i < totalCities; i++) {
            lexicalOrder.add(i);
            lexicalBestEver.add(i);
        }
        lexicalRecordDistance = calcDistance(cities, lexicalOrder);
        lexicalTotalPermutations = factorial(totalCities);
        lexicaltime = 0;


        //GENETIC
        genetictime = 0;
        geneticIsBest = false;
        geneticFitness.clear();
        geneticPopulation.clear();
        geneticBestEver.clear();
        geneticCurrentBest.clear();
        geneticStandartOrder.clear();
        for (int i = 0; i < totalCities; i++) {
            geneticStandartOrder.add(i);
            geneticBestEver.add(i);
            geneticCurrentBest.add(i);
        }
        geneticRecordDistance = Float.MAX_VALUE;
        for (int i = 0; i < geneticPopulationCount; i++) {
            geneticPopulation.add(new ArrayList<>(geneticStandartOrder));
            Collections.shuffle(geneticPopulation.get(i));
        }
        geneticRecordDistance = calcDistance(cities, geneticBestEver);

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

            drawGenetic();
        }
    }

    private void drawRandom() {
        //POINTS
        stroke(0);
        strokeWeight(1);
        fill(255);
        for (Vector v : cities) {
            ellipse(v.getX(), v.getY(), 7, 7);
        }

        //BEST
        stroke(255, 0, 255);
        strokeWeight(2);
        for (int i = 0; i < randomBestEver.size() - 1; i++) {
            Vector v1 = cities.get(randomBestEver.get(i));
            Vector v2 = cities.get(randomBestEver.get(i + 1));
            line(v1.getX(), v1.getY(), v2.getX(), v2.getY());
        }

        //NOW
        stroke(255);
        for (int i = 0; i < cities.size() - 1; i++) {
            Vector v1 = cities.get(randomOrder.get(i));
            Vector v2 = cities.get(randomOrder.get(i + 1));
            line(v1.getX(), v1.getY() + (height / 2), v2.getX(), v2.getY() + (height / 2));
        }

        //TEXT
        textSize(12);
        fill(255);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < randomOrder.size(); i++) {
            s.append(randomOrder.get(i));
        }
        String t = millisToString(randomtime);
        text(s.toString() + "\n" + t, 0, height - 24);

        if (!randomIsBest) {
            randomSwap(randomOrder);

            float d = calcDistance(cities, randomOrder);
            if (d < randomRecordDistance) {
                randomRecordDistance = d;
                Collections.copy(randomBestEver, randomOrder);
                randomtime = time(starttime);
            }

            if (lexicalEnd) {
                int i = 0;
                if (round(randomRecordDistance, 1) <= round(lexicalRecordDistance, 1) && !randomIsBest) {
                    randomIsBest = true;
                } else if (randomRecordDistance > lexicalRecordDistance && !randomIsBest) {
                    randomtime = time(starttime);
                }
            }
        }
    }

    private void drawLexical() {
        stroke(0);
        strokeWeight(1);
        fill(255);
        for (Vector v : cities) {
            ellipse(v.getX() + (width / 3), v.getY(), 7, 7);
        }

        stroke(255, 0, 255);
        strokeWeight(2);
        for (int i = 0; i < lexicalBestEver.size() - 1; i++) {
            Vector v1 = cities.get(lexicalBestEver.get(i));
            Vector v2 = cities.get(lexicalBestEver.get(i + 1));
            line(v1.getX() + (width / 3), v1.getY(), v2.getX() + (width / 3), v2.getY());
        }

        stroke(255);
        for (int i = 0; i < cities.size() - 1; i++) {
            Vector v1 = cities.get(lexicalOrder.get(i));
            Vector v2 = cities.get(lexicalOrder.get(i + 1));
            line(v1.getX() + (width / 3), v1.getY() + (height / 2), v2.getX() + (width / 3), v2.getY() + (height / 2));
        }

        textSize(12);
        fill(255);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < lexicalOrder.size(); i++) {
            s.append(lexicalOrder.get(i));
        }
        String t = millisToString(lexicaltime);
        double percent = 100 * (lexicalCount / lexicalTotalPermutations);
        text(String.valueOf(round(percent, 4)) + "% completed\n" + s + "\n" + t, width / 3, height - 44);

        textSize(16);
        if (frameCount % fps == 0 && percent < 100) {
            estimate.setEnd(percent);
            estimate.setTimeEnd(System.currentTimeMillis());
            estimate.calcEstimate();
        }
        if (percent < 100)
            runtime = System.currentTimeMillis() - starttime;
        String es = millisToString(estimate.getEstimate());
        String cu = millisToString(runtime);
        String a = "estimate: " + es + " - current: " + cu;
        text(a, (width / 2) - (textWidth(a) / 2), 20);


        if (percent < 100) {
            lexicalCount = lexicalOrderSwap(lexicalOrder, lexicalCount);
            float d = calcDistance(cities, lexicalOrder);
            if (d < lexicalRecordDistance) {
                lexicalRecordDistance = d;
                Collections.copy(lexicalBestEver, lexicalOrder);
                lexicaltime = time(starttime);
            }
        } else if (!lexicalEnd) {
            lexicalEnd = true;
        }
    }

    private void drawGenetic() {

        //POINTS
        stroke(0);
        strokeWeight(1);
        fill(255);
        for (Vector v : cities) {
            ellipse(v.getX() + (2 * width / 3), v.getY(), 7, 7);
        }

        //BEST
        stroke(255, 0, 255);
        strokeWeight(2);
        for (int i = 0; i < geneticBestEver.size() - 1; i++) {
            Vector v1 = cities.get(geneticBestEver.get(i));
            Vector v2 = cities.get(geneticBestEver.get(i + 1));
            line(v1.getX() + (2 * width / 3), v1.getY(), v2.getX() + (2 * width / 3), v2.getY());
        }

        //NOW
        stroke(255);
        for (int i = 0; i < cities.size() - 1; i++) {
            Vector v1 = cities.get(geneticCurrentBest.get(i));
            Vector v2 = cities.get(geneticCurrentBest.get(i + 1));
            line(v1.getX() + (2 * width / 3), v1.getY() + (height / 2), v2.getX() + (2 * width / 3), v2.getY() + (height / 2));
        }

        //TEXT
        textSize(12);
        fill(255);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < geneticBestEver.size(); i++) {
            s.append(geneticBestEver.get(i));
        }
        String t = millisToString(genetictime);
        text(s.toString() + "\n" + t, 2 * width / 3, height - 24);

        if (!geneticIsBest) {
            geneticStep();

            if (lexicalEnd) {
                if (round(geneticRecordDistance, 1) <= round(lexicalRecordDistance, 1) && !geneticIsBest) {
                    geneticIsBest = true;
                } else if (geneticRecordDistance > lexicalRecordDistance && !geneticIsBest) {
                    genetictime = time(starttime);
                }
            }
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

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private long time(long starttime) {
        return new Date().getTime() - starttime;
    }

    private void geneticStep() {
        calcFitness();
        normalizeFitness();
        //System.out.println(geneticFitness);
        nextGeneration();
    }

    private void calcFitness() {
        geneticFitness.clear();
        float currentRecord = Float.MAX_VALUE;
        for (int i = 0; i < geneticPopulation.size(); i++) {
            float d = calcDistance(cities, geneticPopulation.get(i));
            if (d < geneticRecordDistance) {
                geneticRecordDistance = d;
                geneticBestEver = new ArrayList<>(geneticPopulation.get(i));
                genetictime = time(starttime);
            }
            if (d < currentRecord) {
                currentRecord = d;
                geneticCurrentBest = new ArrayList<>(geneticPopulation.get(i));
            }
            geneticFitness.add(i, 1 / (1 + d));
        }
    }

    private void normalizeFitness() {
        float sum = 0;
        for (int i = 0; i < geneticFitness.size(); i++) {
            sum += geneticFitness.get(i);
        }
        for (int i = 0; i < geneticFitness.size(); i++) {
            geneticFitness.set(i, geneticFitness.get(i) / sum);
        }
    }

    private void nextGeneration() {
        List<List<Integer>> newPopulation = new ArrayList<>();
        for (int i = 0; i < geneticPopulation.size(); i++) {
            List<Integer> orderA = pickOne(geneticPopulation, geneticFitness);
            List<Integer> orderB = pickOne(geneticPopulation, geneticFitness);
            List<Integer> order = crossOver(orderA, orderB);
            mutatate(order);
            newPopulation.add(order);
        }
        geneticPopulation.clear();
        geneticPopulation.addAll(newPopulation);
    }

    private List<Integer> crossOver(List<Integer> orderA, List<Integer> orderB) {
        int start = floor(random(orderA.size()));
        int end = floor(random(start + 1, orderA.size()));
        List<Integer> newOrder = new ArrayList<>();
        for (int i = start; i < end; i++) {
            newOrder.add(orderA.get(i));
        }
        for (int i = 0; i < orderB.size(); i++) {
            if (!newOrder.contains(orderB.get(i))) {
                newOrder.add(orderB.get(i));
            }
        }
        //System.out.println(newOrder.size() == orderA.size());
        return newOrder.size() == orderA.size() ? newOrder : orderA;

    }

    private List<Integer> pickOne(List<List<Integer>> geneticPopulation, List<Float> fitness) {
        int index = 0;
        float r = random(1);
        while (r > 0) {
            if (index >= fitness.size()) index--;
            r -= fitness.get(index);
            index++;
        }
        index--;
        if (index == -1) index = 0;
        return new ArrayList<>(geneticPopulation.get(index));
    }

    private void mutatate(List<Integer> order) {
        for (int i = 0; i < order.size(); i++) {
            if (random(1) < mutationRate) {
                int i1 = floor(random(order.size()));
                int i2 = i1 + 1;
                if (i2 >= order.size())
                    i2 = 0;
                Collections.swap(order, i1, i2);
            }
        }
    }

    public static String millisToString(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - (days * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - ((days * 24 * 60) + (hours * 60));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - ((days * 24 * 60 * 60) + (hours * 60 * 60) + (minutes * 60));
        millis = millis - ((days * 24 * 60 * 60 * 1000) +(hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000));

        if (days > 0)
            return String.format("%dd %dh %dm %ds %dms", days, hours, minutes, seconds, millis);
        if (hours > 0)
            return String.format("%dh %dm %ds %dms", hours, minutes, seconds, millis);
        else if (minutes > 0)
            return String.format("%dm %ds %dms", minutes, seconds, millis);
        else if (seconds > 0)
            return String.format("%ds %dms", seconds, millis);
        else
            return String.format("%dms", millis);
    }

}
