javac -Xlint:unchecked -classpath "jogl-jar/jogl-all.jar;jogl-jar/gluegen-rt.jar;WumpusEnvironment.jar;." .\Agents\*.java
java -Xint -classpath "jogl-jar/jogl-all.jar;jogl-jar/gluegen-rt.jar;WumpusEnvironment.jar;." WumpusEnvironment.WumpusEnvironment
pause