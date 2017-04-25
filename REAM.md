# AI_Prog_Proj
#####Programming project for AI
###Langauge
#####Java
##### 
 Linux/Mac: $ java -jar server.jar -l levels/MAsimple1.lvl -c "java samp    leclients.RandomWalkClient" > /dev/null
  60 Note that both messages from the client and important server messages (inc    luding success) both use 'standard error' for printing to console, hence t    hey bypass this redirection.
   61
    62 To test the effect of actions you can try the user controlled client:
	 63    Windows: $ java -jar server.jar -l levels/SAsokobanLevel96.lvl -c "java     sampleclients.GuiClient" -g 200
	  64    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/SAso    kobanLevel96.lvl -c "java sampleclients.GuiClient" -g 200
	   65
	    66 GuiClient works by creating a joint action of identical individual actions     for each agent on the level; e.g. clicking Move(W) on a level with 3 agen    ts sends [Move(W),Move(W),Move(W)].
		 67 For each argument passed to GuiClient, a custom text field is created with     that joint action; e.g.:
		  68    Windows: $ java -jar server.jar -l levels/MAsimple3.lvl -c "java sample    clients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Push(E,N)] [Push(E,E),Pull(W    ,N)] [Pull(W,E),NoOp]" -g 100
		   69    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/MAsi    mple3.lvl -c "java sampleclients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Pus    h(E,N)] [Push(E,E),Pull(W,N)] [Pull(W,E),NoOp]" -g 100
		    70 fills the custom commands upon startup.
			 71
			  72 To try out the included ruby random walk client (requires a ruby inteprete    r in your environment):
			   73    Windows: $ java -jar server.jar -l levels/MApacman.lvl -c "ruby client/    random_agent.rb 3" -g -p
			    74    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/MApa    cman.lvl -c "ruby client/random_agent.rb 3" -g -p
				 75 The argument passed to random_agent.rb is the number of agents on the leve    lleclients.RandomWalkClient" > NUL
				  59    Linux/Mac: $ java -jar server.jar -l levels/MAsimple1.lvl -c "java samp    leclients.RandomWalkClient" > /dev/null
				   60 Note that both messages from the client and important server messages (inc    luding success) both use 'standard error' for printing to console, hence t    hey bypass this redirection.
				    61
					 62 To test the effect of actions you can try the user controlled client:
					  63    Windows: $ java -jar server.jar -l levels/SAsokobanLevel96.lvl -c "java     sampleclients.GuiClient" -g 200
					   64    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/SAso    kobanLevel96.lvl -c "java sampleclients.GuiClient" -g 200
					    65
						 66 GuiClient works by creating a joint action of identical individual actions     for each agent on the level; e.g. clicking Move(W) on a level with 3 agen    ts sends [Move(W),Move(W),Move(W)].
						  67 For each argument passed to GuiClient, a custom text field is created with     that joint action; e.g.:
						   68    Windows: $ java -jar server.jar -l levels/MAsimple3.lvl -c "java sample    clients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Push(E,N)] [Push(E,E),Pull(W    ,N)] [Pull(W,E),NoOp]" -g 100
						    69    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/MAsi    mple3.lvl -c "java sampleclients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Pus    h(E,N)] [Push(E,E),Pull(W,N)] [Pull(W,E),NoOp]" -g 100
							 70 fills the custom commands upon startup.
							  71
							   72 To try out the included ruby random walk client (requires a ruby inteprete    r in your environment):
							    73    Windows: $ java -jar server.jar -l levels/MApacman.lvl -c "ruby client/    random_agent.rb 3" -g -p
								 74    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/MApa    cman.lvl -c "ruby client/random_agent.rb 3" -g -p
								  75 The argument passed to random_agent.rb is the number of agents on the leve    lleclients.RandomWalkClient" > NUL
								   59    Linux/Mac: $ java -jar server.jar -l levels/MAsimple1.lvl -c "java samp    leclients.RandomWalkClient" > /dev/null
								    60 Note that both messages from the client and important server messages (inc    luding success) both use 'standard error' for printing to console, hence t    hey bypass this redirection.
									 61
									  62 To test the effect of actions you can try the user controlled client:
									   63    Windows: $ java -jar server.jar -l levels/SAsokobanLevel96.lvl -c "java     sampleclients.GuiClient" -g 200
									    64    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/SAso    kobanLevel96.lvl -c "java sampleclients.GuiClient" -g 200
										 65
										  66 GuiClient works by creating a joint action of identical individual actions     for each agent on the level; e.g. clicking Move(W) on a level with 3 agen    ts sends [Move(W),Move(W),Move(W)].
										   67 For each argument passed to GuiClient, a custom text field is created with     that joint action; e.g.:
										    68    Windows: $ java -jar server.jar -l levels/MAsimple3.lvl -c "java sample    clients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Push(E,N)] [Push(E,E),Pull(W    ,N)] [Pull(W,E),NoOp]" -g 100
											 69    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/MAsi    mple3.lvl -c "java sampleclients.GuiClient [NoOp,Push(E,E)] [Push(E,E),Pus    h(E,N)] [Push(E,E),Pull(W,N)] [Pull(W,E),NoOp]" -g 100
											  70 fills the custom commands upon startup.
											   71
											    72 To try out the included ruby random walk client (requires a ruby inteprete    r in your environment):
												 73    Windows: $ java -jar server.jar -l levels/MApacman.lvl -c "ruby client/    random_agent.rb 3" -g -p
												  74    Linux:   $ java -Dsun.java2d.opengl=true -jar server.jar -l levels/MApa    cman.lvl -c "ruby client/random_agent.rb 3" -g -p
												   75 The argument passed to random_agent.rb is the number of agents on the leve    l
