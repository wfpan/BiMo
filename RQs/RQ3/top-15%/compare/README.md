# compare
This directory contains the *recall* results obtained in the experiments.

## CSV files
This directory contains the *recall* results obtained on each subject systems.

Note that, each CSV file contains 4 columns. For example, in the file of *ant_main_nofilter_recall.csv*, there are 4 columns, i.e., ant_main(10), TSPrideWithMotifCoreness-FGCS, TSPrideWithMotifCoreness-KANG, and TSPrideWithMotifCoreness-SORA. The first column (i.e., ant_main(10)) shows the name of the software (i.e., ant) and the number of key classes in the software (i.e., 10). The 
rest 3 columns actually correspond to the BiMo applied to different CANs (using DWM, OWM, and EWM to assign strengths for different relationship types). It should 
be noted that the name of the 3 approaches in the CSV file is different from that used in our manuscript. The following Table shows the mapping of the name used in CSV file and 
the name used in our manuscript.

|names in the CSV|TSPrideWithMotifCoreness-FGCS | TSPrideWithMotifCoreness-KANG | TSPrideWithMotifCoreness-SORA | 
|-------|-------|-------|-------|
|names in the manuscript|BiMo$_{DWM}$ | BiMo$_{OWM}$ | BiMo$_{EWM}$ | 