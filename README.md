# AI_Prog_Proj

Please go through this readme before asking questions about the workings of the server.    
The following describes the various options for starting the server using provided example clients.   
Inspection of the source code for the example clients may yield useful information regarding the implementation of your own client.   
The commands below must be executed from the directory containing this readme file.   
It is required that the Java runtime environment binaries are available in your system path for the commands below to work.   
***Note*** that if you have the CLASSPATH environment variable set, running the server with a client may/will fail.   

You should not have the CLASSPATH environment variable set unless you know what you're doing.

#### Compile the provided sample clients with:
* ```$ javac sampleclients/*.java```

#### Get help about server options and arguments:
* ```$ java -jar server.jar -?```
    
#### The server takes the following arguments:
*  ```-c <command>```
*  ```-l <level path>```
*  ```-g [<milliseconds>]```
*  ```-p```
*  ```-t <seconds>```
*  ```-o <directory or file>```

##### For this arguments
- The `-c` <command> argument specifies the command to run your client, as you would write it if you ran it from command line (including arguments to your client).
    
- The `-l` <level path> argument specifies the level to run the client on.
    
- The `-g [<milliseconds>]` argument enables the server's graphical interface.

***The GUI will execute an action every `<milliseconds>` (default 150). The minimum value is 30 milliseconds.***
    
- The `-p` argument starts the graphical interface in paused mode. Actions are executed after the GUI is unpaused.
    
- The `-t` <seconds> argument specifies a timeout. If more than <seconds> elapse and the client has not solved the level, the client run is aborted.
    
- The `-o` <file> argument plays a log file. The -c and -l are ignored and the log file pointed to by `-o` is replayed.

- The `-o` <directory> specifies a directory where to save a log of the current run with the `-c` and `-l` arguments.
    
#### Basic usage for the server is either of:
* ```$ java -jar server.jar -c <command> -l <level path> <arguments>```
* ```$ java -jar server.jar -o <file>```

### Important for Unix systems 
Some Unix systems have been known to have bad performance with the graphical interface because hardware acceleration is disabled by default.   

The `-Dsun.java2d.opengl=true` option enables OpenGL hardware acceleration (see [link](http://docs.oracle.com/javase/8/docs/technotes/guides/2d/flags.html)).   

This option may help if you experience low framerates or server instability when rendering.   

Windows defaults to Direct3D and does not require this.
    
An example invocation of the server with command-line interface:
* Windows:   ```$ java -jar server.jar -l levels_map/levels/MAsimple1.lvl -c "java sampleclients.RandomWalkClient"```

* Linux/Mac: ```$ java -jar server.jar -l levels_map/levels/MAsimple1.lvl -c "java sampleclients.RandomWalkClient"```
    
Without the -g argument, the server prints a string representation of the current state to the console.   

To minimize overhead (e.g. when optimizing your client) this output may be redirected to the null device using:
* Windows:   ```$ java -jar server.jar -l levels_map/levels/MAsimple1.lvl -c "java sampleclients.RandomWalkClient" > NUL```

* Linux/Mac: ```$ java -jar server.jar -l levels_map/levels/MAsimple1.lvl -c "java sampleclients.RandomWalkClient" > /dev/null```   

***Note*** that both messages from the client and important server messages (including success) both use 'standard error' for printing to console, hence they bypass this redirection.
    
To test the effect of actions you can try the user controlled client:   
* Windows: ```$ java -jar server.jar -l levels_map/levels/SAsokobanLevel96.lvl -c "java sampleclients.GuiClient" -g 200```

* Linux:   ```$ java -Dsun.java2d.opengl=true -jar server.jar -l levels_map/levels/SAsokobanLevel96.lvl -c "java sampleclients.GuiClient" -g 200```
    
GuiClient works by creating a joint action of identical individual actions for each agent on the level; e.g. clicking `Move(W)` on a level with 3 agents sends `[Move(W),Move(W),Move(W)]`.  
For each argument passed to GuiClient, a custom text field is created with that joint action; e.g.:
* Windows: ```$ java -jar server.jar -l levels_map/levels/MAsimple3.lvl -c "java sampleclients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Push(E,N)] [Push(E,E),Pull(W,N)] [Pull(W,E),NoOp]" -g 100```

* Linux:   ```$ java -Dsun.java2d.opengl=true -jar server.jar -l levels_map/levels/MAsimple3.lvl -c "java sampleclients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Push(E,N)] [Push(E,E),Pull(W,N)] [Pull(W,E),NoOp]" -g 100```   
fills the custom commands upon startup.
    
To try out the included ruby random walk client (requires a ruby intepreter in your environment):
* Windows: ```$ java -jar server.jar -l levels_map/levels/MApacman.lvl -c "ruby client/random_agent.rb 3" -g -p```

* Linux:   ```$ java -Dsun.java2d.opengl=true -jar server.jar -l levels_map/levels/MApacman.lvl -c "ruby client/random_agent.rb 3" -g -p```   

The argument passed to random_agent.rb is the number of agents on the level

## What has changed
***Bihang has updated the code and I merged it to the master.What we should pay attention to is that I have renamed `searchclients` to `clients`.So,please remember that our clients' name is `clients` rather than `searchclients`.Don't forget this.Besides,remember to renew your master.***  
Maybe you can do renew by using command.   
```
git checkout master   
git pull  
git checkout [your branch]
git rebase master
```

### Which levels we can resolve
* Single Agent
```
  1 "competition_levels/SAAIoliMAsh.lvl",
  2 "competition_levels/SABeliebers.lvl",  // solved action 495
  3 "competition_levels/SABlinky.lvl",     // solved action 148
  4 "competition_levels/SABoxBunny.lvl",  // error
  5 "competition_levels/SABronies.lvl",   // can not sovle
  6 "competition_levels/SADAT.lvl",  // can not sovle
  7 "competition_levels/SADoDo.lvl",  // solved action 263
  8 "competition_levels/SAEvilCorp.lvl", //can not sovle
  9 "competition_levels/SAFooBar.lvl",
 10 "competition_levels/SAGeneralAI.lvl",
 11 "competition_levels/SAgroupname.lvl",  // solved action 3959
 12 "competition_levels/SAGroupOne.lvl",
 13 "competition_levels/SAHALnineK.lvl",  // shabi
 14 "competition_levels/SAHiveMind.lvl",  // solve
 15 "competition_levels/SAIamGreedy.lvl",  // time out
 16 "competition_levels/SAJomarki.lvl", // solve  action 72
 17 "competition_levels/SAKalle.lvl",
 18 "competition_levels/SALemmings.lvl", // solve  action 116
 19 "competition_levels/SALiquorice.lvl",  // solve  action 99
 20 "competition_levels/SAMasAiArne.lvl",  // solve  action 614
 21 "competition_levels/SAMASters.lvl",
 22 "competition_levels/SANeverMind.lvl",  // solve
 23 "competition_levels/SAOmnics.lvl",  // solve
 24 "competition_levels/SATALK.lvl",  // solve  action 258
 25 "competition_levels/SAtnrbts.lvl",

```
* Multiple Agents
```
easy/MAYSoSirius.lvl  
medium/MAsampdoria.lvl  
short_solution/MARyther.lvl
```
