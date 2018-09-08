package ru.avaneev.imagetiler.model;

/**
 * @author Andrey Vaneev
 * Creation date: 04.09.2018
 */
public class OptionsHolder {
    private static Options options = new Options();

    private OptionsHolder() {
    }

    public static Options getOptions() {
        return options;
    }

    public static void resetOptions() {
        options = new Options();
    }

    public static void disposeSourceFile() {
        options.setSourceFile(null);
    }
}
