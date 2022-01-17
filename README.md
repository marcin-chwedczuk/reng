
# Reng - Simple backtracking regex engine

Related blog post: [http://blog.marcinchwedczuk.pl/matching-regexes-using-backtracking](http://blog.marcinchwedczuk.pl/matching-regexes-using-backtracking)

How to build:
```
./mvnw clean install
```

By default project requires JDK 17 to compile.
You can change that by editing lines:
```xml
<!-- Java version -->
<maven.compiler.release>17</maven.compiler.release>
```
in `pom.xml` file. Project should compile under older JDK versions (like 11 without any problems).

