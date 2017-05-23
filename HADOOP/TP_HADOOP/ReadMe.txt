



		TP HADOOP:




start-dfs.sh

******************************************************************************************************************************
hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/afrass
hdfs dfs -mkdir /user/afrass/wordcount/
hdfs dfs -mkdir /user/afrass/wordcount/input

******************************************************************************************************************************
echo "hello i'm file01" >> file01
echo "hello i'm file02" >> file02

******************************************************************************************************************************
//ajouter le file01 dans input
bin/hdfs dfs -put file01 wordcount/input

******************************************************************************************************************************
//ajouter le file02 dans input
bin/hdfs dfs -put file02 wordcount/input

******************************************************************************************************************************
bin/hdfs dfs -ls /user/afrass/wordcount/input/
    ====>
  -rw-r--r--   1 afrass supergroup         17 2017-04-03 09:32 /user/afrass/wordcount/input/file01
  -rw-r--r--   1 afrass supergroup         17 2017-04-03 09:32 /user/afrass/wordcount/input/file02
******************************************************************************************************************************
bin/hdfs dfs -cat /user/afrass/wordcount/input/file02
  =====>
  17/04/03 10:07:08
  hello i'm file02


  ******************************************************************************************************************************

Lancer un job MapReduce correspondant à votre programme WordCount :
    bin/hadoop jar wc.jar WordCount /user/afrass/wordcount/input /user/afrass/wordcount/output


******************************************************************************************************************************
// le resultat: le programme wordcount.java calcule les mots.
  bin/hdfs dfs -cat /user/afrass/wordcount/output/part-r-00000
  17/04/03 10:09:44
  file01	1
  file02	1
  hello	2
  i'm	2

  ******************************************************************************************************************************

pour faire l'exo d'anagrames sur eclipse il faut copier les deux librairies dans pom.xml comme des dependency.

<dependencies>
  <!-- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-common -->
  	<dependency>
  	    <groupId>org.apache.hadoop</groupId>
  	    <artifactId>hadoop-common</artifactId>
  	    <version>2.8.0</version>
  	</dependency>

  <!-- https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-core -->
	 <dependency>
    	<groupId>org.apache.hadoop</groupId>
    	<artifactId>hadoop-core</artifactId>
    	<version>1.2.1</version>
	</dependency>
  </dependencies>

    ******************************************************************************************************************************
    start-dfs.sh

    ******************************************************************************************************************************
    hdfs dfs -mkdir /user
    hdfs dfs -mkdir /user/afrass
    hdfs dfs -mkdir /user/afrass/Anagrames/
    hdfs dfs -mkdir /user/afrass/Anagrames/input

    ******************************************************************************************************************************

    echo "crane" >> test.text

******************************************************************************************************************************
    bin/hdfs dfs -ls /user/afrass/Anagrames/input/

    -rw-r--r--   1 afrass supergroup          6 2017-04-24 11:30 /user/afrass/Anagrames/input/test.txt

******************************************************************************************************************************

    bin/hdfs dfs -cat /user/afrass/Anagrames/input/test.txt
    crane


    ******************************************************************************************************************************

    
