# Title
Source code of PMFEA-EDA algorithms for evolutonary multitasking semantic web service composition.

# Getting Started
These instructions will get you a copy of the project and running on your local machine.

# Prerequisites

To run the source code of PMFEA-EDA, the following jar files need to be built in the path of library:

1. Java jar: JAVA_SE 1.8
2. Third party jars: ECJ23, Guava, Lang3, Math3, and JGraphT.

# Run 

1. We have generated and uploaded a runnable jar (i.e., a MultiEDA4MultiTaskingSWSC.jar file) in our code repository.

2. To run MultiEDA4MultiTaskingSWSC.jar via terminals, please use the following command line with five parameters: `java -jar MultiEDA4MultiTaskingSWSC.jar -file MultiEDAmultiTasking.params`. (Note that full paths of MultiEDAmultiTasking.params file must be given in the command line, you can download the dataset files from https://github.com/chenwangnida/Dataset).

   	- the parameter: MultiEDAmultiTasking.params is a path of an output file.

3. The output file, out.stat, records 200 rows (i.e., the number of generations) of space-separated values. For example, one row of values (0 16 162485 0.13839677064697317 0.7374747126322804 0.21657955534542447 0.09157955534542447) represents that generation counter is 0, initialization time is 16ms, computation time of generation 0 is 162485ms, the best fitness found so far is task 1, 2 , 3 and 4 are 0.13839677064697317 0.7374747126322804 0.21657955534542447 0.09157955534542447, respectively). In addition, the last four row of out.stat records the best composite services found by our algorithm for each task. This composite service is described in a simple text language using Graphviz as below(please refer to the link https://graphviz.org for more details on Graphivz).

`digraph g {startNode->serv1900589909_1; startNode->serv1277338356; serv1900589909_1->serv2108886608_1; serv1277338356->serv2108886608_1; serv1900589909_1->serv2039454375; serv1277338356->serv2039454375; serv2039454375->serv1346770589_1; serv1346770589_1->serv30835212_1; serv2108886608_1->serv30835212_1; serv1346770589_1->serv100267445; serv2108886608_1->serv100267445; serv100267445->serv1555067288; serv30835212_1->serv862383464_1; serv1555067288->serv862383464_1; serv862383464_1->serv1624499521; serv30835212_1->serv1624499521; serv862383464_1->serv308564144_1; serv30835212_1->serv308564144_1; serv862383464_1->serv239131911; serv30835212_1->serv239131911; serv1624499521->serv1832796220; serv239131911->serv1832796220; serv308564144_1->serv1832796220; serv1624499521->serv1140112396; serv239131911->serv1140112396; serv308564144_1->serv1140112396; serv1624499521->serv447428610; serv239131911->serv447428610; serv308564144_1->serv447428610; serv1624499521->serv1209544629; serv239131911->serv1209544629; serv308564144_1->serv1209544629; serv1624499521->serv377996377; serv239131911->serv377996377; serv308564144_1->serv377996377; serv1140112396->serv1348409095_1; serv1832796220->serv1348409095_1; serv377996377->serv1348409095_1; serv447428610->serv1348409095_1; serv1209544629->serv1348409095_1; serv1140112396->serv2041092919; serv1832796220->serv2041092919; serv377996377->serv2041092919; serv447428610->serv2041092919; serv1209544629->serv2041092919; serv1140112396->serv586293076; serv1832796220->serv586293076; serv377996377->serv586293076; serv447428610->serv586293076; serv1209544629->serv586293076; serv1348409095_1->endNode; serv2041092919->endNode; serv586293076->endNode; }`


To generate a graph in a png format, please save the above description in a dot file (e.g., namely, dag.dot), and then use the following command line to generate a composition graph as shown in Figure below.

`dot -Tpng dag.dot  -o dag.png`


![dag_best](https://user-images.githubusercontent.com/20468313/130166954-2f3ca3ab-48c3-4670-a799-d45ab37dc246.png)

