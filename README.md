# AI_Prog_Proj

Please go through this readme before asking questions about the workings of the server.
The following describes the various options for starting the server using provided example clients.
Inspection of the source code for the example clients may yield useful information regarding the implementation of your own client.
The commands below must be executed from the directory containing this readme file.
It is required that the Java runtime environment binaries are available in your system path for the commands below to work.
Note that if you have the CLASSPATH environment variable set, running the server with a client may/will fail.
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
