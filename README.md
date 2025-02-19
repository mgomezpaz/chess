# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

([https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIcDcujlZfF5vD59PoHYsgoU+YlsOtzvhMn76K8SwfgC5wFrKaooOUCAHjysL7oeqLorE2IJoYLphm6ZIUgatJvqMJpEuGFocjA3K8gagrCjAoriq60pJpeSHlEx2iOs63FOvKMAlEgABmYRfksAmJgSREsuUnrekGAZBiGHHmpGdHRjAsbBvx+GdsBaaYTy2a5pgJmdgBVyviOowfjWQazs2EH-leIKnNkPYwP2g69PZYGORBzl1o2bm-gcphGVxZxAiWfreMkfLBEG0BIAAXigHAAKIAB4qNgBCJFZ8EdnFtmln0ozqMA5IrLlUDeGmPhpX6GXZcsHnUBV3nnmA5QACxOAAjC+tWqPV9x9E1LXlG106dTlf5mJwq7eH4gReCg6B7gevjMMe6SZJgPkXkUvVphU0i5buuX1LlzQtA+qhPt0LBehwJZLVAWXxIk5QADzTq5aD5O2gHxam5SgxFaBlQlUP4ShB2+hhaNgNhGJ4XKBHydRxEwOSYD6Wp4VzlRTJurR5QMTG6naEKYRw3OoaE5xiHCchemM-Isn4zICmkkYKDcJkZOs+gVNmhGhSWjIYsUoY+nxnjxnlaZmMWQgeaazZnlXD1xxxedg1+QOQ7RbFyaa+USUQCllLtaky15YVKDFU+iOpgbV12ZN02Nc10ALS7burQBnZm8NY0TSyQcTHNoeseHf1daty4beugSQrau7QjAADio6ssdp5nQNbJVRURcPc99ijh9X0-el6cA2gwNS+DkNsiZsMufDPsIUJ4IwMgsQl7VGHQlPajY7hAsasL7rExSkuD5T7PU5x8t0fTvNxkzLHd9vst+9z5Sq4Z6uERzIsT2Ac+qLCMs0dp5RFxSMDAMqxejg6TSEYuZjwLrEIGc9GQQxtkBO2e5Z6lx1nrJGF8QJ9EbrVcYFR+gYJQAASWkOMUavYADMQ0ngnkyAaCsoUpg6AQKABs1DwK-ieLggAciwv8jRjZ9UKDHC2AUcGlywS4MRPDTBLziv3GADsnZpyyjlAqRUSoI2spVQ21VA4NSTiHVqCiM68K8vwgasdxqBW0TNZO+jfqKO6mtFcnhNobmwD4KA2BuDwF1Jkf+owUgnTPDkZgXNry1AaA3JuaBPrAG+mmWxmUO5d03ugfIL4OFcKMX3OB3dZjCNGJwkKUVh4VVHiJZSmQ56wjgN4lAc8F5YikQTHeIsSYbwptLM+78950x5AzI+8hmY-2SQjIBF8x7X35rFJpsslI1MqekyinSaYfz0jaXxKBAErzGSJXBBCpG2yRuUapXoKmjiQcU4xl0Sh2V2YQ0oxCyGZNNqYwRaTRwEKIaQoa1tb4HJhrI1IyVUrxKUR7L2pV1EhLTP0Sxwd5qpxBfYqOzyglmPjnVHRs09Fh0RZndaTic4BEsGLVCKUABSEAeTrMCPQxhlcgnV00dUSkd4Wi4ObjE1uHV26qKSe08GL4PHAGJVAOAEBUJQBWAAdRYHgx6LQABCu4FBwAANJfFuZ8x5vc-nAgHvy3JzwGEirFRKpyUxNX3K+XBFBICRIACtKVoEqRSnkdSUBohxo0oW99V6tNUt3N+yzun0V6YfAyAyT7DKWZzUpPMJnAG9UAkiT9RywluUG3e7Iem8kgcfMItyY1aTtTzPNkzb5xvKGJSSMAQDiugD-KgvxdDcCTSvBa2AtCnNGOm950hZhsRQLMOtErM1aRDZSTtep1lqyQhrQ5MBXXOrOWoSykKhIgSef1VFrzeg-LndIuBcjgVtzscoz2qiLmoOhTVBOmLrE4tPYYnV27fIjXMTCu9VjsUIqfStfd3N53-OPQoBAuoOCWHqMAOc57wVqP1ho-2Wiv1wpTr-cDYR5Bzkjp5aOLyRokPRVNe9P70NfUw9B5sS58Vri2gELwwquxelgMAbAHjCAd38RXM2jKkM3Tug9J6rRjA6tgQukA3A8AwEhJ62IV6S1KUk3CXGB7pnhkU8x1+Ra5bZsVuLQwv8EBGBvge3VJZEDMfOeuq5m6X0mJ3f5Us0VRmIcvgCoFQDYOXpgdDPV7nHY+uaaoLz3trNVU-Ri798LvqqB1MQVRMAIDiRgJQFqOGrp4Z3QAVkthYlDujotqDi3BxLyXUvQDxY4oAA))

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
