# Relation Extraction

This library implements an approach to extract RDF relations from a given sentence.

## Requirements
Java 8, Maven 3

## Example
Input:
````
Barack Obama is married to Michelle Obama.
````
Output:
````
<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/ontology/spouse> <http://dbpedia.org/resource/Michelle_Obama>.
````

## Config
A sample config file
````JSON
{
  "learn":{
    "window": 2,
    "bayes": 1.0,
    "neighbour": 20
  },
  "mongo":{
    "hostname":"...",
    "port": 12345,
    "credentials":{
      "name":"username",
      "password":"password"
    },
    "database":"..."
  }
}
````
Description for learn:
* window: the number of feature used left to the first entity and right to the last entity
* bayes: the bayes smoothing parameter for unknown features
* neighbour: the number of nearest neighbours to select

## How to use
The project consists of three main entry points:
1. ``RelationExtraction.java``
2. ``ExtractionTester.java``
3. ``FileExtractor.java``

### Relation Extraction
This project can be start up by giving a path to a configuration file.
If the path is invalid or not given, then it will start up with a default configuration.
The script will train a model on a set of training data.
The created model will then be evaluated.
Output:
* F1 score for each relation
* Precision for each relation
* Recall for each relation
* The overall precision

### Extraction Tester
This project can be start up by giving a path to a configuration file.
If the path is invalid or not given, then it will start up with a default configuration.
The script will train a model. Then the user can give input sentences. The script will print found relations in Turtle format.

### File Extractor
To start the script you have to provide the following arguments:
1. a config file
2. input path for file containing sentences
3. output path for the found relations in Turtle format

This script do the same as the extraction tester but can automatically
process a bunch of input sentences.