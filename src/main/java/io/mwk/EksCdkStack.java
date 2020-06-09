package io.mwk;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.eks.Cluster;
import software.amazon.awscdk.services.eks.DefaultCapacityType;
import software.amazon.awscdk.services.eks.NodegroupOptions;
import software.amazon.awscdk.services.eks.NodegroupAmiType;
import software.amazon.awscdk.services.iam.AccountRootPrincipal;
import software.amazon.awscdk.services.iam.Role;

import java.util.Map;

import static java.util.Arrays.asList;

public class EksCdkStack extends Stack {
    public EksCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EksCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        /*var clusterAdmin = Role.Builder.create(this, "AdminRole")
                .assumedBy(new AccountRootPrincipal())
                .build();*/

        var cluster = Cluster.Builder.create(this, "cluster-self-managed-ec2")
                /*.mastersRole(clusterAdmin)*/
                .kubectlEnabled(true)
                .defaultCapacityType(DefaultCapacityType.NODEGROUP)
                .defaultCapacity(0)
                .defaultCapacityInstance(new InstanceType("t2.micro"))
                .build();

        cluster.addNodegroup("nodegroup", NodegroupOptions
                .builder()
                .instanceType(new InstanceType("t2.micro"))
                .minSize(1)
                .maxSize(1)
                .amiType(NodegroupAmiType.AL2_X86_64)
                .diskSize(8)
                .build());

        /*cluster.addResource("mypod", Map.of(
                "apiVersion", "v1",
                "kind", "Pod",
                "metadata", Map.of("name", "mypod"),
                "spec", Map.of(
                        "containers", asList(Map.of(
                                "name", "hello",
                                "image", "paulbouwer/hello-kubernetes:1.5",
                                "ports", asList(Map.of("containerPort", 8080)))))));*/
    }
}
