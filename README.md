# JSTAR: JavaScript Specification Type Analyzer using Refinement

**JSTAR** is a **J**avaScript **S**pecification **T**ype **A**nalyzer using
**R**efinement, which is a novel approach to detect specification bugs via type
analysis.  This artifact extends [JISET](https://github.com/kaist-plrg/jiset)
to extract mechanized specification from diverse versions of ECMAScript, and
performs a type analysis to Intermediate Representations for ECMAScript
Specifications (IRES).


## Getting Started Guide
The artifact is open-source can be obtained by cloning the following git
repository:
```bash
$ git clone --recurse-submodules https://github.com/kaist-plrg/jstar.git
```
Please see `INSTALL.md` for the detailed guide of installation.


## Overall Structure

![image](https://user-images.githubusercontent.com/6766660/124905848-67814600-e021-11eb-829f-476e00f9c581.png)

JSTAR consists of three components: 1) specification extraction, 2) type
analysis, and 3) bug detection:

### 1) Specification Extraction
We utilizes another tool [**JISET**](https://github.com/kaist-plrg/jiset),
which is a **J**avaScript **I**R-based **S**emantics **E**xtraction
**T**oolchain, to extract a mechanized specification from given ECMAScript.

### 2) Type Analysis
JSTAR performs a type analysis with flow-sensitivity and type-sensitivity for
arguments.  Each function is split into multiple flow- and type-sensitive
views, and an abstract state stores mapping from views to corresponding
abstract environments.  To handle views separately, we use a worklist
algorithm.  The type analyzer consists of two sub-modules: an **Analysis
Initializer** and an **Abstract Transfer Function**.
- **Analysis Initializer** defines the initial abstract state and the initial
  set of views for a worklist.
- **Abstract Transfer Function** gets a specific view from the worklist and
  updates the abstract environments of the next views based on the abstract
  semantics for each iteration.

### 3) Bug Detection
To detect type-related specification bugs utilizing the type analysis, we
developed four checkers in a bug detector:

- **Reference Checker** detects _reference bugs_ which occur when trying to
  access variables not yet defined (`UnknownVar`) or to redefine variables
  already defined (`DuplicatedVar`).
- **Arity Checker** detects _arity bugs_. An arity bug occurs when the number of
  arguments does not match with the function arity (`MissingParam`).
- **Assertion Checker** detects _assertion failures_. An assertion failure
  (`Assertion`) occurs when the condition of an assertion instruction is not
  true.
- **Operand Checker** detects _ill-typed operand bugs_. An ill-typed operand
  bug occurs when the type of an operand does not conform to its corresponding
  parameter type.  It contains non-numeric operand bugs (`NoNumber`) and
  unchecked abrupt completion bugs (`Abrupt`).


## Basic Commands

You can run the artifact with the following command:
```bash
$ jstar <sub-command> <option>*
```
with the following sub-commands:
- `help` shows the help message.
- TODO

and global options:
- TODO
