# **Elevator Project**
#### Authors: Kyle, Ben and Max


Creates a series of moving elevators which can take user input to carry out commands as well as randomly generating moving commands.



## Contents
* Notes
* Tests
* How to...
* Bugs


# Notes
- Errors and list of commands found in log files
- Presentation file can be found in the GitLab repository (contains UML file)
- Due to time constraints we were unable to implement saving elevator states on program stop

# Tests
Tests don't have great coverage, however this if for a number of reasons
* FrameView, Runner and UserInput are constructed in a way that makes them either impossible or pointless to test
* The while loops the run the program don't have outputs. Instead, usually calling on other methods which we have tests for

# How to...
In order to run the elevator, run the Runner class. To stop the process use the exit button on the console or type stop.

User input can be a command, eg.g., number:number:number, with multiple commands separated by commas.

There are four types of input that are processed by the program. Command, Interval, Simulation, Stopping.


#### Command
Accepts a command of the following format |src:dest:ppl|, these can be chained with a comma like so 1:2:3,4:5:6

#### Interval
Accepts any integer to modify how fast the program generates commands. Intervals are in milliseconds so it is recommended you enter numbers in the thousands i.e. 1000, 2000, 5000

#### Simulation
<ol>
<li>morning</li>
<li>afternoon</li>
<li>normal</li>
</ol>

Morning and Afternoon can lock a floor using this format |simulation floor number|

#### Stopping
Accepts **halt** which stops the random command generator, can be restarted by entering a time interval. Will also accept **stop** which will stop the entire program

# Bugs
* None (hopefully)