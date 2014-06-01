Wumpus 3D Environment
by Bhargav Brahmbhatt

Please refer to the proper documentation for more information about this application.

However, since there is no good documentation, here are a few things to get you going:
ENVIRONMENT:
- To start the environment up, just run the file called "run"
- Once the environment loads up, go to the File menu and select an Agent. Since there is
no documentation yet, it would be difficult to write your own Agent, so I've provided a number
of example Agents
- After you select an Agent, the Map menu option will open up. Feel free to pick a map to use,
though I *strongly* suggest using only maps that have corresponding names to the Agent you
chose, or else the behavior is undefined, because only know how to react on certain maps.
- To have some fun, drag around the mouse to change your view. You can use the mouse scroll to
zoom in or out.
- Use the slider bar at the top to change how long you wait between Agent steps.
- The rest should be self-explanatory. Use the buttons at the top to watch the Agent get to 
the gold.
- REMEMBER, FEEDBACK IS IMPORTANT TO ME AS THIS IS NOT YET A FINAL PRODUCT!!!
*NOTE: There is a known FPS issue on the maps that have lots and lots of objects. For this
reason, sometimes it may look like the Agent is skipping steps in auto-step mode. This can
be remedied by sliding the bar higher so the frame has a chance to refresh itself.

 

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
S = agent start, there can be only one of there
P = pit
W = Wumpus
M = minion
G = goal, there must be as many as are specified
