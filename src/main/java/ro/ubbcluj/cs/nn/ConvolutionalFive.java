package ro.ubbcluj.cs.nn;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * [CONV(3x3, 64) -> NORM -> SUB(2x2, MAX)] 2 -> [CONV(3x3, 64)] 2 -> SUB(2x2, MAX) -> FC(384)
 *
 * @author Mihai Teletin
 */
public class ConvolutionalFive implements Network {


    public MultiLayerNetwork setupModel() {
        int layer = 0;
        final MultiLayerConfiguration.Builder builder = new NeuralNetConfiguration.Builder()
                .seed(SEED)
                .iterations(1)
                .regularization(true).l1(0.0001).l2(0.0001)
                .learningRate(LEARNING_RATE)
                .learningRateDecayPolicy(LearningRatePolicy.Score).lrPolicyDecayRate(0.001)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(Updater.ADAM)
                .momentum(0.9)
                .leakyreluAlpha(0.02)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .useDropConnect(true)
                .list()
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .nIn(1)
                        .nOut(32)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .nOut(32)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new LocalResponseNormalization.Builder().build())
                .layer(layer++, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2).build())
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .nOut(32)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new LocalResponseNormalization.Builder().build())
                .layer(layer++, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2).build())
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .nOut(64)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new LocalResponseNormalization.Builder().build())
                .layer(layer++, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX).kernelSize(2, 2).build())
                .layer(layer++, new ConvolutionLayer.Builder(3, 3)
                        .padding(0, 0)
                        .nOut(32)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.LEAKYRELU)
                        .build())
                .layer(layer++, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2)
                        .build())
                .layer(layer++, new DenseLayer.Builder()
                        .activation(Activation.RELU)
                        .name("dense1")
                        .weightInit(WeightInit.XAVIER)
                        .nOut(400)
                        .dropOut(DROP_OUT)
                        .build())
                .layer(layer++, new DenseLayer.Builder()
                        .activation(Activation.RELU)
                        .name("dense2")
                        .weightInit(WeightInit.XAVIER)
                        .nIn(400)
                        .nOut(400)
                        .dropOut(DROP_OUT)
                        .build())
                .layer(layer++, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(2)
                        .weightInit(WeightInit.XAVIER)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true)
                .pretrain(false)
                .setInputType(InputType.convolutional(96, 48, 1));

        final MultiLayerConfiguration build = builder.build();
        final MultiLayerNetwork net = new MultiLayerNetwork(build);
        net.init();

        return net;
    }

    @Override
    public String getName() {
        return "fifth convolutional network";
    }
}
