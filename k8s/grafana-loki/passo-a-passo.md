1. Instalação: https://artifacthub.io/packages/helm/grafana/loki-stack

    * Adicionando o repo
        ```sh
        helm repo add grafana https://grafana.github.io/helm-charts
        helm repo update
        ```

    * Para personalizar a instalação: `helm show values grafana/loki-stack > grafana-loki/values.yml`

    * Criar um namespace: `kubectl create namespace loki`

    * Executar a instalação: `helm upgrade --install loki grafana/loki-stack --values ./grafana-loki/values.yml --namespace loki`

    * Verificar se foi instalado com sucesso: `helm list -n loki`

    * Pegar senha do usuario *admin* do grafana: `kubectl get secret --namespace loki loki-grafana -o jsonpath="{.data.admin-password}" | base64 --decode ; echo`

    * Crie um serviço para o promtail: 
        ```yml
        apiVersion: v1
        kind: Service
        metadata:
        name: promtail-clusterip
        namespace: loki
        spec:
        selector:
            app.kubernetes.io/instance: loki
            app.kubernetes.io/name: promtail
        ports:
        - port: 3101
            protocol: TCP
            targetPort: 3101
        ```
    * Crie um ingress para o promtail
        ```yml
        apiVersion: networking.k8s.io/v1
        kind: Ingress
        metadata:
        name: promtail-ingress
        namespace: loki
        labels:
            name: promtail-ingress
        spec:
        ingressClassName: nginx # verificar o ingressclass disponivel - kubectl get ingressclass
        defaultBackend:
            service: 
            name: promtail-clusterip
            port: 
                number: 3101
        rules:
        - host: promtail.127.0.0.1.nip.io
            http:
            paths:
            - pathType: Prefix
                path: "/targets"
                backend:
                service:
                    name: promtail-clusterip
                    port: 
                    number: 3101
        ```

    * Crie um ingress para o loki
        ```yml
        apiVersion: networking.k8s.io/v1
        kind: Ingress
        metadata:
        name: loki-ingress
        namespace: loki
        labels:
            name: loki-ingress
        spec:
        ingressClassName: nginx # verificar o ingressclass disponivel - kubectl get ingressclass
        defaultBackend:
            service: 
            name: loki
            port: 
                number: 3100
        rules:
        - host: loki.127.0.0.1.nip.io
            http:
            paths:
            - pathType: Prefix
                path: "/metrics"
                backend:
                service:
                    name: loki
                    port: 
                    number: 3100
        ```

2. Instalando o LogCLI: https://github.com/grafana/loki/releases
    * Encontre e baixe o arquivo para o linux
    * Extraia o binario: `unzip ~/Downloads/logcli-linux-amd64.zip`
    * Transforme o binario em executavel: ` chmod a+x logcli-linux-amd64`
    * Adicione oum alias no arquivo *~/.zshrc*
    * Atualize o bash: `source ~/.zshrc`
    * Informe a URL do loki: `export LOKI_ADDR=http://localhost:3100`
    * ✨Seja Feliz!!!✨

