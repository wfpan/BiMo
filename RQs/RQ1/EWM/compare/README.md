# compare
This directory contains the *recall* results obtained in the experiments.

## CSV files
This directory contains the *recall* results obtained on each subject systems.

Note that, each CSV file contains 13 columns. For example, in the file of *ant_main_nofilter_recall.csv*, there are 13 columns, i.e., ant_main(10), h-index, a-index, k-core, ForwardPageRank-Sora, ForwardBackPageRank-Sora, g-core, MinClass, TSEPageRank, LiuPageRank, TSEPageRank_v15, iFit, and TSPrideWithMotifCoreness. The first column (i.e., ant_main(10)) shows the name of the software (i.e., ant) and the number of key classes in the software (i.e., 10). The 
rest 12 columns actually correspond to 12 approaches: 11 baseline approaches and BiMo. It should 
be noted that the name of the 12 approaches in the CSV file is different from that used in our manuscript. The following Table shows the mapping of the name used in CSV file and 
the name used in our manuscript.

|names in the CSV|h-index | a-index | k-core | ForwardPageRank-Sora | ForwardBackPageRank-Sora | g-core |
|-------|-------|-------|-------|-------|-------|-------|
|names in the manuscript|h-index | a-index | k-core | PageRank | PageRankBR | ICOOK |

|names in the CSV| MinClass | TSEPageRank| LiuPageRank | TSEPageRank_v15|iFit|TSPrideWithMotifCoreness|
|-------|-------|-------|-------|-------|-------|-------|
|names in the manuscript|MinClass| ElementRank | PageRankIVOL |Pride|iFit|BiMo|