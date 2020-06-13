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
To build the project with Maven we offer the following profiles
* server : ` mvn package -Pserver `
* client : ` mvn package -Pclient `

For a faster build you can skip the unit tests activating also the profile `-Pnotest `

The output directory for the jars is set to [/deliveries/final/jar](/deliveries/final/jar)

## Using provided jars

If you want you can use the jars we already built for you, you can find them here:
[here](/deliveries/final/jar)


##Running
Here are the instructions to run the game

### Use our server
We created for you a server you can use to play [santorini40.xyz](http://santorini40.xyz)
Please use port 1234.

### Run your server
If you want to run the server on your machine you can use the following command
```bash
java -jar server.jar
```

## Client 
Default loads the gui, if you wanna use our CoolCli please add cli
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

