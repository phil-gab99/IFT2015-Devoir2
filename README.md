# IFT2015-Devoir2

[Project Instructions](https://ift2015h21.wordpress.com/2021/02/24/projet-2-nos-ancetres-communs/)

## Project Details

This project was built in Java and runs a simulation of a series of events
involving Sim populations. An age model was written describing various rates
factors which allow the generation of random lifetime durations, reproduction
waiting time, mating partners and more. The data from the simulation is then
used to plot datasets of interest such as population growth and coalescence.

## Running the application

This application can be run from the command line/terminal using the command:
```console
java -jar pedigree.jar founders maxTime
```
where the first argument is an `int` indicating the number of **founder Sims**
to be born at year 0, and the second argument is a `double` indicating the
**maximum time length** of the simulation after which the simulation will halt
if it hadn't already run out of events. Alternatively, the application can be
run simply by double-clicking the jar file.

In both cases, one will need to confirm the entered arguments in the dialog
before proceeding with the simulation.

The data from the simulation is then listed in the terminal instance if the
application was launched from the terminal and the data is plotted onto a graph
with a standard number axis for the `x` coordinates and a logarithmic axis for
the `y` coordinates.

The jFreeChart external library was used in order to plot the various datasets
of interest:
- [jFreeChart API Documentation](https://www.jfree.org/jfreechart/api/javadoc/index.html)
- [jFreeChart GitHub Repository](https://github.com/jfree/jfreechart)