# Installation Guide

We explain how to install JSTAR with necessary environment settings from the
scratch.  Before installation, please download JDK 8 and
[`sbt`](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html).

## Requirements

### Machine Requirements
| Name              | Requirements   |
|:-----------------:|:---------------|
| Memory            | ≥ 16GB         |
| Operating System  | Linux or macOS |

### Programming Language Environments
| Name                                                        | Description                          |
|:-----------------------------------------------------------:|:-------------------------------------|
| [jdk8](https://www.oracle.com/java/technologies/java8.html) | Java Standard Edition 8              |
| [sbt](https://www.scala-sbt.org/)                           | An intereactive build tool for Scala |


## Download JSTAR
```bash
$ git clone --recurse-submodules https://github.com/kaist-plrg/jstar.git
$ cd jstar
```


## Environment Setting

Insert the following commands to `~/.bashrc` (or `~/.zshrc`):
```bash
# for JSTAR
export JSTAR_HOME="<path to JSTAR>"
export PATH="$JSTAR_HOME/bin:$PATH"
```
The `<path to JSTAR>` should be the _absolute path_ of JSTAR repository.


## Installation of JSTAR using `sbt`
```bash
$ sbt assembly
```
