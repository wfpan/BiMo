# puttogether
This directory contains the *recall* results obtained at each *k* value across all subject systems. We aggregate the *recall* results obtained at each *k* value across all subject systems into a separate file.

## CSV files
Each csv file contains the *recall* results obtained at a specific *k* value across all subject systems. The file name indicates the *k* value. k_1 means it stores the *recall* results obtained at *k*=1 on each subject system. 

Note that, each CSV file contains 13 columns. The first column shows the software name, while other columns list 12 approaches, i.e., h-index, a-index, k-core, ForwardPageRank-Sora, ForwardBackPageRank-Sora, g-core, MinClass, TSEPageRank, LiuPageRank, TSEPageRank_v15, iFit, and TSPrideWithMotifCoreness. It should 
be noted that the name of the 12 approaches in the CSV file is different from that used in our manuscript. The following Table shows the mapping of the name used in CSV file and 
the name used in our manuscript.

|names in the CSV|h-index | a-index | k-core | ForwardPageRank-Sora | ForwardBackPageRank-Sora | g-core |
|-------|-------|-------|-------|-------|-------|-------|
|names in the manuscript|h-index | a-index | k-core | PageRank | PageRankBR | ICOOK |

|names in the CSV| MinClass | TSEPageRank| LiuPageRank | TSEPageRank_v15|iFit|TSPrideWithMotifCoreness|
|-------|-------|-------|-------|-------|-------|-------|
|names in the manuscript|MinClass| ElementRank | PageRankIVOL |Pride|iFit|BiMo|