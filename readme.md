

# Prova Finale Ingegneria del Software 2020

![SANTORINI LOGO](https://raw.githubusercontent.com/Vito96/ing-sw-2020-Monaco-Lampis-Galbiati/master/src/main/resources/images/santorini-logo.png)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  

- [Building](#building)
  - [Building with Maven](#building-with-maven)
  - [Using provided jars](#using-provided-jars)
- [Server](#server)
  - [Use our server](#use-our-server)
  - [Run your server](#run-your-server)
- [Client](#client)
  - [Comparibility notes for cli](#comparibility-notes-for-cli)
- [Documentation](#documentation)
- [Game screenshots](#game-screenshots)
- [External libraries used](#external-libraries-used)
- [Authors (alphabetical order)](#authors-alphabetical-order)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->
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

## Building
### Building with Maven
To build the project with Maven we offer the following profiles
* server : ` mvn package -Pserver `
* client : ` mvn package -Pclient `

For a faster build you can skip the unit tests activating also the profile `-Pnotest `

The output directory for the jars is set to [/deliveries/final/jar](/deliveries/final/jar)

### Using provided jars

If you want you can use the jars we already built for you, you can find them here:
[here](/deliveries/final/jar)


## Server

### Use our server
We created for you a server you can use to play [santorini40.xyz](http://santorini40.xyz)
Our default port is 1234

You can check the [logger](http://santorini40.xyz) to see the server status any time

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
### Comparibility notes for cli

Our cli version of the game automatically resizes the terminal window for a better game experience.
Most terminals allow this by default, in case you have XTerm or iTerm:  
  * XTerm:  set the following resource to true: allowWindowOps
  * iTerm2: deselect the following: Preferences > Profiles > [profile] > Terminal > Disable session-initiated window resizing
 
 Otherwise please note that the minimum required size of the terminal to play the game is 160x50
## Documentation

Javadoc is available at this link: http://santorini40.xyz/javadoc

## Game screenshots

[![asciicast](https://asciinema.org/a/328942.svg)](
  https://asciinema.org/a/328942?autoplay=1)


## External libraries used

| Library | Link 
| ----------| --------------------------------------- |
| GSON      | https://github.com/google/gson          |       
| AnimateFX | https://github.com/Typhon0/AnimateFX    |        
| word-wrap | https://github.com/davidmoten/word-wrap | 
| Mockito   | https://site.mockito.org                | 

## Authors (alphabetical order)
* [ Tiberio Galbiati](https://github.com/)
* [ Andrea Lampis ](https://github.com/)
* [ Vito Monaco](https://github.com/)

