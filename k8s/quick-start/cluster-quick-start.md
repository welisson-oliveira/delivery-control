``` sh 
    kind create cluster --name k8s --config=./cluster-config.yml 
    kubectl apply -f ingress/deploy.yaml
    kubectl apply -f weavenet/weave-daemonset-k8s.yaml
    kubectl apply -f metrics-server/metrics-server.yml
    kubectl get nodes
    sleep 1m
    kubectl get nodes
    kubectl apply -f quick-start/values.yml
```