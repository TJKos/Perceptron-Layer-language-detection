import java.util.ArrayList;
import java.util.List;

public class Perceptron {
    private double alpha;
    private double theta;
    private int vecSize;
    private List<Double> weights;
    private List<Double> input;
    private double net;
    private int output;
    private int expectedOutput;
    public String rawExpectedOutput;
    public String term;



    public Perceptron(double alpha, int vecSize) {
        this.alpha = alpha;
        this.vecSize = vecSize;
        generateTheta();
        generateWeights();
    }

    public Perceptron(double alpha, int vecSize, String term) {
        this.alpha = alpha;
        this.vecSize = vecSize;
        this.term = term;
        generateTheta();
        generateWeights();
    }

    public void setInput(List<Double> input) {
        this.input = input;
        normalizeInput();
    }

    private void normalizeInput() {
        double sum = 0;
        for (double vec : input){
            sum += vec;
        }

        for (double vec : input){
            vec = vec/sum;
        }
    }

    public void parseOutput(){
        expectedOutput = rawExpectedOutput.equals(term) ? 1 : 0;
    }

    public void setRawExpectedOutput(String rawExpectedOutput) {
        this.rawExpectedOutput = rawExpectedOutput;
        parseOutput();
    }

    public void train(){
        updateOutput();
        updateWeights();
        updateTheta();
    }

    public void updateOutput(){
//        System.out.println("output1: " + output);
//        System.out.println("net1: " + net);
        updateNet();
        output = net >= 0 ? 1 : 0;
//        System.out.println("output2: " + output);
//        System.out.println("net2: " + net);
    }

    public void updateNet(){
        net = getDotProduct(weights, input) - theta;
    }

    public void updateTheta(){
        theta = theta - alpha * (expectedOutput - output);
    }

    public void updateWeights(){
        weights = getSumOfVectors(weights, getMultipliedVector(input, alpha*(expectedOutput-output)));
    }

    public double getDotProduct(List<Double> l1, List<Double> l2){
        double dotProduct = 0;
        for (int i = 0; i < l1.size(); i++) {
            dotProduct += l1.get(i)*l2.get(i);
        }
        return dotProduct;
    }

    public List<Double> getSumOfVectors(List<Double> l1, List<Double> l2){
        List<Double> tmp = new ArrayList<>(l1);
        for (int i = 0; i < tmp.size(); i++) {
            tmp.set(i, l1.get(i)+l2.get(i));
        }
        return tmp;
    }

    public List<Double> getMultipliedVector(List<Double> list, double multiplier) {
        List<Double> tmp = new ArrayList<>(list);
        for (int i = 0; i < tmp.size(); i++) {
            tmp.set(i, tmp.get(i)*multiplier);
        }
        return tmp;
    }

    public void generateTheta(){
        theta = Math.random();
    }

    public void generateWeights(){
        weights = new ArrayList<>(vecSize);
        for (int i = 0; i < vecSize; i++) {
            weights.add(Math.random());
        }
    }

    @Override
    public String toString() {
        return "Perceptron{" +
                "weights=" + weights +
                ", input=" + input +
                ", net=" + net +
                ", output=" + output +
                ", expectedOutput=" + expectedOutput +
                ", rawExpectedOutput='" + rawExpectedOutput + '\'' +
                '}';
    }

    public String toResult() {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";

        if (expectedOutput == output){
            return ANSI_GREEN + "Correct prediction: " + (output == 1 ? term : "Not " + term) + ANSI_RESET;
        }else{
            return ANSI_RED + "Failed prediction: Predicted " + (output == 1 ? term : "Not " + term) + ", but it was " + rawExpectedOutput + ANSI_RESET;
        }
    }

    public int getOutput() {
        return output;
    }

    public int getExpectedOutput() {
        return expectedOutput;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public double getNet() {
        return net;
    }
}
