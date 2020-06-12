package io.mwk;

import software.amazon.awscdk.core.App;

import java.util.Map;

public class EksCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        String defaultImageTag = "latest";
        String envImageTag = "";
        String imageTagEnvProperty = "TWITCH_MS_IMAGE_TAG";
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            if(envName.equals(imageTagEnvProperty)){
                envImageTag = env.get(envName);
            }
        }
        System.out.format("%s=%s%n", imageTagEnvProperty, env.get(envImageTag));

        String imageTag = (envImageTag.isEmpty()) ? defaultImageTag : envImageTag;

        new EksCdkStack(app, "EksCdkStack", envImageTag);

        app.synth();
    }
}
