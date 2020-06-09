package io.mwk;

import software.amazon.awscdk.core.App;

public class EksCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new EksCdkStack(app, "EksCdkStack");

        app.synth();
    }
}
