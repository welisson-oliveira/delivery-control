https://github.com/badtuxx/DescomplicandoPrometheus


clone o repositório do kube-prometheus
    git clone https://github.com/prometheus-operator/kube-prometheus
    cd kube-prometheus

Aplica os manifestos necessários para a instalação do kube-prometheus, Basicamente o que fizemos foi a instalação de alguns CRDs (Custom Resource
Definitions) que são como extensões do Kubernetes:
    kubectl create -f manifests/setup

Aplica os manifestos necessários para a instalação do Prometheus e do Alertmanager
    kubectl apply -f manifests