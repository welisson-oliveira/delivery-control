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

    ```yml
    kind: Cluster
    apiVersion: kind.x-k8s.io/v1alpha4
    nodes:
    - role: control-plane
      extraPortMappings:
      - containerPort: 30000
        hostPort: 30000
        protocol: TCP
    ```

6. Configurar o Metrics-server
    
    6.1. Baixar o manifesto: ``` wget https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml ```
    
    6.2. Remomeie o manifesto ``` mv components.yaml metrics-server.yml ``` 

    6.3. Altere o manifesto metrics-server.yml. Adicione ao field Deployment no container.args a linha:
        
      ```yml
      ...
      containers:
        - args:
          - --kubelet-insecure-tls ao container.args
      ...
      ``` 
      6.4. Aplique o manifesto ``` kubectl apply -f metrics-server.yml ```

7. Criar o LimitRange ``` kubectl apply -f ./container-limitrange.yml ```
    ```yml
    apiVersion: v1
    kind: LimitRange
    metadata:
      name: container-limit-range
    spec:
      limits:
        - max:
            cpu: "800m"
            memory: 900Mi
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

8. Criar os manifestos para o postgres: ```kubectl apply -f postgres/```
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
    
    #### postgres-deployment.yml
    ```yml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
    name: postgres-deployment
    spec:
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
                cpu: "500m"
            ports:
            - containerPort: 5432
            envFrom:
            - secretRef:
                name: postgres-secret
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

9. Criar os manifestos para o postgres: ``` kubectl apply -f redis/ ```
    
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

    #### redis-deployment.yml
    ```yml
    apiVersion: apps/v1
    kind: Deployment
    metadata:
      name: redis-deployment
    spec:
      selector:
        matchLabels:
          app: redis-deployment
      template:
        metadata:
          labels:
            app: redis-deployment
        spec:
          containers:
          - name: redis-deployment
            image: redis:5.0-rc
            resources:
              limits:
                memory: "128Mi"
                cpu: "500m"
            ports:
            - containerPort: 6379
            envFrom:
              - configMapRef:
                  name: redis-configmap
    ```

    - verifique a conexão com o banco:
      - ``` kubectl run -i --tty --image redis:5.0-rc redis-cli-test --restart=Never --rm -- /bin/bash ```
      - ``` redis-cli -h redis-clusterip -p 6379 ```

10. Criar os manifestos para a api: ``` kubectl apply -f api/ ```
    #### default-namespace-resourcequota.yml
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
          containers:
          - name: delivery-control-deployment
            image: welissonoliveira/delivery-control:latest
            resources:
              requests:
                memory: "500Mi"
                cpu: "10m"
              limits:
                memory: "1500Mi"
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
    ```
    #### delivery-control-loadbalancer.yml
    ```yml
    apiVersion: v1
    kind: Service
    metadata:
      name: delivery-control-loadbalancer
    spec:
      selector:
        app: delivery-control-deployment
      ports:
      - port: 8080
        targetPort: 8080
        nodePort: 30000
      type: LoadBalancer
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
      maxReplicas: 3
      metrics:
      - type: Resource
        resource:
          name: cpu
          target:
            type: Utilization
            averageUtilization: 50

    ```

---


