# Prova Finale Ingegneria del Software 2020

![SANTORINI LOGO](https://raw.githubusercontent.com/Vito96/ing-sw-2020-Monaco-Lampis-Galbiati/master/src/main/resources/images/santorini-logo.png)


| Functionality | State |
|:--------------------------------------|:------------------------------------:|
| Basic rules                           | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Complete rules                        | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| CLI                                   | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| GUI                                   | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Socket                                | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Advanced Function: multiple matches   | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
| Advanced Function: persistence        | [![GREEN](https://placehold.it/15/44bb44/44bb44)](#) |
<!--
[![RED](https://placehold.it/15/f03c15/f03c15)](#)
[![YELLOW](https://placehold.it/15/ffdd00/ffdd00)](#)
[![GREEN](https://placehold.it/15/44bb44/44bb44)](#)
-->

## Building with Maven
To build the project with Maven we offer the following profiles:
* server : run maven deploy to build server.jar
* client : run maven deploy to build client.jar


## Using provided jars

If you want you can use the jars we already built for you, you can find them here:
[here](/deliveries/final/jar)
##Running
here are the instructions to run the game

### Server
If you want to run the server on your machine you can use the following command
```bash
java -jar server.jar
```
You can also play on our server reachable at:
santorini40.xyz:1234


## Client 
default loads the gui, if you wanna use our CoolCli please add cli
```bash
java -jar client.jar [cli] 
```

## Documentation

Javadoc is available at this link: http://santorini40.xyz/javadoc

## Screenshots

### Gameplay 

[![asciicast](https://asciinema.org/a/328942.svg)](
  https://asciinema.org/a/328942?autoplay=1)


### External libraries used

| Library | Link | Use |
| ----------| --------------------------------------- | ------ |
| GSON      | https://github.com/google/gson          |        |
| AnimateFX | https://github.com/Typhon0/AnimateFX    |        |
| word-wrap | https://github.com/davidmoten/word-wrap | 
| Mockito   | https://site.mockito.org                | 

## Authors
* [ Vito Monaco](https://github.com/)
* [ Andrea Lampis ](https://github.com/)
* [ Tiberio Galbiati](https://github.com/)

## License

