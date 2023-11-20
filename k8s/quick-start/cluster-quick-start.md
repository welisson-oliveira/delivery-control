``` sh 
    kind create cluster --name k8s --config=./cluster-config.yml 
    kubectl apply -f ingress/deploy.yaml
    kubectl apply -f weavenet/weave-daemonset-k8s.yaml
    kubectl apply -f metrics-server/metrics-server.yml
    kubectl get nodes
    sleep 2m
    kubectl get nodes

    kubectl create namespace loki
    helm upgrade --install loki grafana/loki-stack --namespace loki
    helm list -n loki

    kubectl apply -f quick-start/values.yml
```