package com.Generator;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LstmLevelGenerator implements ILevelGenerationModel {
    private MultiLayerNetwork network;
    private Map<String, Integer> patternToIndex;
    private List<String> indexToPattern;
    private final int lstmLayerSize = 128;
    private final int sequenceLength = 10;
    private final Random rng = new Random();
    private static final String MODEL_SAVE_PATH = "trained_models/level_generator.zip";

    public LstmLevelGenerator() {
        try {
            File modelFile = new File(MODEL_SAVE_PATH);
            if (modelFile.exists()) {
                System.out.println("Wczytywanie istniejącego modelu LSTM...");
                this.network = MultiLayerNetwork.load(modelFile, true);
                initializeMappings();
                System.out.println("Model wczytany pomyślnie.");
            }
        } catch (Exception e) {
            System.err.println("Nie udało się wczytać modelu, będzie wymagał treningu.");
            this.network = null;
        }
    }
    private void initializeMappings() {
        indexToPattern = PatternLibrary.PATTERNS.stream().map(Pattern::getName).distinct().collect(Collectors.toList());
        patternToIndex = IntStream.range(0, indexToPattern.size())
                .boxed().collect(Collectors.toMap(indexToPattern::get, i -> i));
    }

    @Override
    public void train(List<Pattern> sequence) {
        initializeMappings();
        indexToPattern = PatternLibrary.PATTERNS.stream().map(Pattern::getName).distinct().collect(Collectors.toList());
        patternToIndex = IntStream.range(0, indexToPattern.size())
                .boxed().collect(Collectors.toMap(indexToPattern::get, i -> i));
        int numClasses = indexToPattern.size();

        MultiLayerConfiguration conf = null;
        network = new MultiLayerNetwork(conf);
        network.init();

        conf = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.005))
                .list()
                .layer(0, new LSTM.Builder().nIn(numClasses).nOut(lstmLayerSize)
                        .activation(Activation.TANH).build())
                .layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
                        .activation(Activation.SOFTMAX).nIn(lstmLayerSize).nOut(numClasses).build())
                .build();

        network = new MultiLayerNetwork(conf);
        network.init();
        try {
            System.out.println("Zapisywanie wytrenowanego modelu do pliku...");
            File modelFile = new File(MODEL_SAVE_PATH);
            modelFile.getParentFile().mkdirs();
            network.save(modelFile, true);
            System.out.println("Model zapisany w: " + MODEL_SAVE_PATH);
        } catch (Exception e) {
            System.err.println("Błąd podczas zapisywania modelu!");
            e.printStackTrace();
        }

        System.out.println("Rozpoczynanie treningu modelu LSTM...");
        if (sequence.size() <= sequenceLength) {
            System.err.println("Zbyt mało danych do treningu! Potrzeba > " + sequenceLength + " wzorców.");
            return;
        }
        var dataIterator = LevelDataSetIterator.create(sequence, 32, sequenceLength);

        for (int epoch = 0; epoch < 30; epoch++) {
            network.fit(dataIterator);
            dataIterator.reset();
        }
        System.out.println("Trening zakończony.");
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        if (network == null) {
            return PatternLibrary.getRandomPattern();
        }

        INDArray input = Nd4j.zeros(1, indexToPattern.size());
        String lastPatternName = currentState.isEmpty() ? indexToPattern.get(0) : currentState.get(currentState.size() - 1);
        int idx = patternToIndex.getOrDefault(lastPatternName, 0);
        input.putScalar(new int[]{0, idx}, 1);

        INDArray output = network.rnnTimeStep(input);

        float[] probabilities = output.toFloatVector();
        float rand = rng.nextFloat();
        float sum = 0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            if (rand <= sum) {
                return PatternLibrary.getPatternByName(indexToPattern.get(i));
            }
        }
        return PatternLibrary.getRandomPattern();
    }
}