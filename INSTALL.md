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


## Basic Commands

You can run the artifact with the following command:
```bash
$ jstar <sub-command> <option>*
```
with the following sub-commands:
- `help` shows the help message.
- `extract` represents **Specification Extraction** phase that extracts a
  mechanized specification from ECMAScript defined in `ecma262/spec.html`.
  - `-extract:version={string}` is given, set the git version of ecma262.
- `build-cfg` builds control flow graph (CFG).
- `analyze` represents **Type Analysis** and **Bug Detection** phases that
  performs type analysis of a given mechanized specification and detects
  type-related specification bugs. We merged two phases to immediately detect
  specification bugs during the type analysis.
  - `-analyze:no-refine` is given, not use the abstract state refinement.

_NOTE_: We omitted several options for the brevity. Please see the other
options using `jstar help` command.

and global options:
- `-silent` is given, do not show final results.
- `-debug` is given, turn on the debug mode.
- `-log` is given, turn on the logging mode.
- `-time` is given, display the duration time.
