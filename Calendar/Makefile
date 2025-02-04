SRC_DIR := src
BIN_DIR := bin
LIB_DIR := lib
MAIN_CLASS := CalendarApp
JAR_NAME := MyCalendarApp.jar
MANIFEST := manifest.txt

JAVAC := javac
JAVA := java
JAR := jar

CLASSPATH := $(LIB_DIR)/*
SOURCES := $(shell find $(SRC_DIR) -name "*.java")
CLASSES := $(patsubst $(SRC_DIR)/%.java,$(BIN_DIR)/%.class,$(SOURCES))

all: $(JAR_NAME)

$(BIN_DIR):
	mkdir -p $(BIN_DIR)

$(BIN_DIR)/%.class: $(SRC_DIR)/%.java | $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) -cp "$(CLASSPATH)" $<

classes: $(CLASSES)

$(JAR_NAME): classes manifest.txt
	$(JAR) cfm $(JAR_NAME) $(MANIFEST) -C $(BIN_DIR) .

RUN_CLASSPATH := $(JAR_NAME)$(if $(wildcard $(LIB_DIR)/*.jar),:$(wildcard $(LIB_DIR)/*.jar))

run: $(JAR_NAME)
	$(JAVA) -cp "$(RUN_CLASSPATH)" $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR) $(JAR_NAME)

manifest.txt:
	echo "Main-Class: $(MAIN_CLASS)" > $(MANIFEST)
	echo "" >> $(MANIFEST)

.PHONY: all classes run clean manifest.txt
