## Alura Ecommerce - Microservices with Java Spring Boot

``kafka-topics.sh --create 
    --bootstrap-server [server]:[port] 
    --replication-factor 1  
    --partitions 1 
    --topic [name]`` 

``kafka-topics.sh --list 
    --bootstrap-server [server]:[port]``

``kafka-topics.sh --describe
    --bootstrap-server [server]:[port]``

``kafka-topics.sh --alter
    --bootstrap-server [server]:[port]
    --topic [name]
    --patitions 3``

``kafka-console-producer.sh 
    --broken-list [server]:[port]
    --topic [name]``

``kafka-console-consumer.sh
    --bootstrap-server [server]:[port]
    --topic [name]
    --from-beginning``

``kafka-consumer-groups.sh --all-groups
    --bootstrap-server [server]:[port]
    --describe``

