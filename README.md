# J6Crypto Engine
J6Crypto is backend engine to run user defined trading strategy. Each trading strategy will start a bot instance to run in kubernete pod. 1 pod may run multiple trading bots with spring boot. 

# J6Crypto Engine Architecture
![J6crypto Engine Architecture](./doc/j6crypto.PNG?raw=true)

## Existing Quantitative Trading Logic
There are 2 existing main trading logic's.
![Rebound Martingale Trading Logic](./doc/reboundmartingale.PNG?raw=true)
![Break Resistance Trading Logic](./doc/breakresistance.PNG?raw=true)

## Why Zookeeper
* Why Zookeeper service mesh and not kubernetes 
1. Local development environmet is same as cloud environment.
2. Architecture not depend on kubernete.
3. Since Kafka is using it.
4. Ready to use more features from zookeeper.
5. Centralised config/data to enable decentralise microservices/logic.


## Local Setup
1. Download kafka_2.13-2.8.0
2. Start Zookeeper
```
cd C:\dev\tools\kafka_2.13-2.8.0\bin\windows
zookeeper-server-start.bat ..\..\config\zookeeper.properties
```
3. Start Kafka 
```
cd C:\dev\tools\kafka_2.13-2.8.0\bin\windows
kafka-server-start.bat ..\..\config\server.properties
```
4. Checkout this repo and run
```
./mvn spring-boot:run
```
4. Checkout j6crypto-gateway repo and run
```
./mvn spring-boot:run
```

## Kubernetes Setup
```
kubectl create -f c:\workspace\j6crypto-engine\kube\zookeeper.yml --namespace=j6crypto
kubectl scale deployment.apps/zookeeper --replicas=1 --namespace=j6crypto

kubectl create -f c:\workspace\j6crypto-engine\kube\kafka.yml --namespace=j6crypto
kubectl scale deployment.apps/kafka --replicas=1 --namespace=j6crypto

kubectl create -f c:\workspace\j6crypto-engine\j6crypto-engine.yml --namespace=j6crypto
kubectl scale deployment.apps/j6crypto-engine --replicas=1 --namespace=j6crypto
 
kubectl create -f c:\workspace\j6crypto-gateway\j6crypto-gateway.yml --namespace=j6crypto 
kubectl scale deployment.apps/j6crypto-gateway --replicas=1 --namespace=j6crypto

kubectl apply -f c:\workspace\j6crypto-engine\kube\public-ingres.yml
```

kubectl get pods<br/>
![J6crypto Engine Architecture](./doc/getpods.PNG?raw=true)

## Spring boot application starter
1. J6CryptoAllApp - All in one starter.
2. J6CryptoApiApp - Start Rest API only.
3. J6CryptoEngineApp - Start crypto engine only.
4. J6CryptoProducerApp - Start Coin producer only. It pull data from binance exchange, etc.
5. J6CryptoEngineLocalTestApp - Dev testing trading logic 


## Customisation on zookeeper
1. Auto assign spring.application.instance_id. instance_id must be same as DB Table field AutoTradeOrder.ms_id. Thus, microservices are able to restart and restore the unassigned AutoTradeOrder from DB. If no unassigned ATO, new max msId + 1 will be assign to this microservice.
2. Edit **apache curator** zookeeper caller ServiceDiscoveryImpl.java line 175 to throw error when zookeeper registers new instance by using existing instance_id/msId. This is needed becaue pods may start at the same time. Currently it will delete existing service/node in zookeeper and replace with new service/node.


## Entity Relationship
![J6crypto Entity Relationship](./doc/entity.PNG?raw=true)

## J6Crypto-Test
This is API Testing framework. Run [EngineApiTest.java](./src/test/java/com/j6crypto/engine/EngineApiTest.java) for testing.
![junit](./doc/junit.PNG?raw=true)

## Roadmap
Auto adjust/assign pod crypto-engine base on memory and cpu usage by using Spring Boot Actuator.


