##### BiMo is implemented as a step-by-step procedure consisting of the following automated steps:

- CAN Construction: We utilize our own-developed software SNAP to automatically construct CANs from the source code. Specifically, SNAP reads a directory containing the source code of a project and outputs the CAN (a .net file) for each subject system (e.g., ant_CAN.net). But at present, SNAP is only obtained on request. In this repository, we have open-sourced an earlier version of our parsing tool, dfParser, to facilitate replication. Please refer to the source code in [dfParser.zip](https://github.com/wfpan/BiMo/tree/main/Code/dfParser.zip) .

- Motif Extraction: The detection of 3- or 4-node motifs is performed automatically using mfinder. The software reads the .net network file (CAN) and identifies the IDs of all significant motifs in the CAN, along with the node combinations corresponding to each motif ID. Please use the software in [mfinder1.2.zip](https://github.com/wfpan/BiMo/tree/main/Code/mfinder1.2.zip), [fanmod.zip](https://github.com/wfpan/BiMo/tree/main/Code/fanmod.zip), and [FANMODPlus-main](https://github.com/wfpan/BiMo/tree/main/Code/FANMODPlus-main.zip).

- MCCN Construction: We developed custom scripts to construct the MCCN. Specifically, our scripts read the motif results and output the MCCN for each subject system (e.g., ant_CAN.net.motif). Please use the scripts in [motif-parser.zip](https://github.com/wfpan/BiMo/tree/main/Code/motif-parser.zip).

- *BE* Calculation: We adapted the scripts provided by Gao et al. [26, 27] from their online replication package to calculate *BE* values. The adapted version is contained in [CGRAB.zip](https://github.com/wfpan/BiMo/tree/main/Code/CGRAB.zip.).

- *MC* Calculation: We applied generalized k-core decomposition to calculate the MC values for classes in the MCCN. To calculate *MC* values of classes int the MCCN, please use [GCore.jar](GCore.jar).

- Ranking: Finally, the BiCoRank algorithm is executed to compute the *BMR* value for each class, automatically generating the ranked list of candidate key classes. To calculate *BMR* values, please use [BiMoV1.0.jar]().


##### To facilitate future research, we have made the software and datasets publicly available on GitHub (https://github.com/wfpan/BiMo/tree/main/Code). The repository includes:

- All the software and scripts used for the procedure described above, available at https://github.com/wfpan/BiMo/tree/main/Code.

- All data used in our work, available at https://github.com/wfpan/BiMo/tree/main/CANs, https://github.com/wfpan/BiMo/tree/main/MCCNs, https://github.com/wfpan/BiMo/tree/main/goldset, and https://github.com/wfpan/BiMo/tree/main/Code/CGRAB.zip.

- All results obtained in our work, available at https://github.com/wfpan/BiMo/tree/main/RQs, https://github.com/wfpan/BiMo/tree/main/discussion, and https://github.com/wfpan/BiMo/tree/main/otherResults.

##### How to use our dataset and software

The steps to use BiMoV1.0.jar is shown as follows. To run the software, you should install the jdk (java development kit) 1.6 (or higher) first in your computer. JDK can be downloaded from https://www.oracle.com/technetwork/java/javase/downloads/index.html
	- Download the BiMoV1.0.jar and the dataset file to the same directory.
	- Double-click the BiMoV1.0.jar
	- Select File->Open a CAN file... 
	- Browse the data directory and select a specific CAN file (ending with .net).
	- Select Analysis -> BiCoRank
	- Then you will get the BMR values for each class in the popup window.


##### When using the software and scripts mentioned above, please adapt the paths to suit your specific needs.