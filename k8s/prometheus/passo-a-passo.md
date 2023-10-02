instalação: https://artifacthub.io/packages/helm/prometheus-community/prometheus

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo update

helm inspect values prometheus-community/prometheus > ./prometheus/values.yml
editar o arquivo ./prometheus/values.yml
    no service: trocar para loadbalancer e adicionar o nodePort: 30001

kubectl create namespace prometheus
helm upgrade --install prometheus prometheus-community/prometheus --values ./prometheus/values.yml --namespace prometheus

adicionar as anotations aos pods:
spec:
    template:   
        metadata:
            annotations:
                prometheus.io/scrape: "true"
                prometheus.io/path: /actuator/prometheus
                prometheus.io/port: "8081"