Auckland Layout Model (ALM)
===========================

This is the ALM project. ALM is a layout management and solving framework written in Java and Scala. It also contains the Auckland Layout Editor (ALE).
The development environment recommended is IntelliJ (Eclipse will also work but you may need to configure the project differently from the instructions given below).


Installation
------------

1. Install Software:
First you need to install:
- Scala: http://scala-lang.org
- IntelliJ (at least version 14): http://www.jetbrains.com/idea/
- Scala plugin for IntelliJ:
  In IntelliJ choose File -> Settings -> Plugins -> Browse repositories -> Search for "scala"... and install.

2. Open the PDStore project in IntelliJ
Do not "import" it, this won't work -- just open its folder (File -> Open).

3. Register JDK:
In File -> Project Structure -> Project ensure the normal Java JDK is set as the Project SDK (e.g. "1.7"). For that to work you need to make sure there is a Java SDK installed on your computer (at least JDK version 1.7) and that the JDK is registered in File -> Project Structure -> Platform Settings -> SDKs (click + to add it if it isn't).

4. Register Scala:
In File -> Project Structure -> Global Libraries, look for a global library called "Scala". This is the Scala compiler library which is essential for correct compilation. If it is not there, add it by clicking the + and select Scala SDK (if you have installed Scala it should be detected automatically). Make sure the library has the name "Scala" (rename it with the text field at the top of the tab). You may have to adjust its path. The correct path is the one where the Scala JAR files were installed (under Windows typically "C:\Program Files (x86)\scala\lib").

5. Install lp_solve [OPTIONAL]
This is only necessary if you would like to use the external lp_solve solver, as opposed to one of the built-in solvers.
AIM is using lp_solve_5.5.2.0. There are actually three libraries related to lpsolver which are used by ALM:
- lpsolve55j.jar  : same for 32 and 64 bit (AIM source and library already contain this in the folder libs)
The following two libraries are platform dependent so proper versions of them for the target platform should be copied
to the standard library directory. On Windows, a typical place would be \WINDOWS or \WINDOWS\SYSTEM32. On Linux, a typical
place would be the directory /usr/local/lib. For more information, refer to http://web.mit.edu/lpsolve_v5520/doc/Java/README.html.
- lpsolve55j.dll which can be downloaded from http://hivelocity.dl.sourceforge.net/project/lpsolve/lpsolve/5.5.2.0/lp_solve_5.5.2.0_java.zip.
- lpsolve55.dll  which can be downloaded from http://sourceforge.net/projects/lpsolve/files/lpsolve/5.5.2.0/lp_solve_5.5.2.0_dev_<your-platform>.zip

6. Run PDStore examples:
Everything should be ready to go now. Test it by running the examples. In the project tree, find the files in package alm.examples and right-click -> Run.


Documentation
-------------

The most recent code documentation is contained in JavaDoc comments in the various files.
Other starting points are:
- The GUI layout specification and editing examples in alm.examples
- The project report about the ALM and ALE code base by Irene Zhang:
https://www.cs.auckland.ac.nz/~lutteroth/publications/theses/ALE-IreneZhang-2014.pdf
- A UIST 2013 publication on ALE:
https://www.cs.auckland.ac.nz/~lutteroth/publications/ZeidlerEtAl2013-AucklandLayoutEditor.pdf
- A theoretical description of ALM:
https://www.cs.auckland.ac.nz/~lutteroth/publications/LutterothStrandhWeber2008-AucklandLayoutModel.pdf
- A more practical description of the conceptual ideas in ALM:
http://www.cs.auckland.ac.nz/~lutteroth/publications/KimLutteroth2009-DocumentOrientedGUIs.pdf
- A paper describing the reverse engineering of non-ALM GUI layouts to ALM:
http://www.cs.auckland.ac.nz/~lutteroth/publications/Lutteroth2008-LayoutReverseEng.pdf


Contact
-------

Project maintainers:
Christof Lutteroth, christof@cs.auckland.ac.nz
Gerald Weber, gerald@cs.auckland.ac.nz

Project website:
https://github.com/aucklandhci/alm