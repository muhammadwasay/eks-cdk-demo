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

import static java.util.Arrays.asList;

public class EksCdkStack extends Stack {
    public EksCdkStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EksCdkStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

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
                .minSize(1)
                .maxSize(1)
                .diskSize(8)
                .build();
    }
}
