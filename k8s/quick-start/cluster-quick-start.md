``` sh 
    kind create cluster --name k8s --config=./cluster-config.yml 
    kubectl apply -f ingress/deploy.yaml
    kubectl apply -f weavenet/weave-daemonset-k8s.yaml
    # kubectl apply -f metrics-server/metrics-server.yml
    kubectl get nodes
    sleep 1m
    kubectl get nodes

    kubectl create -f prometheus/kube-prometheus-stack/kube-prometheus/manifests/setup
    kubectl apply -f prometheus/kube-prometheus-stack/kube-prometheus/manifests

    sleep 1m
    kubectl apply -f quick-start/values.yml

    kubectl delete networkpolicy/alertmanager-main -n monitoring
    kubectl delete networkpolicy/prometheus-k8s -n monitoring
    kubectl delete networkpolicy/grafana -n monitoring

    kubectl apply -f prometheus/kube-prometheus/delivery-control-servicemonitor.yml
    kubectl apply -f prometheus/kube-prometheus/delivery-control-podmonitor.yml
    kubectl apply -f prometheus/kube-prometheus/delivery-control-prometheusrule.yml

    kubectl patch alertmanager main -n monitoring --type merge -p '{"spec": {"alertmanagerConfigSelector": {"matchLabels": {"alertmanagerConfig": "main"}}}}'
    kubectl patch alertmanager main -n monitoring --type merge -p '{"spec": {"replicas": 1}}'

    kubectl patch prometheus k8s -n monitoring --type merge -p '{"spec": {"replicas": 1}}'

    kubectl cp /home/welisson/projetos-estudos/delivery-control/alertmanager.yml alertmanager-main-0:/etc/alertmanager/alertmanager.yml -c alertmanager

    kubectl create namespace loki
    helm upgrade --install loki grafana/loki-stack --namespace loki
    helm list -n loki
```