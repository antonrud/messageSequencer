# Message Sequencer

This project was a part of our homework for Distributed Systems class at Technical University of Berlin during the summer term 2018.

## What is does
TODO verbal description
![Source: Technical University of Berlin, Department of Telecommunication Systems, Distributed Systems class](what_it_does.png?raw=true "Source: Technical University of Berlin, Department of Telecommunication Systems, Distributed Systems class")


## How to use
1. Pack with maven:
```
mvn package
```

2. Run providing a number of nodes that you wish and a number of messages to be sent, e.g.:
```
java -jar target/vs18ha2-1.0-SNAPSHOT-jar-with-dependencies.jar 5 10
```

Logs are saved to log.txt, node memory is represented by storage/node_xxx.txt

## Autors

* **Anton Rudacov** - [@antonrud](https://github.com/antonrud)
* **Stefan Pawlowski** - [@Stefuniverse](https://github.com/Stefuniverse)
* **Hagen Anuth** - [@Hagislav](https://github.com/Hagislav)


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

