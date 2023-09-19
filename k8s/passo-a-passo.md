# Deploy Kubernetes

1. Criar o dockerfile e executar: ``` docker build welissonoliveira/delivery-control:v1 . ```
   #### Dockerfile
    ```docker
    FROM openjdk:8-jdk-alpine
    MAINTAINER welisson
    WORKDIR /app
    COPY target/delivery-control-0.0.1-SNAPSHOT.jar /app/app.jar
    CMD ["java", "-jar", "/app/app.jar"]
    EXPOSE 8080
    ```

2. Subir para o Docker Hub: ``` docker push welissonoliveira/delivery-control:v1```

3. Criar o arquivo .env com as variaveis ambiente:
    ```
    TAG=v1
    ```

4. Criar docker-compose.yml e executar: ``` docker compose --env-file .env up -d --build ```
   #### docker-compose.yml
    ```yml 
    services:
      delivery-control:
        #    image: welissonoliveira/delivery-control:${TAG}
        build:
          context: .
          dockerfile: Dockerfile
        ports:
          - "8080:8080"
        networks:
          - delivery-control-network
        environment:
          SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-delivery-control:5432/delivery_control
          SPRING_DATASOURCE_USERNAME: delivery_control
          SPRING_DATASOURCE_PASSWORD: delivery_control
          REDIS_HOST: redis-delivery-control
          REDIS_PORT: 6379
          REDIS_URI: redis-delivery-control:6379
        depends_on:
          - redis
          - postgres
      postgres:
        image: postgres
        container_name: postgres-delivery-control
        ports:
          - "5432:5432"
        networks:
          - delivery-control-network
        environment:
          POSTGRES_USER: delivery_control
          POSTGRES_PASSWORD: delivery_control
          POSTGRES_DB: delivery_control
      redis:
        image: redis:5.0-rc
        container_name: redis-delivery-control
        ports:
          - "6379:6379"
        networks:
          - delivery-control-network
    networks:
      delivery-control-network:
        name: delivery-control-network
        driver: bridge
    ```

5. Criar cluster kubernetes ``` kind create cluster --name k8s --config=./cluster-config.yml ```

   #### cluster-config.yml
    ```yml
    kind: Cluster
    apiVersion: kind.x-k8s.io/v1alpha4
    networking:
      disableDefaultCNI: true
      podSubnet: 129.168.0.0/16
    nodes:
      - role: control-plane
        extraPortMappings:
        - containerPort: 30000
          hostPort: 80
          protocol: TCP
      - role: worker
      - role: worker
      - role: worker
      - role: worker
      - role: worker
    ```

6. Configurar o Ingress: ```kubectl apply -f ./ingress```

   6.1 baixe o arquivo de configração do ingress
   ```wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/baremetal/deploy.yaml ```

   6.2 adicione o nodePort:30000 no Service ingress-nginx-controller no arquivo deploy.yaml

7. Configurar weavenet para fornecer a rede entre os
   pods: ```kubectl apply -f https://github.com/weaveworks/weave/releases/download/v2.8.1/weave-daemonset-k8s.yaml```

8. Configurar o Metrics-server

   8.1. Baixar o
   manifesto: ``` wget https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml ```

   8.2. Remomeie o manifesto ``` mv components.yaml metrics-server.yml ```

   8.3. Altere o manifesto metrics-server.yml. Adicione ao manifesto do Deployment no container.args a linha:

   #### metrics-server.yml
      ```yml
      ...
      containers:
        - args:
          - --kubelet-insecure-tls
      ...
      ``` 
   8.4. Aplique o manifesto ``` kubectl apply -f ./delivery-control/metrics-server/metrics-server.yml ```

9. Criar o LimitRange para limitar os recursos por container no namespace
   default: ``` kubectl apply -f ./delivery-control/default-namespace-limitrange.yml ```
    ```yml
    apiVersion: v1
    kind: LimitRange
    metadata:
      name: default-namespace-limit-range
    spec:
      limits:
        - max:
            cpu: "1000m"
            memory: 2100Mi
          min:
            cpu: "150m"
            memory: 100Mi
          default:
            cpu: "250m"
            memory: "128Mi"
          defaultRequest:
            cpu: "150m"
            memory: "110Mi"
          type: Container
    ```

10. Criar o Resource quota para limitar os recursos totais do namespace
    default: ``` kubectl apply -f ./delivery-control/default-namespace-resourcequota.yml ```
    ```yml
    apiVersion: v1
    kind: ResourceQuota
    metadata:
      name: default-namespace-quota
    spec:
      hard:
        requests.cpu: "2"
        requests.memory: "4Gi"
        limits.cpu: "3"
        limits.memory: "6Gi"
    ```

11. Crie os labels para os nodes do redis
    ```kubectl
    kubectl label node k8s-worker database=redis
    kubectl label node k8s-worker2 database=redis
    ```

12. Adicione um Taint no node k8s-worker3: ``` kubectl taint nodes k8s-worker3 motivo=manutencao:NoExecute ```

13. Criar os manifestos para o redis: ``` kubectl apply -f ./delivery-control/redis/ ```

    #### redis-configmap.yml
    ```yml
    apiVersion: v1
    kind: ConfigMap
    metadata:
      name: redis-configmap
    data:
      REDIS_HOST: localhost
      REDIS_PORT: "6379"
      REDIS_URI: localhost:6379
    ```

    #### redis-pvc.yml
    ```yml
    apiVersion: v1
    kind: PersistentVolumeClaim
    metadata:
      name: redis-pvc
    spec:
      resources:
        requests:
          storage: 1Gi
      volumeMode: Filesystem
      storageClassName: redis
      accessModes:
        - ReadWriteOnce
    ```

    #### redis-pv.yml
    ```yml
    apiVersion: v1
    kind: PersistentVolume
    metadata:
      name: redis-pv
    spec:
      capacity:
        storage: 1Gi
      volumeMode: Filesystem
      accessModes:
        - ReadWriteOnce
      persistentVolumeReclaimPolicy: Retain
      storageClassName: redis
      hostPath:
        path: /volumes/redis
    ```

    #### redis-deployment.yml
    ```yml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: redis-deployment
    spec:
      replicas: 2
      selector:
        matchLabels:
          app: redis-deployment
      template:
        metadata:
          labels:
            app: redis-deployment
        spec:
          nodeSelector:
            database: redis
          containers:
          - name: redis-deployment
            image: redis:5.0-rc
            resources:
              limits:
                memory: "128Mi"
                cpu: "250m"
            ports:
            - containerPort: 6379
            envFrom:
              - configMapRef:
                  name: redis-configmap
            volumeMounts:
                - mountPath: /data/redis
                  name: redis-data
          volumes:
            - name: redis-data
              persistentVolumeClaim:
                claimName: redis-pvc
          affinity:
            podAntiAffinity:
              requiredDuringSchedulingIgnoredDuringExecution:
                - labelSelector:
                    matchExpressions:
                      - key: app
                        operator: In
                        values:
                          - redis-deployment
                          - postgres-deployment
                  topologyKey: "kubernetes.io/hostname"
            podAffinity:
              preferredDuringSchedulingIgnoredDuringExecution:
                - weight: 100
                  podAffinityTerm:
                    topologyKey: "kubernetes.io/hostname"
                    labelSelector:
                      matchExpressions:
                        - key: app
                          operator: In
                          values:
                            - delivery-control-deployment

    ```

    #### redis-clusterip.yml
    ```yml
    apiVersion: v1
    kind: Service
    metadata:
      name: redis-clusterip
    spec:
      selector:
        app: redis-deployment
      ports:
      - port: 6379
        targetPort: 6379
    ```

    - verifique a conexão com o redis:
        - ``` kubectl run -i --tty --image redis:5.0-rc redis-cli-test --restart=Never --rm -- /bin/bash ```
        - ``` redis-cli -h redis-clusterip -p 6379 ```

14. Criar os manifestos para o postgres: ```kubectl apply -f ./delivery-control/postgres/```
    #### postgres-secret.yml
    ```yml
    apiVersion: v1
    kind: Secret
    metadata:
      name: postgres-secret
    type: Opaque
    data:
      POSTGRES_USER: ZGVsaXZlcnlfY29udHJvbA==
      POSTGRES_PASSWORD: ZGVsaXZlcnlfY29udHJvbA==
      POSTGRES_DB: ZGVsaXZlcnlfY29udHJvbA==
    ```

    #### postgres-pvc.yml
    ```yml
    apiVersion: v1
    kind: PersistentVolumeClaim
    metadata:
      name: postgres-pvc
    spec:
      resources:
        requests:
          storage: 1Gi
      volumeMode: Filesystem
      storageClassName: postgres
      accessModes:
        - ReadWriteOnce
    ```

    #### postgres-pv.yml
    ```yml
    apiVersion: v1
    kind: PersistentVolume
    metadata:
      name: postgres-pv
    spec:
      capacity:
        storage: 1Gi
      volumeMode: Filesystem
      accessModes:
        - ReadWriteOnce
      persistentVolumeReclaimPolicy: Retain
      storageClassName: postgres
      hostPath:
        path: /var/lib/postgresql/data
    ```

    #### postgres-deployment.yml
    ```yml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: postgres-deployment
    spec:
      replicas: 5
      selector:
        matchLabels:
          app: postgres-deployment
      template:
        metadata:
          labels:
            app: postgres-deployment
        spec:
          containers:
            - name: postgres-deployment
              image: postgres
              resources:
                limits:
                  memory: "128Mi"
                  cpu: "250m"
              ports:
                - containerPort: 5432
              envFrom:
                - secretRef:
                    name: postgres-secret
              volumeMounts:
                - mountPath: /data/postgresql
                  name: postgres-data
          volumes:
            - name: postgres-data
              persistentVolumeClaim:
                claimName: postgres-pvc
          affinity:
            podAntiAffinity:
              preferredDuringSchedulingIgnoredDuringExecution:
                - weight: 100
                  podAffinityTerm:
                    labelSelector:
                      matchExpressions:
                      - key: app
                        operator: In
                        values:
                          - postgres-deployment
                    topologyKey: "kubernetes.io/hostname"
              requiredDuringSchedulingIgnoredDuringExecution:
                - labelSelector:
                    matchExpressions:
                      - key: app
                        operator: In
                        values:
                          - redis-deployment
                  topologyKey: "kubernetes.io/hostname"
    ```

    #### postgres-clusterip.yml
    ```yml
    apiVersion: v1
    kind: Service
    metadata:
      name: postgres-clusterip
    spec:
      selector:
        app: postgres-deployment
      ports:
        - port: 5432
          targetPort: 5432
    ```

    - verifique a conexão com o banco:
        - ``` kubectl run -i --tty --image postgres psql-test --restart=Never --rm -- /bin/bash ```
        - ``` psql -h postgres-clusterip -p 5432 -U delivery_control -d delivery_control ```

15. Criar um Service Account e role para a api: ```kubectl apply -f ./delivery-control/api/service-account```

    #### service-account.yml
    ```yml
    apiVersion: v1
    kind: ServiceAccount
    metadata:
      name: delivery-control-user
    ```

    #### role.yml
    ```yml
    apiVersion: rbac.authorization.k8s.io/v1
    kind: Role
    metadata:
      name: delivery-control-role
    rules:
      - apiGroups:
          - ""
          - "apps"
        resources:
          - services
        verbs:
          - get
          - list
          - watch
    ```

    #### role-binding.yml
    ```yml
    apiVersion: rbac.authorization.k8s.io/v1
    kind: RoleBinding
    metadata:
      name: delivery-control-bind
    subjects:
    - kind: ServiceAccount
      name: delivery-control-user
      namespace: default
    roleRef:
      kind: Role
      name: delivery-control-role
      apiGroup: rbac.authorization.k8s.io
    ```

16. Criar os manifestos para a api: ``` kubectl apply -f ./delivery-control/api/ ```
    #### delivery-control-configmap.yml
    ```yml
    apiVersion: v1
    kind: ConfigMap
    metadata:
      name: delivery-control-configmap
    data:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres-clusterip:5432/delivery_control
      REDIS_HOST: redis-clusterip
      REDIS_URI: redis-clusterip:6379
    ```
    #### delivery-control-deployment.yml
    ```yml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: delivery-control-deployment
    spec:
      selector:
        matchLabels:
          app: delivery-control-deployment
      template:
        metadata:
          labels:
            app: delivery-control-deployment
        spec:
          serviceAccountName: delivery-control-user
          initContainers:
            - name: init-pod-postgres
              image: busybox:1.28
              command: ["sh", "-c", "until nslookup postgres-clusterip.default.svc.cluster.local; do echo Aguardando o Postgres; sleep 2; done"]
            - name: init-pod-redis
              image: busybox:1.28
              command: ["sh", "-c", "until nslookup redis-clusterip.default.svc.cluster.local; do echo Aguardando o Redis; sleep 2; done"]
          containers:
          - name: delivery-control-deployment
            image: welissonoliveira/delivery-control:latest
            resources:
              requests:
                memory: "700Mi"
                cpu: "150m"
              limits:
                memory: "2100Mi"
                cpu: "1000m"
            ports:
            - containerPort: 8080
            envFrom:
              - configMapRef:
                  name: delivery-control-configmap
              - secretRef:
                  name: postgres-secret
            env:
              - name: REDIS_PORT
                valueFrom:
                  configMapKeyRef:
                    name: redis-configmap
                    key: REDIS_PORT
            # Self Healing
            startupProbe:
              httpGet:
                path: /startup-check
                port: 8080
                scheme: HTTP
              initialDelaySeconds: 3
              periodSeconds: 3
              timeoutSeconds: 1
              failureThreshold: 30
            readinessProbe:
              httpGet:
                path: /readiness-check
                port: 8080
                scheme: HTTP
              initialDelaySeconds: 3
              periodSeconds: 3
              timeoutSeconds: 1
              failureThreshold: 1
            livenessProbe:
              httpGet:
                path: /health-check
                port: 8080
                scheme: HTTP
              initialDelaySeconds: 3
              periodSeconds: 3
              timeoutSeconds: 1
              failureThreshold: 1
            # Self Healing end
            imagePullPolicy: Always
            # lifecycle:
            #   postStart:
            #     exec:
            #       command:
            #         - "curl"
            #         - "-X"
            #         - "GET"
            #         - "https://jsonplaceholder.typicode.com/comments"
            #   preStop:
            #     exec:
            #       command:
            #         - "curl"
            #         - "-X"
            #         - "GET"
            #         - "https://jsonplaceholder.typicode.com/photos"
          affinity:
            podAffinity:
              preferredDuringSchedulingIgnoredDuringExecution:
                - weight: 100
                  podAffinityTerm:
                    topologyKey: "kubernetes.io/hostname"
                    labelSelector:
                      matchExpressions:
                        - key: app
                          operator: In
                          values:
                            - "redis-deployment"
            podAntiAffinity:
              requiredDuringSchedulingIgnoredDuringExecution:
                - labelSelector:
                    matchExpressions:
                      - key: app
                        operator: In
                        values:
                          - delivery-control-deployment
                  topologyKey: "kubernetes.io/hostname"
    ```
    #### delivery-control-clusterip.yml
    ```yml
    apiVersion: v1
    kind: Service
    metadata:
      name: delivery-control-clusterip
    spec:
      selector:
        app: delivery-control-deployment
      ports:
      - port: 8080
        targetPort: 8080
    ```
    #### delivery-control-hpa.yml
    ```yml
    apiVersion: autoscaling/v2
    kind: HorizontalPodAutoscaler
    metadata:
      name: delivery-control-hpa
    spec:
      scaleTargetRef:
        apiVersion: apps/v1
        kind: Deployment
        name: delivery-control-deployment
      minReplicas: 1
      maxReplicas: 10
      metrics:
      - type: Resource
        resource:
          name: cpu
          target:
            type: Utilization
            averageUtilization: 75
    ```
    #### delivery-control-ingress.yml
    ```yml
    apiVersion: networking.k8s.io/v1
    kind: Ingress
    metadata:
      name: delivery-control-ingress
      labels:
        name: delivery-control-ingress
    spec:
      ingressClassName: nginx # verificar o ingressclass disponivel - kubectl get ingressclass
      defaultBackend:
        service: 
          name: delivery-control-clusterip
          port: 
            number: 8080
      rules:
      - host: 127.0.0.1.nip.io
        http:
          paths:
          - pathType: Prefix
            path: "/"
            backend:
              service:
                name: delivery-control-clusterip
                port: 
                  number: 8080
    ```

17. Criar a policy para o postgres: ```kubectl apply -f ./delivery-control/postgres/postgres-policy.yml```
    #### postgres-policy.yml
    ```yml
    apiVersion: networking.k8s.io/v1
    kind: NetworkPolicy
    metadata:
      name: postgres-policy
    spec:
      podSelector:
        matchLabels: 
          app: postgres-deployment
      policyTypes:
        - Egress
      ingress:
        - from:
          - podSelector:
              matchLabels:
                app: delivery-control-deployment
          ports:
          - port: 5432
          - protocol: TCP
      egress:
        - to:
          - podSelector:
              matchLabels:
                app: delivery-control-deployment
          ports:
          - port: 5432
          - protocol: TCP
    ```


18. Criar a policy para o redis: ```kubectl apply -f ./delivery-control/redis/redis-policy.yml```
    #### redis-policy.yml
    ```yml
    apiVersion: networking.k8s.io/v1
    kind: NetworkPolicy
    metadata:
      name: redis-policy
    spec:
      podSelector:
        matchLabels: 
          app: redis-deployment
      policyTypes:
        - Egress
      ingress:
        - from:
          - podSelector:
              matchLabels:
                app: delivery-control-deployment
          ports:
          - port: 6379
          - protocol: TCP
      egress:
        - to:
          - podSelector:
              matchLabels:
                app: delivery-control-deployment
          ports:
          - port: 6379
          - protocol: TCP
    ```

---


