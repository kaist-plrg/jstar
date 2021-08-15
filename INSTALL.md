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
| [sbt](https://www.scala-sbt.org/)                           | An interactive build tool for Scala  |
| [bash](https://www.gnu.org/software/bash/)                  | GNU Bash (version >= 3.2.57)         |

### Environments for Reproduction of Evaluation Results

| Name                                                        | Description                          |
|:-----------------------------------------------------------:|:-------------------------------------|
| [python3](https://www.python.org/downloads/)                | Python 3 (version >= 3.7.4)          |
| [Excel](https://www.microsoft.com/en-us/microsoft-365/excel)| Excel (version >= 2016)              |

_NOTE_: Even though our tool is available in any Linux machine, macOS is
required to draw evaluation results as figures and tables using Microsoft Excel
Spreadsheet Software.


## Download JSTAR

### Git repository
```bash
$ git clone --recurse-submodules https://github.com/kaist-plrg/jstar.git
$ cd jstar
```

### Zenodo
https://doi.org/10.5281/zenodo.5084816

### Docker
```bash
$ docker run -it -m=16g --rm jhnaldo/jstar
# user: jstar, password: jstar
```
_WARNING_: The docker image is 1.5GB large thus be patient when you
download it and please assign more than 16GB memory for the docker engine.

## Environment Setting (!!IMPORTANT!!)

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

_NOTE_: It might take several minutes at the first time because of the
installation of a proper `sbt` version.


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

and global options:
- `-silent` is given, do not show final results.
- `-debug` is given, turn on the debug mode.
- `-log` is given, turn on the logging mode.
- `-time` is given, display the duration time.

_NOTE_: We omitted several options for the brevity. Please see the other
options using `jstar help` command.

## Simple Examples
```bash
# performs type analysis for the most recent version of ECMAScript
# - date: Tue Mar 9 06:56:46 2021
# - tag: es2021-candidate-2021-03
# - commit hashcode: fc85c50181b2b8d7d75f034800528d87fda6b654
$ jstar analyze -silent
```
It might takes several minutes and the following result will be shown:
```
========================================
 extract phase
----------------------------------------
version: recent (fc85c50181b2b8d7d75f034800528d87fda6b654)
extracting spec.html... (8,436 ms)
========================================
 build-cfg phase
----------------------------------------
build CFG... (208 ms)
========================================
 analyze phase
----------------------------------------
[Bug] unchecked abrupt completions: __x2__ @ ArgumentsExoticObject.GetOwnProperty
[Bug] unchecked abrupt completions: __x3__ @ ArgumentsExoticObject.DefineOwnProperty
[Bug] non-numeric types: oldLen @ ArrayExoticObject.DefineOwnProperty
[Bug] already defined variable: succeeded @ ArrayExoticObject.DefineOwnProperty
[Bug] non-numeric types: oldLen @ ArraySetLength
[Bug] already defined variable: succeeded @ ArraySetLength
[Bug] unchecked abrupt completions: ref @ EvaluateCall
[Bug] unchecked abrupt completions: lref.ReferencedName @ AssignmentExpression[8,0].Evaluation
[Bug] already defined variable: hasUndefinedLabels @ TryStatement[2,0].ContainsUndefinedContinueTarget
[Bug] already defined variable: hasUndefinedLabels @ CaseBlock[1,3].ContainsUndefinedContinueTarget
[Bug] already defined variable: hasUndefinedLabels @ CaseBlock[1,3].ContainsUndefinedBreakTarget
[Bug] already defined variable: hasUndefinedLabels @ TryStatement[2,0].ContainsUndefinedBreakTarget
[Bug] already defined variable: hasDuplicates @ TryStatement[2,0].ContainsDuplicateLabels
[Bug] already defined variable: hasDuplicates @ CaseBlock[1,3].ContainsDuplicateLabels
[Bug] already defined variable: v @ ArrowParameters[0,0].IteratorBindingInitialization
[Bug] unchecked abrupt completions: alreadyDeclared @ FunctionDeclarationInstantiation
[Bug] already defined variable: index @ CreateMappedArgumentsObject
[Bug] unchecked abrupt completions: g @ CreateMappedArgumentsObject
[Bug] already defined variable: value @ YieldExpression[2,0].Evaluation
[Bug] unchecked abrupt completions: ref @ CallExpression[0,0].Evaluation
[Bug] unchecked abrupt completions: __x7__ @ EvalDeclarationInstantiation
[Bug] unchecked abrupt completions: V @ IsPropertyReference
[Bug] unchecked abrupt completions: ref.ReferencedName @ CallExpression[0,0].Evaluation
[Bug] unchecked abrupt completions: lref.ReferencedName @ AssignmentExpression[6,0].Evaluation
[Bug] unchecked abrupt completions: lref.ReferencedName @ AssignmentExpression[7,0].Evaluation
[Bug] unknown variable: HostEnqueuePromiseJob @ PerformPromiseThen
[Bug] unknown variable: ClassHeritage @ ClassTail[0,3].Contains
[Bug] unknown variable: Statement @ IfStatement[0,0].EarlyErrors
[Bug] assertion failed: (= (typeof target) Object) @ FlattenIntoArray
[Bug] assertion failed: (= __x0__ true) @ Construct
[Bug] assertion failed: (= (typeof O) Object) @ CreateDataPropertyOrThrow
[Bug] assertion failed: (= name absent) @ AsyncGeneratorExpression[0,1].InstantiateAsyncGeneratorFunctionExpression
[Bug] assertion failed: (is-instance-of V ReferenceRecord) @ IsPropertyReference
[Bug] assertion failed: (= name absent) @ AsyncFunctionExpression[0,1].InstantiateAsyncFunctionExpression
[Bug] assertion failed: (= (typeof functionPrototype) Object) @ OrdinaryFunctionCreate
[Bug] assertion failed: false @ RequireObjectCoercible
[Bug] assertion failed: (= (typeof iterResult) Object) @ IteratorValue
[Bug] assertion failed: (= (typeof O) Object) @ Get
[Bug] assertion failed: (= (typeof obj) Object) @ LengthOfArrayLike
[Bug] assertion failed: (is-completion completion) @ IteratorClose
[Bug] assertion failed: (= (typeof source) Object) @ FlattenIntoArray
[Bug] assertion failed: false @ ToObject
[Bug] assertion failed: (= name absent) @ GeneratorExpression[0,1].InstantiateGeneratorFunctionExpression
[Bug] assertion failed: (= __x0__ true) @ GetPrototypeFromConstructor
[Bug] assertion failed: (= (typeof O) Object) @ Set
[Bug] assertion failed: (= name absent) @ FunctionExpression[0,1].InstantiateOrdinaryFunctionExpression
```

If you want to run type analysis for a specific version of ECMAScript, please
insert any branch name, tag name, or commit hashcode to the option
`-extract:version={string}` as follows:
```bash
# performs type analysis for the most recent version of ECMAScript
# - date: Thu Apr 30 01:40:37 2020
# - tag: es2020
# - commit hashcode: dfd5ea2ec5862de21b005737650ba08bc57271fa
$ jstar analyze -silent -extract:version=es2020
# ...
```
or
```bash
# performs type analysis for the most recent version of ECMAScript
# - date: Thu Feb 7 23:01:32 2019
# - commit hashcode: 143931e9feab858402014cc80c7d163560e2ba99
$ jstar analyze -silent -extract:version=143931e9feab858402014cc80c7d163560e2ba99
# ...
```

## How To Reproduce Evaluation Results

To easily reproduce evaluation results, we developed a wrapper of `jstar`
called `jstar-exp` which performs type analysis for multiple versions of
ECMAScript and extracts raw data for figures and tables:
```bash
$ jstar-exp <option>*
```
with the following options:
```
-h, --help                      show this help message and exit
-s, --stat                      dump status of eval/result/raw/*
-v VERSION, --version VERSION   run analyzer to target version
-nr, --no-refine                use -analyze:no-refine during analysis
```

### Raw Data Creation
First, please change the working directory to `eval`:
```bash
$ cd eval
```
To create raw data in the directory `result/raw`, you should run the
following commands for type analysis with and without refinement:
```bash
$ jstar-exp     # create raw data for type analysis with refinement
$ jstar-exp -s  # create summary of raw data
```
and
```bash
$ jstar-exp -nr # create raw data for type analysis without refinement
$ jstar-exp -s  # create summary of raw data
```
**HOWEVER**, `jstar-exp` and `jstar-exp -nr` commands might take more than 40
hours, respectively. Thus, we recommaend you to use given raw data in
directories `raw-refine` and `raw-no-refine`:
```bash
$ rm -rf result && mkdir result  # clean up result directory
$ cp -r raw-refine result/raw    # copy the given raw data
$ jstar-exp -s                   # create summary of raw data
```
and
```bash
$ rm -rf result && mkdir result  # clean up result directory
$ cp -r raw-no-refine result/raw # copy the given raw data
$ jstar-exp -s                   # create summary of raw data
```
If you want to check the given raw data, please run original commands in any
length of time and compare the generated raw data with them.  You can also
randomly pick any version between the following two versions:
```
- 8fadde42cf6a9879b4ab0cb6142b31c4ee501667 # Fri Jan 12 11:09:21 2018
- fc85c50181b2b8d7d75f034800528d87fda6b654 # Tue Mar 9 06:56:46 2021
```
and perform and dump the raw data for that version to `result/raw/<version>`
using the following command:
```bash
$ jstar-exp -v <version> # add `-nr` for no refinement
```
For example, if you want to check the raw data for the version defined with
commit hashcode `6460fcd9ca7a6e2f8eed533395029c5edd13d672`, please run the
following commands:
```bash
$ jstar-exp -v 6460fcd9ca7a6e2f8eed533395029c5edd13d672
$ diff result/raw/6460fcd9ca7a6e2f8eed533395029c5edd13d672/errors \
    raw-refine/6460fcd9ca7a6e2f8eed533395029c5edd13d672/errors
```

### Drawing Figures and Tables
Please open the excel file `figures.xlsx`.  After extracting raw data for type
analysis with refinement, copy the data in `result/summary.tsv` and
`result/detected-bugs.tsv` to `summary-refine` and `detected-bugs-refine` tab,
respectively.  Similarly, after extracting raw data for type analysis without
refinement, copy the data in `result/summary.tsv` and
`result/detected-bugs.tsv` to `summary-no-refine` and `detected-bugs-no-refine`
tab, respectively.

### RQ1) Performance (Section V.A)
Please open `figure-6` tab in `figures.xlsx`.

### RQ2) Precision (Section V.B)
Please open `figure-7` and `table-2` tabs in `figures.xlsx`.

### RQ3) Effectiveness of Refinement (Section V.C)
Please open `figure-8` and `table-2` tabs in `figures.xlsx`.

### RQ4) Detection of New Bugs (Section V.D)
Please open `table-3` tab in `figures.xlsx`.
