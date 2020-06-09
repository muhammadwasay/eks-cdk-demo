package com.myorg;

import software.amazon.awscdk.core.App;

import java.util.Arrays;

public class EksCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new EksCdkStack(app, "EksCdkStack");

        app.synth();
    }
}
