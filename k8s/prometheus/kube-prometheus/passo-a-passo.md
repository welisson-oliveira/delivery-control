https://github.com/badtuxx/DescomplicandoPrometheus

1. Instalação
    * Clone o repositório do kube-prometheus:
        - `sh git clone https://github.com/prometheus-operator/kube-prometheus `

        - `cd kube-prometheus/kube-prometheus`

    * Aplica os manifestos necessários para a instalação do kube-prometheus, Basicamente o que fizemos foi a instalação de alguns CRDs (Custom Resource Definitions) que são como extensões do Kubernetes:
        - ` kubectl create -f manifests/setup `

    * Aplica os manifestos necessários para a instalação do Prometheus e do Alertmanager
        - ` kubectl apply -f manifests `

2. Acessando os serviços (grafana, prometheus e alertmanager):

    ```kubectl
    kubectl port-forward -n monitoring svc/grafana 33000:3000
    kubectl port-forward -n monitoring svc/prometheus-k8s 39090:9090
    kubectl port-forward -n monitoring svc/alertmanager-main 39093:9093
    ```

3. Criando um ServiceMonitor para monitorar um serviço: ` kubectl apply -f prometheus/kube-prometheus/delivery-control-servicemonitor.yml  `
    ```yml
    apiVersion: monitoring.coreos.com/v1
    kind: ServiceMonitor
    metadata:
    name: delivery-control-servicemonitor
    labels:
        app: delivery-control
    spec:
    selector: # seletor para identificar os pods que serão monitorados
        matchLabels: # labels que identificam os pods que serão monitorados
        app: delivery-control-deployment # label que identifica o app que será monitorado
    endpoints: # endpoints que serão monitorados
        - interval: 10s # intervalo de tempo entre as requisições
        path: /actuator/prometheus # caminho para a requisição
        targetPort: 8081 # porta do target
    ```
* Verificar se o Prometheus esta capturando as métricas: `curl http://localhost:39090/api/v1/targets | jq . | grep delivery-control-deployment`

4. Criando um PodMonitor para monitorar um pod: ` kubectl apply -f prometheus/kube-prometheus/delivery-control-podmonitor.yml  `
    ```yml
    apiVersion: monitoring.coreos.com/v1
    kind: PodMonitor
    metadata:
    name: delivery-control-podmonitor
    labels:
        app: delivery-control-pod
    spec:
    namespaceSelector: # seletor de namespaces a serem monitorados
        matchNames: # namespaces que serão monitorados
        - default # namespace que será monitorado
    selector: # seletor para identificar os pods que serão monitorados
        matchLabels: # labels que identificam os pods que serão monitorados
        app: delivery-control-deployment
    podMetricsEndpoints: # endpoints que serão monitorados
        - interval: 10s # intervalo de tempo entre as requisições
        path: /actuator/prometheus # caminho para a requisição
        targetPort: 8081 # porta do target
    ```
    * Obs: delivery-control-podmonitor deve aparecer nos targets do prometheus

5. Criando um Alerta:
    * Definindo o PrometheusRule ` kubectl apply -f prometheus/kube-prometheus/delivery-control-prometheusrule.yml `
        ```yml
        apiVersion: monitoring.coreos.com/v1
        kind: PrometheusRule
        metadata:
        name: delivery-control-prometheus-rule
        namespace: monitoring
        labels: # Labels do recurso
            prometheus: k8s # Label que indica que o PrometheusRule será utilizado pelo Prometheus do Kubernetes
            role: alert-rules # Label que indica que o PrometheusRule contém regras de alerta
            app.kubernetes.io/name: kube-prometheus # Label que indica que o PrometheusRule faz parte do kube-prometheus
            app.kubernetes.io/part-of: kube-prometheus # Label que indica que o PrometheusRule faz parte do kube-prometheus
        spec:
        groups: # Lista de grupos de regras
        - name: delivery-control-prometheus-rule # Nome do grupo de regras
            rules: # Lista de regras
            - alert: DeliveryControlToManyRequests # Nome do alerta
            expr: rate(http_server_requests_seconds_count{method="GET",uri="/startup-check"}[10m]) > 0.02 # Expressão que será utilizada para disparar o alerta
            for: 5s
            labels:
                team: delivery-control
                severity: warning
            annotations: # Anotações do alerta
                summary: "Muitas Consultas" # Título do alerta
                description: "Esse recurso {{ $labels.instance }} está com muitas consultas ({{ $value }})" # Descrição do alerta
            - alert: DeliveryControlDown # Nome do alerta
            expr: up{container="delivery-control-deployment"} == 0 # Expressão que será utilizada para disparar o alerta
            for: 5s # Tempo que a expressão deve ser verdadeira para que o alerta seja disparado
            labels: # Labels do alerta
                team: delivery-control
                severity: critical # Label que indica a severidade do alerta
            annotations: # Anotações do alerta
                summary: "DeliveryControl is down" # Título do alerta
                description: "DeliveryControl está fora do ar por mais de 5s. Nome do pod {{ $labels.pod }}" # Descrição do alerta
        ```
    * Obs: deve aparecer na guia de alerts do prometheus
    * Execute esses comandos para testar tomanyrequests:
        ```kubernetes
        kubectl run -i --tty --image yauritux/busybox-curl curl-test --restart=Never --rm -- /bin/sh
        while true; do curl http://delivery-control-clusterip:8081/startup-check; sleep 1; done;
        ```
    
    * Baixe o manifesto do alertmanager: ` kubectl get alertmanager -n monitoring main -o yaml > prometheus/kube-prometheus/alertmanager/alertmanager.yml `
    * Adicione ao spec:
        ```yml
        ...
        alertmanagerConfigSelector:
            matchLabels:
                alertmanagerConfig: main # label do seu alertmanager config
        ...
        ```
    * Applique o manifesto: ` kubectl apply -f prometheus/kube-prometheus/alertmanager/alertmanager.yml `
    * Criando o alertmanager-config com as configurações de alertas ` kubectl apply -f prometheus/kube-prometheus/alertmanager/alertmanager-config.yml `
        ```yml
        apiVersion: monitoring.coreos.com/v1alpha1
        kind: AlertmanagerConfig
        metadata:
        name: delivery-control-alert-config
        namespace: monitoring
        labels:
            alertmanagerConfig: main
        spec:
        route:
            groupBy: [ 'alertname' ]
            groupWait: 15s
            groupInterval: 15s
            repeatInterval: 1m
            receiver: Default
            routes:
            - receiver: AlertaDeliveryControl
                match:
                team: delivery-control

        receivers:
            - name: Default
            webhookConfigs: # pode ser webhook, email, slack, teams
                - url: 'https://pruu.herokuapp.com/dump/wpwebhook-default'
            - name: AlertaDeliveryControl
            webhookConfigs: # pode ser webhook, email, slack, teams
            - url: 'https://pruu.herokuapp.com/dump/wpwebhook-delivery-control'

        ```


---

watch -n 1 ls -la src/main/resources/logs/files/
while true; do curl http://localhost:8081/log; sleep 1; done;