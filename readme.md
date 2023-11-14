special invocation: A way to call a method statically, from an object that is not an instance of the class that defines the method. This is done by passing the object as the first argument to the method.


## Installation of z3
mvn install:install-file -Dfile="<path-to-com.microsoft.z3.jar>" -DgroupId=com.microsoft -DartifactId=z3 -Dversion=4.11.0 -Dpackaging=jar -DgeneratePom=true