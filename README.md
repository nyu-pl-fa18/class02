# Class 2: Names, Binding, Scoping

## Names

What is a *name* in a program?

An *identifier*, made up of a string of characters, used to represent something else.

What can be named?

* Execution points (labels)
* Mutable variables
* Constant values
* Subroutines (functions, procedures, methods)
* Types
* Type constructors (e.g., list or vector)
* Classes
* Modules/packages
* Execution points with environment (continuation)

Names are a key part of *abstraction*, which helps to make programming
easier:

* Abstraction reduces conceptual complexity by hiding irrelevant details
* Names for subroutines: *control abstraction*
* Names for types/classes: *data abstraction*

## Bindings

A *binding* is an association of two things, such as:

* Names and the things they name
* A question about how to implement a feature and the answer to that question.

*Binding time* is the time at which the association is made.

* *Language design time*: built-in features such as keywords
* *Language implementation time*: implementation dependent semantics
    such as bit-width of an integer
* *Program writing time*: names chosen by programmer
* *Compile time*: bindings of high-level constructs to machine code
* *Link time*: final bindings of names to addresses
* *Load time*: physical addresses (can change during run time)
* *Run time*: bindings of variables to values, includes many bindings
    which change during execution such as the binding of function
    parameters to the passed argument values.
 
*Static binding* (aka *early binding*) means before run time; *Dynamic
 binding* (aka *late binding*) means during run time.

What are some advantages of static binding times?

* *Efficiency*: the earlier decisions are made, the more optimizations are
  available to the compiler.

* *Ease of implementation*: Static binding makes compilation easier.

What are some advantages of dynamic binding times?

* *Flexibility*: Languages that allow postponing binding give more
  control to programmer

* *Polymorphic code*: Code that can be used on objects of different
  types is *polymorphic*. Examples:
  
  * subtype polymorphism (dynamic method dispatch)
  
  * parametric polymorphism (e.g. generics)

Typically, early binding times are associated with compiled languages and late
binding times with interpreted languages.

### Lifetimes

The period of time between the creation and destruction of a name-to-object
binding is called the binding's *lifetime*.

The time between the creation of an object and its destruction is the
*object's lifetime*.

Is it possible for these to be different?

* When a variable is passed by reference, the binding of the reference
  variable has a shorter lifetime than that of the object being
  referenced.
  
* If there are multiple pointers to an object and one of the pointers
  is used to delete the object, the object has a shorter lifetime than
  the bindings of the pointers.
  
* A pointer to an object that has been destroyed is called a *dangling
  reference*. Dereferencing a dangling pointer is usually a bug.

### Static Allocation

*Static objects* are objects whose lifetime spans the entire program
execution time. Examples:

* the contents of global variables
* the actual program instructions (in machine code)
* numeric and string literals
* tables produced by the compiler

Static objects are often allocated in *read-only* memory (the
program's data segment) so that attempts to change them will be
reported as an error by the operating system.

Under what conditions could local variables be allocated statically?

* The original Fortran did not support recursion, allowing local
  variables of functions to be allocated statically.

* Some languages (e.g. C++) allow local variables of functions to be
  declared `static` which causes them to be treated as static objects.

### Dynamic Allocation

For most languages, the amount of memory used by a program cannot be
determined at compile time (exceptions include earlier versions of Fortran).

Some features that require dynamic memory allocation:
* Recursive functions
* Pointers, explicit allocation (e.g., `new`, `malloc`)
* First-class functions

We distinguish *stack-based* and *heap-based* dynamic allocation.

In languages with recursion, the natural way to allocate space for subroutine
calls is on the *stack*. This is because the lifetimes of objects
belonging to subroutines follow a last-in, first-out (LIFO) discipline.

Each time a subroutine is called, space on the stack is allocated for
the objects needed by the subroutine. This space is called a *stack
frame* or *activation record*.

Objects in the activation record may include:
* Return address
* Pointer to the stack frame of the caller (*dynamic link*)
* Arguments and return values
* Local variables
* Temporary variables
* Miscellaneous bookkeeping information

Even in a program that does not use recursion, the number of
subroutines that are active at the same time at any point during
program execution is typically much smaller than the total number of
subroutines in the program. So even for those programs, it is usually
beneficial to use a stack for allocating memory for activation
records, rather than allocating that space statically.

Some objects may not follow a LIFO discipline, e.g.

* objects allocated with `new`, `malloc`,
* the contents of local variables and parameters in functional languages.

The lifetime of these objects may be longer than the lifetime of the
subroutine in which they were created. These objects are therefore
allocated on the *heap*: a section of memory set aside for such
objects (not to be confused with the data structure for implementing
priority queues).

The heap is finite: if we allocate too many objects, we will run out of space.

Solution: deallocate space when it is no longer needed by an
object. Different languages implement different strategies for
managing the deallocation of dynamic objects:

* Manual deallocation with e.g., `free`, `delete` (C, Pascal)

* Automatic deallocation via garbage collection (Java, Scala, C#,
  OCaml, Haskell, Python, JavaScript, ...)

* Semi-automatic deallocation using destructors, smart pointers, and
  reference counting (C++, Ada, Objective-C, Rust) 

  * Automatic because the destructor is called at certain points automatically

  * Manual because the programmer writes the code for the destructor
    and/or needs to decide which smart pointer type to use.

Manual deallocation is a common source of bugs:
* Dangling references
* Memory leaks

We will discuss automatic memory management in more detail
later. However, it is helpful to understand how allocation and
deallocation requests by the program are implemented.

Ultimately, a program's requests for fresh heap memory are relayed to
the operating system via system calls. The operating in turn grants
the program access to memory blocks that it can use to store its
dynamically allocated objects. Since system calls are expensive, the
program's memory management subsystem usually requests large memory
blocks from the operating system at a time which it then manages
itself to satisfy dynamic allocation requests for smaller chunks of
memory that fit into the large block.

The heap thus starts out as a single block of memory. As objects are
allocated and deallocated, the heap becomes broken into smaller
subblocks, some in use and some not in use.

Most heap-management algorithms make use of a *free list*: a singly
linked list containing blocks not in use.

* *Allocation*: a search is done through the free list to find a free
  block of adequate size. Two possible algorithms:
  * *First fit*: first available block is taken
  * *Best fit*: all blocks are searched to find the one that fits the best
* *Deallocation*: the deallocated block is put back on the free list

*Fragmentation* is an issue that degrades performance of heaps over time:
* *Internal fragmentation* occurs when the block allocated to an
  object is larger than needed by the object.
* *External fragmentation* occurs when unused blocks are scattered
  throughout memory so that there may not be enough memory in any one
  block to satisfy a request.

Some allocation algorithms such as
the
[buddy system](https://en.wikipedia.org/wiki/Buddy_memory_allocation)
are designed to minimize external fragmentation while still being
efficient.

## Scope

The region of program text where a binding is active is called its *scope*.
Notice that scope is different from lifetime. Though, the scope of a
binding often determines its lifetime.

Kinds of scoping include

* *Static* or *lexical*: binding of a name is determined by rules that
 refer only to the program text (i.e. its syntactic structure).
    * Typically, the scope is the smallest block in which a variable is declared
    * Most languages use some variant of this
    * Scope can be determined at compile time
* *Dynamic*: binding of a name is given by the most recent declaration encountered during run-time
  * Used in Snobol, APL, some versions of Lisp
  * Considered a historic mistake by most language designers because
  it often leads to programmer confusion and bugs that are hard to
  track down.

To understand the difference between dynamic and static scoping,
consider the following Scala code

```scala
 1: var x = 1
 2: 
 3: def f () = { println(x) }
 4: def g () = { 
 5:  var x = 10
 6:  f ()
 7: }
 8: def h () = { 
 9:  var x = 100
10:  f ()
11: }
12:
13: f (); g (); h ()
```

Scala uses static scoping, so the occurrence of `x` on line 3 always refers to
the variable `x` declared on line 1. So the program will print `1`,
`1`, and `1`.

If Scala were to use dynamic scoping, the occurrence of `x` on line 3
will always refer to the most recent binding of `x` before `f` is
called. For the first call to `f` on line `13` this is `x` on line 1. 
For the call to `f` via `g`, it is `x` on line 5 and for the call
to `f` via `h`, it is `x` on line 9. So in this case the program will
print `1`, `10`, and `100`.

### Nested Scopes

Most languages allow nested subroutines or other kinds of nested
scopes such as nested code blocks, classes, packages, modules,
namespaces, etc. Typically, bindings created inside a nested scope are
not available outside that scope.

On the other hand, bindings at one scope typically are available
inside nested scopes.  The exception is if a new binding is created
for a name in the nested scope. In this case, we say that the original
binding is *hidden*, and has a *hole* in its scope.

Many languages allow nested scopes to access hidden bindings by using
a *qualifier* or *scope resolution operator*.

* In Ada, `A.x` refers to the binding of `x` created in subroutine
  `A`, even if there is a lexically closer scope that binds `x`.

* Similarly, Java, Scala, and OCaml use `A.x` to refer to the binding
  of `x` created in class (package, object, or module) `A`.
  
* In C++, `A::x` refers to the binding of `x` in *namespace* `A`, and
  `::x` refers to the global scope.

Scope qualifiers can be nested, e.g. `A.B.x` refers to the binding of
`x` in nested scope `B` of scope `A`. Qualified names are typically
interpreted relative to the scope in which they occur (respectively,
the global scope). For instance a qualified name `B.C.x` that occurs
in scope `A` may refer to (1) `A.B.C.x` or (2) `B.C.x` relative to the global
scope.

Some languages allow bindings of other named scopes to be imported
into the current scope so that they can be referred to without qualifiers

* In Java, `import java.util.ArrayList;` imports the class `ArrayList`
  from package `java.util` into the current scope, whereas `import
  java.util.*` imports all classes of that package.

* Scala uses similar syntax for imports as Java but allows `import`
  directives to occur in any scope and not just at the top-level scope
  of a package.
  
* In C++, `using std::cout;` imports the name `cout` from namespace
  `cout` into the current scope. On the other hand, `using namespace
  std;` imports all bindings made in namespace `std`.

* In OCaml, the directive `open Format` imports all bindings of module
  `Format` into the current scope.

Often, the visibility of bindings of names whose scope is outside of
the path to the current scope can be restricted using *visibility
modifiers* (e.g. `public`, `protected`, and `private`).

Some languages, such as Ada, support nested subroutines, which
introduce some extra complexity to identify the correct
bindings. Specifically, how does a nested subroutine find the right
binding for an object declared in an outer scope?

Solution:
* Maintain a *static link* to the *parent frame*
* The parent frame is the most recent invocation of the lexically
  surrounding subroutine
* The sequence of static links from the current stack frame to the frame
  corresponding to the outermost scope is called a *static chain*.

Finding the right binding:
* The level of nesting can be determined at compile time
* If the level of nesting is `j`, the compiler generates code to
  traverse the static chain `j` times to find the right stack frame.


### Declaration Order

What is the scope of `x` in the following code snippet?

```Scala
{
  statements1;
  var x = 5;
  statements2;
}
```

* C, C++, Ada, Java: `statements2`
* JavaScript, Modula3: entire block
* Pascal, Scala, C#: entire block, but not allowed to be used in
  `statements1`! If `x` is used in `statement1`, the compiler will
  reject the program with a static semantic error.

C and C++ require names to be declared before they are used. This
requires a special mechanism for recursive data types, which is to
separate the *declaration* from the *definition* of a name:

* A *declaration* introduces a name and indicates the scope of the name.
* A *definition* describes the object to which the name is bound.

Example of a recursive data type in C++:

```c++
struct manager;              // Declaration
struct employee {
  struct manager* boss;
  struct employee* next_employee;
  ...
};

struct manager {             // Definition
  struct employee* first_employee;
  ...
};
```

### Redeclaration

Some languages (in particular, interpreted ones) allow names to
be *redeclared* within the same scope.

```javascript
function addx(x) { return x + 1; }

function add2(x) {
  x = addx(x);
  x = addx(x);
  return x;
}

function addx(x) { return x + x; } // Redeclaration of addx
```

What happens if we call `add2(2)`?

In most languages that support redeclaration, the new definition
replaces the old one in all contexts, so we would get `8`.

In languages of the ML family like OCaml, the new binding only applies
to later uses of the name, not previous uses. For example, the
corresponding OCaml code of the above example looks like this

```ocaml
let addx x = x + 1

let add2 x =
  let x1 = addx x in
  let x2 = addx x1 in
  x2

let addx x = x + x
```

Calling `add2` with value `2` now yields `4` instead of `8`.


## Scala Intro

### Setting up your Scala toolchain

I will provide support and instructions for OSX and Linux (Ubuntu).
If you have a system running Windows, you are on your own. However,
you can install a Ubuntu virtual machine using VirtualBox and follow
the instructions for Ubuntu. VirtualBox is free. Instructions can be
found [here]( http://www.psychocats.net/ubuntu/virtualbox).

Make sure to give your system plenty of disk space, at least 30 GB,
if possible. Don't worry VirtualBox will only actually use what it
needs.

Once you've followed the above instructions, start the VM. Open the
Devices menu option and click 'Insert guest additions CD image.' You
will be prompted to run some software from that image. Follow the
instructions and install the guest additions. This will give you
better screen resolution.

#### Homebrew [OSX only] 
 
Homebrew is a package manager for OSX, which makes installing
development software much easier. We will use it to install Sbt. You
will find it useful in the future for install of other things as well.

* [OSX] Install using the instructions [here](http://brew.sh/)

#### XCode [OSX only]

XCode is a development environment for Macs. We will not be using it,
but installing it installs a number of useful Unix command line tools.

* [OSX] Install the most recent version of XCode from [here](https://developer.apple.com/xcode/downloads/)

#### Git

* [Ubuntu] Git is pre-installed on Ubuntu.
* [OSX] From terminal: ```brew install git```
* You can test the install of git on your system by running the command `git` from terminal. You should see usage information.
* Finally run the following commands from terminal:<br>
   ```git config --global user.email "your@email.com"```<br>
   ```git config --global user.name "Your Name"```<br>
   (The email should be the same email you used to register your github account)

Here are some Git-related resources:
* If you are unfamiliar with Git, watch the [first two git basics video](http://git-scm.com/videos).
* If you are unfamiliar with Github, watch [this YouTube video](https://www.youtube.com/watch?v=0fKg7e37bQE).
* A [simple git cheatsheet](http://rogerdudler.github.io/git-guide/).
* A [complete reference](http://www.git-scm.com/book/en/v2).
* I suggest using the command line or the IntelliJ integration to interact with Git, but in a pinch [this GUI](https://desktop.github.com/) might be useful.

#### Sbt

Sbt is an open source build tool for Scala projects. More information can be
found [here](https://en.wikipedia.org/wiki/SBT_%28software%29). (You
will need this to run Scala code that I provide)

* [OSX]  From terminal: ```brew install sbt```
* [Ubuntu] From terminal:<br>
   ```echo "deb https://dl.bintray.com/sbt/debian/" | sudo tee -a /etc/apt/sources.list.d/sbt.list```<br>
   ```sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 642AC823```<br>
   ```sudo apt-get update```<br>
   ```sudo apt-get install sbt```  
* Confirm success by running the command from terminal: 
    ```sbt    ```
    (Sbt should start. Use `Ctrl+c` to quit or type `exit`.)

More detailed instructions can be found [here](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Linux.html).

#### IntelliJ Idea

I will be using
the [IntelliJ Idea Java IDE](https://www.jetbrains.com/idea/) to
demonstrate Scala and Java code in class. While it is not necessary
for you to install an IDE, I recommend using it as it will make your
life easier.

* Sign up
  for
  [free student licenses](https://www.jetbrains.com/shop/eform/students) (Reminder:
  use your NYU email address)
* In the meantime, download the [Ultimate Edition Free 30-day trial](https://www.jetbrains.com/idea/download/) of Intellij.
* [Ubuntu] Untar the downloaded archive by clicking it and then using the "Extract" menu item. Extract to location of your choice. Open that location and follow the instructions inside the "Install-Linux-tar.txt" file.
* [OSX] Open the disk image and use the installer.
* When prompted, select "Evaluate for 30 days". Install the license when you get them in an email from Jetbrains.
* During the "Customize" phase on the "*Featured* plugins screen",
  select and install the 'Scala' plugin. It should be in the top left
  corner of this screen. This is necessary to get sbt integration and
  Scala support in Intellij.
* For reference, here is a link to the [Intellij documentation](https://www.jetbrains.com/idea/help/basic-concepts.html).

There are many free plugins available for Intellij. You should feel free to install anything that sounds useful to you. You can explore what is available from the "Preferences" menu in Intellij.

#### Importing a Scala Sbt Project into Intellij

To import the Scala sbt project for Class 2 into Intellij, do the following:

* Choose a place on your computer for your project files to reside and open a terminal to that location.

* Clone this repository from Github by executing the following git
  command in your terminal: <br/>
  ```git clone https://github.com/nyu-pl-fa18/class02.git```

* Open Intellij and click the "Import Project" menu item
  (Alternatively, press Ctrl+Shift+a [Ubuntu] or Command+Shift+a [OSX]
  and type 'import project'. Then select the command 'Import Project'
  from the drop down menu).
  
* Navigate to your cloned repository and select the "class02"
  directory and click "Import".
  
* Click the radio button "Import project from external model".

* Highlight sbt. Click Next.

* Check "Use sbt shell for build and import", and under "Download:
  check "Library sources" and "sbt sources". Do not hit "Finish" yet.
  
* The dropdown for the Project SDK will mostly likely be empty. We
  need to configure a JVM.
  
  * Click "New" and then "JDK". 
  * Most likely IntelliJ will guess correctly where your JDK is. If not..
  * [Ubuntu] it is ```/usr/lib/jvm/java-8-openjdk-amd64```
  * [OSX] Where your JVM is depends on what version of OSX you are
    using. On newer versions of OSX you can use this command to find
    the location of the JDK ```/usr/libexec/java_home -v 1.8```).
  * Select the JDK folder.
    
* Under "JVM Options" below "Global sbt settings", remove the text in
  the field labeled "VM parameters". Click "Finish".

* It may take IntelliJ a few minutes to initialize the project. Future
  project imports will be faster.

* If you are prompted with a message like "Unregistered VCS root
  detected", simply click "Add root".

* Open the worksheet `src/main/scala/pl/class02/Demo.sc` and type in some Scala
  expressions (see below). Alternatively, start the Scala REPL by
  typing `console` in the sbt shell. If the sbt shell is not already
  open, you can open it by pressing Crtl+Shift+s [Ubuntu] or
  Command+Shift+s [OSX].

* Post on Piazza if you need help, most likely others have had the
  same problem and may have figured it out.

### Scala Basics

In the following, we assume that you have started the Scala
REPL. Though, (almost) all of these steps can also be done in a Scala
worksheet.

#### Expressions, Values, and Types

After you type an expression in the REPL, such as `3 + 4`,
and hit enter:

```scala
scala> 3 + 4
```

The interpreter will print:

```scala
res0: Int = 7
```

This line includes:

* an automatically generated name `res0`, which refers
  to the value resulting from evaluating the expression,
* a colon `:`, followed by the type `Int` of the expression,
* an equals sign `=`,
* the value `7` resulting from evaluating the expression.

The type `Int` names the class `Int` in the
package `scala`. Packages in Scala partition the global
name space and provide mechanisms for information hiding, similar to
Java packages. Values of class `Int` correspond to values of
Java's primitive type `int` (Scala makes no difference
between primitive and object types). More generally, all of Java's
primitive types have corresponding classes in the `scala` package.

We can reuse the automatically generated name `res0` to
refer to the computed value in subsequent expressions (this only works
in the REPL but not in a worksheet):

```scala
scala> res0 * res0
res1: Int = 9 
```

Java's ternary conditional operator `? :` has an equivalent in Scala,
which looks as follows:
```scala
scala> if (res1 > 10) res0 - 5 else res0 + 5
res2: Int = -2
```

In addition to the `? :` operator, Java also has if-then-else
statements. Scala, on the other hand, is a functional language and
makes no difference between expressions and statements: every
programming construct is an expression that evaluates to some
value. In particular, we can use if-then-else expressions where we
would normally use if-then-else statements in Java.
```scala
scala> if (res1 > 2) println("Large!") 
       else println("Not so large!")
res3: Unit = ()
```
In this case, the if-then-else expression evaluates to the value
`()`, which is of type `Unit`. This type indicates
that the sole purpose of evaluating the expression is the side effect
of the evaluation (here, printing a message on standard output). In
other words, in Scala, statements are expressions of type
`Unit`. Thus, the type `Unit` is similar to the
type `void` in Java, C, and C++ (which however, has no values). The value
`()` is the only value of type `Unit`. 

#### Names

We can use the `val` keyword to give a user-defined name to
a value, so that we can subsequently refer to it in other expressions:
```scala
scala> val x = 3
x: Int = 3
scala> x * x
res0: Int = 9
```
Note that Scala automatically infers that `x` has type
`Int`. Sometimes, automated type inference fails, in which
case you have to provide the type yourself. This can be done by
annotating the declared name with its type:
```scala
scala> val x: Int = 3
x: Int = 3 
```
A `val` is similar to a `final` variable in
Java or a `const` variable in JavaScript. That is, you cannot reassign it another value:
```scala
scala> x = 5
<console>>:8: error: reassignment to val
       x = 5
         ^
```
Scala also supports mutable variables, which can be
reassigned. These are declared with the `var` keyword
```scala
scala> var y = 5
y: Int = 5
scala> y = 3
y: Int = 3
```
The type of a variable is the type inferred from its initialization
expression. This type is fixed. Attempting to reassign a variable to a value of incompatible type results in a type error:
```scala
scala> y = "Hello"
<console>:8: error: type mismatch;
 found   : String("Hello")
 required: Int
       y = "Hello"
           ^
```

#### Functions

Here is how you write functions in Scala:

```scala
scala> def max(x: Int, y: Int): Int = {
         if (x > y) x
         else y
       }
max: (x: Int, y: Int)Int
```

Function definitions start with `def`, followed by the
function's name, in this case `max`. After the name comes a
comma separated list of parameters enclosed by parenthesis, here
`x` and `y`. Note that the types of parameters
must be provided explicitly since the Scala compiler does not infer
parameter types. The type annotation after the parameter list gives
the result type of the function. The result type is followed by the
equality symbol, indicating that the function returns a value, and the
body of the function which computes that value. The expression in the
body that defines the result value is enclosed in curly braces.

If the defined function is not recursive, as is the case for
`max`, the result type can be omitted because it is
automatically inferred by the compiler. However, it is often helpful
to provide the result type anyway to document the signature of the
function. Moreover, if the function body only consists of a single
expression or statement, the curly braces can be omitted. Thus, we
could alternatively write the function max like this:

```scala
scala> def max(x: Int, y: Int) = if (x > y) x else y
max: (x: Int, y: Int)Int
```

Once you have defined a function, you can call it using its name:
```scala
scala> max(6, 3)
res3: Int = 3
```

Naturally, you can use values and functions that are defined outside of a function's body in the function's body:
```scala
scala> val pi = 3.14159
pi: Double = 3.14159

scala> def circ(r: Double) = 2 * pi * r
circ: (r: Double)Double
```

You can also nest value and function definitions:
```scala
scala> def area(r: Double) = {
         val pi = 3.14159
         def square(x: Double) = x * x
         pi * square(r)
       }
area:(r: Double)Double
```

Recursive functions can be written as expected. For example, the
following function `fac` computes the factorial numbers:
```scala
scala> def fac(n: Int): Int = if (n <= 0) 1 else n*fac(n-1)
fac: (n: Int)Int

scala> fac(5)
res4: Int = 120
```

#### Scopes

Scala's scoping rules are similar to Java's:

```scala
val a = 5
// only a in scope
{
  val b = 4
  // b and a in scope

  def f(x: Int) = {
    // f, x, b, and a in scope
    a * x + b 
  }
  // f, b, and a in scope
}
// only a in scope
```

There are some differences to Java, though. Scala allows you to redefine
names in nested scopes, even if that name has already been bound in
an outer local scope:
```scala
val a = 3;
{
  val a = 4 // hides outer definition of a
  a + a     // yields 8
}
```
However, as in Java, you cannot redefine a name in the same scope:
```scala
  val a = 3
  val a = 4 // does not compile
```
Also, unlike in Java, you can't refer to a name before it is bound in
the same block, even if that name has been bound in an outer scope:

```scala
{
  val x = 2;
  {
    println(x) // Forward reference to `x` declared in this  block. Does not compile
    val x = 3;
    x + x
  }
}
```

