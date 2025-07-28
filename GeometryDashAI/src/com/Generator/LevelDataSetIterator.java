package com.Generator;

import org.deeplearning4j.datasets.iterator.INDArrayDataSetIterator;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Ta klasa konwertuje listę wzorców (Pattern) na dane zrozumiałe dla sieci neuronowej.
public class LevelDataSetIterator {

    public static INDArrayDataSetIterator create(List<Pattern> levelPatterns, int batchSize, int sequenceLength) {
        List<String> patternNames = PatternLibrary.PATTERNS.stream().map(Pattern::getName).toList();
        Map<String, Integer> patternToIndex = IntStream.range(0, patternNames.size())
                .boxed().collect(Collectors.toMap(patternNames::get, i -> i));
        int numClasses = patternNames.size();

        INDArray features = Nd4j.create(levelPatterns.size() - sequenceLength, numClasses, sequenceLength);
        INDArray labels = Nd4j.create(levelPatterns.size() - sequenceLength, numClasses, sequenceLength);

        for (int i = 0; i < levelPatterns.size() - sequenceLength; i++) {
            for (int j = 0; j < sequenceLength; j++) {
                Pattern currentPattern = levelPatterns.get(i + j);
                int featureIdx = patternToIndex.getOrDefault(currentPattern.getName(), 0);
                features.putScalar(new int[]{i, featureIdx, j}, 1.0);

                Pattern nextPattern = levelPatterns.get(i + j + 1);
                int labelIdx = patternToIndex.getOrDefault(nextPattern.getName(), 0);
                labels.putScalar(new int[]{i, labelIdx, j}, 1.0);
            }
        }

        DataSet dataSet = new DataSet(features, labels);
        List<DataSet> dataSetList = dataSet.asList();

        List<Pair<INDArray, INDArray>> pairList = (List<Pair<INDArray, INDArray>>) dataSetList.stream()
                .map((Function<? super DataSet, ?>) ds -> new Pair<>(ds.getFeatures(), ds.getLabels()))
                .collect(Collectors.toList()).reversed();
        
        return new INDArrayDataSetIterator(pairList, batchSize);
    }
}