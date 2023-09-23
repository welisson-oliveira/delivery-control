```sh 
    kind create cluster --name k8s --config=./cluster-config.yml 
    kubectl apply -f weavenet/weave-daemonset-k8s.yaml
    kubectl apply -f metrics-server/metrics-server.yml
    kubectl get nodes
```