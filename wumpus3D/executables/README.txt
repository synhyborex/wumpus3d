Wumpus 3D Environment
by Bhargav Brahmbhatt

Please refer to the proper documentation for more information about this application.
This README file is a rudimentary guide meant to show you how to run the environment; the
information provided on the proper documentation provides detailed information on how to
use the environment.

Here are a few things to get you going:
ENVIRONMENT:
- To start the environment up, just run the file called "runEnvironment"
	- Some users may not be able to open the environment. While I haven't found a formal fix
	for this yet, make sure you have the latest version of Java installed. For others, 
	installing JDK 7 or later fixes the problem. Here's a handy link to download the JDK
	and the JRE if you need it: 
	http://www.oracle.com/technetwork/java/javase/downloads/index.html
- Once the environment loads up, go to the File menu and select an Agent. Since there is
no documentation yet, it would be difficult to write your own Agent, so I've provided a number
of example Agents. More detail about these in the AGENTS section below.
- After you select an Agent, the Map menu option will open up. More information is in the MAPS
section below. Feel free to pick a map to use, though I *strongly* suggest using only maps that 
have corresponding names to the Agent you chose, or else the behavior is undefined, because 
they only know how to react on certain maps.
	- Details about what Agents work on what Maps is in the AGENTS section below.
- To have some fun, drag around the mouse to change your view. You can use the mouse scroll to
zoom in or out.
- Use the slider bar at the top to change how long you wait between Agent steps.
- The rest should be self-explanatory. Use the buttons at the top to watch the Agent get to 
the gold.
- REMEMBER, FEEDBACK IS IMPORTANT TO ME AS THIS IS NOT YET A FINAL PRODUCT!!
*NOTE: There is a known FPS issue on the maps that have lots and lots of objects. For this
reason, sometimes it may look like the Agent is skipping steps in auto-step mode. This can
be remedied by sliding the bar higher so the frame has a chance to refresh itself.



AGENTS:
Here's a listing of what Agents are designed for what maps, so that you don't need to send me
bug reports about Agents not working on every map.
- TestA2Agent 
	- BasicMap, ChallengeMap, MiniMap, WumpusMap
- TestBFSAgent, TestDFSAgent, TestUniformAgent 
	- astar-map,bfs-map
	- NOTE: BFS and DFS will look like they don't work with step or autostep other than
	lighting up the floor. This is intended behavior and is a result of my badly programming
	those Agents. This isn't a problem with the environment!
	- NOTE: These maps are intended to have a "fairy". This is the little ball that jumps
	around (this is what lights up the squares in BFS and DFS)--you can actually see it in
	Uniform, but not in BFS and DFS.
- TestModelBasedAgent 
	- modelbased-map
- TestReflexAgent 
	- reflex-map
- TestTableAgent 
	- table-map, testMap
Using Agents on maps they're not designed for will result in undefined behavior. The system
is designed so that all Agents can spawn properly on all maps, but not necessarily run
the way that they're intended to, except on the maps it was designed for.

 

MAPS:
- If you want, you can edit the maps by opening up the Maps folder and using your favorite
text editor. However, doing this may cause unexpected behavior in the Agents that solve
those maps!
- The numbers at the top are as follows: map width in nodes, map height in nodes, and the number
of goals on the map. The test of the file MUST adhere to these limitations or there will be
undefined behavior--this system was designed to be used by someone who knew how, and doesn't
handle that many exceptions regarding initializing the environment.
	- There can be additional flags following the three numbers, these are the strings "fairy"
	and "learning <number>". The fairy flag will activate the mechanism to use a fairy to search
	the map. The learning flag will enable learning on the map. The <number> after the learning
	flag is how many repetitions of the map you want--this cannot be left blank. More details
	about the learning implementation will be in the actual documentation.
- As before, feedback is very important to me! Any bugs or suggestions, let me know!
- Here is how you can change the map
X  = wall
- = blank space
S = agent start, there can be only one of these
P = pit
W = Wumpus
M = minion
G = goal, there must be as many as are specified
