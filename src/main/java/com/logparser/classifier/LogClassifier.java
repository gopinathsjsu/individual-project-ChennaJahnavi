package com.logparser.classifier;

/**
 * Template-method style classifier: concrete implementations define how lines are matched to {@link LogType}.
 */
public abstract class LogClassifier {

    public abstract LogType classify(String line);
}
