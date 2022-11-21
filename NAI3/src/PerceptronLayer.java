import java.util.ArrayList;
import java.util.List;

public class PerceptronLayer {
    private List<Perceptron> perceptronList;

    public PerceptronLayer(int vecSize) {
        perceptronList = new ArrayList<>();
    }

    public void addPerceptron(Perceptron perceptron){
        perceptronList.add(perceptron);
    }

    public List<Perceptron> getPerceptronList() {
        return perceptronList;
    }

    public Perceptron getWinner(){
        Perceptron winner = perceptronList.get(0);
        for (Perceptron perceptron : perceptronList){
            if (perceptron.getNet() > winner.getNet()){
                winner = perceptron;
            }
        }

        return winner;
    }
}
