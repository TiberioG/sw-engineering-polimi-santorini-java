# Prova Finale Ingegneria del Software 2020

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  

- [Features implemented](#features-implemented)
- [Building](#building)
  - [Building with Maven](#building-with-maven)
  - [Using provided jars](#using-provided-jars)
- [Server](#server)
  - [Use our server](#use-our-server)
  - [Run your server](#run-your-server)
- [Client](#client)
  - [Compatibility notes for cli](#compatibility-notes-for-cli)
- [Documentation](#documentation)
  - [Javadoc](#javadoc)
  - [UML diagrams](#uml-diagrams)
  - [Coverage report](#coverage-report)
- [Gameplay screenshots](#gameplay-screenshots)
- [External libraries used](#external-libraries-used)
- [Authors (alphabetical order)](#authors-alphabetical-order)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Features implemented
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

If you want you can use the jars we already built for you, you can find them [here](/deliveries/final/jar)


## Server

### Use our server
We created for you a server you can use to play [santorini40.xyz](http://santorini40.xyz)
Our default port is 1234

You can check the [logger](http://santorini40.xyz) to see the server status any time

### Run your server
If you want to run the server on your machine you can use the following command, this is the easiest way to run it
```bash
java -jar server.jar
```
If your're planning to run the server jar on a real server, please edit the [service.sh](/deliveries/final/jar/service.sh) script, editing the following paths and port: 
```bash
PATH_TO_JAR=/path/to/server.jar
PATH_TO_LOGFILE=/path/to/logfile.log
PID_PATH_NAME=/path/to/pid-file
PORT=xxxx
```
Don't forget to `chmod +x ./service.sh` and then you can start, stop and restart the server using the following command which automatically creates a new thread, and ignores the HUP
```bash
./service.sh [ start | stop | restart ]
```

## Client 
By default, the client jar loads the GUI, if you wanna use the command line interface please add `cli` as argument. Otherwise, you can also double-click the jar file
```bash
java -jar client.jar [ cli ] 
```
### Compatibility notes for cli
The command line version of the game is optimized for unix-like terminals.

Our cli version of the game automatically resizes the terminal window for a better game experience.
Most terminals allow this by default, in case you have XTerm or iTerm:  
  * XTerm:  set the following resource to true: allowWindowOps
  * iTerm2: deselect the following: Preferences > Profiles > [profile] > Terminal > Disable session-initiated window resizing
 
 Otherwise please note that the minimum required size of the terminal to play the game is 160x50
## Documentation

### Javadoc 
Javadoc is available at this link: [http://santorini40.xyz/javadoc](http://santorini40.xyz/javadoc)  
Javadoc is also in this repo [here](/deliveries/final/javadoc) 
### UML diagrams 
UML diagrams are [here](/deliveries/final/uml) 
 
### Coverage report  
The final report of the coverage is [here](/deliveries/final/report) 

## Gameplay screenshots
![Alt Text](https://user-images.githubusercontent.com/7725068/86405047-f17d2d00-bcb0-11ea-8ebc-c70749ab197b.gif)

[![asciicast](https://asciinema.org/a/M7hixox96x6bTS28k6X1SDAz2.svg )](https://asciinema.org/a/M7hixox96x6bTS28k6X1SDAz2 )

## External libraries used

| Library | Link 
| ----------| --------------------------------------- |
| GSON      | https://github.com/google/gson          |       
| AnimateFX | https://github.com/Typhon0/AnimateFX    |        
| word-wrap | https://github.com/davidmoten/word-wrap | 
| Mockito   | https://site.mockito.org                | 

## Authors (alphabetical order)
* [ Tiberio Galbiati](https://github.com/TiberioG)
* [ Andrea Lampis ](https://github.com/sup3rgiu)
* [ Vito Alessandro Monaco](https://github.com/Vito96)

