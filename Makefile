JAVAC=javac
sources = $(wildcard ProgramLogic/*.java Network/*.java)
classes = $(sources:.java=.class)

all: $(classes)

clean :
	rm -f -r *.class

%.class : %.java
	$(JAVAC) $<