package io.mwk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.eks.Cluster;
import software.amazon.awscdk.services.eks.DefaultCapacityType;
import software.amazon.awscdk.services.eks.Nodegroup;
import software.amazon.awscdk.services.eks.NodegroupAmiType;
import software.amazon.awscdk.services.iam.AccountRootPrincipal;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.Role;

import java.util.Map;

import static java.util.Arrays.asList;

public class EksCdkStack extends Stack {
    public EksCdkStack(final Construct scope, final String id, String imageTag) {
        this(scope, id, null, imageTag);
    }

    public EksCdkStack(final Construct scope, final String id, final StackProps props, String imageTag) {
        super(scope, id, props);

        var deployMicroservice = (!imageTag.isBlank()) ? true : false;
        var twitchMicroserviceImage = "481137230390.dkr.ecr.us-east-1.amazonaws.com/java-twitch-integration:"+imageTag;
        var twitchAppClientId = "djso1368ggr0fmbxgpijy0pxhsw712";
        var twitchAppClientSecret = "gp807tyjhao85g86z3269qvq06g6zw";

        var clusterAdmin = Role.Builder.create(this, "AdminRole")
                .assumedBy(new AccountRootPrincipal())
                .managedPolicies(asList(ManagedPolicy.fromManagedPolicyArn(this,"admin-access", "arn:aws:iam::aws:policy/AdministratorAccess")))
                .build();

        var cluster = Cluster.Builder.create(this, "demo-eks-cluster")
                .mastersRole(clusterAdmin)
                .kubectlEnabled(true)
                .defaultCapacityType(DefaultCapacityType.NODEGROUP)
                .defaultCapacity(0)
                .defaultCapacityInstance(new InstanceType("t2.micro"))
                .build();

        Nodegroup.Builder.create(this, "nodegroup")
                .nodegroupName("eks-demo-nodegroup")
                .cluster(cluster)
                .instanceType(new InstanceType("t2.micro"))
                .amiType(NodegroupAmiType.AL2_X86_64)
                .minSize(2)
                .maxSize(2)
                .diskSize(8)
                .build();

        Map<String, String> appLabel = Map.of("app", "twitch-ms");

        Object deployment = Map.of(
                "apiVersion", "apps/v1",
                "kind", "Deployment",
                "metadata", Map.of("name", "twitch-ms"),
                "spec", Map.of(
                        "replicas", 1,
                        "selector", Map.of("matchLabels", appLabel),
                        "template", Map.of(
                                "metadata", Map.of("labels", appLabel),
                                "spec", Map.of(
                                        "containers", asList(Map.of(
                                                "name", "twitch-ms",
                                                "image", twitchMicroserviceImage,
                                                "env",asList(Map.of("name","TWITCH_CLIENT_ID",
                                                        "value", twitchAppClientId),
                                                        Map.of("name","TWITCH_CLIENT_SECRET",
                                                                "value", twitchAppClientSecret)),
                                                "ports", asList(Map.of("containerPort", 8080))))))));

        Object service = Map.of(
                "apiVersion", "v1",
                "kind", "Service",
                "metadata", Map.of("name", "twitch-ms"),
                "spec", Map.of(
                        "type", "LoadBalancer",
                        "ports", asList(Map.of("port", 80, "targetPort", 8080)),
                        "selector", appLabel));

        if(deployMicroservice){
            cluster.addResource("twitch-microservice", deployment, service);
        }
    }
}
