# Demo EKS Cluster
* Build an [AWS EKS](https://aws.amazon.com/eks/) cluster backed by EC2 [managed node group](https://docs.aws.amazon.com/eks/latest/userguide/managed-node-groups.html) consisting of two t2.micro worker nodes.
* This demo creates a new IAM role and grants it the system:masters permissions in k8s cluster's RBAC configuration.
* You can use configure you aws client and kubectl client to use this role in order to get access to teh Kubernetes cluster.