# Maze_solver
## Project Description
This project is an application of Search Algorithms such as:
* backtracking 
* Greedy Algorithm (with heuristics)

on a task with a touch of imagination and innovation. The task description
can be found in detail through this [link](https://acrobat.adobe.com/link/review/?pdfnow%21verb=extract-pages&x_api_client_id=adobe_com&x_api_client_location=extract_pages&uri=urn%3Aaaid%3Ascds%3AUS%3AZiQWyfCrT_-WTVK4xTUU4Q).

## Project's Structure
This consists of a number of Java files:
1. **Solver**: A file providing the different utilities as well as the classes that model the task's environment
2. **Actor**: an abstract class that represents the ***agents*** that will solve the maze: find the book then the exit
3. Every actor is characterized by three main properties:
    * Field of vision: the squares that the actor can perceive at any point
    * Field of action: to which can the actor can reach in the next move
    * Algorithm: the actor's brain that governs it movement
4. The first property is incorporated in the **VisionField** file
5. The second is built in the **ActionField**
6. As for the Greedy actor, it is written in the **GreedyActor** file
7. The backtracking actor is written in the **BackTrackingActor**
8. **TestAuto** was created for large scale actors' testing: creating random maps' settings
9. **Statistics** file includes statistical summary of each of the actors' performance.

A more detailed description of each file functionality can be found [here](https://acrobat.adobe.com/link/track?uri=urn:aaid:scds:US:a0ca7a69-a05b-4771-80ef-8693a39caa1f)

## Project's Installment and usage
### Requirements
The only requirement is having Java.14 as this project includes more recent features such as the **Record** type.
### Installing 
clone the repository into a local directory 
### Usage
run the main function of the Main class and follow the CLI's instructions.

## Possible improvements
* Adding more complex algorithms such as A* (A star)
* adding visualization using Java Swing library